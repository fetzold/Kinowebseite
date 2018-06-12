package kickstart.model.programOverView;

/**
 * Created by codemunin on 06.12.15.
 */
public class Timestap {
    private Integer hour;
    private Integer minute;
    private Integer colorId; //white == 1, green == 2, red == 3, gray == 4

    /**
     * Timestap represent a single entry inside the time table
     * @param minute Integer: amount of the minutes 
     * @param hour	 Integer: amount of the hours
     * A timestap can take for example 1h30min so you have to pass both
     * @param colorId Integer: white == 1, green == 2, red == 3
     */
    public Timestap (Integer minute, Integer hour, Integer colorId){
        this.minute = minute;
        this.hour = hour;
        this.colorId = colorId; 
    }

    public Integer getMinute() {
        return minute;
    }

    public Integer getHour() {
        return hour;
    }

    public Integer getColorId() {
        return colorId;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public void setColorId(Integer colorId) {
        this.colorId = colorId;
    }
}
