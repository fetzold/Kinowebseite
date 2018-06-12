package kickstart.Repositorys;

import kickstart.SavedClasses.CCart;
import kickstart.SavedClasses.CCart.CartType;

import org.salespointframework.core.SalespointRepository;

/**
 * Repository collecting every single completed reservation or purchase
 * @author Beatrice
 * @since 3.12.2015
 */

public interface CartRepo extends SalespointRepository<CCart, Long> {
    public CCart save(CCart cart);
    public Iterable<CCart> findByCartType(CartType cartType);
    
    public CCart findByResCode(String resCode);
}