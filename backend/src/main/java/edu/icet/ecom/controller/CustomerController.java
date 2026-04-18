package edu.icet.ecom.controller;

import edu.icet.ecom.model.dto.CustomerDTO;
import edu.icet.ecom.model.entity.Customer;
import edu.icet.ecom.service.CustomerService;
import edu.icet.ecom.util.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/customer")
@RestController
public class CustomerController {
    private final CustomerService customerService;

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/getCustomer/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable String id) {
//        return customerService.getCustomer(id);

        log.info("Fetching customer with ID: {}", id);
        CustomerDTO customerDTO = customerService.getCustomer(id);

        if (customerDTO == null) {
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            throw new RuntimeException("Customer not found with ID: " + id);
        }

        return new ResponseEntity<>(
                new StandardResponse(200, "Customer retrieved successfully", customerDTO),
                HttpStatus.OK
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping("/putCustomer")
    public ResponseEntity<StandardResponse> putCustomer(@Valid @RequestBody CustomerDTO customerDTO){
        log.info("Creating new customer: {}", customerDTO.getCustomerName());

        CustomerDTO savedCustomer = customerService.putCustomer(customerDTO);

       // return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
        //what if the customer doesnot exists
        return new ResponseEntity<>(
                new StandardResponse(201,"Customer created successfully", savedCustomer),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/updateCustomer")
    public ResponseEntity<StandardResponse> updateCustomer(@Valid @RequestBody CustomerDTO customerDTO){
        log.info("Updating customer ID: {}", customerDTO.getCustomerId());
        CustomerDTO updatedCus = customerService.updateCustomer(customerDTO);

        return new  ResponseEntity<>(
                new StandardResponse(200, "Customer updated successfully",updatedCus),
                HttpStatus.OK
                );

    }

    //why not add @valid to delete
    @DeleteMapping("deleteCustomer/{id}")
    public ResponseEntity<StandardResponse> deleteCustomer(@PathVariable String id){
//        try {
            log.info("Attempting to delete customer ID: {}", id);
            CustomerDTO customerDTO = customerService.deleteCustomer(id);

            if (customerDTO == null) {
                throw new RuntimeException("Can not delete. Customer notfound with ID: " + id);
            }

            return new ResponseEntity<>(
                    new StandardResponse(200, "Customer deleted successfully", customerDTO.getCustomerId()),
                    HttpStatus.OK
        );
//            if (customerDTO != null) {
               // return ResponseEntity.status(HttpStatus.OK).body("Customer deleted successfully: " + customerDTO.getCustomerName());
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//            }
//        } catch (Exception e){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid ID format");
//        }
    }
}
