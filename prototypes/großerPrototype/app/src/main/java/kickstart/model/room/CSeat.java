package kickstart.model.room;

/**
 * Created by codemunin on 12.11.15.
 */
public class CSeat {
    private Integer i_row;
    private Integer i_number;
    private Etype e_Type;
    private EStatus e_Status;

    public static enum EStatus {
        DEFECTIVE, RESERVED, AVAILABLE, PAID;
    }
    public static enum Etype{
        Loge, Parkett;
    }

    public CSeat (Integer i_row, Integer i_number, Etype e_Type){
        this.i_row = i_row;
        this.i_number = i_number;
        this.e_Type = e_Type;
    }

    public Integer getI_number() {
        return i_number;
    }

    public Integer getI_row() {
        return i_row;
    }

    public void setI_number(Integer i_number) {
        this.i_number = i_number;
    }

    public void setI_row(Integer i_row) {
        this.i_row = i_row;
    }



}
