package kickstart.validation;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;


/**
 * Validates the new input if film attributes are changed
 * @author codemunin 
 * @since  26.11.15
 */
public class changeAFilmForm {
	
	private Boolean isRent;

	@NotBlank(message = "{Form.NotEmpty}")
	private String title;
	
	@NotBlank(message = "{Form.NotEmpty}")
	private String description;
	
	@NotBlank(message = "{Form.NotEmpty}")
	@Pattern(regexp = "\\d+", message = "{Form.NotNumber}")
	private String runTime;
	
	@NotBlank(message = "{Form.NotEmpty}")
	@Pattern(regexp = "\\d+\\,\\d{2}", message = "{Form.NotMoney}")
	private String baseCost;
	
	@NotBlank(message = "{Form.NotEmpty}")
	@Pattern(regexp = "\\d+\\,\\d{2}", message = "{Form.NotMoney}")
	private String rent;
	
	@NotBlank(message = "{Form.NotEmpty}")
	@Pattern(regexp = "0|6|12|16|18", message = "{RentAFilm.fsk}")
	private String fsk;
	
	
	public void setTitle(String title){
		this.title = title;
	}
	
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public void setRunTime(String runTime){
		this.runTime = runTime;
	}
	
	public void setBaseCost(String baseCost){
		this.baseCost = baseCost;
	}
	
	public void setRent(String rent){
		this.rent = rent;
	}
	
	public void setFsk(String fsk){
		this.fsk = fsk;
	}
	
    public String getTitle() {
		return title;
	}

    
	public String getDescription() {
		return description;
	}

    
    public String getRunTime() {
		return runTime;
	}

    public String getBaseCost() {
		return baseCost;
	}
    
     public String getRent() {
		return rent;
	}
    
    public String getFsk() {
		return fsk;
	}


	public Boolean getIsRent() {
		return isRent;
	}


	public void setIsRent(Boolean isRent) {
		this.isRent = isRent;
	}

}
