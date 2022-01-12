package rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import model.Coord;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Map {
	private String token;
	private String url;
	private OkHttpClient client;

	public Map(String token) {
		super();
		this.token = token;
        this.url = "http://www.mapquestapi.com/directions/v2/route?key=" + this.token;
		this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
	}
	
	public ArrayList<Coord> getCoords(Coord start, Coord end) throws IOException {
		Request request = new Request.Builder()
	            .url(this.url + "&from=" + start.toApi() + "&to=" + end.toApi())
	            .build();
		
		ArrayList<Coord> coords = new ArrayList<Coord>();

		 try (Response response = client.newCall(request).execute()) {
			 JSONArray jsonArr = new JSONObject(response.body().string()).getJSONObject("route").getJSONArray("legs").getJSONObject(0).getJSONArray("maneuvers");
			 for(Object o: jsonArr) {
				 JSONObject json = new JSONObject(o.toString()).getJSONObject("startPoint");
				 coords.add(new Coord(json.getDouble("lat"), json.getDouble("lng")));
			 }
		 }
	return coords;
	}
}
