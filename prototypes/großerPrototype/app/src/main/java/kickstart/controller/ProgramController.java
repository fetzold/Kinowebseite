package kickstart.controller;

import kickstart.model.Menu.Menu;
import kickstart.model.eventManagement.CEvent;
import kickstart.model.eventManagement.ICatalog;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

/**
 * Created by codemunin on 02.11.15.
 */
@Controller
public class ProgramController {

    private ICatalog catalog;
    private Menu menu;

    @Autowired
    ProgramController(ICatalog catalog){
        this.catalog = catalog;
        menu = new Menu();
    }

    @RequestMapping("/program.html")
    public String program(ModelMap modelMap) {
        modelMap.addAttribute("catalog", catalog.findAll());
        return "program";
    }

    @RequestMapping("/changeprogram.html")
    public String changeprogram(Model model, @LoggedIn Optional<UserAccount> userAccount){
        if (userAccount.isPresent()) {
            UserAccount user = userAccount.get();

            if ((user.hasRole(Role.of("ROLE_BOSS")))||(user.hasRole(Role.of("ROLE_AUTHORIZED")))) {
                model.addAttribute("catalog", catalog.findAll());
                return "changeprogram";
            }
        }

        return "login";
    }

    @RequestMapping("/event/{pid}")
    public String event(@PathVariable("pid")CEvent event, Model model) {

        model.addAttribute("menu", menu);
        model.addAttribute("event", event);

        return "event";
    }
}
