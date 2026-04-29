package edu.icet.ecom.repository;

import edu.icet.ecom.model.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

//    @Query("""
//SELECT o FROM Orders o
//JOIN FETCH o.orderDetails
//""")
//    List<Order> findAllWithDetails();
Page<Customer> findByCustomerNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email, Pageable pageable);

}
