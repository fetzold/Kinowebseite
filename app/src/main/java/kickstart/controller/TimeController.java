package kickstart.controller;

import java.time.Duration;

import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Provides a simple interface for changing the server's system time
 * @author Beatrice
 * @since 09.01.2016
 */

@Controller
public class TimeController {
	
	private BusinessTime businessTime;
	
	@Autowired
    public TimeController(BusinessTime businessTime) {

        this.businessTime = businessTime;
    }

	/**
	 * Method displaying the input page
	 * @return timemachine
	 */
	
	@RequestMapping(value = "/timemachine", method = RequestMethod.GET)
	  public String time(){
	    return "timemachine";
	  }
	  
	/**
	 * Method changing the overall time received via BusinessTime fowards or backwards
	 * @param hrs String hours to change the time by, accepts positive or negative integers
	 * @param model Model provided by Spring
	 * @return timemachine.html
	 */
	
	@RequestMapping(value = "/timemachine", method = RequestMethod.POST)
	  public String changetime(@RequestParam("hours") String hrs, Model model){
		  if (!hrs.matches("\\d+") && !hrs.matches("\\-\\d+")){
			  model.addAttribute("error", hrs);
			  return "timemachine";
		  }
	    businessTime.forward(Duration.ofHours(Long.valueOf(hrs)));
	    model.addAttribute("success", hrs);
	    return "timemachine";
	  }
}
