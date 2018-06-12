package kickstart.model.eventManagement;

import kickstart.Repositorys.*;
import kickstart.SavedClasses.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
* @author codemunin
* @since  21.11.15.
* Represents all events. Create new ones, delete old, the whole administration of the events
*/

@Component
public class CProgram {
    private Collection<CProgrampoint> programPoints = new ArrayList<>();
    private DBEvent dbEvent;
    private DBDate dbDate;
    private DBCRoom dbcRoom;
    private DBMovie dbMovie;
    
    public CProgram(){}

    /**
     * Initialization method, won't work without it
     */
    public void inizalize(){
        programPoints.clear();
        for (CEvent event : dbEvent.findAll()){
            this.addEvent(event);
        }
    }

    public Collection<CProgrampoint> getProgramPoints() {
        return programPoints;
    }

    /**
     * Update function. Throws all passed events out
     * @param acualTime CDateStorage, the current time
     */
    public void updateProgramPoints(CDateStorage acualTime){
        Collection<CProgrampoint> c1 = new ArrayList<>(programPoints);
        for (CProgrampoint programpoint : c1){
            //programpoint.setMovie(dbMovie.findById(programpoint.getMovie().getId()));
            Collection<CEvent> c2 = new ArrayList<>(programpoint.getEvents());
            for (CEvent event : c2){
                if (event.getO_startDate().erlierThen(acualTime)){
                   programpoint.removeEvent(event);

                }
            }
            if (programpoint.getEvents().isEmpty()){
                programPoints.remove(programpoint);
            }
        }
    }

    /**
     * Searches for the events by the room id
     * @param roomId Long : The room id that represent the room 
     * @return returns the events
     */
    public Collection<CEvent> findEventsByRoom(Long roomId){
        CEvent event;
        Collection<CEvent> collection = new ArrayList<>();
        Iterator<CProgrampoint> i1;
        Iterator i2;
        CProgrampoint programpoint;
        i1 = programPoints.iterator();

        while (i1.hasNext()) {
            programpoint = (CProgrampoint) i1.next();
            i2 = programpoint.getEvents().iterator();
            while (i2.hasNext()){
                event = (CEvent) i2.next();
                if (event.getO_room().getI_id().equals(roomId))
                    collection.add(event);
            }
        }
        return collection;
    }


    /**
     * Searches the event by its ID
     * @param id Long the identification number of the event
     * @return returns the event
     */
    public CEvent findEventByID(Long id){
        return dbEvent.findById(id);
    }

    /**
     * Create a new Event
     * @param movie	CMovie The movie for the event
     * @param room Integer The room for the event
     * @param startTime CDateStorage the start time of the movie
     * @param roomName	The name of the room
     * @param roomId	The room Id
     * @param dbRow DBRow Row Database
     * @param dbSeat DBSeat Seat Database
     * @param isPrivateB Boolean, is it a public event or a private "ordered" by a customer
     * @return successfull creation = true, it fails = false
     */


    public Boolean createEvent(CMovie movie, String room, String roomName, Long roomId, CDateStorage startTime, Boolean isPrivateB, DBRow dbRow, DBSeat dbSeat)
    {
        CDateStorage endTime = dbDate.save(new CDateStorage(startTime));
        endTime.addMinutes(movie.getRunTime() + Integer.valueOf(30));
        startTime = dbDate.save(startTime);
        CEvent event = new CEvent(startTime, endTime, movie, dbcRoom.save(new CRoom(roomName, roomId, createRows(room, dbRow, dbSeat))));
        event.setIsPrivate(isPrivateB);
        dbEvent.save(event);

        return this.addEvent(event);
    }

    /**
     * Adds the passed event to the program
     * @param event CEvent , the event that is to add
     * @return successfull creation = true, it fails = false
     */

    public Boolean addEvent(CEvent event){
        CMovie movie = event.getMovie();
        Boolean readyExisting = false;
        Iterator<CProgrampoint> it = programPoints.iterator();
        CProgrampoint programpoint = null;
        while (it.hasNext()){
            programpoint = (CProgrampoint) it.next();
            if (programpoint.getMovie().getId().equals(movie.getId())){
                programpoint.addEvent(event);
                return true;
            }
        }

        programpoint = new CProgrampoint(movie);
        programpoint.addEvent(event);
        programPoints.add(programpoint);
        return true;
    }
    
    /**
     * method generating the row collection for a given room
     * @param s_room String the room layout
     * @param dbRow DBRow row database
     * @param dbSeat DBSeat seat database
     * @return myRows
     */

    public Collection<CRow> createRows (String s_room, DBRow dbRow, DBSeat dbSeat){
        Collection<CRow> myRows = new ArrayList<>();
        char[] a_room = s_room.toCharArray();
        CRow row = new CRow(0,true);
        int rownum = 1;
        int seatnum = 1;

        for (char tmp : a_room){

            if (tmp =='-'){
                row = dbRow.save(new CRow(rownum, false));
                myRows.add(row);
                rownum++;
                seatnum = 1;
            }

            if (tmp == 'p'){
                row.addSeat(dbSeat.save(new CSeat(rownum, seatnum, CSeat.Etype.Parkett)));	//Cheapest seat gets only basicCharge
                seatnum++;
            }

            if (tmp == 'l'){
                row.addSeat(dbSeat.save(new CSeat(rownum, seatnum, CSeat.Etype.Loge)));	//The expensive one gets basic and extra charge
                seatnum++;
            }

            // Actually maybe not correct because we have no idea what is was originally we should change "Defect" to a bool variable so we can keep the original type "Loge" or "Baguette" -alex
            if (tmp == 'd'){
                row.addSeat(dbSeat.save(new CSeat(rownum, seatnum, CSeat.Etype.DEFECTIVE)));
                seatnum++;
            }


        }
        return myRows;
    }

    public void setDbEvent(DBEvent dbEvent) {
        this.dbEvent = dbEvent;
    }
    public void setDbDate(DBDate dbDate) {
        this.dbDate = dbDate;
    }
    public void setDbcRoom(DBCRoom dbcRoom) {
        this.dbcRoom = dbcRoom;
    }
    
    public void setDBMovie(DBMovie dbMovie) {
        this.dbMovie = dbMovie;
    }
}