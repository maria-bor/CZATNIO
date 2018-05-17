/**
 *
 *  @author Borowiec Mariia S13715
 *
 */

package zad1;

public class Main {

  public static void main(String[] args) {
	  Server.main(args);
	  Client.main(args);
	  Client.main(args);
	  Client.main(args);

	  try {
		Server.getThreadServer().join();
	  } catch (InterruptedException e) {
			e.printStackTrace();
	  }   
  }
}