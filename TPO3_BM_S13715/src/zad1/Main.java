/**
 *
 *  @author Borowiec Mariia S13715
 *
 */

package zad1;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

  public static void main(String[] args) {
	  Server.main(args);
	    
	    System.out.println("[MAIN: RUN CLIENT]");
	    Runnable runClient = new Runnable() {
	    	public void run() {
	    		try {
	    			new Client(InetAddress.getLocalHost(), 11111).runClient();
	    		} catch (UnknownHostException e) {
	    			e.printStackTrace();
	    		}        
	    	}
	    };
	    Runnable runClient2 = new Runnable() {
	    	public void run() {
	    		try {
	    			new Client(InetAddress.getLocalHost(), 11111).runClient();
	    		} catch (UnknownHostException e) {
	    			e.printStackTrace();
	    		}        
	    	}
	    };
	    
	    
	    Thread threadClient = new Thread(runClient);
	    Thread threadClient2 = new Thread(runClient2);
	    
	    
	    threadClient.start();
	    threadClient2.start();
	    try {
			Server.getThreadServer().join();
			threadClient.join();
			threadClient2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}   
	    System.out.println("[MAIN: END]");
  }
}
