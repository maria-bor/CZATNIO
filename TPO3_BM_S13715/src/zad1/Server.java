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
import java.util.Map;

public class Server {

	private int port;
	private InetAddress inetAddress;
	private ServerSocketChannel serverSocketChannel;
	private Selector selector;
	private volatile boolean serverRunning = true;
	private Map<SocketChannel, Integer> dataSizeToRead = new HashMap<SocketChannel, Integer>();
	private static Thread threadServer;
	
	public Server(InetAddress inetAddres, int port) {
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
	
	private void read(SelectionKey key) {
		SocketChannel sc = (SocketChannel) key.channel();		
		Integer s = dataSizeToRead.get(sc);
		int numbetOfBytes = 0;
		int len = 0;
		
		if (s == 0) {
			ByteBuffer bb = ByteBuffer.allocate(4);
			try {				
				numbetOfBytes = sc.read(bb);
				if (numbetOfBytes != 4) {
					System.out.println("#########");
				}
			} 
			catch (IOException e) {
			e.printStackTrace();
			key.cancel();
			}
			
			if (numbetOfBytes == -1) {
				try {
					key.channel().close();
					System.out.println("@@@@@@@@@@");
				} catch (IOException e) {
					e.printStackTrace();
				}
				key.cancel();
				return;
			}
			
			if (numbetOfBytes == 4) {
				// byte[] => int
				for (int i = 0; i < 4; ++i) {
					len |= (bb.array()[3-i] & 0xff) << (i << 3);
				}
				dataSizeToRead.put(sc, len);
			}
			else {
				dataSizeToRead.put(sc, null);
				len = 0;
			}
		}
		else {
			len = s;
		}
		ByteBuffer bb2 = ByteBuffer.allocate(len);
		try {
			numbetOfBytes = sc.read(bb2);
		} catch (IOException e) {
			e.printStackTrace();
			key.cancel();
			return;
		}
		
		if (numbetOfBytes == -1) {
			try {
				key.channel().close();
				System.out.println("$$$$$$$$$$");
			} catch (IOException e) {
				e.printStackTrace();
			}
			key.cancel();
			return;
		}
		
		if (numbetOfBytes == len) {
			dataSizeToRead.put(sc, 0);
			ByteArrayInputStream bais = new ByteArrayInputStream(bb2.array());
			try(ObjectInputStream ois = new ObjectInputStream(bais)) {
				ICommand cmd = (ICommand) ois.readObject();
				Object result = null;
				// check instance of Class ?? TO-DO
				if (result != null)
					send(sc, result);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}		
	}
	
	private void send(SocketChannel sc, Object result) {
		byte[] size = new byte[4];
		byte[] data;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try(ObjectOutputStream oos = new ObjectOutputStream(baos)){
			oos.writeObject(result);
			oos.flush();
			data = baos.toByteArray();
			
			int number = data.length;;
			// int -> byte[]
			for (int i = 0; i < 4; ++i) {
				int shift = i << 3; // i * 8
				size[3-i] = (byte)((number & (0xff << shift)) >>> shift);
			}
			
			ByteBuffer sizeBB = ByteBuffer.wrap(size);
			ByteBuffer dataBB = ByteBuffer.wrap(data);
			sc.write(sizeBB);
			sc.write(dataBB);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
 	}
	

	public static Thread getThreadServer() {
		return threadServer;
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
