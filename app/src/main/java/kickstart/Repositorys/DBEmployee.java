/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kickstart.Repositorys;

import org.springframework.data.repository.CrudRepository;
import kickstart.SavedClasses.Employee;
/**
 *
 * @author felix
 */
public interface DBEmployee extends CrudRepository <Employee, Long> {
  //  Employee findBySalary(Long d_salary);
	public Iterable<Employee> findAll();
}
