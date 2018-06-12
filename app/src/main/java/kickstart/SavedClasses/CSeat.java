package kickstart.SavedClasses;

import javax.persistence.*;

/**
 * @author codemuin 
 * @since 12.11.15
 * changed by Alexej 12/7/2015
 */

@Entity
public class CSeat {
	
	//============================================================Variables===========================================
	private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    private Integer i_row;
    private Integer i_number;
    private Etype e_Type;
    private EStatus e_Status;
    private @ManyToOne CDateStorage reservedUntil;

    public static enum EStatus {
        RESERVED, AVAILABLE, PAID;
    }
    public static enum Etype{
        DEFECTIVE, Loge, Parkett;
    }

  //-------------------------------------------------------------C&D--------------------------------------------------

   
    public CSeat(){}
    /**Constructor of the CSeat, every object represent a single seat with its attributes and status 
     * @param i_row  Integer: 	The row of the seats
     * @param i_number  Integer: 	The seat number in its row
     * @param e_Type  Etype  :	Type of the Seat. "Parkett" Basic seat, "Loge" more expensive, "DEFECTIVE" its broken
     */
    
    public CSeat (Integer i_row, Integer i_number, Etype e_Type){
    	this.i_row = i_row;
        this.i_number = i_number;
        this.e_Type = e_Type;
        e_Status = EStatus.AVAILABLE;
    }
    
//END C&D
    
 //============================================================Public Methods=========================================
    /**
     * @author Alexej
     * @since 12/7/15
     * @return if Seat is Loge then basicCharge + extraCharge else basicCharge
     */
    
    public Boolean seatAvailable() {
    	if(getE_Status().equals(EStatus.AVAILABLE)){
    		return true;
    	} else {
    		return false;
    	}
    }
    
    /**
     * Checks if the seat has the type Loge
     * @return true if Loge false if not
     */
    public Boolean seatLoge() {
    	if(getE_Type().equals(Etype.Loge)){
    		return true;
    	}else {
    		return false;
    	}
    }
    
//_____________________________________________________________GETTER___________________________________________________
    
    public CDateStorage getReservedUntil() 		{return reservedUntil;}
    public Integer 		getI_number() 			{return i_number;}
    public Integer 		getI_row() 				{return i_row;}
    public Etype 		getE_Type() 			{return e_Type;}
    public EStatus 		getE_Status() 			{return e_Status;}

//_____________________________________________________________SETTER___________________________________________________
    
    public void setI_number			(Integer i_number) 				{this.i_number = i_number;}
    public void setI_row			(Integer i_row) 				{this.i_row = i_row;}
    public void setE_Type			(Etype e_Type) 					{this.e_Type = e_Type;}
    public void setE_Status			(EStatus e_Status) 				{this.e_Status = e_Status;}
    
    /**
     * Timesetter sets the reservation time point
     * @param reservedUntil CDateStorage the time in the future till which the seat will be reserved
     */
    public void setReservedUntil (CDateStorage reservedUntil) {
        this.reservedUntil = reservedUntil;
    }
}