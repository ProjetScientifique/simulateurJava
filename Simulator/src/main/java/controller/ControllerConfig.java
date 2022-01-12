package controller;

import java.util.ArrayList;

import org.apache.commons.math3.util.Precision;

import model.Coord;

public class ControllerConfig {
	public static final int NUMB_TURN = 100;
	public static final double LATITUDE_MIN = 45.730603;
	public static final double LATITUDE_MAX = 45.787363;
	public static final double LONGITUDE_MIN = 4.793432;
	public static final double LONGITUDE_MAX = 4.894026;
	public static final int NUMB_SENSOR_X = 6;
	public static final int NUMB_SENSOR_Y = 10;
	public static final double STEP_X = Precision.round((LATITUDE_MAX-LATITUDE_MIN)/NUMB_SENSOR_X, 7);
	public static final double STEP_Y = Precision.round((LONGITUDE_MAX-LONGITUDE_MIN)/NUMB_SENSOR_Y, 7);
	public static final double RANGE = Math.max(STEP_X, STEP_Y);
	public static final ArrayList<Coord> COORDS = generateCoordForDetectors();
	
	public static ArrayList<Coord> generateCoordForDetectors() {
		ArrayList<Coord> coordForDetectors = new ArrayList<>();
		for (int i = 0; i < NUMB_SENSOR_Y; i++) {
			for (int j = 0; j < NUMB_SENSOR_X; j++) {
				coordForDetectors.add(new Coord(Precision.round(LATITUDE_MIN + STEP_X/2 + j * STEP_X, 7), Precision.round(LONGITUDE_MIN + STEP_Y/2 + i * STEP_Y, 7)));
			}
		}
		System.out.println(coordForDetectors);
		return coordForDetectors;
	}
}
