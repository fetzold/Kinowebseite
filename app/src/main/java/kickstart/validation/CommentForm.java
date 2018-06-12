package kickstart.validation;

import javax.validation.constraints.Max;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by codemunin on 12.01.16.
 */
public class CommentForm {
	
	@NotBlank(message = "{comment.norating}")
	@Max(value= 10, message = "{comment.highrating}")
    private String rating;
    
    @NotBlank(message = "{Form.NotEmpty}")
    @Length(max = 300, message = "{comment.max}")
    private String comment;

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}
