package kickstart.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import javax.validation.Valid;

import kickstart.Repositorys.DBDate;
import kickstart.Repositorys.DBEvent;
import kickstart.SavedClasses.CMovie;
import kickstart.Repositorys.DBMovie;
import kickstart.model.eventManagement.CProgram;
import kickstart.validation.changeAFilmForm;
import kickstart.validation.rentAFilmForm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Responsible for renting movies from external firms/companies 
 * @author codemuin
 * @since 02.11.15
 */
@Controller
public class RentA_FilmController {

	private static final String MOVIE_IMAGES = "movieImages";
    private static final String TOMCAT_HOME_PROPERTY = "catalina.home";
    private static final String TOMCAT_HOME_PATH = System.getProperty(TOMCAT_HOME_PROPERTY);
    private static final String IMAGES_PATH = TOMCAT_HOME_PATH + File.separator + MOVIE_IMAGES;

    private static final File IMAGES_DIR = new File(IMAGES_PATH);
    private static final String IMAGES_DIR_PATH = IMAGES_DIR.getAbsolutePath() + File.separator;

    private DBMovie dbMovie;
	private CProgram program;

    /**
     * Creates the new Controller and passes the film whitch will be rented
     * @param dbMovie dbMovie Movie database
     * @param cProgram CProgram the program
     */
    @Autowired
    public RentA_FilmController(DBMovie dbMovie, CProgram cProgram){
        this.dbMovie = dbMovie;
		program = cProgram;
    }
    
    /**
     * Rents a new movie. Only the Boss or AuthEmp can rent it.
     * @param rentafilmform rentAFilmForm form managing the inputs
     * @param modelMap ModelMap Provided by Spring
     * @return Returns you to the rentafilm page or if fails(for example not auth. person/account) login page
     */
    @RequestMapping(value = "/rentafilm" , method = RequestMethod.GET)
    @PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED')")
    public String rentafilm(rentAFilmForm rentafilmform, ModelMap modelMap) {
        modelMap.addAttribute("rentafilmform",new rentAFilmForm());
        return "rentafilm";
    }

    /**
     * Boss or Authorized can add a new movie to the movie catalog.
     * @param form rentAFilmForm form managing the inputs
     * @param model Model provided by Spring
     * @param result BindingResult contains form errors
     * @param redirectAttributes provided by Spring
     * @return returns a Sting "success" if everything worked otherwise "login"
     * @throws IOException regular exception throw
     */
    
    @RequestMapping(value = "/rentafilm" , method = RequestMethod.POST)
    @PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED')")
    public String addfilm(@ModelAttribute("rentafilmform")  @Valid rentAFilmForm form, BindingResult result, RedirectAttributes redirectAttributes, Model model)  throws IOException {
    	if (result.hasErrors()) {
			return "rentafilm";
		}
    	if (form.getFile().isEmpty() || (!form.getFile().getContentType().equals("image/jpeg"))) {
    		model.addAttribute("fileerror", new String("fileerror")); 
    		return "rentafilm";
    	}
    	try {
    		if (!IMAGES_DIR.exists()) {
                IMAGES_DIR.mkdirs();
            }
            File image = new File(IMAGES_DIR_PATH + form.getFile().getOriginalFilename());
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(image));
            stream.write(form.getFile().getBytes());
            stream.close();
            
            String BC = form.getBaseCost().replace(",", ".");
            
            String R = form.getRent().replace(",", ".");

            if (image.getName().endsWith("jpeg")){
            	File rep = new File(IMAGES_DIR_PATH +image.getName().replaceAll("jpeg", "jpg"));
            	image.renameTo(rep);
            	CMovie movie = new CMovie(form.getTitle(), form.getDescription(), image.getName(), new Integer(form.getRunTime()), BC, R, form.getFsk());
            	dbMovie.save(movie);
            }else {
            	CMovie movie = new CMovie(form.getTitle(), form.getDescription(), image.getName(), new Integer(form.getRunTime()), BC, R, form.getFsk());
            	dbMovie.save(movie);
            }
        }catch (Exception e) {model.addAttribute("fileerror", new String("uploaderror")); return "rentafilm";}
        redirectAttributes.addFlashAttribute("succsesaMsg", new String("Film wurde erfolgreich erstellt."));
        return "redirect:/rentafilm";
    }

    /**
     * Method to change a rented movie
     * @param form changeAFilmForm form managing the inputs
     * @param modelMap Model provided by Spring
     * @param model Model provided by Spring
     * @param movie String associated movie's ID
     * @return changeafilm.html
     */
    
    @RequestMapping(value = "/changeafilm" , method = RequestMethod.GET)
    @PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED')")
    String changefilm(changeAFilmForm form, Model modelMap, Model model, @RequestParam("movie") String movie) {
    	Long ID = new Long(movie);
    	CMovie m1 = dbMovie.findById(ID);
    	form.setBaseCost(m1.getBaseCost().replace(".", ","));
    	form.setDescription(m1.getDescription());
    	form.setFsk(m1.getFsk());
    	form.setRent(m1.getRent().replace(".", ","));
    	form.setRunTime(m1.getRunTime().toString());
    	form.setTitle(m1.getName());
    	modelMap.addAttribute("form",form);
    	model.addAttribute("movie", movie);
    	return "changeafilm";
    }

    /**
     * 
     * @param form changeAFilmForm form managing the inputs
     * @param result BindingResult contains form errors
     * @param movieID String associated movie's ID
     * @param redirectAttributes provided by Spring
     * @return rentafilm
     */
    
    @RequestMapping(value = "/changeafilm" , method = RequestMethod.POST)
    @PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED')")
    public String editfilm(@ModelAttribute("form") @Valid changeAFilmForm form, BindingResult result, @RequestParam("movie") String movieID, RedirectAttributes redirectAttributes) {
    	if (result.hasErrors()) {
			return "changeafilm";
		}
    	Long ID = new Long(movieID);
    	CMovie oldmovie = dbMovie.findById(ID);
    	oldmovie.setBaseCost(form.getBaseCost().replace(",", "."));
    	oldmovie.setRent(form.getRent().replace(",", "."));
    	oldmovie.setDescription(form.getDescription());
    	oldmovie.setFsk(form.getFsk());
    	oldmovie.setName(form.getTitle());
    	oldmovie.setRunTime(new Integer(form.getRunTime()));
    	if (form.getIsRent()){
    		oldmovie.setIsRent(false);
    	}
    	dbMovie.save(oldmovie);
        redirectAttributes.addFlashAttribute("succsesaMsg", new String("Film wurde erfolgreich bearbeitet"));
        program.inizalize();
        return "redirect:/rentafilm";
    }
    
    /**
     * Method changing a movie's picture
     * @param file MultipartFile uploaded via html
     * @param movieID String of selected mvoie's ID
     * @param redirectAttributes Provided by Spring
     * @return rentafilm
     */
    
    @RequestMapping(value = "/changepicture" , method = RequestMethod.POST)
    @PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED')")
    public String changepicture(@RequestParam() MultipartFile file, @RequestParam("movie") String movieID, RedirectAttributes redirectAttributes) {
    	Long ID = new Long(movieID);
    	CMovie oldmovie = dbMovie.findById(ID);
    	if (!file.isEmpty()) {
			if (!file.getContentType().equals("image/jpeg")) {
				redirectAttributes.addFlashAttribute("fileerror", new String("fileerror")); 
				return "redirect:/changeprogram";
			}
	    	try {
	    		if (!IMAGES_DIR.exists()) {
	                IMAGES_DIR.mkdirs();
	            }
	    		File image = new File(IMAGES_DIR_PATH + file.getOriginalFilename());
	            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(image));
	            stream.write(file.getBytes());
	            stream.close();
	            if (image.getName().endsWith("jpeg")){
	            	File rep = new File(IMAGES_DIR_PATH +image.getName().replaceAll("jpeg", "jpg"));
	            	image.renameTo(rep);
	            	oldmovie.setImgPath(image.getName());
	            	dbMovie.save(oldmovie);
	            }else {
	            	oldmovie.setImgPath(image.getName());
	            	dbMovie.save(oldmovie);
	            }
	        }catch (Exception e) {redirectAttributes.addFlashAttribute("fileerror", new String("uploaderror")); return "redirect:/changeprogram";}
    	}
    	program.inizalize();
    	redirectAttributes.addFlashAttribute("succsesaMsg", new String("Bild wurde erfolgreich geändert"));
    	return "redirect:/rentafilm";
    }

    /**
     * handles requests to display uploaded images
     * @param imageName String Name of requested image file
     * @return image path
     * @throws IOException exception on faulty data transfer
     */
    
    @RequestMapping(value = "/image/{imageName}")
    @ResponseBody
    public byte[] getImage(@PathVariable(value = "imageName") String imageName) throws IOException {
    	if (!IMAGES_DIR.exists()) {
            IMAGES_DIR.mkdirs();
        }
        File serverFile = new File(IMAGES_DIR_PATH + imageName + ".jpg");
        return Files.readAllBytes(serverFile.toPath());
    }
}
