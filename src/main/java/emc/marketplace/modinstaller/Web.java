package emc.marketplace.modinstaller;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Utils for http traffic
 * 
 * @author Deftware
 *
 */
public class Web {

	/**
	 * The user agent used for all the web traffic
	 */
	private static final String userAgent = "EMC-Marketplace";

	/**
	 * Sends a GET request to a given url
	 * 
	 * @param uri
	 * @return String
	 */
	public static String get(String uri, String[] headers) throws Exception {
		URL url = new URL(uri);
		Object connection = (uri.startsWith("https://") ? (HttpsURLConnection) url.openConnection()
				: (HttpURLConnection) url.openConnection());
		((URLConnection) connection).setConnectTimeout(8 * 1000);
		((URLConnection) connection).setRequestProperty("User-Agent", userAgent);
		if (headers != null) {
			for (String header : headers) {
				String key = header.split(": ")[0],
						value = header.split(": ")[1];
				((URLConnection) connection).setRequestProperty(key, value);
			}
		}
		((HttpURLConnection) connection).setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(((URLConnection) connection).getInputStream()));
		String text;
		String result = "";
		while ((text = in.readLine()) != null) {
			result = result + text;
		}
		in.close();
		return result;
	}

	/**
	 * Sends a POST request to a given url, with JSON data as payload
	 *
	 * @return String
	 */
	public static String post(String uri, HashMap<String, String> payload) throws Exception {
		URL url = new URL(uri);
		Object connection = (uri.startsWith("https://") ? (HttpsURLConnection) url.openConnection()
				: (HttpURLConnection) url.openConnection());
		((URLConnection) connection).setConnectTimeout(8 * 1000);
		((URLConnection) connection).setRequestProperty("User-Agent", userAgent);

		((URLConnection) connection).setDoInput(true);
		((URLConnection) connection).setDoOutput(true);
		((HttpURLConnection) connection).setRequestMethod("POST");
		((URLConnection) connection).setRequestProperty("Accept", "application/json");
		((URLConnection) connection).setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		OutputStreamWriter writer = new OutputStreamWriter(((URLConnection) connection).getOutputStream(), "UTF-8");
		writer.write(mapToJson(payload));
		writer.close();
		BufferedReader br = new BufferedReader(new InputStreamReader(((URLConnection) connection).getInputStream()));
		StringBuffer data = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) {
			data.append(line);
		}
		br.close();
		((HttpURLConnection) connection).disconnect();
		return data.toString();
	}

	public static void downloadMod(String uri, String fileName, String sessionid, String modid) throws Exception {
		URL url = new URL(uri);
		Object connection = (uri.startsWith("https://") ? (HttpsURLConnection) url.openConnection()
				: (HttpURLConnection) url.openConnection());
		((URLConnection) connection).setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
		((URLConnection) connection).setRequestProperty("sessionid", sessionid);
		((URLConnection) connection).setRequestProperty("modid", modid);
		((HttpURLConnection) connection).setRequestMethod("GET");
		FileOutputStream out = new FileOutputStream(fileName);
		InputStream in = ((URLConnection) connection).getInputStream();
		int read;
		byte[] buffer = new byte[4096];
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
		in.close();
		out.close();
	}

	/**
	 * Converts a HashMap to a JSON string
	 * 
	 * @param map
	 * @return String
	 */
	private static String mapToJson(HashMap<String, String> map) {
		String json = "{";
		for (String key : map.keySet()) {
			json += "\"" + key + "\":\"" + map.get(key) + "\",";
		}
		return json.substring(0, json.length() - 1) + "}";
	}

}
