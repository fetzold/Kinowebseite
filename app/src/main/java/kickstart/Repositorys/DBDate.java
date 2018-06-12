package kickstart.Repositorys;

import kickstart.SavedClasses.CDateStorage;
import org.springframework.data.repository.Repository;

/**
 * Created by codemunin on 12.12.15.
 */
public interface DBDate extends Repository<CDateStorage, Long> {
	public CDateStorage save (CDateStorage cDateStorage);
}
