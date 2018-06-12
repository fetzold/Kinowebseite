package kickstart.model;

import javax.persistence.Column;
import org.salespointframework.catalog.Product;
import org.springframework.boot.autoconfigure.web.ResourceProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by codemunin on 24.11.15.
 */
@Entity
public class Ticket {
    
	/*******************************************************************************************
	*
	*							Variablen
	*		@Column sorgt dafür das Vairablen persistent werden
	*
	*******************************************************************************************/
    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO) 
    @Column(name = "ticketId", nullable = false)
    private Long ticketId;
    
    @Column(name = "filmtitle", nullable = false)
    private String filmTitle;
    
    @Column(name = "roomid", nullable = false)
    private String roomId;
    
    @Column(name = "date", nullable = false)
    private String date;
    
    @Column(name = "time", nullable = false)
    private String time;
    
    @Column(name = "cost", nullable = false)
    private Double cost;
    
    @Column(name = "type", nullable = false)
    private String type;
    
    @Column(name = "status", nullable = false)
    private String status;

    /*******************************************************************************************
    *
    *							Konstruktor
    *		Konstruktor überprüfen ob Vairablen fehlen!!!!
    *
    *******************************************************************************************/    
    private Ticket(){}
    
    /*******************************************************************************************
    *
    *							Methoden GETTER
    *
    *******************************************************************************************/
    public Long getTicketId() {
        return ticketId;
    }

    public String getFilmTitle() {
        return filmTitle;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Double getCost() {
        return cost;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }
    
    /*******************************************************************************************
    *
    *							Methoden SETTER
    *
    *******************************************************************************************/
    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public void setFilmTitle(String filmTitle) {
        this.filmTitle = filmTitle;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }  
}
