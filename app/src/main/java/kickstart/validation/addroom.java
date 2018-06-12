package kickstart.validation;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Add room called when the add room button is pressed it validates all the input and saves the room in the database
 * @author codemuin
 * @since 26.11.15
 */

public class addroom {
	
	@NotBlank(message = "{Form.NotEmpty}")
	@Min(value = 1, message = "{addroom.numbers.min}")			//needs changing to real constraints
	@Max(value= 30, message = "{addroom.rows.max}")
	private String rows;
	
	@NotBlank(message = "{Form.NotEmpty}")
	@Min(value = 1, message = "{addroom.numbers.min}")			//needs changing to real constraints
	@Max(value= 20, message = "{addroom.seats.max}")
	private String seats;
	
	@NotBlank(message = "{Form.NotEmpty}")
	private String name;
	
	@NotBlank(message = "{Form.NotEmpty}")
	@Pattern(regexp = "\\d+\\,\\d{2}", message = "{Form.NotMoney}")
	private String basicCharge;
	
	@NotBlank(message = "{Form.NotEmpty}")
	@Pattern(regexp = "\\d+\\,\\d{2}", message = "{Form.NotMoney}")
	private String extraCharge;

	public String getRows() {
		return rows;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	public String getSeats() {
		return seats;
	}

	public void setSeats(String seats) {
		this.seats = seats;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBasicCharge() {
		return basicCharge;
	}

	public void setBasicCharge(String basicCharge) {
		this.basicCharge = basicCharge;
	}

	public String getExtraCharge() {
		return extraCharge;
	}

	public void setExtraCharge(String extraCharge) {
		this.extraCharge = extraCharge;
	}

}
