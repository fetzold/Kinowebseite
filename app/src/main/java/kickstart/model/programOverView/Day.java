package kickstart.model.programOverView;

import kickstart.SavedClasses.CDateStorage;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by codemunin on 06.12.15.
 * Represents a single day
 */
public class Day {
    private CDateStorage date;
    private Collection<Timestap> collection = new ArrayList<>();

    /**
     * Converts a date from type CDateStorage into day, it takes the variables from it
     * @param date CDateStorage : Reads it content and saves it in the Day object
     */
    public void setDate(CDateStorage date) {
        this.date = new CDateStorage(date.getMinute(),
                date.getHour(),
                date.getDay(),
                date.getMonth(),
                date.getYear());
    }

    public CDateStorage getDate() {
        return date;
    }

    public void add (Timestap i){
        collection.add(i);
    }

    public Collection<Timestap> getCollection() {
        return collection;
    }
}
