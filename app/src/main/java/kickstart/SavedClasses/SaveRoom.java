package kickstart.SavedClasses;

import kickstart.SavedClasses.CSeat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by codemunin on 28.11.15.
 * Saves the room with all its seats and rows and its attributes 
 */
@Entity
public class SaveRoom {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    @Column(length=900)private String room;
    private String name;
    private String basicCharge;
    private String extraCharge;
    private String active;


    public SaveRoom(){};

    /**
     * Saves the specific room
     * @param name	the room name
     * @param room	the room represented through the string
     * @param basicCharge	the value for the cheap "Parkett" seats
     * @param extraCharge	the value for the expensive "Loge" seats
     */
    public SaveRoom(String name, String room, String basicCharge, String extraCharge){
        this.name = name;
        this.room = room;
        this.basicCharge = basicCharge;
        this.extraCharge = extraCharge;
        if (active == null) active = "yes";
    }


    public String getRoom() {
        return room;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRoom(String room) {
        this.room = room;
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
	

	public String getPrice(CSeat.Etype type){
		if (type.equals(CSeat.Etype.Loge)){return getExtraCharge();}
		if (type.equals(CSeat.Etype.Parkett)){return getBasicCharge();}
		else {return null;}
	}

    public void setActive(Boolean active) {
        System.out.println(this.active);
        if (!active){
            this.active = "no";

        }System.out.println(this.active);
    }
}
