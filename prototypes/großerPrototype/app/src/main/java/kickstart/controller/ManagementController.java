package kickstart.controller;

import kickstart.model.Menu.Menu;
import org.hsqldb.rights.UserManager;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

/**
 * Created by codemunin on 02.11.15.
 */

@Controller
public class ManagementController {

    @RequestMapping("/management.html")
    public String management(ModelMap modelMap, @LoggedIn Optional<UserAccount> userAccount) {

        if (userAccount.isPresent()) {
            UserAccount user = userAccount.get();

            if (user.hasRole(Role.of("ROLE_BOSS"))) {
                return "staff";
            }

            if (user.hasRole(Role.of("ROLE_CUSTOMER"))){
                return "account";
            }

            if (user.hasRole(Role.of("ROLE_EMPLOYEE"))){
                return "account";
            }

            if (user.hasRole(Role.of("ROLE_AUTHORIZED"))){
                return "account";
            }
        }

        return "login";
    }

    @RequestMapping("account.html")
    public String account(Model model, @LoggedIn Optional<UserAccount> userAccount){
        if (userAccount.isPresent()) {
            UserAccount user = userAccount.get();
                return "account";
        }
        return "login";
    }

    @RequestMapping("staff.html")
    public String staff(Model model, @LoggedIn Optional<UserAccount> userAccount){
        if (userAccount.isPresent()) {
            UserAccount user = userAccount.get();

            if (user.hasRole(Role.of("ROLE_BOSS"))) {
                return "staff";
            }
        }
        return "login";
    }

    @RequestMapping("rooms.html")
    public String rooms(Model model, @LoggedIn Optional<UserAccount> userAccount){
        if (userAccount.isPresent()) {
            UserAccount user = userAccount.get();

            if (user.hasRole(Role.of("ROLE_BOSS"))) {
                return "rooms";
            }
        }
        return "login";
    }
}
