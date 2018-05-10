/**
 *
 *  @author Borowiec Mariia S13715
 *
 */

package zad1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import zad1.ICommand.IResponse;
import zad1.MessageCommand.Response;

public class Server extends BaseCommunication {

	private int port;
	private ServerSocketChannel serverSocketChannel;
	private volatile boolean serverRunning = true;
	
	private Map<String, SocketChannel> loginOnChannel;
	private static Thread threadServer;
	private Map<String, String> usersLoginData; // <login, password>
	private Map<String, User> loggedUsers; // <login>
	private Map<String, User> registeredUsers; // <login, User(imie,nazwisko)>
	
	public Server(InetAddress inetAddres, int port) {
		super();
		this.port = port;

		InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddres, port);
		
		try {
			this.serverSocketChannel = ServerSocketChannel.open();
			this.serverSocketChannel.configureBlocking(false);
			
			serverSocketChannel.socket().bind(inetSocketAddress);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		this.usersLoginData = new HashMap<String, String>();	
		this.loginOnChannel = new HashMap<String, SocketChannel>();
		this.loggedUsers = new HashMap<String, User>();
		this.registeredUsers = new HashMap<String, User>();
	}
	
	@SuppressWarnings("rawtypes")
	public void runServer() {
		while(serverRunning) {
			try {
				System.out.println("S.selector.select()");
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
					System.out.println("key.isAcceptable");
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
					System.out.println("key.isAcceptable");
					read(key);
				}
			}
		}
	}
	
	public void read(SelectionKey key) {
		System.out.println("Server.read()");
		super.read(key);
		SocketChannel scLocal = (SocketChannel) key.channel();
		if (dataPackage != null && dataPackage instanceof ICommand) {
			Object result = null;
			if (dataPackage instanceof ReUngisterCommand) {
				try {
					result = registerUser((ReUngisterCommand) dataPackage);
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
			else if (dataPackage instanceof LoginCommand) {
				result = loginUser((LoginCommand) dataPackage);
			}
			else if (dataPackage instanceof UnregisterCommand) {
				unregisterUser((UnregisterCommand)dataPackage);
			}
			else if (dataPackage instanceof MessageCommand) {
				result = forwardMessage((MessageCommand)dataPackage);
			}
			
			if (result != null)
				send(scLocal, result);
			
			if (dataPackage instanceof LoginCommand) {
				if (((String)result).equals("SUCCESS")) {
					broadcastLoggedUsers((LoginCommand)dataPackage, key, true);
				}
				else if (result == null && !((LoginCommand)dataPackage).isLogging()) {
					broadcastLoggedUsers((LoginCommand)dataPackage, key, false);
				}
			}
		}
		else if(dataPackage != null && dataPackage instanceof IResponse) {
			if (dataPackage instanceof MessageCommand.Response) {
				forwardMessageResponse((MessageCommand.Response)dataPackage);
			}
		}
		else {
			System.out.println("[Server.READ() FAILED]");
		}
	}
	
	private void forwardMessageResponse(Response response) {
		System.out.println("forwardMessageResponse()");
		if (this.loginOnChannel.containsKey(response.getRecipient())) {
			send(this.loginOnChannel.get(response.getRecipient()), response);
		}
		else {
			System.out.println("forwardMessageResponse() FAIL");
		}
	}

	private MessageCommand.Response forwardMessage(MessageCommand cmd) {
		System.out.println("cmd.getRecipient()" + cmd.getRecipient());
		if (this.loginOnChannel.containsKey(cmd.getRecipient())) {
			send(this.loginOnChannel.get(cmd.getRecipient()), cmd);
			return null;
		}
		else {
			System.out.println("forwardMessage() FAIL");
			return cmd.new Response(cmd.getSender(), cmd.getRecipient(), new String("FAIL"));
		}
	}
	
	private void broadcastLoggedUsers(LoginCommand logCmd, SelectionKey key, boolean isUserLogged) {
		if (isUserLogged) {
			for(Map.Entry<String, SocketChannel> entry : loginOnChannel.entrySet()) {
				LoggedUsersMapCommand lumc = new LoggedUsersMapCommand(logCmd.getLogin(), registeredUsers.get(logCmd.getLogin()), isUserLogged);
				send(entry.getValue(), lumc);
			}	
			send((SocketChannel) key.channel(), new LoggedUsersMapCommand(this.loggedUsers, isUserLogged));
			loginOnChannel.put(logCmd.getLogin(), (SocketChannel) key.channel());
			this.loggedUsers.put(logCmd.getLogin(), registeredUsers.get(logCmd.getLogin()));
		}
		else {
			for(Map.Entry<String, SocketChannel> entry : loginOnChannel.entrySet()) {
				LoggedUsersMapCommand lumc = new LoggedUsersMapCommand(logCmd.getLogin(), registeredUsers.get(logCmd.getLogin()), isUserLogged);
				send(entry.getValue(), lumc);
			}
		}
	}
	
	public static Thread getThreadServer() {
		return threadServer;
	}
	
	private Object registerUser(ReUngisterCommand regCmd) throws Exception {
		if (!this.usersLoginData.containsKey(regCmd.getLogin())) {
			usersLoginData.put(regCmd.getLogin(), regCmd.getHaslo());		
			registeredUsers.put(regCmd.getLogin(), regCmd.getUser());
			return regCmd.handle(new String("SUCCESS"));
		}
		else {
			return regCmd.handle(new String("FAIL"));
		}
	}
	
	private void unregisterUser(UnregisterCommand unregCmd) {
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
	
	private Object loginUser(LoginCommand logCmd) {
		if (logCmd.isLogging()) {
			if (this.usersLoginData.containsKey(logCmd.getLogin())) {
				if (this.usersLoginData.get(logCmd.getLogin()).equals(logCmd.getHaslo())) {
					return new String("SUCCESS");
				}
			}
			return new String("FAIL");
		}
		else  {
			if (this.loginOnChannel.containsKey(logCmd.getLogin())) {
				this.loginOnChannel.remove(logCmd.getLogin());
			} 
			if (this.loggedUsers.containsKey(logCmd.getLogin())) {
				this.loggedUsers.remove(logCmd.getLogin());
			}
			return null;
		}
	}
	
  public static void main(String[] args) {
	  InetAddress inetAddress = null;
	  int port;
	  if (args.length == 2) {
		  try {
			inetAddress = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		  port = Integer.parseInt(args[1]);
	  }
	  else {
		  try {
			inetAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		  port = 11111;
	  }
	  final InetAddress inetAddress2 = inetAddress;
	  System.out.println("[MAIN: RUN SERVER]");
	  Runnable runServer = new Runnable() {
	      public void run() {
	        new Server(inetAddress2, port).runServer();        
	      }
	    };
	    threadServer = new Thread(runServer);
	    threadServer.start();   
  }
}
