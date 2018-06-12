package kickstart.controller;

import kickstart.model.Menu.Menu;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by codemunin on 02.11.15.
 */

@Controller
public class CheckReservationController {

    @RequestMapping("/checkreservation.html")
    public String checkreservation(ModelMap modelMap) {

        return "checkreservation";
    }
}
