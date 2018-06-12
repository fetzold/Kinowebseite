package kickstart.Repositorys;

import kickstart.SavedClasses.CRow;
import org.springframework.data.repository.Repository;

/**
 * Created by codemunin on 12.12.15.
 */
public interface DBRow extends Repository<CRow, Long> {
	public CRow save (CRow cRow);
}
