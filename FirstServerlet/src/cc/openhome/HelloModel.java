package cc.openhome;

import java.util.HashMap;

public class HelloModel {

	private HashMap<String, String> messages = new HashMap<String, String>();

	public HelloModel() {
		messages.put("caterpillar", "Hello");
		messages.put("Justin", "Welcome");
		messages.put("momor", "Hi");
	}

	public String doHello(String user) {
		String message = messages.get(user);
		return String.format("%s ,%s !", message, user);
	}

	public Boolean checkUserIsExist(String userKey, String userValue) {
		if (messages.containsKey(userKey) && messages.values().contains(userValue)) {
			return true;
		} else
			return false;
	}

	public Boolean checkUserIsExist(String userKey) {
		if (messages.containsKey(userKey)) {
			return true;
		} else
			return false;
	}
}
