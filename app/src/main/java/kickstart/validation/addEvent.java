package kickstart.validation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by codemunin on 28.11.15.
 * required to add a new event
 */
public class addEvent {
	
	@NotNull private Long movieID;
	@NotNull private Long roomID;
	private Long eventId;
    private Boolean delete;
    @NotNull private Boolean b_private;
    
    @NotBlank(message = "{Form.NotEmpty}")
    private String title;
    
    @NotBlank(message = "{Form.NotEmpty}")
    private String description;
    
    @NotBlank(message = "{Form.NotEmpty}")
    @Pattern(regexp = "\\d+\\,\\d{2}", message = "{Form.NotMoney}")
    private String rent;
    
    private Integer runTime;
    
    @NotNull(message = "{editevent.duration}")
    private String allday;
    
    @NotBlank(message = "{Form.NotEmpty}")
    @Pattern(regexp = "\\d+\\,\\d{2}", message = "{Form.NotMoney}")
    private String baseCost;

    public void setRunTime(Integer runTime) {
        this.runTime = runTime;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setMovieID(Long movieID) {
        this.movieID = movieID;
    }

    public void setB_private(Boolean b_private) {
        this.b_private = b_private;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setRoomID(Long roomID) {
        this.roomID = roomID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getB_private() {
        return b_private;
    }

    public Boolean getDelete() {
        return delete;
    }


    public Long getRoomID() {
        return roomID;
    }

    public String getRent() {
        return rent;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public Long getMovieID() {
        return movieID;
    }

    public Long getEventId() {
        return eventId;
    }

    public Integer getRunTime() {
        return runTime;
    }

	public String getAllday() {
		return allday;
	}

	public void setAllday(String allday) {
		this.allday = allday;
	}

	public String getBaseCost() {
		return baseCost;
	}

	public void setBaseCost(String baseCost) {
		this.baseCost = baseCost;
	}
}
