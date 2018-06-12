package kickstart.model.room;

import kickstart.model.CDateStorage;
import kickstart.model.room.CSeat.EStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * Created by codemunin on 13.11.15.
 */
@Entity
public class Room {
	
	/*******************************************************************************************
	*
	*							Variablen
	*		@Column sorgt dafür das Vairablen persistent werden
	*
	*******************************************************************************************/
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "roomid", nullable = false)
    private Long roomId;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "totalseats", nullable = false)
    private Integer totalSeats;
    
    @Transient
    private Collection<CRow> myRows = new ArrayList<>();

	/*******************************************************************************************
	*
	*							Konstruktor
	*		Konstruktor überprüfen ob Vairablen fehlen!!!!
	*
	*******************************************************************************************/   
    public Room (String s_room, String name){
        this.name = name;
        char[] a_room = s_room.toCharArray();
        CRow row = new CRow(0,true);
        int rownum = 1;
        int seatnum = 1;

        for (char tmp : a_room){

            if (tmp =='-'){
                row = new CRow(rownum, false);
                myRows.add(row);
                rownum++;
                seatnum = 1;
            }

            if (tmp == 'p'){
                row.addSeat(new CSeat(rownum, seatnum, CSeat.Etype.Parkett));
                seatnum++;
            }

            if (tmp == 'l'){
                row.addSeat(new CSeat(rownum, seatnum, CSeat.Etype.Loge));
                seatnum++;
            }

            if (tmp == 'd'){
                row.addSeat(new CSeat(rownum, seatnum, CSeat.Etype.DEFECTIVE));
                seatnum++;
            }


        }
        /*
        System.out.println();

        for (Integer n = 1; n <= 13; n++) {
            CRow row = new CRow(n, false);
            for (Integer i = 1; i <= 11; i++) {
                row.addSeat(new CSeat(n, i, CSeat.Etype.Loge));
                System.out.print("*");
            }
            myRows.add(row);
            System.out.println();
        }
		*/
    }

    public Collection<CRow> getMyRows() {
        return myRows;
    }

    public Boolean changeSeatStatus(Integer i_rowNumber, Integer i_seatnumber, CSeat.EStatus e_Status, CDateStorage rentUntil){
        // Methode die den status des angegebenen Sitzes ändert


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
    
    public CSeat.EStatus getSeatStatus(Integer i_rowNumber, Integer i_seatnumber){
        // Methode die den status des angegebenen Sitzes ändert


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
    
    
    public CSeat getSeat(Integer i_rowNumber, Integer i_seatnumber){
        // Methode die den status des angegebenen Sitzes ändert


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

    public Boolean hasReservation(){
        // Prüft den Raum auf Reservierungen
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
    
    public void clearTimedout(LocalDateTime localDateTime){
        // Prüft den Raum auf abgelaufene Warenkorb-Ablagen
    	CRow tmprow;
    	CSeat tmpseat;
    	
    	Iterator<CRow> rowIterator = myRows.iterator();
    	while (rowIterator.hasNext()){
    		tmprow = rowIterator.next();
    		Iterator<CSeat> seatIterator = tmprow.getSeats().iterator();
    		while (seatIterator.hasNext()){
    			tmpseat = seatIterator.next();
    			if (tmpseat.getE_Status().equals(CSeat.EStatus.RESERVED)){
    				if (tmpseat.getReservedUntil() != null){
	    				CDateStorage actualTime = new CDateStorage(localDateTime.getMinute(),
	    					localDateTime.getHour(),
	    					localDateTime.getDayOfMonth(),
	    					localDateTime.getMonth().getValue(),
	    					localDateTime.getYear());    				
		    				if (actualTime.laterThan(tmpseat.getReservedUntil())){
		    					changeSeatStatus(tmprow.getRowNumber(), tmpseat.getI_number(), EStatus.AVAILABLE, new CDateStorage());
		    					}
    			}
    			}
    		}
    	}
    }


    public void creatRow(Integer rowNumber, Integer seats, CSeat.Etype e_type){
        CRow row = new CRow(rowNumber, false);
        for (Integer i = 1; i <= seats; i++){
            row.addSeat(new CSeat(rowNumber, i, e_type));
        }
    }

    /*@Override
    public String toString(){
        String s_room = "'";
        Integer accual_row = 1;
        CSeat tmp;

        Iterator<CSeat> seatIterator = o_mySeat.iterator();
        while (seatIterator.hasNext()){
            tmp = seatIterator.next();
            if (!accual_row.equals(tmp.getI_row())){
                s_room = s_room + "'\n'";
                accual_row = tmp.getI_row();
            }
            s_room = s_room + "a";
        }
        s_room = s_room + "'";
        return s_room;
    }*/

	/*******************************************************************************************
	*
	*							Methoden GETTER
	*
	*******************************************************************************************/
    public Long getRoomId() {
        return roomId;
    }

    public String getName() {
        return name;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }
	
	/*******************************************************************************************
	*
	*							Methoden SETTER
	*
	*******************************************************************************************/
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }
}
