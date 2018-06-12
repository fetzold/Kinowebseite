package kickstart.model.programOverView;

import kickstart.SavedClasses.CDateStorage;
import kickstart.SavedClasses.CMovie;
import kickstart.SavedClasses.CEvent;
import kickstart.model.eventManagement.CProgram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by codemunin on 06.12.15.
 * This class is responsible for the time table. Events take place inside the 
 * sectors of this class, depends on the length of the event
 */
public class Generator {
    private Collection<Day> collection = new ArrayList<>(7);
    private Collection<String> time = new ArrayList<>();
    private CDateStorage acualTime;
    private CMovie movie;
    private Long roomId;
    private CProgram cinema_program;
    
    
    public Generator (CDateStorage acualTime, Long roomId, CMovie movie, CProgram cProgram) {
        this.acualTime = acualTime;
        this.movie = movie;
        this.roomId = roomId;
        this.cinema_program = cProgram;
        this.setFreeTime(new CDateStorage(acualTime), roomId);
        this.markTime(movie);
        this.creatTimes();
        this.removeTimeToAcual(acualTime);

    }

    /**
     * Lifetime update, the time changes so the table have to be updated
     */
    public void update(){
        collection = new ArrayList<>();
        time = new ArrayList<>();
        this.setFreeTime(new CDateStorage(acualTime), roomId);
        this.markTime(movie);
        this.creatTimes();
    }

    //Erstellt die Zeitschritte für die Tabelle
    /**
     * Create the time sectors for the time table
     */
    private void creatTimes(){
        String s;
        Integer hour;
        Integer minute;

        for (hour = 0; hour < 24; hour++) {
            for (minute = 0; minute < 60; minute = minute + 15) {
                s = "";
                if (0 < hour.compareTo(Integer.valueOf(9))) {
                    s = s + hour.toString();
                } else {
                    s = s + "0" + hour.toString();
                }
                s = s + ":";
                if (0 < minute.compareTo(Integer.valueOf(9))) {
                    s = s + minute.toString();
                } else {
                    s = s + "0" + minute.toString();
                }
                time.add(s);
            }
        }
    }


    //Markiert jeden frei Timestap wo gerade kein film läuft
    /**
     * Marks every free time sector where no movie is running inside a specific room
     * @param acualTime CDateStorage: The day for the specific room
     * @param roomId	Long		: The specific room will be checked on the passed day
     */
    private void setFreeTime(CDateStorage acualTime, Long roomId){
        Collection<CEvent> events;
        Day day;
        Boolean tmp;
        Integer hour;
        Integer minute;
        CDateStorage cds;
        events = cinema_program.findEventsByRoom(roomId);
        int i;

        for (i = 0; i < 7; i++) {
            day = new Day();
            day.setDate(acualTime);
            for (hour = 0; hour < 24; hour++) {
                for (minute = 0; minute < 60; minute = minute + 5) {
                    tmp = false;
                    cds = new CDateStorage(
                            minute,
                            hour,
                            acualTime.getDay(),
                            acualTime.getMonth(),
                            acualTime.getYear()
                    );
                    for (CEvent event : events) {
                        if ((event.getO_startDate().erlierThen(cds)) && (event.getO_endDate().laterThan(cds))) {
                            tmp = true;
                        }
                    }
                    if (tmp) {
                        day.add(new Timestap(minute, hour, 3));
                    } else day.add(new Timestap(minute, hour, 1));
                }
            }
            collection.add(day);
            acualTime.addMinutes(24 * 60);
        }
    }


    //marktime schaut nach ob ein film dazwischen passt
    /**
     * The method checks if a film fits inside the time sectors
     * @param movie CMovie, the movie which will be checked if its length fits in the time
     */
    private void markTime(CMovie movie) {
        Collection<Timestap> mark = new ArrayList<>();
        Integer freeTime=0;
        Timestap temp;
        for (Day dy : collection){
            for (Timestap timestap : dy.getCollection()){
                if (timestap.getColorId().equals(Integer.valueOf(1))){
                    if (freeTime > movie.getRunTime()){
                        Iterator<Timestap> iterator = mark.iterator();
                        if (iterator.hasNext()){
                            temp = iterator.next();
                            temp.setColorId(Integer.valueOf(2));
                            mark.remove(temp);
                        }

                        mark.add(timestap);
                    } else mark.add(timestap);

                    freeTime = freeTime + 5;
                }else{
                    mark.clear();
                    freeTime = 0;
                }
            }
        }
    }

    /**
     * Validates the passed time. Checks if the passed time is ok/valid
     * @param starttime CDateStorage the start time which will be checked
     * @return false if starttime is earlier than the time sector true if time fits
     */
    public Boolean startTimeIsValid(CDateStorage starttime){
        if (starttime.erlierThen(acualTime))
            return false;


        for (Day day : collection){
            if ((day.getDate().getYear().equals(starttime.getYear()))&&
                    (day.getDate().getMonth().equals(starttime.getMonth()))&&
                    (day.getDate().getDay().equals(starttime.getDay()))){
                for (Timestap timestap : day.getCollection()){
                    if ((timestap.getHour().equals(starttime.getHour()))&&timestap.getMinute().equals(starttime.getMinute())) {
                        if (timestap.getColorId().equals(2)) {
                            return true;
                        } else return false;
                    }
                }
            }

        }
        return false;
    }


    public Collection<Day> getCollection() {
        return collection;
    }

    public Collection<String> getTime() {
        return time;
    }



    public void removeTimeToAcual(CDateStorage acualTime){
        for (Day day : collection){
            for (Timestap timestap : day.getCollection()){
                if (acualTime.laterThan(new CDateStorage(timestap.getMinute(), timestap.getHour(), day.getDate().getDay(), day.getDate().getMonth(), day.getDate().getYear()))){
                    timestap.setColorId(4);
                } else return;
            }
        }
    }
}
