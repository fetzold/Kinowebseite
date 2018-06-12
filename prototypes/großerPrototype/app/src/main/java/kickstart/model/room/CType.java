package kickstart.model.room;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by codemunin on 13.11.15.
 */
public class CType {
    private Collection<String> types = new ArrayList<>();

    public Boolean addtype (String type){
        if (!types.contains(type)) {
            types.add(type);
            return true;
        }
        else return false;
    }

    public Collection<String> getTypes() {
        return types;
    }
}

