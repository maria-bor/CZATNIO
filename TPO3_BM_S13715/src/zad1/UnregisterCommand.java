package zad1;

public class UnregisterCommand implements ICommand {
	private String login;
	private String haslo;
	
	public UnregisterCommand(String login, String haslo) {
		this.login = login;
		this.haslo = haslo;

	}
	
	public String getLogin() {
		return this.login;
	}

	public String getHaslo() {
		return this.haslo;
	}

	@Override
	public Object handle(Object... args) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
