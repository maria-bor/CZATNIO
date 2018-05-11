package zad1;

public class ReUngisterCommand implements ICommand{
	private String login;
	private String haslo;
	private User user;
	
	public ReUngisterCommand(String login, String haslo, User user) {
		this.login = login;
		this.haslo = haslo;
		this.user = user;
	}
	
	public ReUngisterCommand(String login, String haslo) {
		this.login = login;
		this.haslo = haslo;
		this.user = null;
	}
	
	public String getLogin() {
		return this.login;
	}

	public String getHaslo() {
		return this.haslo;
	}

	public User getUser() {
		return this.user;
	}

	@Override
	public Object handle(Object... args) throws Exception {
		if (getUser() != null) {
			return new Response(args[0].toString(), true);
		}
		else {
			return new Response(args[0].toString(), false);
		}
	}

	public class Response implements IResponse {
		private String result;
		private boolean isRegistering;
		
		public Response(String result, boolean isRegistering) {
			this.result = result;
			this.isRegistering = isRegistering;
		}

		public boolean isRegistering() {
			return isRegistering;
		}

		public String getResult() {
			return result;
		}
	}
}

