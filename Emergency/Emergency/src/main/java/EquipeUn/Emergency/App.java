package EquipeUn.Emergency;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import rest.EmergencyApi;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws JSONException, IOException
    {
		EmergencyApi emergencyApiClient = new EmergencyApi("CB814D37E278A63D3666B1A1604AD0F5C5FD7E177267F62B8D719F49182F410A");
		String res = emergencyApiClient.deleteApi("detecte");
		System.out.println(res);
    }
}
