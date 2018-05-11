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
		SocketChannel scLocal = (SocketChannel) key.channel();
		if (dataPackage != null && dataPackage instanceof ICommand) {
			Object result = null;
			if (dataPackage instanceof ReUngisterCommand) {
				if (((ReUngisterCommand) dataPackage).getUser() != null) {
					try {
						result = registerUser((ReUngisterCommand) dataPackage);
					} catch (Exception e) {
						e.printStackTrace();
					}	
				} 
				else {
					try {
						result = unregisterUser((ReUngisterCommand)dataPackage);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			else if (dataPackage instanceof LogInOutCommand) {
				try {
					result = logInOutUser((LogInOutCommand) dataPackage, key);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (dataPackage instanceof MessageCommand) {
				result = forwardMessage((MessageCommand)dataPackage);
			}
			
			if (result != null)
				send(scLocal, result);
			
			if (dataPackage instanceof LogInOutCommand) {
				LogInOutCommand cmd = (LogInOutCommand)dataPackage;
				if(cmd.isLogging()) {
					if (((LogInOutCommand.Response)result).getResult().equals("SUCCESS")) {
						broadcastLoggedUsers(cmd, key, true);
					}					
				}
				else  {
					broadcastLoggedUsers(cmd, key, false);
				}
			}
		}
		else if(dataPackage != null && dataPackage instanceof IResponse) {
			if (dataPackage instanceof MessageCommand.Response) {
				forwardMessageResponse((MessageCommand.Response)dataPackage);
			}
		}
	}
	
	private void forwardMessageResponse(Response response) {
		if (this.loginOnChannel.containsKey(response.getRecipient())) {
			send(this.loginOnChannel.get(response.getRecipient()), response);
		}
	}

	private MessageCommand.Response forwardMessage(MessageCommand cmd) {
		if (this.loginOnChannel.containsKey(cmd.getRecipient())) {
			send(this.loginOnChannel.get(cmd.getRecipient()), cmd);
			return null;
		}
		else {
			return cmd.new Response(cmd.getSender(), cmd.getRecipient(), new String("FAIL"));
		}
	}
	
	private void broadcastLoggedUsers(LogInOutCommand logCmd, SelectionKey key, boolean isUserLogged) {
		if (isUserLogged) {
			for(Map.Entry<String, SocketChannel> entry : loginOnChannel.entrySet()) {
				NewLoggedUsersMapCommand lumc = new NewLoggedUsersMapCommand(logCmd.getLogin(), registeredUsers.get(logCmd.getLogin()), isUserLogged);
				send(entry.getValue(), lumc);
			}	
			send((SocketChannel) key.channel(), new NewLoggedUsersMapCommand(this.loggedUsers, isUserLogged));
			if (!this.loginOnChannel.containsKey(logCmd.getLogin())) {
				loginOnChannel.put(logCmd.getLogin(), (SocketChannel) key.channel());
				this.loggedUsers.put(logCmd.getLogin(), registeredUsers.get(logCmd.getLogin()));
			}
		}
		else {
			for(Map.Entry<String, SocketChannel> entry : loginOnChannel.entrySet()) {
				NewLoggedUsersMapCommand lumc = new NewLoggedUsersMapCommand(logCmd.getLogin(), registeredUsers.get(logCmd.getLogin()), isUserLogged);
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
	
	private Object unregisterUser(ReUngisterCommand unregCmd) throws Exception {
		if (!this.loggedUsers.containsKey(unregCmd.getLogin()) && this.usersLoginData.containsKey(unregCmd.getLogin()) && this.registeredUsers.containsKey(unregCmd.getLogin())) {
			if (this.usersLoginData.get(unregCmd.getLogin()).equals(unregCmd.getHaslo())) {
				this.registeredUsers.remove(unregCmd.getLogin());
				this.usersLoginData.remove(unregCmd.getLogin());
				return unregCmd.handle(new String("SUCCESS"));
			}
		}
		return unregCmd.handle(new String("FAIL"));
	}
	
	private Object logInOutUser(LogInOutCommand logCmd, SelectionKey key) throws Exception {
		if (logCmd.isLogging()) {
			if (this.usersLoginData.containsKey(logCmd.getLogin()) && !this.loggedUsers.containsKey(logCmd.getLogin())) {
				if (this.usersLoginData.get(logCmd.getLogin()).equals(logCmd.getHaslo())) {
					return logCmd.handle(new String("SUCCESS"), this.registeredUsers.get(logCmd.getLogin()));
				}
			}
			return logCmd.handle(new String("FAIL"), new User("", ""));
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
	  Runnable runServer = new Runnable() {
	      public void run() {
	        new Server(inetAddress2, port).runServer();        
	      }
	    };
	    threadServer = new Thread(runServer);
	    threadServer.start();   
  }
}
