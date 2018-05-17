package zad1;

public class User {
	private String imie;
	private String nazwisko;

	public User(String imie, String nazwisko) {
		this.imie = imie;
		this.nazwisko = nazwisko; 
	}

	public String getImie() {
		return imie;
	}

	public String getNazwisko() {
		return nazwisko;
	}
	
	public String toString() {
		return getImie() + " " + getNazwisko();
	}
}
