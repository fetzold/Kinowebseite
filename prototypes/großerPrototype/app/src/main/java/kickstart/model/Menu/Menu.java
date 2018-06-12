package kickstart.model.Menu;

import org.springframework.beans.factory.annotation.Autowired;

import javax.print.attribute.standard.Media;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by codemunin on 31.10.15.
 */
public class Menu {

    private Collection<MenuPoint> menu = new LinkedList<>();

    public Menu(){

        //Beispielmenüpunkte:
        MenuPoint home = new MenuPoint();
        MenuPoint login = new MenuPoint();
        MenuPoint statistics = new MenuPoint();
        MenuPoint program = new MenuPoint();
        MenuPoint stuff = new MenuPoint();
        MenuPoint checkreservation = new MenuPoint();
        MenuPoint rentafilm = new MenuPoint();
        MenuPoint register = new MenuPoint();

        rentafilm.setLink("rentafilm.html");
        rentafilm.setName("FILM MIETEN");
        checkreservation.setLink("checkreservation.html");
        checkreservation.setName("RESERVIERUNG\nPRÜFEN");
        stuff.setName("PERSONAL");
        stuff.setLink("staff.html");
        program.setName("PROGRAMM");
        program.setLink("program.html");
        statistics.setName("STATISTIK");
        statistics.setLink("statistic.html");
        home.setName("HOME");
        home.setLink("index.html");
        login.setName("LOGIN");
        login.setLink("login.html");
        register.setName("REGISTRIERUNG");
        register.setLink("register.html");
        
        
        menu.add(home);
        menu.add(program);
        menu.add(rentafilm);
        menu.add(checkreservation);
        menu.add(stuff);
        menu.add(statistics);
        menu.add(login);
        menu.add(register);
    }

    public Collection<MenuPoint> getmenu(){

        return menu;
    }

}
