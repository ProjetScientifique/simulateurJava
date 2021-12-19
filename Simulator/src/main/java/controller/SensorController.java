package controller;

import model.Coord;
import model.DetectorFactory;
import model.SensorFactory;

public class SensorController extends DetectorController{
	public SensorController() {
		super();
		for (Coord coordForDetector : ControllerConfig.COORDS) {
			detectorArray.add(DetectorFactory.getDetector(new SensorFactory(0, null, coordForDetector, ControllerConfig.RANGE)));
		}
	}
}
