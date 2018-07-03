package com.techelevator;

import java.util.ArrayList;
import java.util.List;

public interface ParkDAO {
	public void save(Park newPark);
	
	public ArrayList<Park> findAllParks();
	
	public Park findParkByParkId (long id);
	
	public Park findParkByName(String name);
	
	public ArrayList<Park> findParkByLocation(String location);
	
	
	}

