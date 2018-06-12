package kickstart.Repositorys;

import kickstart.SavedClasses.CCinemaTicket;
import kickstart.SavedClasses.CCinemaTicket.PriceClass;

import org.salespointframework.catalog.Catalog;

/**
 * Repository collecting every single sold ticket.
 * Returned, exchanged or reservations that timed
 * out will be removed.
 * @author Beatrice
 * @since 3.12.2015
 */

public interface TicketCatalog extends Catalog<CCinemaTicket> {
        //Iterable<CCinemaTicket> findByPriceClass(PriceClass priceClass);
	public CCinemaTicket findByPriceClass(PriceClass priceClass);
}