package rest;

import java.io.IOException;

import org.json.JSONArray;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EmergencyApi {	
	public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
	public static final int idTypeEmergency = 1; // Fire
	public static final int idTypeStatusEmergency = 1; // Not treated year
	public static final int idTypeEmergencyFake = 100; // Potential Fires
	private String token;
	private OkHttpClient client;
	
	public EmergencyApi(String token) {
		super();
		this.token = token;
		this.client = new OkHttpClient();
	}

	public JSONArray getApi(String url) throws IOException {
		Request request = new Request.Builder()
				.url("http://127.0.0.1:8001/" + url+"/?token_api="+token)
				.build();
		
		try (Response response = client.newCall(request).execute()) {
			String jsonData = response.body().string();
			JSONArray jsonArr;
			try {
				jsonArr = new JSONArray(jsonData);
			} 
			catch(Exception e) {
				jsonArr = new JSONArray("["+jsonData+"]");
			}
			return jsonArr;
		}
	}
	
	public String postApi(String url, String json) throws IOException {
		RequestBody body = RequestBody.create(json, JSON);
		Request request = new Request.Builder()
				.url("http://127.0.0.1:8001/" + url + "/?token_api="+token)
				.post(body)
				.build();
		  
		  try (Response response = client.newCall(request).execute()) {
		    return response.body().string();
		  }
	}
	
	public String deleteApi(String url, String json) throws IOException {
		RequestBody body = RequestBody.create(json, JSON);
		Request request = new Request.Builder()
				.url("http://127.0.0.1:8001/" + url + "/?token_api="+token)
				.delete(body)
				.build();
		  
		  try (Response response = client.newCall(request).execute()) {
		    return response.body().string();
		  }
	}
}
