package zad1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ClientCommunicationThread extends BaseCommunication {
	
	private Thread thread;
	private GUI gui;
    
    public ClientCommunicationThread(InetAddress inetAddres, int port) {
    	super();
    	InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddres, port);
		try {
			this.scMain = SocketChannel.open();
			this.scMain.configureBlocking(false);
			connectToServer(inetSocketAddress);
			this.scMain.register(this.selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	Runnable runnable = new Runnable() {
        	public void run() {
        		while(true) {
        			try {
        				int nrKeys = selector.select();
        			} catch (IOException e) {
        				e.printStackTrace();
        			}
        			
        			Iterator selectedKeys = selector.selectedKeys().iterator();
        			while(selectedKeys.hasNext()) {
        				SelectionKey key = (SelectionKey) selectedKeys.next();
        				selectedKeys.remove();
        				
        				if (!key.isValid()) {
        					continue;
        				}
        				
        				if (key.isReadable()) {
        					try {
								read(key);
							} catch (Exception e) {
								e.printStackTrace();
							}
        				}
        			}
        			try {
    					Thread.sleep(1000);
    					
    				} catch (InterruptedException e1) { e1.printStackTrace(); }
        		}
        	}
        };
        this.thread = new Thread(runnable);
        this.thread.start();
	}

    public void setGUI(GUI gui) {
    	this.gui = gui;
    }
    
	private void connectToServer(InetSocketAddress inetSocketAddress) {
		boolean isConnected = false;
		try {
			isConnected = this.scMain.connect(inetSocketAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while(!isConnected || !this.scMain.isConnected()) {
			try {
				isConnected = this.scMain.finishConnect();
			} catch (IOException e) {
				e.printStackTrace();
				try {
					Thread.sleep(1000);
					
				} catch (InterruptedException e1) { e1.printStackTrace(); }
			}			
		}
	}
	
	public void read(SelectionKey key) throws Exception {
		super.read(key);
		SocketChannel scLocal = (SocketChannel) key.channel();
		String str = reqString.toString();
		String[] arr = str.split("\\s+", 2);
		if (arr[0].equals("Register")) {
			gui.userRegistered(arr[1]);
		}
		else if (arr[0].equals("Unregister")) {
			gui.userUnregistered(arr[1]);
		}
		else if (arr[0].equals("Login")) {
			gui.userLogged(arr[1]);
		}
		else if (arr[0].equals("BroadcastLogin")) {
			gui.updateComboBox(arr[1], true);
		}
		else if (arr[0].equals("BroadcastLogout")) {
			gui.updateComboBox(arr[1], false);
		}
		else if (arr[0].equals("Message")) {
			gui.showReceivedText(arr[1]);
		}
		else if (arr[0].equals("MessageResponse")) {
			gui.showSendedText(arr[1]);
		}
	}
}
