package kickstart.model.room;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import kickstart.model.CDateStorage;

/**
 * Created by codemunin on 12.11.15.
 */
@Entity
public class Seat {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "seatid", nullable = false)
    private Long seatId;
    
    @Column(name = "row", nullable = false)
    private Integer row;
    
    @Column(name = "number", nullable = false )
    private Integer number;
    
    @Column(name = "status", nullable = false)
    private EStatus status;
    
    @Column(name = "type", nullable = false)
    private Etype type;
    
    @Column(name = "roomid", nullable = false)
    private Long roomId;
    
    @Transient
    private CDateStorage reservedUntil;

    public static enum EStatus {
        RESERVED, AVAILABLE, PAID;
    }
    public static enum Etype{
        DEFECTIVE, Loge, Parkett;
    }

    public void setReservedUntil(CDateStorage reservedUntil) {
        this.reservedUntil = reservedUntil;
    }

    public CDateStorage getReservedUntil() {
        return reservedUntil;
    }

    public Seat (Integer row, Integer number, Etype type){
        this.row = row;
        this.number = number;
        this.type = type;
        status = EStatus.AVAILABLE;
    }

    public Integer getRow() {
        return row;
    }
    
    public Etype getType() {
    	return type;
    }

    public EStatus getStatus() {
    	return status;
    }    
    
    public Boolean seatAvailable() {
    	switch(getStatus()) {
    	case AVAILABLE: return true;
    	default: return false;
    	}
    }
    
    public Boolean seatLoge() {
    	switch(getType()) {
    	case Loge: return true;
    	default: return false;
    	}
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public void setType(Etype type) {
    	this.type = type;
    }

    public void setStatus(EStatus status) {
    	this.status = status;
    }    

    public Long getSeatId() {
        return seatId;
    }

    public Integer getNumber() {
        return number;
    }

    public Long getRoomId() {
        return roomId;
    }
    
}
