package kickstart.SavedClasses;
import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.*;

import org.salespointframework.useraccount.UserAccount;

/**
 * Saves a single comment with all the relevant content, namely the commenter's name, the date,
 * their email and the corresponding movie's ID.
 * @author Felix, codemunin
 * @since 10.01.2016
 *
 */

@Entity
public class Comment implements Serializable {

    private static final long serialVersionUID = -7114101035786254953L;
    private @Id @GeneratedValue long id;

    private String text;
    private Integer rating;
    private String forname;
    private String lastname;
    private String email;
    private @ManyToOne CDateStorage date;
    private Long movieid;

    @SuppressWarnings("unused")
    private Comment() {}

    public Comment(String text, Integer rating, CDateStorage dateTime, String forname, String lastname, String email, Long movieid) {
        this.text = text;
        this.rating = rating;
        this.date = dateTime;
        this.forname = forname;
        this.lastname = lastname;
        this.email = email;
        this.movieid = movieid;
    }

    public long getId() {
            return id;
    }

    public String getText() {
            return text;
    }

    public LocalDateTime getDate() {
            return date.toLocalDateTime();
    }

    public int getRating() {
            return rating;
    }

    @Override
    public String toString() {
            return text;
    }

    public String getForname() {
        return forname;
    }

    public String getLastname() {
        return lastname;
    }

    public Long getMovieid() {
        return movieid;
    }

    public String getEmail() {
        return email;
    }
}

