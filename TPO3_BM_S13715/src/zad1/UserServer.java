package zad1;

public class UserServer extends User {
	private String haslo;
	private boolean isLogged;
	
	public UserServer(String imie, String nazwisko, String haslo) {
		super(imie, nazwisko);
		this.haslo = haslo;
	}

	public boolean isLogged() {
		return isLogged;
	}

	public void setLogged(boolean isLogged) {
		this.isLogged = isLogged;
	}

	public String getHaslo() {
		return haslo;
	}
	
	public String toString() {
		return getImie() + " " + getNazwisko();
	}
}
