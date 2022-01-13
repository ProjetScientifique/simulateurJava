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
	public static final int idTypeStatusEmergencyNotTreated = 1;
	public static final int idTypeStatusEmergencyBeingTreated = 2;
	public static final int idTypeStatusEmergencyForDetected = 3;
	public static final int idTypeEmergencyPotential = 100; // Potential Fires
	public static final int idTypeDispoVehiculeAvailable = 1;
	public static final int idTypeDispoVehiculeNotAvailable = 2;
	public static final int idEmergencyFake = 1; // Id of Emergency used to regroup all detectors sent
	private String token;
	private OkHttpClient client;
	
	public EmergencyApi(String token) {
		super();
		this.token = token;
		this.client = new OkHttpClient();
	}

	public JSONArray getApi(String url) throws IOException {
		Request request = new Request.Builder()
				.url("http://emergency-api:8000/" + url+"/?token_api="+token)
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
				.url("http://emergency-api:8000/" + url + "/?token_api="+token)
				.post(body)
				.build();
		  
		  try (Response response = client.newCall(request).execute()) {
		    return response.body().string();
		  }
	}
	
	public String patchApi(String url, String json) throws IOException {
		RequestBody body = RequestBody.create(json, JSON);
		Request request = new Request.Builder()
				.url("http://emergency-api:8000/" + url + "/?token_api="+token)
				.patch(body)
				.build();
		  
		  try (Response response = client.newCall(request).execute()) {
		    return response.body().string();
		  }
	}
	
	public String deleteApi(String url) throws IOException {
		Request request = new Request.Builder()
				.url("http://emergency-api:8000/" + url + "/?token_api="+token)
				.delete()
				.build();
		  
		  try (Response response = client.newCall(request).execute()) {
		    return response.body().string();
		  }
	}
	
	public String deleteDetecte(int idEmergency, int idDetector) throws IOException {
		Request request = new Request.Builder()
				.url("http://emergency-api:8000/detecte/?id_incident=" + idEmergency + "&id_detecteur=" + idDetector + "&token_api="+token)
				.delete()
				.build();
		 
		  try (Response response = client.newCall(request).execute()) {
		    return response.body().string();
		  }
	}
}
