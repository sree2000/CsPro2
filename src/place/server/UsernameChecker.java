package place.server;

import java.util.ArrayList;

public class UsernameChecker {

	static ArrayList names = new ArrayList<>();
	public static boolean sameName(String username) {
		if(names.contains(username)) {
			return true;
		}
		names.add(username);
		return false;

	}

}
