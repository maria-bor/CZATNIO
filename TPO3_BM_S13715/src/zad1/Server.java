/**
 *
 *  @author Borowiec Mariia S13715
 *
 */

package zad1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sun.nio.cs.ext.ISCII91;

public class Server extends BaseCommunication {

	private int port;
	private InetAddress inetAddress;
	private ServerSocketChannel serverSocketChannel;
	private Selector selector;
	private volatile boolean serverRunning = true;
	
	private Map<String, SocketChannel> loginOnChannel;
	private static Thread threadServer;
	private Map<String, String> usersLoginData;
	private List<String> listLoggedUsers;
	private Map<String, User> registeredUsers;
	
	public Server(InetAddress inetAddres, int port) {
		super();
		this.port = port;
		this.inetAddress = inetAddres;
		
		InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddres, port);
		
		try {
			this.selector = SelectorProvider.provider().openSelector();
			this.serverSocketChannel = ServerSocketChannel.open();
			this.serverSocketChannel.configureBlocking(false);
			
			serverSocketChannel.socket().bind(inetSocketAddress);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		this.usersLoginData = new HashMap<String, String>();	
		this.loginOnChannel = new HashMap<String, SocketChannel>();
		this.listLoggedUsers = new LinkedList<String>();
		this.registeredUsers = new HashMap<String, User>();
	}
	
	@SuppressWarnings("rawtypes")
	public void runServer() {
		while(serverRunning) {
			try {
				this.selector.select();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Iterator selectedKeys = this.selector.selectedKeys().iterator();
			while(selectedKeys.hasNext()) {
				SelectionKey key = (SelectionKey) selectedKeys.next();
				selectedKeys.remove();
				
				if (!key.isValid()) {
					continue;
				}
				
				if (key.isAcceptable()) {
					try {
						ServerSocketChannel serverSocketChannelTmp = (ServerSocketChannel) key.channel();
						SocketChannel socketChannel = serverSocketChannelTmp.accept();
						socketChannel.configureBlocking(false);
						socketChannel.register(this.selector, SelectionKey.OP_READ);	
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else if (key.isReadable()) {
					read(key);
				}
			}
		}
	}
	
	public void read(SelectionKey key) {
		super.read(key);
		if (cmd != null) {
			Object result = null;
			// check instance of Class ?? TO-DO
			if (cmd instanceof RegisterCommand) {
				try {
					result = registerUser((RegisterCommand) cmd, key);
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
			else if (cmd instanceof LoginCommand) {
				result = loginUser((LoginCommand) cmd);
			}
			else if (cmd instanceof UnregisterCommand) {
				unregisterUser((UnregisterCommand)cmd);
			}
			
			if (result != null)
				send(sc, result);
		}
		else {
			System.out.println("[Server.READ() FAILED]");
		}
	}
	
	

	public static Thread getThreadServer() {
		return threadServer;
	}
	
	public Object registerUser(RegisterCommand regCmd, SelectionKey key) {
		if (!this.usersLoginData.containsKey(regCmd.getLogin())) {
			usersLoginData.put(regCmd.getLogin(), regCmd.getHaslo());	
			loginOnChannel.put(regCmd.getLogin(),  (SocketChannel) key.channel());
			registeredUsers.put(regCmd.getLogin(), regCmd.getUser());
			return new String("SUCCESS");
		}
		else {
			return new String("FAIL");
		}
	}
	
	public void unregisterUser(UnregisterCommand unregCmd) {
		if (this.registeredUsers.containsKey(unregCmd.getLogin())) {
			this.registeredUsers.remove(unregCmd.getLogin());
		}
		if (this.usersLoginData.containsKey(unregCmd.getLogin())) {
			this.usersLoginData.remove(unregCmd.getLogin());
		}
		if (this.loginOnChannel.containsKey(unregCmd.getLogin())) {
			this.loginOnChannel.remove(unregCmd.getLogin());
		}
	}
	
	public Object loginUser(LoginCommand logCmd) {
		if (this.usersLoginData.containsKey(logCmd.getLogin())) {
			if (this.usersLoginData.get(logCmd.getLogin()).equals(logCmd.getHaslo())) {
				this.listLoggedUsers.add(logCmd.getLogin());
				return new String("SUCCESS");
			}
		}
		return new String("FAIL");
	}
	
  public static void main(String[] args) {
	  System.out.println("[MAIN: RUN SERVER]");
	  Runnable runServer = new Runnable() {
	      public void run() {
	        try {
				new Server(InetAddress.getLocalHost(), 11111).runServer();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}        
	      }
	    };
	    threadServer = new Thread(runServer);
	    threadServer.start();   
  }
}
