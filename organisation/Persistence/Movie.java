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
public class Movie {
    
	/*******************************************************************************************
	*
	*							Variablen
	*		@Column sorgt dafür das Vairablen persistent werden
	*
	*******************************************************************************************/
    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO) 
    @Column(name = "movieid", nullable = false)
    private Long movieId;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "runtime", nullable = false)
    private String runtime;
    
    @Column(name = "rent", nullable = false)
    private Boolean rent;
    
    @Column(name = "rentcost", nullable = false)
    private Double rentcost;
    
    @Column(name = "totalrundays", nullable = false)
    private Integer totalRunDays;
    
    @Column(name = "image", nullable = false)
    private String imgPath;
    
    @Column(name = "description", nullable = false)
    private String description;

	/*******************************************************************************************
	*
	*							Konstruktor
	*		Konstruktor überprüfen ob Vairablen fehlen!!!!
	*
	*******************************************************************************************/    
    private Movie(){
        
    }
    
	public Movie(String name, String description, String imgPath){
        this.title = name;
        this.description = description;
        this.imgPath = imgPath;
    }
	
	/*******************************************************************************************
	*
	*							Methoden GETTER
	*
	*******************************************************************************************/
    public Long getMovieId() {
        return movieId;
    }
	
	public String getDescription() {
        return description;
    }

    public String getImgPath() {
        return imgPath;
    }
	

    public String getTitle() {
        return title;
    }

    public String getRuntime() {
        return runtime;
    }

    public Boolean getRent() {
        return rent;
    }

    public Double getRentcost() {
        return rentcost;
    }

    public Integer getTotalRunDays() {
        return totalRunDays;
    }
	

	/*******************************************************************************************
	*
	*							Methoden SETTER
	*
	*******************************************************************************************/
    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public void setRent(Boolean rent) {
        this.rent = rent;
    }

    public void setRentcost(Double rentcost) {
        this.rentcost = rentcost;
    }

    public void setTotalRunDays(Integer totalRunDays) {
        this.totalRunDays = totalRunDays;
    }   

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
