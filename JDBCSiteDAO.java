package com.techelevator;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCSiteDAO implements SiteDAO{
	
	
private JdbcTemplate jdbcTemplate;
	
	public JDBCSiteDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public void save(Site newSite) {
		String sqlInsertSite = "INSERT INTO site(site_id,campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities) " +
				   "VALUES(?, ?, ?, ?, ?, ?, ?)";
		newSite.setSite_id(getNextSiteId());
		jdbcTemplate.update(sqlInsertSite, newSite.getSite_id(),
										  newSite.getCampground_id(),
					    					  newSite.getSite_number(),
							 	 		  newSite.getMax_occupancy(), 
										  newSite.isAccessible(),
										  newSite.getMax_rv_length(),
										  newSite.isUtilities());
	}@Override
	public ArrayList<Site> getAllAvailableReservationsFromPark(int parkByNumberFromUser, Date parkByArrivalDate, Date parkByDepartureDate){
		ArrayList<Site> reservations = new ArrayList<>();
		String sqlGetAllAvailableReservationsFromPark = "SELECT site.*, campground.daily_fee " + 
				" FROM site " + 
				" JOIN campground " + 
				" ON site.campground_id = campground.campground_id " +
				" Join park " +
				" On campground.park_id = park.park_id " +
				" Where park.name = ? And site_id " + 
				" NOT IN (SELECT reservation.site_id FROM reservation WHERE  reservation.from_date >= ? and  reservation.to_date <= ?) " +   
				" ORDER BY site_id " +
				" Limit 5 ";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetAllAvailableReservationsFromPark,parkByNumberFromUser,parkByArrivalDate, parkByDepartureDate);
		while(results.next()) {
			Site theReservation = mapRowToSite(results);    
			reservations.add(theReservation);
	}
		return reservations;
}

	@Override
	public ArrayList<Site> getAllAvailableReservationsFromCampground(int campgroundByNumberFromUser, Date campgroundByArrivalDate, Date campgroundByDepartureDate){
		ArrayList<Site> reservations = new ArrayList<>();
		String sqlGetAllAvailableReservationsFromCampground = " SELECT site.*, campground.daily_fee" + 
				" FROM site" + 
				" JOIN campground" + 
				" ON site.campground_id=campground.campground_id" + 
				" Where campground.campground_id = ?" +
				" And site_id" + 
				" NOT IN (SELECT reservation.site_id FROM reservation WHERE reservation.from_date >= ? and reservation.to_date <= ?)" +   
				" ORDER BY site_id" +
				" Limit 5";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetAllAvailableReservationsFromCampground, campgroundByNumberFromUser, campgroundByArrivalDate, campgroundByDepartureDate);
		while(results.next()) {
			Site theReservation = mapRowToSite(results);    
			reservations.add(theReservation);
	}
		return reservations;
}
	
	@Override
	public Site findSiteBySiteId (long site_id) {
		Site theSite = null;
		String sqlFindSiteById = "SELECT reservation_id, site_id, name, from_date, to_date, create_date "+
							   "FROM reservation "+
							   "WHERE id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindSiteById, site_id);
		if(results.next()) {
			theSite = mapRowToSite(results);
		}
		return theSite;
	}
	
	@Override
	public List<Site> findSiteByCampgroundId(long campground_id){
		ArrayList<Site> sites = new ArrayList<>();
		String sqlFindSitesByCampgroundId = "SELECT * "+
							   "FROM site "+
							   "WHERE campground_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindSitesByCampgroundId, campground_id);
		while(results.next()) {
			Site theSite = mapRowToSite(results);    
			sites.add(theSite);
		}
		return sites;
	}
	
	@Override
	public List<Site> findSiteByAccessible(){
		ArrayList<Site> sites = new ArrayList<>();
		String sqlFindSiteByAccessible = "SELECT * "+
							   "FROM site "+
							   "WHERE accessible = true";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindSiteByAccessible);
		while(results.next()) {
			Site theSite = mapRowToSite(results);    
			sites.add(theSite);
		}
		return sites;
	}
	
	private long getNextSiteId() {
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('seq_site_id')");
		if(nextIdResult.next()) {
			return nextIdResult.getLong(1);
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the new site");
		}
	}
	
	private Site mapRowToSite(SqlRowSet results) {
		Site theSite;
		theSite = new Site();
		theSite.setSite_id(results.getLong("site_id"));
		theSite.setCampground_id(results.getLong("campground_id"));
		theSite.setSite_number(results.getInt("site_number"));
		theSite.setMax_occupancy(results.getInt("max_occupancy"));
		theSite.setAccessible(results.getBoolean("accessible"));
		theSite.setMax_rv_length(results.getInt("max_rv_length"));
		theSite.setUtilities(results.getBoolean("utilities"));
		return theSite;
	}

	
}
