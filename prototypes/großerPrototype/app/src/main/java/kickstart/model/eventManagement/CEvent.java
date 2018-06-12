package kickstart.model.eventManagement;

import org.salespointframework.catalog.Product;

import javax.persistence.Entity;


/**
 * Created by codemunin on 05.11.15.
 */
@Entity
public class CEvent extends Product {
    private String link;


    public void setLink(String link) {
        this.link = link;
    }


    public String getLink() {
        return link;
    }
}