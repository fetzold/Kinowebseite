package kickstart.SavedClasses;



import javax.persistence.*;

import java.time.LocalDateTime;

import kickstart.SavedClasses.CSeat.Etype;

/**
 * @author codemunin
 * @since 05.11.15
 * 
 */

@Entity
public class CEvent {
	
	//============================================================Variables============================================

    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    private @ManyToOne CDateStorage o_startDate;
    private @ManyToOne CDateStorage o_endDate;
    private @ManyToOne CRoom o_room;
    private @ManyToOne CMovie movie;
    private Boolean isPrivate;

    
    //-------------------------------------------------------------C&D------------------------------------------------
/**
 * The main Event constructor
 * @param start 				CDateStorage 	: Start "Time/Date" of the event. From the beginning moment on the event runs
 * @param end					CDateStorage	: End   "Time/Date" of the event. This is the ending point of the event after it passes this point its over
 * @param movie					CMovie			: The movie for the event it will run inside the room
 * @param croom	CRoom room the event is set up in
 */
    public CEvent(CDateStorage start, CDateStorage end, CMovie movie, CRoom croom)
    {
        o_room = croom;
        o_startDate = start;
        o_endDate = end;
        this.movie = movie;
    }

    public CEvent(){}

//END C&D
    //============================================================Public Methods=======================================


    
    /**
     * Checks if an event still allows reservations to be made by comparing start time to the current time + 30 minutes
     * No reservations are possible if the event starts in less than 30 min
     * @param localDateTime	LocalDateTime 	: The current time
     * @param minutesBeforeMovieDeleteReservation Integer : Time till the vent starts
     * @return Boolean true if possible, false if not
     */
    
    public Boolean canReserve(LocalDateTime localDateTime, Integer minutesBeforeMovieDeleteReservation){
    	CDateStorage actualTime = new CDateStorage(localDateTime.getMinute(),
                localDateTime.getHour(),
                localDateTime.getDayOfMonth(),
                localDateTime.getMonth().getValue(),
                localDateTime.getYear());
		actualTime.addMinutes(minutesBeforeMovieDeleteReservation);
		return o_startDate.laterThan(actualTime);
    }

    /**
     * Counts all seats in a room and checks how many have the PAID status,
     * providing a way to see an event's total amount of seats sold.
     * @return String paidSeats / allSeats
     */
    
    public String roomOccPy(){
        Integer allSeats = 0;
        Integer paidSeats = 0;
        
        for (CRow row : o_room.getMyRows()){
            for (CSeat seat : row.getSeats()){
            	if (seat.getE_Type().equals(Etype.Loge) || seat.getE_Type().equals(Etype.Parkett)){
            		allSeats++;
            	}
                if (seat.getE_Status().equals(CSeat.EStatus.PAID)){
                    paidSeats++;
                }
            }
        }
        return (paidSeats.toString()+"/"+allSeats.toString());
    }

    //_____________________________________________________________GETTER____________________________________________
    
    public CDateStorage 	getO_startDate() 	{return o_startDate;}
    public CDateStorage 	getO_endDate() 		{return o_endDate;}
    public CMovie 			getMovie() 			{return movie;}
    public Long 			getId() 			{return id;}
    public CRoom 			getO_room()	 		{return o_room;}
    public String 			getStartTime()		{return o_startDate.toString();}  //"12.00(Bsp.)"
    public Boolean          getIsPrivate()      {return isPrivate;}

    
  //_____________________________________________________________SETTER________________________________________________
    
    public void setId(Long id) {this.id = id;}
    public void setIsPrivate(Boolean isPrivate)      {this.isPrivate = isPrivate;}
    public int compareTo(CEvent o2) {
        return getO_startDate().compareTo(o2.getO_startDate());
    }
}
