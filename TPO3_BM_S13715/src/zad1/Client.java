/**
 *
 *  @author Borowiec Mariia S13715
 *
 */

package zad1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Client extends ClientCommunicationThread {
	private Object cmd = null;
	private Object result = null;
	
	public Client(InetAddress inetAddres, int port) {
		super(inetAddres, port);
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

			this.scMain.write(sizeBuffer);
			this.scMain.write(bb);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void runGUI() {
		GUI.runGUI(this);
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
