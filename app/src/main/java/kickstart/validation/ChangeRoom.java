package kickstart.validation;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by codemunin on 07.12.15.
 * Requed Interface to be able to change room attributes
 */
public class ChangeRoom {

	private String seats;
	private String roomId;
	
	@NotBlank(message = "{Form.NotEmpty}")
	private String name;
	private Integer action;
	
	@NotBlank(message = "{Form.NotEmpty}")
	@Pattern(regexp = "\\d+\\,\\d{2}", message = "{Form.NotMoney}")
	private String basicCharge;
	
	@NotBlank(message = "{Form.NotEmpty}")
	@Pattern(regexp = "\\d+\\,\\d{2}", message = "{Form.NotMoney}")
	private String extraCharge;
	
	
    public String getSeats(){
    	return seats;
    };

    public void setSeats(String seats){
    	this.seats = seats;
    };

    public String getRoomId(){
    	return roomId;
    };

    public void setRoomId(Long id){
    	this.roomId = id.toString();
    };

    public Integer getAction(){
    	return action;
    };

    public void setAction(Integer action){
    	this.action = action;
    };

    public String getName(){
    	return name;
    };

    public void setName(String name){
    	this.name = name;
    };

    public String getBasicCharge(){
    	return basicCharge;
    };

    public void setBasicCharge(String basicCharge){
    	this.basicCharge = basicCharge;
    };

    public String getExtraCharge(){
    	return extraCharge;
    };

    public void setExtraCharge(String extraCharge){
    	this.extraCharge = extraCharge;
    };
}