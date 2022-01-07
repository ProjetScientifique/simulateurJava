package controller;

import java.util.ArrayList;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import model.Coord;
import model.Detector;

public class EmergencyManagerController {
	private ArrayList<Detector> arrTriggeredDetectors;

	public EmergencyManagerController(ArrayList<Detector> arrTriggeredDetectors) {
		super();
		this.arrTriggeredDetectors = arrTriggeredDetectors;
	}
	
	private ArrayList<ArrayList<Detector>> getLinkedDetectors() {
		ArrayList<ArrayList<Detector>> arrLinkPerDetector = new ArrayList<ArrayList<Detector>>();
		for(Detector detector: arrTriggeredDetectors) {
			ArrayList<Detector> arrLinkedDetectors = new ArrayList<Detector>();
			arrLinkedDetectors.add(detector);
			arrTriggeredDetectors.remove(detector);
			for(Detector detectorBis: arrTriggeredDetectors) {
				if (detector.getCoord().getLongitude() == detectorBis.getCoord().getLongitude() || detector.getCoord().getLatitude() == detectorBis.getCoord().getLatitude()) {
					arrLinkedDetectors.add(detectorBis);
					arrTriggeredDetectors.remove(detectorBis);
				}
			}
			arrLinkPerDetector.add(arrLinkedDetectors);
		}
		return arrLinkPerDetector;
	}
	
	public void detectPotentialFire() {
		for(ArrayList<Detector> arrLinkedDetector: getLinkedDetectors()) {
			if (arrLinkedDetector.size() >= 3) {
				// DETECT FIRE
				double[][] positions = getDetectorsPosition(arrLinkedDetector);
	            double[] distances = getDetectorsDistance(arrLinkedDetector);
	            
	            NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
	            Optimum optimum = solver.solve();
	            
	            double[] centroid = optimum.getPoint().toArray();
	            
	            Coord fireCoordinates = new Coord(centroid[0], centroid[1]);
	            System.out.println("Potential fire : " + fireCoordinates);
			} else {
				System.out.println("Not enough data to triangulate position !");
			}
		}
	}
	
	private double[][] getDetectorsPosition(ArrayList<Detector> arrLinkedDetectors) {
        double[][] positions = new double[arrLinkedDetectors.size()][2];

        for(int i = 0; i<arrLinkedDetectors.size(); i++) {
            Detector detector = arrLinkedDetectors.get(i);
            positions[i][0] = detector.getCoord().getLatitude();
            positions[i][1] = detector.getCoord().getLongitude();
        }

        return positions;
    }

    private double[] getDetectorsDistance(ArrayList<Detector> arrLinkedDetectors) {
        double[] positions = new double[arrLinkedDetectors.size()];

        for(int i = 0; i<arrLinkedDetectors.size(); i++) {
            Detector detector = arrLinkedDetectors.get(i);
            positions[i] = detector.getRange() - (detector.getRange() * detector.getIntensity() / 100);
        }

        return positions;
    }
	
	
}
