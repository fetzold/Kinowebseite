package kickstart.model.eventManagement;


import kickstart.model.CDateStorage;
import kickstart.model.CmovieId;
import kickstart.model.room.Room;
import kickstart.model.room.CRow;
import kickstart.model.room.Seat;
import org.salespointframework.time.BusinessTime;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Iterator;

/**
 * Created by codemunin on 05.11.15.
 */

@Entity
public class Event {
	
	/*******************************************************************************************
	*
	*							Variablen
	*		@Column sorgt dafür das Vairablen persistent werden
	*
	*******************************************************************************************/
    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO) 
    @Column(name = "eventid", nullable = false)
    private Long eventId;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "date", nullable = false)
    private CDateStorage date;
    
    @Column(name = "starttime", nullable = false)
    private CDateStorage startDate;
    
    @Column(name = " endtime", nullable = false)
    private CDateStorage endDate;
    
    @Column(name = "soldticket", nullable = false)
    private Integer soldticket;
    
    @Column(name = "sales", nullable = false)
    private Integer sales;
    
    @Column(name = "roomid", nullable = false)
    private Room room;
    
    @Column(name = "movieid", nullable = false)
    private CMovie movieId;

	/*******************************************************************************************
	*
	*							Konstruktor
	*		Konstruktor überprüfen ob Vairablen fehlen!!!!
	*
	*******************************************************************************************/
    public Event(CDateStorage start, CDateStorage end, String roomID, String name, CMovie movieId){
        room = new Room(roomID, name);
        startDate = start;
        endDate = end;
        this.movieId = movieId;
        System.out.println(eventId);
    }

	/*******************************************************************************************
	*
	*							Methoden GETTER
	*
	*******************************************************************************************/	
    public CDateStorage getStartDate() {
        return startDate;
    }

    public CDateStorage getEndDate() {
        return endDate;
    }

    public CmovieId getMovieId() {
        return movieId;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getStartTime(){
        return startDate.toString();   //"12.00(Bsp.)"
    }

    public Room getRoomId() {			//Unnötig?
        return room;
    }
	
    public String getName() {
        return name;
    }

    public CDateStorage getDate() {
        return date;
    }

    public Integer getSoldticket() {
        return soldticket;
    }

    public Integer getSales() {
        return sales;
    }

    public Room getRoom() {		//REFACTOREN!!!!
        return room;
    }	
	
	/*******************************************************************************************
	*
	*							Methoden SETTER
	*
	*******************************************************************************************/
    public void setEventId(Long id) {
        eventId = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(CDateStorage date) {
        this.date = date;
    }

    public void setStartDate(CDateStorage startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(CDateStorage endDate) {
        this.endDate = endDate;
    }

    public void setSoldticket(Integer soldticket) {
        this.soldticket = soldticket;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setMovieId(Cmovie movieId) {
        this.movieId = movieId;
    }

	/*******************************************************************************************
	*
	*										Methoden 
	*						Ticket changeEvent, reserveTicket, clearSeats
	*******************************************************************************************/
    public Boolean reserveTicket(Integer row, Integer number, CDateStorage rentUntil){
        return room.changeSeatStatus(row, number, Seat.EStatus.RESERVED, rentUntil);
    }

    public Boolean changeEvent(CDateStorage start, CDateStorage end, String roomID, String name){
        if (room.hasReservation())
            return false;

        room = new Room(roomID, name);
        startDate = start;
        endDate = end;
        return true;
    }

    public void clearSeats(BusinessTime businessTime){
        Iterator <CRow> rowIt;
        Iterator <Seat> seatIt;
        Seat tmp;

        LocalDateTime localDateTime = businessTime.getTime();
        CDateStorage actualTime = new CDateStorage(localDateTime.getMinute(),
                localDateTime.getHour(),
                localDateTime.getDayOfMonth(),
                localDateTime.getMonth().getValue(),
                localDateTime.getYear());
        rowIt = room.getMyRows().iterator();

        while (rowIt.hasNext()){
            seatIt = rowIt.next().getSeats().iterator();
            while (seatIt.hasNext()){
                tmp = seatIt.next();
                if (tmp.getStatus().equals(Seat.EStatus.RESERVED)) {
                    if (actualTime.laterThan(tmp.getReservedUntil())) {
                        tmp.setStatus(Seat.EStatus.AVAILABLE);
                    }
                }
            }
        }
    }
}
