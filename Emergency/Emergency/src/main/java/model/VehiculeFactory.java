package model;

public class VehiculeFactory {
	public static Vehicule getVehicule(VehiculeAbstractFactory factory){
		return factory.createVehicule();
	}
}
