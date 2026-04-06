package edu.icet.ecom.repository;

import edu.icet.ecom.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

//    @Query("""
//SELECT o FROM Orders o
//JOIN FETCH o.orderDetails
//""")
//    List<Order> findAllWithDetails();


}
