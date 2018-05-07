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
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Client extends ClientCommunicationThread {
	private Object cmd = null;
	private Object result = null;
	
	public Client(InetAddress inetAddres, int port) {
		super(inetAddres, port);
		System.out.println("Client()");
	}
	
	public void sendOnly(ICommand cmd) {
		System.out.println("[CLIENT.sendOnly()]");
		System.out.println("isConnected:"+this.scMain.isConnected()+"isOpened:"+this.scMain.isOpen()+"isRegistered:"+this.scMain.isRegistered());
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

			this.scMain.write(sizeBuffer);
			this.scMain.write(bb);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public Object sendAndReceive(ICommand cmd) {
		System.out.println("CLIENT.sendAndReceive()");
		this.cmd = cmd;
		sendOnly(cmd);
		
		try{
			ByteBuffer responseSize = ByteBuffer.allocate(4);
			int numRead = 0;
			int totalRead = 0;
			while (totalRead < 4) {
				numRead = this.scMain.read(responseSize);
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
				numRead = this.scMain.read(responseBuffer);
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
	
	public void runGUI() {
		GUI.runGUI(this);
	}
	
	  public static void main(String[] args) {
		  System.out.println("[MAIN: RUN CLIENT]");
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
		  Runnable runClient = new Runnable() {
			
			@Override
			public void run() {
				new Client(inetAddress2, port).runGUI();
			}
		};
		Thread threadClient = new Thread(runClient);
		threadClient.start();
	  }
}
