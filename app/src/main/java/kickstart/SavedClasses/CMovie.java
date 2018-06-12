package kickstart.SavedClasses;

import java.util.*;
import javax.persistence.*;

/**
 * Movie class. It represents a single movie.
 * @author codemunin
 * @since  21.11.15.
 */
@Entity
public class CMovie {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    private String name;
    private String description;
    private String imgPath;
    private Integer runTime;
    private String rent;
    private String baseCost;
    private String fsk;
    private Boolean isRent = true;
    private Boolean isSpecial = false;
    private @Transient Collection<Comment> comments = new ArrayList<>();
    
    private CMovie(){
        
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CMovie(String name, String description, String imgPath, Integer runTime, String baseCost, String rent, String fsk){
        this.name = name;
        this.description = description;
        this.imgPath = imgPath;
        this.runTime = runTime;
        this.baseCost = baseCost;
        this.rent = rent;
        this.fsk = fsk;
    }

    public Integer getRunTime() {
        return runTime;
    }

    public void setRunTime(Integer runTime) {
        this.runTime = runTime;
    }

    public String getDescription() {
        return description;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getBaseCost() {
            return baseCost;
    }

    public void setBaseCost(String baseCost) {
            this.baseCost = baseCost;
    }

    public String getRent() {
            return rent;
    }

    public void setRent(String rent) {
            this.rent = rent;
    }

    public String getFsk() {
            return fsk;
    }

    public void setFsk(String fsk) {
            this.fsk = fsk;
    }

    public Boolean getIsRent() {
        return isRent;
    }

    public void setIsRent(Boolean rent) {
        isRent = rent;
    }

	public Boolean getIsSpecial() {
		return isSpecial;
	}

	public void setIsSpecial(Boolean isSpecial) {
		this.isSpecial = isSpecial;
	}
	
	public Iterable<Comment> getComments() {
		return comments;
	}

    public void setComments(Collection<Comment> comments) {
        this.comments = comments;
    }

    /**
     * Sums up the ratings of all the movie's comments and calculates the average
     * @return rating or 0
     */
    
    public Integer getRating() {
        Integer rating, counter = 0, sum = 0;
        if (comments.isEmpty())
            return 0;
		for (Comment cm : comments){
			counter++;
			sum = sum + cm.getRating();
		}
		if (!(counter.equals(0))) {
			rating = (sum - (sum % counter))/counter;
            return rating;
		}
		return 0;
	}
}