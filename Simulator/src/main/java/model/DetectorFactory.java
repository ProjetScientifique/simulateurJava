package model;

public class DetectorFactory {
	public static Detector getDetector(DetectorAbstractFactory factory){
		return factory.createDetector();
	}
}
