package kickstart.controller;


import kickstart.Repositorys.*;
import kickstart.SavedClasses.CDateStorage;
import kickstart.SavedClasses.CMovie;
import kickstart.model.eventManagement.*;

import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import kickstart.SavedClasses.Comment;

/**
 * Program Controller. One of the most important classes. It is responsible for Program. Program is a Interface
 * between different classes and contains the logical data cinema events moviecatalog
 * @author codemunin
 * @since 02.11.15. 
 */
@Controller
public class ProgramController {
    private CProgram cinema_program;
    private BusinessTime businessTime;
    private DBMovie dbMovie;
    private DBComment dbComment;

    /**
     * The Programm Controller responsible for the management of all programms
     * @param businessTime	BuisnessTime : The SalesPoint Time
     * @param dbMovie		DBMovie  	 : Movie Database, stores all movies with ids atributes 
     * @param  dbComment DBComment comment database
     * @param  dbMovie DBMovie movie database
     * @param  dbDate DBDate CDateStorage database
     * @param  cProgram CProgram the program
     */
    @Autowired
    public ProgramController(DBComment dbComment, BusinessTime businessTime, DBMovie dbMovie, DBDate dbDate, CProgram cProgram){
        cinema_program = cProgram;
        this.businessTime = businessTime;
        this.dbMovie = dbMovie;
        this.dbComment = dbComment;
    }

    /**
     * Leads you to the program.html, actually an overview of all active programms
     * @param model Model provided by Spring
     * @return program.html
     */
    @RequestMapping("/program")
    public String program(Model model) {

    	//setting up list of all comments for valid programpoints,
    	//the actual sorting of comments is done in program.html
    	List<Comment> comment = new ArrayList<Comment>();
    	for (CProgrampoint cProgrampoint : cinema_program.getProgramPoints()){
    		
    		//setting up list to create non-persistent comment list for a specific
    		//movie, this allows calculation of rating
    		List<Comment> commentTemp = new ArrayList<>();
    		for (Comment c : dbComment.findAll()){
    			if (c.getMovieid().equals(cProgrampoint.getMovie().getId())){
    				comment.add(c);
    				commentTemp.add(c);
    			}
    		}
    		cProgrampoint.getMovie().setComments(commentTemp);
    	}
        
        cinema_program.updateProgramPoints(new CDateStorage(businessTime.getTime()));
        model.addAttribute("program", cinema_program.getProgramPoints());
        model.addAttribute("server", System.getProperty("catalina.home"));
        model.addAttribute("comment", comment);
        return "program";
    }
    
    /**
     * Responsible for changing the program. Only the logined boss/admin can change it
     * @param model Model provided by Spring
     * @return Return "changeprogram" get you back to changeprogram page
     */
    @RequestMapping("/changeprogram")
    @PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED')")
    public String changeprogram(Model model){
    	List<CMovie> mv = new ArrayList<CMovie>();
    	for (CMovie movie : dbMovie.findAll()){
    		if (movie.getIsRent()){
    			mv.add(movie);
    		}
    	}
        model.addAttribute("movies", mv);
    	return "changeprogram";
    }
}
