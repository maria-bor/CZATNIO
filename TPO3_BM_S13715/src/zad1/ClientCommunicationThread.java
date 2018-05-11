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
        					read(key);
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
	
	public void read(SelectionKey key) {
		super.read(key);
		SocketChannel scLocal = (SocketChannel) key.channel();
		if (dataPackage != null) {
			if (dataPackage instanceof ReUngisterCommand.Response) {
				ReUngisterCommand.Response r = (ReUngisterCommand.Response)dataPackage;
				if (r.isRegistering()) {
					gui.userRegistered(r);
				}
				else {
					gui.userUnregistered(r);
				}
			}
			else if (dataPackage instanceof LogInOutCommand.Response) {
				LogInOutCommand.Response r = (LogInOutCommand.Response)dataPackage;
				if (r.isLogging()) {
					gui.userLogged(r);
				}			
			}
			else if (dataPackage instanceof NewLoggedUsersMapCommand) {
				gui.updateComboBox((NewLoggedUsersMapCommand)dataPackage);
			}
			else if (dataPackage instanceof MessageCommand) {
				MessageCommand cmd = (MessageCommand)dataPackage;
				gui.showReceivedText(cmd);
				try {
					send(scLocal, (MessageCommand.Response)cmd.handle(new String("SUCCESS")));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (dataPackage instanceof MessageCommand.Response) {
				gui.showSendedText((MessageCommand.Response)dataPackage);
			}
		}
	}
}
