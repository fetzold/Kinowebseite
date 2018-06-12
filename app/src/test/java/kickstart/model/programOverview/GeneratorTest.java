package kickstart.model.programOverview;

import junit.framework.TestCase;
import kickstart.Application;
import kickstart.Repositorys.*;
import kickstart.SavedClasses.*;
import kickstart.model.eventManagement.CProgram;
import kickstart.model.eventManagement.CProgrampoint;
import kickstart.model.programOverView.Generator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.Iterator;

/**
 * Created by codemunin on 08.01.16.
 */



@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class GeneratorTest extends TestCase{
    private @Autowired
    UserAccountManager userAccountManager;
    private @Autowired
    CustomerRepository customerRepository;
    private @Autowired
    BusinessTime businessTime;
    private @Autowired
    DBRoom dbRoom;
    private @Autowired
    DBMovie dbMovie;
    private @Autowired
    DBDate dbDate;
    private @Autowired
    DBEvent dbEvent;
    private @Autowired DBRow dbRow;
    private @Autowired DBCRoom dbcRoom;
    private @Autowired DBSeat dbSeat;
    private @Autowired
    CProgram cProgram;
    private @Autowired CartRepo cartRepo;
    private @Autowired DBEmployee dbEmployee;

    Generator generator;
    CDateStorage cds1 = new CDateStorage(15,20,12,5,2016);

    LocalDateTime today = LocalDateTime.now();

    SaveRoom sr1 = new SaveRoom("SR1", "ppp-llll-pppp","2.20","3.30");
    SaveRoom sr2 = new SaveRoom("SR2", "ppp-pppp-pppp-pppppp","7.00","9.00");

    CRoom r1;
    CRoom r2;

    CMovie m2 = new CMovie("Boat", "Not a boat.", "bt", Integer.valueOf(120), "10.67", "1.10", "12");
    CMovie m3 = new CMovie("Forest", "Not a forest.", "frst", Integer.valueOf(90), "25", "3.10", "6");

    CEvent e2;
    CProgram cinema_program;

    CProgrampoint p1;
    CProgrampoint p2;


    @Before
    public void setUp(){
        sr1 = dbRoom.save(sr1);
        sr2 = dbRoom.save(sr2);
        cinema_program = new CProgram();
        cinema_program.setDbcRoom(dbcRoom);
        cinema_program.setDbEvent(dbEvent);
        cinema_program.setDbDate(dbDate);

        dbMovie.save(m2);
        m3.setIsRent(false);
        dbMovie.save(m3);

        CDateStorage cds2 = new CDateStorage(today.getMinute(),today.getHour(),today.getDayOfMonth(), today.getMonthValue(),today.getYear());
        cds2.subMinutes(20);

        cinema_program.createEvent(m2, sr1.getRoom(), sr1.getName(), sr1.getId(), cds1, false, dbRow, dbSeat);
        cinema_program.createEvent(m3, sr1.getRoom(), sr1.getName(), sr1.getId(), cds2, true, dbRow, dbSeat);

        e2 = cinema_program.findEventByID(Long.valueOf(2));

        CProgram helper = new CProgram();
        r1 = new CRoom(sr1.getName(), new Long(3), helper.createRows(sr1.getRoom(),dbRow,dbSeat));
        r2 = new CRoom(sr2.getName(), new Long(7), helper.createRows(sr2.getRoom(),dbRow,dbSeat));
        r1.creatRow(10, CSeat.Etype.Parkett);
        r1.changeSeatStatus(1, 3, CSeat.EStatus.RESERVED,null);

        Iterator<CProgrampoint> it = cinema_program.getProgramPoints().iterator();
        p1 = it.next();
        p2 = it.next();

        generator = new Generator(new CDateStorage(businessTime.getTime()), sr1.getId(), m2, cinema_program);
    }

    @Test
    public void testCreateTimes(){
        Integer h = 0;
        Integer m = 0;
        String tmp;
        for (String s : generator.getTime()){
            if (h < 10){
                tmp = "0" + h.toString() + ":";
            }else tmp = h.toString() + ":";
            if (m < 10){
                tmp = tmp + "0" + m.toString();
            }else tmp = tmp + m.toString();
            assertTrue("Time should equal " + tmp + " but was " + s, tmp.equals(s));
            m = m + 15;
            if (m >= 60){
                m = 0;
                h++;
            }
        }
    }
    
    @Test
    public void testStartTimeIsValid(){
    	CDateStorage cds = new CDateStorage(today.getMinute(),today.getHour(),today.getDayOfMonth(), today.getMonthValue(),today.getYear());
    	cds.subMinutes(60);
    	assertFalse("Given time should be invalid", generator.startTimeIsValid(cds));
    }
}
