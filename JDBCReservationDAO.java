package com.techelevator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCReservationDAO implements ReservationDAO{
	
private JdbcTemplate jdbcTemplate;
	
	public JDBCReservationDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public void save(Reservation newReservation){
		String sqlInsertReservation = "INSERT INTO reservation(reservation_id, site_id, name, from_date, to_date, create_date) " +
				   "VALUES(?, ?, ?, ?, ?, ?)";
		newReservation.setReservation_id(getNextReservationId());
		jdbcTemplate.update(sqlInsertReservation, newReservation.getReservation_id(),
															newReservation.getSite_id(),
															newReservation.getName(), 
															newReservation. getFrom_date(), 
															newReservation.getTo_date(), 
															newReservation.getCreate_date());	
	}
		
	@Override
	public Reservation createReservation(String reservationName, Long siteId, LocalDate userArrivalDate, LocalDate userDepartureDate) {
				Reservation newReservation = new Reservation();
		
				String sqlGetNextInt = "SELECT nextval('reservation_reservation_id_seq')";
				SqlRowSet result = jdbcTemplate.queryForRowSet(sqlGetNextInt);
				result.next();
				Long id = result.getLong(1);
		
				String sqlCreateReservation = "INSERT INTO reservation(reservation_id, site_id, name, from_date, to_date, create_date)" +
											  " VALUES (?, ?, ?, ?, ?, ? )";
		
		jdbcTemplate.update(sqlCreateReservation, id, siteId, reservationName, userArrivalDate, userDepartureDate, LocalDate.now());
		newReservation.setReservation_id(id);
		newReservation.setSite_id(siteId);
		newReservation.setName(reservationName);
		newReservation.setFrom_date(userArrivalDate);
		newReservation.setTo_date(userDepartureDate);
		newReservation.setCreate_date(LocalDate.now());
		return newReservation;
	}

	@Override
	public Reservation findReservationByReservationId (long reservation_id) {
		Reservation theReservation = null;
		String sqlFindReservationById = "SELECT reservation_id, site_id, name, from_date, to_date, create_date "+
							   "FROM reservation "+
							   "WHERE id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindReservationById, reservation_id);
		if(results.next()) {
			theReservation = mapRowToReservation(results);
		}
		return theReservation;
	}
	
	@Override
	public ArrayList<Reservation> findReservationBySiteId(long site_id){
		ArrayList<Reservation> reservations = new ArrayList<>();
		String sqlFindReservationBySiteId = "SELECT * "+
							   "FROM campground "+
							   "WHERE park_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindReservationBySiteId, site_id);
		while(results.next()) {
			Reservation theReservation = mapRowToReservation(results);    
			reservations.add(theReservation);
		}
		return reservations;
	}
	
	@Override
	public ArrayList<Reservation> findReservationByName(String name){
		ArrayList<Reservation> reservations = new ArrayList<>();
		String sqlFindReservationByName = "SELECT * "+
							   "FROM reservation "+
							   "WHERE name = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindReservationByName, name);
		while(results.next()) {
			Reservation theReservation = mapRowToReservation(results);    
			reservations.add(theReservation);
		}
		return reservations;
	}
	
	
	private long getNextReservationId() {
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('seq_reservation_id')");
		if(nextIdResult.next()) {
			return nextIdResult.getLong(1);
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the new reservation");
		}
	}
	
	
	private Reservation mapRowToReservation(SqlRowSet results) {
		Reservation theReservation;
		theReservation = new Reservation();
		theReservation.setReservation_id(results.getLong("reservation_id"));
		theReservation.setSite_id(results.getLong("site_id"));
		theReservation.setName(results.getString("name"));
		theReservation.setFrom_date(results.getDate("from_date").toLocalDate());
		theReservation.setTo_date(results.getDate("to_date").toLocalDate());
		theReservation.setCreate_date(results.getDate("create_date").toLocalDate());
		return theReservation;
	}

	

}
