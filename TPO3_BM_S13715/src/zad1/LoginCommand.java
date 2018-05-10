package zad1;

public class LoginCommand implements ICommand {
	private String login;
	private String haslo;
	private boolean isLogging; // if log-in or log-out
	
	public LoginCommand(String login, String haslo, boolean isLogging) {
		this.login = login;
		this.haslo = haslo;
		this.isLogging = isLogging;
	}

	public String getLogin() {
		return login;
	}

	public String getHaslo() {
		return haslo;
	}

	public boolean isLogging() {
		return isLogging;
	}

	@Override
	public Object handle(Object... args) throws Exception {
		return null;
	}
}
