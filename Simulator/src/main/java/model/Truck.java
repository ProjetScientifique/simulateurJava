package model;

import java.io.IOException;
import java.util.ArrayList;

import rest.Map;

public class Truck extends Vehicule {
	private int year;
	private Coord coord;
	private int numbIntMax;
	private int id;
	private int idType;
	private int idEmergency;
	private boolean isDone;
	
	public Truck(int year, int numbIntMax, Coord coord, int id, int idType, int idEmergency) {
		super();
		this.year = year;
		this.coord = coord;
		this.numbIntMax = numbIntMax;
		this.id = id;
		this.idType = idType;
		this.idEmergency = idEmergency;
		this.isDone = false;
	}

	@Override
	public int getYear() {
		return year;
	}

	@Override
	public Coord getCoord() {
		return coord;
	}

	@Override
	public int getNumbIntMax() {
		return numbIntMax;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getIdType() {
		return idType;
	}
	
	@Override
	public int getIdEmergency() {
		return idEmergency;
	}
	
	@Override
	public boolean isDone() {
		return isDone;
	}
	
	@Override
	public Coord move(Coord newCoord, Map clientMap) throws IOException {
		ArrayList<Coord> arrCoords = clientMap.getCoords(coord, newCoord);
		Coord coordBeg = coord;
		if (arrCoords.get(arrCoords.size()-1).equal(coord)) {
			coord = newCoord;
			isDone = true;
		}else {
			if (arrCoords.size() > 1) {
				if (!arrCoords.get(0).equal(coord)) {
					coord = arrCoords.get(0);
				} else {
					coord = arrCoords.get(1);
				}
			} else {
				coord = newCoord;
				isDone = true;
			}
		}
		System.out.println("Vehicule with Id : " + id + " is going from " + coordBeg + " to " + newCoord + ", next step is : " + coord);
		return coord;
	}
}
