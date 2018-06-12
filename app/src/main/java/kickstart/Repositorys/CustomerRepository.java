package kickstart.Repositorys;

import org.springframework.data.repository.CrudRepository;

import kickstart.SavedClasses.Customer;
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    public Customer findById(Long id);
}