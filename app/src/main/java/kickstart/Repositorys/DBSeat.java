package kickstart.Repositorys;

import kickstart.SavedClasses.CSeat;
import org.springframework.data.repository.Repository;

/**
 * Created by codemunin on 12.12.15.
 */
public interface DBSeat extends Repository<CSeat, Long> {
	public CSeat save(CSeat cSeat);
}
