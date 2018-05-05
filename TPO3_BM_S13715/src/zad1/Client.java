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
import java.nio.channels.SocketChannel;

public class Client {
	private InetSocketAddress inetSocketAddress;
	private SocketChannel socketChannel;
	private Object cmd = null;
	private Object result = null;
	
	public Client(InetAddress inetAddres, int port) {
		this.inetSocketAddress = new InetSocketAddress(inetAddres, port);
		try {
			this.socketChannel = SocketChannel.open();
			this.socketChannel.configureBlocking(true);
			connectToServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void connectToServer() {
		boolean isConnected = false;
		while(!isConnected) {
			try {
				this.socketChannel.connect(inetSocketAddress);
				isConnected = true;
				System.out.println("[CLIENT: CONNECTED]");
			} catch (IOException e) {
				e.printStackTrace();
				try {
					Thread.sleep(1000);
					System.out.println("[CLIENT: STILL CONNECTING...]");
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			
		}
	}
	
	public void sendOnly(ICommand cmd) {
		this.cmd = cmd;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try(ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(cmd);
			oos.flush();
			
			byte[] buf = baos.toByteArray();
			int sizeBuf = buf.length;
			byte[] data = new byte[4];
			
			// int -> byte[]
			for (int i = 0; i < 4; ++i) {
				int shift = i << 3; // i * 8
				data[3-i] = (byte)(( sizeBuf & (0xff << shift)) >>> shift);
			}

			ByteBuffer sizeBuffer = ByteBuffer.wrap(data);
			ByteBuffer bb = ByteBuffer.wrap(buf);
			this.socketChannel.write(sizeBuffer);
			this.socketChannel.write(bb);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public Object sendAndReceive(ICommand cmd) {
		this.cmd = cmd;
		sendOnly(cmd);
		
		try{
			ByteBuffer responseSize = ByteBuffer.allocate(4);
			int numRead = 0;
			int totalRead = 0;
			while (totalRead < 4) {
				numRead = this.socketChannel.read(responseSize);
				if(numRead != -1) {
					totalRead += numRead;
				}
			}
			if(numRead != 4) {
				System.out.println("CLIENT: &&&&&&&&&&&&");
			}
			byte[] dataSize = new byte[4];
			dataSize = responseSize.array();
			int len = 0;
			// byte[] -> int
			for (int i = 0; i < 4; ++i) {
				len |= (dataSize[3-i] & 0xff) << (i << 3);
			}
			
			ByteBuffer responseBuffer = ByteBuffer.allocate(len);
			totalRead = 0;
			while (totalRead < len) {
				numRead = this.socketChannel.read(responseBuffer);
				if (numRead != -1) {
					totalRead += numRead;
				}
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(responseBuffer.array());
			ObjectInputStream ois = new ObjectInputStream(bais);
			this.result = ois.readObject();
			ois.close();
			return result;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public void runClient() {
		GUI.runGUI(this);
	}
	
	  public static void main(String[] args) {
		  try {
			GUI.runGUI(new Client(InetAddress.getLocalHost(), 11111));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	  }
}
