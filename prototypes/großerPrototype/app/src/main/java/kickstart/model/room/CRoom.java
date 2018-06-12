package kickstart.model.room;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by codemunin on 13.11.15.
 */
public class CRoom {
    private Collection<CSeat> o_mySeat = new ArrayList<>();
    private String s_name;
    private Integer i_id;

    public CRoom (String s_name, Integer i_id){
        this.s_name = s_name;
        this.i_id = i_id;
    }

    public void creatRow(Integer rowNumber, Integer seats, CSeat.Etype e_type){
        for (Integer i = 1; i <= seats; i++){
            o_mySeat.add(new CSeat(rowNumber, i, e_type));
        }
    }

    @Override
    public String toString(){
        String s_room = "'";
        Integer accual_row = 1;
        CSeat tmp;

        Iterator<CSeat> seatIterator = o_mySeat.iterator();
        while (seatIterator.hasNext()){
            tmp = seatIterator.next();
            if (!accual_row.equals(tmp.getI_row())){
                s_room = s_room + "'\n'";
                accual_row = tmp.getI_row();
            }
            s_room = s_room + "a";
        }
        s_room = s_room + "'";
        return s_room;
    }
}
