package com.techelevator;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;



public class JDBCCampgroundDAO implements CampgroundDAO{
	
private JdbcTemplate jdbcTemplate;
	
	public JDBCCampgroundDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public void save(Campground newCampground){
		String sqlInsertCampground = "INSERT INTO campground(campground_id, park_id, name, open_from_mm, open_to_mm, daily_fee) " +
				   "VALUES(?, ?, ?, ?, ?, ?)";
		newCampground.setCampground_id(getNextCampgroundId());
		jdbcTemplate.update(sqlInsertCampground, newCampground.getCampground_id(),
															newCampground.getPark_id(), 
															newCampground.getName(), 
															newCampground. getOpen_from_mm(), 
															newCampground.getOpen_to_mm(), 
															newCampground.getDaily_fee());	
	}
	
	@Override
	public Campground findCampgroundByCampgroundId (long campground_id) {
		Campground theCampground = null;
		String sqlFindCampgroundById = "SELECT campground_id, park_id, name, open_from_mm, open_to_mm, daily_fee "+
							   "FROM campground "+
							   "WHERE id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindCampgroundById, campground_id);
		if(results.next()) {
			theCampground = mapRowToCampground(results);
		}
		return theCampground;
	}
	@Override
	public ArrayList<Campground> findCampgrounds(String parkName){
		ArrayList<Campground> campgrounds = new ArrayList<>();
		String sqlFindCampgrounds = "SELECT *" +
									" FROM campground"+
				                    " JOIN park"+
									" ON park.park_id =campground.park_id"+
									" WHERE park.name = ?" +
									" ORDER BY campground.name";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindCampgrounds, parkName);
		while(results.next()) {
			Campground theCampground = mapRowToCampground(results);    
			campgrounds.add(theCampground);
	}
		return campgrounds;
}
	
	@Override
	public ArrayList <Campground> findCampgroundByParkId(long park_id){
		ArrayList<Campground> campgrounds = new ArrayList<>();
		String sqlFindCampgroundByParkId = "SELECT * "+
						   "FROM campground "+
						   "WHERE park_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindCampgroundByParkId, park_id);
		while(results.next()) {
			Campground theCampground = mapRowToCampground(results);    
			campgrounds.add(theCampground);
	}
		return campgrounds;
}
	
	
	@Override
	public ArrayList<Campground> findCampgroundByName(String name){
		ArrayList<Campground> campgrounds = new ArrayList<>();
		String sqlFindCampgroundByName = "SELECT * "+
							   "FROM campground "+
							   "WHERE name = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindCampgroundByName, name);
		while(results.next()) {
			Campground theCampground = mapRowToCampground(results);    
			campgrounds.add(theCampground);
		}
		return campgrounds;
	}
	
	private long getNextCampgroundId() {
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('seq_campground_id')");
		if(nextIdResult.next()) {
			return nextIdResult.getLong(1);
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the new campground");
		}
	}
	
	
	private Campground mapRowToCampground(SqlRowSet results) {
		Campground theCampground;
		theCampground = new Campground();
		theCampground.setCampground_id(results.getLong("campground_id"));
		theCampground.setPark_id(results.getLong("park_id"));
		theCampground.setName(results.getString("name"));
		theCampground.setOpen_from_mm(results.getString("open_from_mm"));
		theCampground.setOpen_to_mm(results.getString("open_to_mm"));
		theCampground.setDaily_fee(results.getBigDecimal("daily_fee"));
		return theCampground;
	}


}
