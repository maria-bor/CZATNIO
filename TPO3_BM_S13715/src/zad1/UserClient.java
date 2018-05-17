package zad1;

public class UserClient extends User {
	private String login;

	public UserClient(String login, String imie, String nazwisko) {
		super(imie, nazwisko);
		this.login = login;
	}

	public String getLogin() {
		return login;
	}
}
