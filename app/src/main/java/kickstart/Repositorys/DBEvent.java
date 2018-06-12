package kickstart.Repositorys;

import kickstart.SavedClasses.CEvent;
import org.springframework.data.repository.Repository;

/**
 * Created by codemunin on 12.12.15.
 */
public interface DBEvent extends Repository<CEvent, Long> {
	public CEvent save (CEvent cEvent);
	public Iterable<CEvent> findAll();
	public CEvent findById(Long id);
	public Boolean delete(Long id);
}