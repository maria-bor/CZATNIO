package zad1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import zad1.ICommand.IResponse;

public class ClientCommunicationThread extends BaseCommunication {
	
	private Thread thread;
	private GUI gui;
	private boolean isRunning = false;
    
    public ClientCommunicationThread(InetAddress inetAddres, int port) {
    	super();
    	System.out.println("ClientCommunicationThread()");
    	InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddres, port);
		try {
			this.scMain = SocketChannel.open();
			this.scMain.configureBlocking(false);
			connectToServer(inetSocketAddress);
			System.out.println("CCT:isConnected:"+this.scMain.isConnected()+"isOpened:"+this.scMain.isOpen()+"isRegistered:"+this.scMain.isRegistered());
			this.scMain.register(this.selector, SelectionKey.OP_READ);
			System.out.println("CCT:isConnected:"+this.scMain.isConnected()+"isOpened:"+this.scMain.isOpen()+"isRegistered:"+this.scMain.isRegistered());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	Runnable runnable = new Runnable() {
        	public void run() {
        		while(true) {
        			// TODO close thread on close
        			// kolejka command do wysłania 
        			try {
        				System.out.println("CCT.selector.select()");
        				int nrKeys = selector.select();
        				System.out.println("THREAD:nrKeys:"+nrKeys);
        			} catch (IOException e) {
        				e.printStackTrace();
        			}
        			
        			Iterator selectedKeys = selector.selectedKeys().iterator();
        			while(selectedKeys.hasNext()) {
        				System.out.println("THREAD.selectedKeys.hasNext()");
        				SelectionKey key = (SelectionKey) selectedKeys.next();
        				selectedKeys.remove();
        				
        				if (!key.isValid()) {
        					System.out.println("THREAD.isValid()");
        					continue;
        				}
        				
        				if (key.isReadable()) {
        					System.out.println("THREAD.read()");
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
//        this.thread.start();
	}
    
    public void startThread() {
    	if(!isRunning) {
    		this.thread.start();
    		this.isRunning = true;
    	}
    	else {
    		System.out.println("CCT.isRunning");
    	}
    }

    public void setGUI(GUI gui) {
    	this.gui = gui;
    }
    
	private void connectToServer(InetSocketAddress inetSocketAddress) {
		System.out.println("connectToServer()");
		boolean isConnected = false;
		try {
			isConnected = this.scMain.connect(inetSocketAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while(!isConnected || !this.scMain.isConnected()) {
			try {
				System.out.println("[CLIENT: STILL CONNECTING...]");
				isConnected = this.scMain.finishConnect();
			} catch (IOException e) {
				e.printStackTrace();
				try {
					Thread.sleep(1000);
					
				} catch (InterruptedException e1) { e1.printStackTrace(); }
			}
			System.out.println("connectToServer():while");
			
		}
		System.out.println("[CLIENT: CONNECTED]");
	}
	
	public void read(SelectionKey key) {
		super.read(key);
		System.out.println("CCT.read po BC.read()");
		SocketChannel scLocal = (SocketChannel) key.channel();
		if (dataPackage != null && dataPackage instanceof LoggedUsersMapCommand) {
			gui.updateComboBox((LoggedUsersMapCommand)dataPackage);
		}
		else if (dataPackage != null && dataPackage instanceof MessageCommand) {
			MessageCommand cmd = (MessageCommand)dataPackage;
			gui.showReceivedText(cmd);
			try {
				send(scLocal, (MessageCommand.Response)cmd.handle(new String("SUCCESS")));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (dataPackage != null && dataPackage instanceof MessageCommand.Response) {
			gui.showSendedText((MessageCommand.Response)dataPackage);
		}
		else {
			System.out.println("CCT.read.cos nie tak"+ (dataPackage==null));
		}
	}
}
