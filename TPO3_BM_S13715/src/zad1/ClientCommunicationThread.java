package zad1;


public class ClientCommunicationThread extends BaseCommunication {
	
	private Thread thread;
    
    public ClientCommunicationThread() {
    	Runnable runnable = new Runnable() {
        	public void run() {
        		
        	}
        };
        this.thread = new Thread(runnable);
        this.thread.start();
	}

}
