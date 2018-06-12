package kickstart.model.eventManagement;

import kickstart.SavedClasses.CEvent;
import kickstart.SavedClasses.CMovie;

import java.util.*;


/**
 * Class saving seperate program points, as in collections of events for the same movie
 * @author codemuin
 * @since 21.11.15
 * 
 */
public class CProgrampoint {
    private List<CEvent> events;
    private CMovie movie;

    public CProgrampoint(CMovie movie){
        this.movie = movie;
        events = new ArrayList<CEvent>();
    }

    public void setMovie(CMovie movie) {
        this.movie = movie;
    }

    public CMovie getMovie() {
        return movie;
    }

    public String getName(){
        return movie.getName();
    }

    public void addEvent(CEvent event) {
        events.add(event);
        Comparator <CEvent> c = new Comparator<CEvent>() {
            @Override
            public int compare(CEvent o1, CEvent o2) {
                return o1.compareTo(o2);
            }
        };
        Collections.sort(events, c);

    }

    /**
     * Returns a Boolean whether or not the programpoint has a private event. It might
     * include more than one but will return true after the first hit.
     * @return true if yes, false if no
     */
    
    public Boolean includesPrivateEvents(){
        for (CEvent event : events){
            if (event.getIsPrivate()){
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a Boolean whether or not the programpoint has a non-private event. It might
     * include more than one but will return true after the first hit.
     * @return true if yes, false if no
     */
    
    public Boolean includesNonPrivateEvents(){
        for (CEvent event : events){
            if (!event.getIsPrivate()){
                return true;
            }
        }
        return false;
    }

    public Boolean removeEvent(CEvent event){
        return events.remove(event);
    }

    public Collection<CEvent> getEvents() {
        return events;
    }
}