package kickstart.SavedClasses;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * Date Storage class. Its a logical container. 
 * @author codemunin
 * @since  21.11.15.
 */

@Entity
public class CDateStorage {
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;
    private Integer minute;
    private Integer hour;
    private Integer day;
    private Integer month;
    private Integer year;

    public  CDateStorage(){}

    public CDateStorage(CDateStorage time){
        minute = time.getMinute();
        hour = time.getHour();
        day = time.getDay();
        month = time.getMonth();
        year = time.getYear();
    }

    public CDateStorage(LocalDateTime time){
        minute = time.getMinute();
        hour = time.getHour();
        day = time.getDayOfMonth();
        month = time.getMonthValue();
        year = time.getYear();
    }

    public CDateStorage(Integer minute, Integer hour, Integer day, Integer month, Integer year){
        this.minute = minute;
        this.hour = hour;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public LocalDateTime toLocalDateTime(){
        return LocalDateTime.of(year,month,day ,hour, minute);
    }


    public int compareTo(CDateStorage o_startDate) {
        LocalDateTime ld1 = LocalDateTime.of(year,month,day ,hour, minute);
        LocalDateTime ld2 = o_startDate.toLocalDateTime();
        return ld1.compareTo(ld2);
    }

    public void addMinutes(Integer minutes){

        LocalDateTime localDateTime = LocalDateTime.of(year,month,day ,hour, minute);
        localDateTime = localDateTime.plusMinutes(minutes);
        year = localDateTime.getYear();
        month = localDateTime.getMonthValue();
        day = localDateTime.getDayOfMonth();
        hour = localDateTime.getHour();
        minute = localDateTime.getMinute();

    }

    public void subMinutes(Integer minutes){
        LocalDateTime localDateTime = LocalDateTime.of(year,month,day ,hour, minute);
        localDateTime = localDateTime.plusMinutes(Long.valueOf(minutes * (-1)));
        year = localDateTime.getYear();
        month = localDateTime.getMonthValue();
        day = localDateTime.getDayOfMonth();
        hour = localDateTime.getHour();
        minute = localDateTime.getMinute();
    }

    public void setDate (Integer minute, Integer hour, Integer day, Integer month, Integer year){
        this.minute = minute;
        this.hour = hour;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    @Override
    public String toString(){
        String s = "";
        if (0 < hour.compareTo(Integer.valueOf(9))){
            s = s + hour.toString();
        }else{
            s = s + "0" + hour.toString();
        }
        s = s + ":";
        if (0 < minute.compareTo(Integer.valueOf(9))){
            s = s + minute.toString();
        }else{
            s = s + "0" + minute.toString();
        }

        s = s + " ("+day.toString()+"."+month.toString()+"."+year.toString()+")";

        return s;
    }

    public String getDate(){
        return (day.toString()+"."+month.toString());
    }

    public Boolean laterThan(CDateStorage sec){
        if (sec == null){
            return false;
        }
        if (0 < year.compareTo(sec.getYear()))
            return true;
        if (0 == year.compareTo(sec.getYear())){
            if (0 < month.compareTo(sec.getMonth()))
                return true;
            if (0 == month.compareTo(sec.getMonth())){
                if (0 < day.compareTo(sec.getDay()))
                    return true;
                if (0 == day.compareTo(sec.getDay())){
                    if (0 < hour.compareTo(sec.getHour()))
                        return true;
                    if (0 == hour.compareTo(sec.getHour())){
                        if (0 <= minute.compareTo(sec.getMinute()))
                            return true;
                    }
                }
            }

        }

        return false;
    }

    public Boolean erlierThen(CDateStorage sec){
        if (sec == null){
            return false;
        }
        if (0 > year.compareTo(sec.getYear()))
            return true;
        if (0 == year.compareTo(sec.getYear())){
            if (0 > month.compareTo(sec.getMonth()))
                return true;
            if (0 == month.compareTo(sec.getMonth())){
                if (0 > day.compareTo(sec.getDay()))
                    return true;
                if (0 == day.compareTo(sec.getDay())){
                    if (0 > hour.compareTo(sec.getHour()))
                        return true;
                    if (0 == hour.compareTo(sec.getHour())){
                        if (0 >= minute.compareTo(sec.getMinute()))
                            return true;
                    }
                }
            }

        }

        return false;
    }

    public Integer getDay() {
        return day;
    }

    public Integer getHour() {
        return hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getYear() {
        return year;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }


}
