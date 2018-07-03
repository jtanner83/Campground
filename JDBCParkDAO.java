package com.techelevator;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;




public class JDBCParkDAO implements ParkDAO{
	
private JdbcTemplate jdbcTemplate;
	
	public JDBCParkDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	@Override
	public void save(Park newPark) {
		String sqlInsertPark = "INSERT INTO park(park_id, name, location, establish_date, area, visitors, description) " +
				   "VALUES(?, ?, ?, ?, ?, ?, ?)";
					newPark.setPark_id(getNextParkId());
					jdbcTemplate.update(sqlInsertPark, newPark.getPark_id(),	
								  newPark.getName(), 
								  newPark.getLocation(), 
								  newPark.getEstablish_date(),
								  newPark.getArea(),
								  newPark.getVisitors(),
								  newPark.getDescription());
	}
	
	@Override
	public Park findParkByParkId (long park_id){
		Park thePark = null;
		String sqlFindParkById = "SELECT * "+
							   "FROM park "+
							   "WHERE id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindParkById, park_id);
		if(results.next()) {
			thePark = mapRowToPark(results);
		}
		return thePark;
	}
	@Override
	public ArrayList<Park> findAllParks(){
		ArrayList<Park> parks = new ArrayList<>();
		String sqlFindAllParks = "SELECT * " +
							    "FROM park " +
							    "ORDER BY name;";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindAllParks);
		while(results.next()) {
			Park thePark = mapRowToPark(results);    
			parks.add(thePark);
		}
		return parks;
	}
	@Override
	public Park findParkByName(String name){
		Park thePark = new Park();
		String sqlFindParkByName = "SELECT * "+
							   "FROM park "+
							   "WHERE name = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindParkByName, name);
		while(results.next()) {
			thePark = mapRowToPark(results);   
		}
			return thePark;
	}
	
	@Override
	public ArrayList<Park> findParkByLocation(String location){
		ArrayList<Park> parks = new ArrayList<>();
		String sqlFindParkByLocation = "SELECT * "+
							"FROM park "+
							   "WHERE location = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindParkByLocation, location);
		while(results.next()) {
			Park thePark = mapRowToPark(results);    
			parks.add(thePark);
		}
		return parks;
	}
	

	
	private long getNextParkId() {
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('seq_park_id')");
		if(nextIdResult.next()) {
			return nextIdResult.getLong(1);
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the new park");
		}
	}
	
	
	private Park mapRowToPark(SqlRowSet results) {
		Park thePark;
		thePark = new Park();
		thePark.setPark_id(results.getLong("park_id"));
		thePark.setName(results.getString("name"));
		thePark.setLocation(results.getString("location"));
		thePark.setEstablish_date(results.getDate("establish_date"));
		thePark.setArea(results.getInt("area"));
		thePark.setVisitors(results.getInt("visitors"));
		thePark.setDescription(results.getString("description"));
		return thePark;
	}
}
