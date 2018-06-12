package kickstart.Repositorys;

import kickstart.SavedClasses.SaveRoom;
import org.hibernate.annotations.NamedQuery;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by codemunin on 28.11.15.
 */


public interface DBRoom extends Repository<SaveRoom, Long> {
	public SaveRoom save(SaveRoom room);

	public SaveRoom findById(Long id);

	public Iterable<SaveRoom> findAll();


	@Query("select a from SaveRoom a where active = 'yes'")
	public Iterable<SaveRoom> findAllActive();
}
