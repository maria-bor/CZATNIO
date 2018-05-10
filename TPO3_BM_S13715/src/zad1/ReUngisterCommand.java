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
		return new Response(args[0].toString());
	}

	public class Response implements IResponse {
		private String result;
		
		public Response(String result) {
			this.result = result;
		}

		public String getResult() {
			return result;
		}
	}
}

