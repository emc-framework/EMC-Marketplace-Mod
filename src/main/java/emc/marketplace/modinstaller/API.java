package emc.marketplace.modinstaller;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;

/**
 * Handles communication between this plugin and the backend
 * 
 * @author Deftware
 *
 */
public class API {

	public static final String endpoint = "https://emc.aristois.net:2053/";
	public static String sessionID = "";

	@Getter
	public static Mod[] mods = null;

	public static boolean updateSession(String oauthToken) throws Exception {
		sessionID = oauthToken;
		JsonObject json = new Gson().fromJson(Web.get(endpoint + Types.GetSessionID.url, new String[]{ "token: " + sessionID }), JsonObject.class);
		if (json.get("status").getAsBoolean()) {
			sessionID = json.get("sessionID").getAsString();
			return true;
		}
		sessionID = "";
		return false;
	}

	public static boolean validSession() throws Exception {
		if (!sessionID.equals("")) {
			JsonObject json = new Gson().fromJson(fetchEndpoint(Types.ValidSession, null), JsonObject.class);
			return json.get("status").getAsBoolean();
		}
		return false;
	}

	/**
	 * All endpoints
	 *
	 */
	public enum Types {

		/**
		 * Returns a list of all mods
		 */
		ListMods("listmods"),

		/**
		 * Validates the current session
		 */
		ValidSession("validsession"),

		/**
		 * Get's a new session id
		 */
		GetSessionID("getsessionid"),

		/**
		 * Downloads a mod as a base64 encoded binary string
		 */
		Download("download");

		String url;

		Types(String url) {
			this.url = url;
		}

	}

	/**
	 * Fetches a given endpoint and returns the data as a string
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static String fetchEndpoint(Types type, Object[] args) throws Exception {
		return Web.get(args != null ? String.format(endpoint + type.url, args) : endpoint + type.url, new String[]{ "sessionid: " + sessionID });
	}

	/**
	 * Returns an array of all mods
	 * 
	 * @return
	 */
	public static Mod[] fetchMods() {
		try {
			JsonObject json = new Gson().fromJson(fetchEndpoint(Types.ListMods, null), JsonObject.class);
			Mod[] mods = new Gson().fromJson(json.get("mods"), Mod[].class);
			for (Mod mod : mods) {
				mod.init();
			}
			return mods;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new Mod[0];
	}

}
