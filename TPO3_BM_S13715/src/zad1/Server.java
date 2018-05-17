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

public class Server extends BaseCommunication {

	private int port;
	private ServerSocketChannel serverSocketChannel;
	private volatile boolean serverRunning = true;

	private Map<String, SocketChannel> loginOnChannel;
	private Map<String, UserServer> registeredUsers; // <login, User(imie,nazwisko)>
	private static Thread threadServer;

	public Server(InetAddress inetAddres, int port) {
		super();
		this.port = port;

		InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddres, port);

		try {
			this.serverSocketChannel = ServerSocketChannel.open();
			this.serverSocketChannel.configureBlocking(false); // nieblokujące

			serverSocketChannel.socket().bind(inetSocketAddress);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			e.printStackTrace();
		}	

		this.loginOnChannel = new HashMap<String, SocketChannel>();
		this.registeredUsers = new HashMap<String, UserServer>();
	}

	public void runServer() throws Exception {
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

	public void read(SelectionKey key) throws Exception { 
		super.read(key);
		SocketChannel scLocal = (SocketChannel) key.channel();
		String str = reqString.toString();
		String[] arr = str.split("\\s+", 2);
		String result = "";
		if (arr[0].equals("Register")) {
			result = "Register ";
			result += registerUser(arr[1]);
		}
		else if (arr[0].equals("Unregister")) {
			result = "Unregister ";
			result += unregisterUser(arr[1]);
		}
		else if (arr[0].equals("Login")) {
			result = "Login ";
			result += loginUser(arr[1]);
		}
		else if (arr[0].equals("Logout")) {
			logoutUser(arr[1]);
		}
		else if (arr[0].equals("Message")) {
			String forward = forwardMessage(arr[1]);
			if (!forward.isEmpty()) {
				result += "MessageResponse " + forward;
			}
		}
		else if (arr[0].equals("MessageResponse")) {
			forwardMessageResponse(arr[1]);
		}
		
		if (result.length() != 0) {
			send(scLocal, result);
		}

		if (result.startsWith("Login SUCCESS")) {
			broadcastLoggedUsers(arr[1], key, true);
		}
		if (arr[0].equals("Logout")) {
			broadcastLoggedUsers(arr[1], key, false);
		}
	}

	private void forwardMessageResponse(String s) throws IOException {
		String[] arr = s.split(" ", 2);
		if (this.loginOnChannel.containsKey(arr[0])) {
			send(this.loginOnChannel.get(arr[0]),"MessageResponse", arr[1]);
		}
	}

	private String forwardMessage(String s) throws IOException {
		String[] arr = s.split(" ", 2);
		if (this.loginOnChannel.containsKey(arr[0])) {
			send(this.loginOnChannel.get(arr[0]), "Message", arr[1]);
			return "";
		}
		else {
			String[] tab = arr[1].split(" ", 2);
			return tab[0] + " " + arr[0] + " FAIL";
		}
	}

	private void broadcastLoggedUsers(String log, SelectionKey key, boolean isUserLogged) throws IOException {
		String[] arr = log.split(" ");
		UserServer user = registeredUsers.get(arr[0]);
		if (isUserLogged) {
			for(Map.Entry<String, SocketChannel> entry : loginOnChannel.entrySet()) {
				send(entry.getValue(), "BroadcastLogin", arr[0], user.getImie(), user.getNazwisko());
			}	
			String str = "";			
			for (String e : loginOnChannel.keySet()) {			
				str += e + " " + registeredUsers.get(e).toString() + " ";
			}
			if (!str.isEmpty()) {
				send((SocketChannel) key.channel(), "BroadcastLogin", str);
			}
			if (!this.loginOnChannel.containsKey(arr[0])) {
				loginOnChannel.put(arr[0], (SocketChannel) key.channel());
				registeredUsers.get(arr[0]).setLogged(true);
			}
			else {
				System.out.println("Juz jest w mapie");
			}
		}
		else {
			for(Map.Entry<String, SocketChannel> entry : loginOnChannel.entrySet()) {
				send(entry.getValue(), "BroadcastLogout", arr[0], user.getImie(), user.getNazwisko());
			}
		}
	}

	public static Thread getThreadServer() {
		return threadServer;
	}

	private String registerUser(String args) throws Exception {
		String[] arr = args.split("\\s+");
		if (!this.registeredUsers.containsKey(arr[0])) {
			UserServer user = new UserServer(arr[1], arr[2], arr[3]);
			this.registeredUsers.put(arr[0], user);
			return "SUCCESS";
		}
		else {
			return "FAIL";
		}
	}

	private String unregisterUser(String args) throws Exception {
		String[] arr = args.split("\\s+");
		if (this.registeredUsers.containsKey(arr[0])) {
			UserServer user = this.registeredUsers.get(arr[0]);
			if (!user.isLogged() && user.getHaslo().equals(arr[1])) {
				this.registeredUsers.remove(arr[0]);
				return "SUCCESS";
			}
			else {
				return "FAIL";
			}
		}
		else {
			return "FAIL";
		}
	}

	private String loginUser(String args) throws Exception {
		String[] arr = args.split("\\s+");
		if (this.registeredUsers.containsKey(arr[0])) {
			UserServer user = this.registeredUsers.get(arr[0]);
			if (!user.isLogged() && user.getHaslo().equals(arr[1])) {
				return "SUCCESS " + arr[0] + " "+ user.getImie() + " " + user.getNazwisko();
			}
			else {
				return "FAIL";
			}
		}
		else {
			return "FAIL";
		}
	}

	private void logoutUser(String args) throws Exception {
		String[] arr = args.split("\\s+");
		if (this.loginOnChannel.containsKey(arr[0])) {
			this.loginOnChannel.remove(arr[0]);
			this.registeredUsers.get(arr[0]).setLogged(false);
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
				try {
					new Server(inetAddress2, port).runServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		threadServer = new Thread(runServer);
		threadServer.start();   
	}
}
