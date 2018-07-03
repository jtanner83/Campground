package com.techelevator;

import java.time.LocalDate;
import java.util.ArrayList;

public interface ReservationDAO {
	
	public void save(Reservation newReservation);
	
	public Reservation createReservation(String reservationName, Long siteId, LocalDate userArrivalDate, LocalDate userDepartureDate);
	
	public Reservation findReservationByReservationId (long id);
	
	public ArrayList<Reservation> findReservationBySiteId(long site_id);
	
	public ArrayList<Reservation> findReservationByName(String name);
	
}
