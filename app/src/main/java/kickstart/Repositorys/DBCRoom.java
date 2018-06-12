package kickstart.Repositorys;

import kickstart.SavedClasses.CRoom;
import org.springframework.data.repository.Repository;

/**
 * Created by codemunin on 12.12.15.
 */
public interface DBCRoom extends Repository<CRoom, Long> {
	public CRoom save (CRoom cRoom);
}
