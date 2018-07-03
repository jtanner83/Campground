package com.techelevator;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public interface SiteDAO {
	
	public void save(Site newSite);
	
	ArrayList<Site> getAllAvailableReservationsFromPark(int parkByNumberFromUser, Date parkByArrivalDate,Date parkByDepartureDate);
	
	ArrayList<Site> getAllAvailableReservationsFromCampground(int campgroundByNumberFromUser, Date campgroundArrivalDate, Date campgroundDepartureDate);
	
	public Site findSiteBySiteId (long id);
	
	public List<Site> findSiteByCampgroundId(long campground_id);
	
	public List<Site> findSiteByAccessible();

	

	
	
}
