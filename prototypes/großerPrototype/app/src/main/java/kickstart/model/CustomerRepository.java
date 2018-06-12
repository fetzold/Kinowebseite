package kickstart.model;

import org.springframework.data.repository.CrudRepository;

import kickstart.model.Customer;
public interface CustomerRepository extends CrudRepository<Customer, Long> {}