package kickstart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by codemunin on 08.11.15.
 */

@Controller
public class CartController {

    @RequestMapping ("/cart.html")
    public String cart(ModelMap modelMap){
        return "cart";
    }

    @RequestMapping ("/reserve.html")
    public String reserve(Model model){
        return "reserve";
    }

    @RequestMapping ("/buy.html")
    public String buy(Model model){
        return "buy";
    }
}
