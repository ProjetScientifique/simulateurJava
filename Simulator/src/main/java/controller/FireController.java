package controller;

import java.sql.Timestamp;

import org.apache.commons.math3.util.Precision;

import model.Coord;
import model.Emergency;
import model.EmergencyFactory;
import model.FireFactory;

public class FireController extends EmergencyController {

	public FireController() {
		super();
	}
	
	@Override
	public Emergency generateEmergency() {
		double longitude = Precision.round(ControllerConfig.LONGITUDE_MIN + (Math.random() * (ControllerConfig.LONGITUDE_MAX - ControllerConfig.LONGITUDE_MIN)), 7);
		double latitude = Precision.round(ControllerConfig.LATITUDE_MIN + (Math.random() * (ControllerConfig.LATITUDE_MAX - ControllerConfig.LATITUDE_MIN)), 7);
		Coord coordEmergency = new Coord(longitude, latitude);
		return EmergencyFactory.getEmergency(new FireFactory(Precision.round(Math.random() * 10, 2), new Timestamp(System.currentTimeMillis()), coordEmergency));
	}

}
