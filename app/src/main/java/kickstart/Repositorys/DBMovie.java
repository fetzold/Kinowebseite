package kickstart.Repositorys;

import kickstart.SavedClasses.CMovie;
import org.springframework.data.repository.Repository;


/**
 * @author codemunin
 * @since  21.11.15.
 */


public interface DBMovie extends Repository<CMovie,Long>{
	public CMovie findById(Long id);

    public CMovie save(CMovie movie);

    public Iterable<CMovie> findAll();
}