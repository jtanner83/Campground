package com.techelevator;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import org.apache.commons.dbcp2.BasicDataSource;

public class CampgroundCLI { 
	
	private static final String MAIN_MENU_OPTION_VIEW_CAMPGROUNDS = "View Campgrounds";
	private static final String MAIN_MENU_OPTION_SEARCH_RESERVATIONS = "Search for Available Reservations";
	private static final String MAIN_MENU_OPTION_RETURN_FROM_CAMPGROUNDS = "Return to Main Menu";
	private static final String[] MAIN_MENU_OPTION_CAMPGROUNDS = new String[] { MAIN_MENU_OPTION_VIEW_CAMPGROUNDS,MAIN_MENU_OPTION_SEARCH_RESERVATIONS,MAIN_MENU_OPTION_RETURN_FROM_CAMPGROUNDS};
	
	private static final String MAIN_MENU_OPTION_CAMPGROUND_RESERVATIONS = "Which campground (enter 0 to cancel)?";
	private static final String MAIN_MENU_OPTION_ARRIVAL_DATES = "When would you like to arrive (YYYY/MM/DD)?";
	private static final String MAIN_MENU_OPTION_DEPARTURE_DATES = "When would you like to depart ((YYYY/MM/DD)) ?";
	private static final String[] MAIN_MENU_OPTION_RESERVATIONS = new String[] {MAIN_MENU_OPTION_CAMPGROUND_RESERVATIONS, MAIN_MENU_OPTION_ARRIVAL_DATES, MAIN_MENU_OPTION_DEPARTURE_DATES };
	
	private static final String MAIN_MENU_OPTION_MAKE_RESERVATIONS = "Make a Campsite Reservation";
	private static final String MAIN_MENU_OPTION_RETURN_FROM_CREATE_RESERVATIONS = "Return to Main Menu";
	private static final String[] MAIN_MENU_OPTION_CREATE_RESERVATIONS = new String[] {MAIN_MENU_OPTION_MAKE_RESERVATIONS, MAIN_MENU_OPTION_RETURN_FROM_CREATE_RESERVATIONS };

	Scanner input = new Scanner(System.in);
	Menu menu = new Menu(System.in, System.out);
	boolean innerLoop = true;
	boolean outerLoop = true;
	private ParkDAO parkDAO;
	private CampgroundDAO campgroundDAO;
	private SiteDAO siteDAO;
	private ReservationDAO reservationDAO;

	
	
	public static void main(String[] args) throws ParseException {
		
		CampgroundCLI application = new CampgroundCLI();
		application.run();
	}

	public CampgroundCLI() {
		
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
		dataSource.setUsername("jamestanner");
		dataSource.setPassword("Windi@ns216!");
		
		parkDAO = new JDBCParkDAO(dataSource);
		campgroundDAO = new JDBCCampgroundDAO(dataSource);
		siteDAO = new JDBCSiteDAO(dataSource);
		reservationDAO = new JDBCReservationDAO(dataSource);
	}
	
	public void run() throws ParseException {
		displayApplicationBanner();
		while (outerLoop) {
			ArrayList<Park> parks = parkDAO.findAllParks();
			String[] parkNames = new String[parks.size()];

			int i = 0;
			for (Park currPark : parks) {
				parkNames[i] = currPark.getName();
				++i;
			}
			String parkChoice = (String) menu.getChoiceFromOptions(parkNames);
			Park userChoice = parkDAO.findParkByName(parkChoice);
			parkInformation(userChoice);
			while (innerLoop) {
				String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTION_CAMPGROUNDS);
				if (choice.equals(MAIN_MENU_OPTION_VIEW_CAMPGROUNDS)) {
					ArrayList<Campground> campgrounds = campgroundDAO.findCampgrounds(parkChoice);
					String[] campgroundNames = new String[campgrounds.size()];

					int c = 0;
					for (Campground currCampground : campgrounds) {
						campgroundNames[c] = currCampground.getName();
						++c;
					}
					System.out.println();
					System.out.println("************ PARK        CAMPGROUNDS ************");
					System.out.println();
					System.out.println("\tName\t\tOpen\tClose\tDaily Fee");
					System.out.println("==================================================");
					for (int d = 0; d < campgroundNames.length; d++) {
						Campground campgroundChoice = campgrounds.get(d);
						campgroundList(campgroundChoice);
					}
					Scanner input = new Scanner(System.in);
					System.out.println("Which campground would you like a reservation for ?" + " Or Enter 0 To go back to parks?" );
					String campgroundByNameFromUser = input.nextLine();
					int campgroundByNumberFromUser = Integer.parseInt(campgroundByNameFromUser);
						if (campgroundByNumberFromUser == 0) {
							run();
						}
						else {
					System.out.println("When would you like to arrive (YYYY/MM/DD)?");
					String campgroundArrivalDate = input.nextLine();
					Date arrivalDate = new SimpleDateFormat("yyyy/MM/dd").parse(campgroundArrivalDate);
					java.sql.Date sqlArrivalDate = new java.sql.Date(arrivalDate.getTime());
 					System.out.println("When would you like to depart (YYYY/MM/DD) ?");
					String campgroundDepartureDate = input.nextLine();
					Date departureDate = new SimpleDateFormat("yyyy/MM/dd").parse(campgroundDepartureDate);
					java.sql.Date sqlDepartureDate = new java.sql.Date(departureDate.getTime());
							ArrayList<Site> reservations = siteDAO.getAllAvailableReservationsFromCampground(campgroundByNumberFromUser, sqlArrivalDate, sqlDepartureDate);
							String[] availableCampgroundReservations = new String[reservations.size()];
							
							int e = 0;
							for(Site currSite : reservations) {
								availableCampgroundReservations[e] = (Integer.toString(currSite.getSite_number()));
								++e;
							}
							System.out.println("********Results   Matching   Your   Search Criteria");
							System.out.println("Site No.\t"+ "Max Occup\t"+ "Accesible\t"+ "Max RV Length\t"+ "Utility\t" + "Cost");
							for (int f = 0; f < availableCampgroundReservations.length; f++) {
								Site siteChoice = reservations.get(f);
								availableReservationList(siteChoice,campgrounds.get(campgroundByNumberFromUser -1), arrivalDate, departureDate );
							}
						}	
							System.out.println("Create a Reservation");
							System.out.println();
							System.out.println("What Site Do You Want To Reserve (Enter 0 to Cancel)  :");
							String siteId = input.nextLine();
							if (siteId.equals("0")) {
								run();
							}
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
							Long longSiteId = Long.parseLong(siteId);
							System.out.println("What Name Do You Want the Reservation Under? ");
							String reservationName = input.nextLine();
							System.out.println("What was the arrival Date? (YYYY/MM/DD) ");
							String userArrivalDate = input.nextLine();
							System.out.println(" What was the departure Date? (YYYY/MM/DD) ");
							String userDepartureDate = input.nextLine();
							Reservation newReservation = reservationDAO.createReservation(reservationName, longSiteId, LocalDate.parse(userArrivalDate, formatter),LocalDate.parse(userDepartureDate, formatter));
							System.out.println("\n***" + newReservation.getName() + " created! **** ");
							System.out.println("*** Your Confirmation Number is: " + newReservation.getReservation_id());
				}
				 //else if (choice.equals(MAIN_MENU_OPTION_SEARCH_RESERVATIONS)) {
				//}Bonus!!!!!!!BOOOM!!!!!!
				 else if (choice.equals(MAIN_MENU_OPTION_RETURN_FROM_CAMPGROUNDS)) {
						run();
					}
			}
		}	
			 

			// else {
		} // }
	
	private void parkInformation(Park fromUser) {
	
			System.out.println();
			System.out.println("*******PARK        INFORMATION*******");
			System.out.println(fromUser.getName()+ " National Park");
			System.out.println("======================================");
			System.out.println("Location:    " + "\t\t" +fromUser.getLocation());
			System.out.println("Established:    " + "\t\t" +fromUser.getEstablish_date());
			System.out.println("Area:    " + "\t\t" +fromUser.getArea());
			System.out.println("Annual Visitors:    " +"\t" + fromUser.getVisitors());
			System.out.println();
			System.out.println();
			System.out.println(fromUser.getDescription());
		
	}


	private void campgroundList(Campground campgroundsByPark) {
		System.out.println(campgroundsByPark.getCampground_id() + "\t" +campgroundsByPark.getName() + "\t " + campgroundsByPark.getOpen_from_mm() + "\t " + campgroundsByPark.getOpen_to_mm() + "\t " + "$" +campgroundsByPark.getDaily_fee()+"0");
		System.out.println();
		System.out.println();
		
	}
		
	private static BigDecimal daysBetween(Date arrivalDate, Date departureDate ) {
		BigDecimal oneBigDecimal = BigDecimal.valueOf(1.00);
		
			int difference = (int) ((arrivalDate.getTime()-departureDate.getTime())/86400000);
			Math.abs(difference);
			BigDecimal difference1 = oneBigDecimal.multiply(new BigDecimal(difference));
		return difference1;
	}
	private void availableReservationList(Site reservationsByCampground, Campground campgrounds, Date arrivalDate, Date departureDate) {
		
		System.out.print(reservationsByCampground.getSite_id() + "\t\t " + reservationsByCampground.getMax_occupancy()+ "\t\t\t " + reservationsByCampground.isAccessible() + "\t\t\t " + reservationsByCampground.getMax_rv_length() + "\t\t\t" + reservationsByCampground.isUtilities() + "\t\t" + (campgrounds.getDaily_fee().multiply(daysBetween(departureDate, arrivalDate))));
		System.out.println();
		System.out.println();
	}
	
	
	
		private void displayApplicationBanner() {
				System.out.println("  __  __  ___  ______ __   ___   __  __  ___  __       ____   ___  ____  __ __  __");
				System.out.println("  ||\\|| // \\ | || | ||  // \\  ||\\|| // \\ ||       || \\ // \\ || \\ || // (( \\");
				System.out.println("  ||\\|| ||=||   ||   || ((   )) ||\\|| ||=|| ||       ||_// ||=|| ||_// ||<<   \\ ");
				System.out.println("  ||\\|| || ||   ||   ||  \\_//  ||\\|| || || ||__|    ||    || || || \\ || \\ \\_))");
				System.out.println("                                                              // \\");
				System.out.println("                       /\\                                  //    \\");
				System.out.println("  					 /  \\                //\\            //       \\ ");
				System.out.println("     /\\             /    \\              //   \\        //          \\ ");
				System.out.println("	    /  \\		   /    _/ \\          //  	   \\     //            \\");
				System.out.println("   /    \\ _       //\\    /'\\       //         \\   // \\/\\   //   \\");
				System.out.println("  /      \\\\   _// \\ /      \\    //\\\\        \\//     \\__  _///// \\");
				System.out.println(" /  		   \\ //    \\/        \\ //     \\       \\    \\               \\");
				System.out.println("/            \\/                \\//        \\        \\    \\               \\");
				System.out.println("--------------------|-----------------------------|---------|--------------\\-");
				System.out.println("  *");
				System.out.println("  **");
				System.out.println("  ***");
				System.out.println("  *****");
				System.out.println("   *******"); 
				System.out.println("     *******");
				System.out.println("      *******");
				System.out.println("       ***********");
				System.out.println("        *************");
				System.out.println("          **************");
				System.out.println("           ****************");
				System.out.println("             ********************");
				System.out.println("               *********************");
				System.out.println("                **********************");
				System.out.println("                  ***********************");
				System.out.println("                    ***********************");
				System.out.println("                    ***********************");
				System.out.println("                  ***********************");
				System.out.println("                **********************");
				System.out.println("               *********************");
				System.out.println("              *********************");
				System.out.println("             *********************");
				System.out.println("            *********************");
				System.out.println("           *********************");
				System.out.println("          *********************");
				System.out.println("         *********************");
				System.out.println("        *********************");
				System.out.println("       *********************");
				System.out.println("      *********************");
				System.out.println("      **********************");
				System.out.println("      **********************");
				System.out.println("       *********************");
				System.out.println("          *********************");
				System.out.println("           *********************");	
				System.out.println("            *********************");
				System.out.println("              *********************");
				System.out.println("               *********************");
				System.out.println("                 *********************");
				System.out.println("                   *********************");
				System.out.println("                     *********************");
				System.out.println("                       *********************");
				System.out.println("                         *********************");
				System.out.println("                           *********************");
				System.out.println("                            *********************");
				System.out.println("                              *********************");
				System.out.println("                                *********************");
				System.out.println("                                  *********************");
				System.out.println("                                    *********************");
				System.out.println("                                      *********************");
				System.out.println("                                        *********************");
				System.out.println("                                          *********************");
				System.out.println("                                            *********************");
				System.out.println("                                              *********************");
				System.out.println("                                                *********************");
				System.out.println("                                                  *********************");
				System.out.println("                                                    *********************");
				System.out.println("                                                      *********************");
				System.out.println("                                                         *********************");
				System.out.println("                                                             *********************");
				System.out.println("                                                                     *********************");
				System.out.println("                                                                           *********************");
				System.out.println("                                                                                 *********************");
				System.out.println("                                                                                      *********************");
		}	
	}		


