/**
 *
 *  @author Borowiec Mariia S13715
 *
 */

package zad1;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Client extends ClientCommunicationThread {
	
	public Client(InetAddress inetAddres, int port) {
		super(inetAddres, port);
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
