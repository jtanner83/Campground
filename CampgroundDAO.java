package com.techelevator;

import java.util.ArrayList;

public interface CampgroundDAO {

	public void save(Campground newCampground);
	
	public Campground findCampgroundByCampgroundId (long campground_id);
	
	public ArrayList<Campground> findCampgrounds(String parkName);
	
	public ArrayList <Campground> findCampgroundByParkId(long park_id);
	
	public ArrayList<Campground> findCampgroundByName(String name);
	
	
}
