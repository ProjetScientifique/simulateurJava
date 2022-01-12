package rest;

import java.io.IOException;

import org.json.JSONArray;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SimulatorApi {	
	public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
	private String token;
	private OkHttpClient client;
	
	public SimulatorApi(String token) {
		super();
		this.token = token;
		this.client = new OkHttpClient();
	}

	public JSONArray getApi(String url) throws IOException {
		Request request = new Request.Builder()
				.url("http://127.0.0.1:8000/" + url+"/?token_api="+token)
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
				.url("http://127.0.0.1:8000/" + url + "/?token_api="+token)
				.post(body)
				.build();
		  
		  try (Response response = client.newCall(request).execute()) {
		    return response.body().string();
		  }
	}
	
	public String deleteApi(String url) throws IOException {
		Request request = new Request.Builder()
				.url("http://127.0.0.1:8000/" + url + "/?token_api="+token)
				.delete()
				.build();
		  
		  try (Response response = client.newCall(request).execute()) {
		    return response.body().string();
		  }
	}
	
	public String deleteDetecte(int idEmergency, int idDetector) throws IOException {
		Request request = new Request.Builder()
				.url("http://127.0.0.1:8000/detecte/?id_incident=" + idEmergency + "&id_detecteur=" + idDetector + "&token_api="+token)
				.delete()
				.build();
		 
		  try (Response response = client.newCall(request).execute()) {
		    return response.body().string();
		  }
	}
}
