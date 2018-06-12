package kickstart.SavedClasses;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author codemunin 
 * @since 25.11.15.
 * The Row represents a single row inside a cinema room. It contains the seats 
 */
@Entity
public class CRow {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    private Integer rowNuber;
    private @ManyToMany Collection<CSeat> seats = new ArrayList<>();
    private Boolean empty = false;

    public CRow(){}

    /**
     * Creates a single row
     * @param rowNuber : row number
     * @param empty	   : if empty is true no seats can be created in this raw
     */
    public CRow (Integer rowNuber, Boolean empty){
        this.rowNuber = rowNuber;
        this.empty = empty;
    }

    public Boolean isEmpty() {
        return empty;
    }

    public Collection<CSeat> getSeats() {
        return seats;
    }

    public Integer getRowNumber() {
        return rowNuber;
    }

    public void setRowNuber(Integer rowNuber) {
        this.rowNuber = rowNuber;
    }

    /**
     * Add the seat to the raw
     * @param seat The single seat that will be added
     * @return seats could be added = true else false
     */
    public Boolean addSeat(CSeat seat){
        return seats.add(seat);
    }

    public Boolean changeSeatStatus(Integer i_seatnumber, CSeat.EStatus e_Status, CDateStorage rentUntil){
        // Methode die den status des angegebenen Sitzes auf reserviert ändert
        //wäre es nicht besser einfach nur eine Methode changeSeatStatus zu schreiben?

        CSeat tmp;
        Iterator<CSeat> seatIterator = seats.iterator();
        while (seatIterator.hasNext()){
            tmp = seatIterator.next();
            if ((tmp.getI_number().equals(i_seatnumber))) {
                tmp.setE_Status(e_Status);
                tmp.setReservedUntil(rentUntil);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get the specific status of the seat in the raw
     * @param i_seatnumber target seat number
     * @return returns the seat if it does not exist null
     */
    public CSeat.EStatus getSeatStatus(Integer i_seatnumber){

        CSeat tmp;
        Iterator<CSeat> seatIterator = seats.iterator();
        while (seatIterator.hasNext()){
            tmp = seatIterator.next();
            if ((tmp.getI_number().equals(i_seatnumber))) {
                return tmp.getE_Status();
            }
        }
        return null;
    }

    /**
     * Getter for the specific seat depends on the seat number
     * @param i_seatnumber the seat number of the specific sea
     * @return returns the seat if it exists else null
     */
    public CSeat getSeat(Integer i_seatnumber){

        CSeat tmp;
        Iterator<CSeat> seatIterator = seats.iterator();
        while (seatIterator.hasNext()){
            tmp = seatIterator.next();
            if ((tmp.getI_number().equals(i_seatnumber))) {
                return tmp;
            }
        }
        return null;
    }
}
