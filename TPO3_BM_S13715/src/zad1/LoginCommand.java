package zad1;

public class LoginCommand implements ICommand {
	private String login;
	private String haslo;
	
	public LoginCommand(String login, String haslo) {
		this.login = login;
		this.haslo = haslo;
	}

	public String getLogin() {
		return login;
	}

	public String getHaslo() {
		return haslo;
	}

	@Override
	public Object handle(Object... args) throws Exception {
		return null;
	}
}
