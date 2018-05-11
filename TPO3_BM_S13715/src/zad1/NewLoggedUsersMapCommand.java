package zad1;

import java.util.HashMap;
import java.util.Map;

public class NewLoggedUsersMapCommand implements ICommand {

	private Map<String, User> listUser;
	private boolean isUserLogged; // if log-in or log-out
	
	public boolean isUserLogged() {
		return isUserLogged;
	}

	public NewLoggedUsersMapCommand(String login, User newUser, boolean isUserLogged) {
		this.listUser = new HashMap<>();
		this.listUser.put(login, newUser);
		this.isUserLogged = isUserLogged;
	}
	
	public NewLoggedUsersMapCommand(Map<String, User> listUser, boolean isUserLogged) {
		this.listUser = listUser;
		this.isUserLogged = isUserLogged;
	}
	
	@Override
	public Object handle(Object... args) throws Exception {
		return listUser;
	}
}
