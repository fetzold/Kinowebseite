package kickstart.SavedClasses;

import kickstart.SavedClasses.CSeat.EStatus;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Create the cinema room with its seats. Different seat number in every raw possible
 * @author codemuin 
 * @since 13.11.15
 * changed by Alexej 12/7/2015
 */

@Entity
public class CRoom {
	
	//============================================================Variables=================================================================

    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long key;
    private @ManyToMany Collection<CRow> myRows = new ArrayList<>();
    private String s_name;
    private Long id;

    //-------------------------------------------------------------C&D-------------------------------------------------------------


    public CRoom(){}


    /**
     * Creates the Room with Seats which are saved inside the database
     * @param name String the basic cinema room name
     * @param id  The room id
     * @param myRows Collection&#60;CRow&#62; Room's rows
     * @param name String The String that represent the room inside the database
     */
    public CRoom(String name,Long id , Collection<CRow> myRows){
        this.myRows = myRows;
        this.s_name = name;
        this.id = id;
    }



    
//END C&D
    
    //============================================================Public Methods=============================================================	
    public Collection<CRow> getMyRows() 	{return myRows;}
	public Long 			getI_id() 		{return id;}
	public String 			getS_name() 	{return s_name;}
	
	public void setS_name(String s_name) {this.s_name = s_name;}
	
    /**
     * Changes the seat status of one seat
     * @param i_rowNumber Integer    : the number of the specific row where the seat is
     * @param i_seatnumber Integer   : the number of the seat inside the row
     * @param e_Status CSeat.EStatus : the new/target satus of our seat 
     * @param rentUntil	CDateStorage : the time till the seat will be free again, for example for the next movie 
     * @return true if it was possible otherwise false, if the seat wasn't inside the room
     */
    
    public Boolean changeSeatStatus(Integer i_rowNumber, Integer i_seatnumber, CSeat.EStatus e_Status, CDateStorage rentUntil)
    {
        CRow tmp;
        Iterator<CRow>rowsIterator = myRows.iterator();
        while (rowsIterator.hasNext()){
            tmp = rowsIterator.next();
            if (tmp.getRowNumber().equals(i_rowNumber)) {
                return tmp.changeSeatStatus(i_seatnumber, e_Status, rentUntil);
            }
        }
        return false;
    }
    
    /**
     * Returns a seat status by row and seat number, not requiring the object to be passed
     * @param i_rowNumber	Integer : The specific row number of the seat
     * @param i_seatnumber	Integer : The seat number depends on the row number
     * @return CSeat.EStatus, or null if it fails
     */
    
    public CSeat.EStatus getSeatStatus(Integer i_rowNumber, Integer i_seatnumber){


        CRow tmp;
        Iterator<CRow>rowsIterator = myRows.iterator();
        while (rowsIterator.hasNext()){
            tmp = rowsIterator.next();
            if (tmp.getRowNumber().equals(i_rowNumber)) {
                return tmp.getSeatStatus(i_seatnumber);
            }
        }
        return null;
    }
    
    /**
     * Returns seat by their row and seat number
     * @param i_rowNumber  Integer row number
     * @param i_seatnumber Integer seat number
     * @return CSeat object, or null if it fails
     */
    
    public CSeat getSeat(Integer i_rowNumber, Integer i_seatnumber){

        CRow tmp;
        Iterator<CRow>rowsIterator = myRows.iterator();
        while (rowsIterator.hasNext()){
            tmp = rowsIterator.next();
            if (tmp.getRowNumber().equals(i_rowNumber)) {
                return tmp.getSeat(i_seatnumber);
            }
        }
        return null;
    }
    
    /**
     * Checks if the room has any reservations at all. Needed to make sure events with reservations can't be changed
     * @return if has reservations return true. no reservations false
     */
    
    public Boolean hasReservation(){
    	CRow tmprow;
    	CSeat tmpseat;
    	
    	Iterator<CRow> rowIterator = myRows.iterator();
    	
    	while (rowIterator.hasNext()){
    		tmprow = rowIterator.next();
    		Iterator<CSeat> seatIterator = tmprow.getSeats().iterator();
    		while (seatIterator.hasNext()){
    			tmpseat = seatIterator.next();
    			if (tmpseat.getE_Status().equals(CSeat.EStatus.RESERVED) ||tmpseat.getE_Status().equals(CSeat.EStatus.PAID)) return true;
    		}
    	}
    	
        return false;
    }
    
    /**
     * Method iterating through all seats in a room, checking their
     * reservation date and freeing those that ran out.
     * @param localDateTime current time passed by calling controller
     * @param cds CDateStorage new empty date object to save to seats
     * @return seatlist
     */
    
    public ArrayList<CSeat> clearTimedout(LocalDateTime localDateTime, CDateStorage cds){
    	ArrayList<CSeat> seatlist = new ArrayList<CSeat>();
    	CRow tmprow;
    	CSeat tmpseat;
    	CDateStorage actualTime = new CDateStorage(localDateTime.getMinute(),
				localDateTime.getHour(),
				localDateTime.getDayOfMonth(),
				localDateTime.getMonth().getValue(),
				localDateTime.getYear());
    	
    	Iterator<CRow> rowIterator = myRows.iterator();
    	while (rowIterator.hasNext()){
    		tmprow = rowIterator.next();
    		Iterator<CSeat> seatIterator = tmprow.getSeats().iterator();
    		while (seatIterator.hasNext()){
    			tmpseat = seatIterator.next();
    			if (tmpseat.getE_Status().equals(CSeat.EStatus.RESERVED) && actualTime.laterThan(tmpseat.getReservedUntil())){
		    		changeSeatStatus(tmprow.getRowNumber(), tmpseat.getI_number(), EStatus.AVAILABLE, cds);
		    		seatlist.add(tmpseat);
		    		//dbSeat.save(tmpseat);
    			}
    		}
    	}
    	return seatlist;
    }



    /**
     * Create a additional raw with a individual seat number
     * @param seats  		Integer: 	number of the seat inside the row
     * @param e_type		CSeat.Etype:the type of the seat Loge or Prakett oder Defective 
      * changed by Alexej 12/7/2015
     */
    
    public void creatRow(Integer seats, CSeat.Etype e_type)
    {
        Integer rowNumber = myRows.size() + 1;
        CRow row = new CRow(rowNumber, false);
        for (Integer i = 1; i <= seats; i++)
        {
            row.addSeat(new CSeat(rowNumber, i, e_type));
        }
        myRows.add(row);
    }

    @Override
    public String toString(){
        String s_room = "";
        for (CRow row : myRows){
            s_room = s_room + "-";
            Collection<CSeat> coll = row.getSeats();
            for (CSeat seat : coll){
                if (seat.getE_Type().equals(CSeat.Etype.DEFECTIVE)){
                    s_room = s_room + "d";
                }
                if (seat.getE_Type().equals(CSeat.Etype.Loge)){
                    s_room = s_room + "l";
                }
                if (seat.getE_Type().equals(CSeat.Etype.Parkett)){
                    s_room = s_room + "p";
                }
            }
        }
        return s_room;
    }

	/**
	 * Checks for a single CCinemaTicket if the corresponding
	 * CSeat is either AVAILABLE or RESERVED but past its
	 * reservation time.
	 * @param localDateTime	stores the current date and time
	 * @param ticket the ticket that will be checked
	 * @return	true if the seat fulfills the criteria, false if the seat is PAID or still within its RESERVED time
	 */
	
	public Boolean checkTimedoutOrAvailabe(LocalDateTime localDateTime, CCinemaTicket ticket){
		CDateStorage actualTime = new CDateStorage(localDateTime.getMinute(),
				localDateTime.getHour(),
				localDateTime.getDayOfMonth(),
				localDateTime.getMonth().getValue(),
				localDateTime.getYear());
		if (getSeatStatus(ticket.getI_row(), ticket.getI_seat()).equals(CSeat.EStatus.RESERVED)){
			if (actualTime.laterThan(getSeat(ticket.getI_row(), ticket.getI_seat()).getReservedUntil())){
				changeSeatStatus(ticket.getI_row(), ticket.getI_seat(), CSeat.EStatus.AVAILABLE, new CDateStorage());
				return true;
			}
		}
		if (getSeatStatus(ticket.getI_row(), ticket.getI_seat()).equals(CSeat.EStatus.AVAILABLE)){
			return true;			
		}

		
		return false;
	}

	/**
	 * Deletes all seats inside the Collection
	 * @param seats the target Collection, inside it the seats will be deleted
	 */
    public void deleteSeats(Collection<CSeat> seats) {
        for (CSeat toDelet : seats)
            for (CRow row : myRows){
                Collection<CSeat> coll = row.getSeats();
                row.getSeats().remove(toDelet);

            }
    }
}