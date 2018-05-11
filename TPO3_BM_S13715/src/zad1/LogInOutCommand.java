package zad1;

public class LogInOutCommand implements ICommand {
	private String login;
	private String haslo;
	private boolean isLogging; // if log-in or log-out
	
	public LogInOutCommand(String login, String haslo, boolean isLogging) {
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
		if (isLogging) {
			return new Response(args[0].toString(), (User) args[1], true);
		}
		else {
			return new Response(args[0].toString(), (User) args[1], false);
		}
	}

	public class Response implements IResponse {
		private String result;
		private boolean isLogging;
		private User user;
		
		public Response(String result, User user, boolean isLogging) {
			this.result = result;
			this.isLogging = isLogging;
			this.user = user;
		}

		public User getUser() {
			return user;
		}

		public boolean isLogging() {
			return isLogging;
		}

		public String getResult() {
			return result;
		}
	}
}
