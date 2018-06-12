package kickstart.controller;

import kickstart.model.Menu.Menu;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

/**
 * Created by codemunin on 02.11.15.
 */
@Controller
public class RentA_FilmController {

    @RequestMapping("/rentafilm.html")
    public String rentafilm(ModelMap modelMap, @LoggedIn Optional<UserAccount> userAccount) {

        if (userAccount.isPresent()) {
            UserAccount user = userAccount.get();

            if ((user.hasRole(Role.of("ROLE_BOSS")))||(user.hasRole(Role.of("ROLE_AUTHORIZED")))) {
                return "rentafilm";
            }
        }

        return "login";
    }
}
