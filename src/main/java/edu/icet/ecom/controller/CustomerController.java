package edu.icet.ecom.controller;

import edu.icet.ecom.model.dto.CustomerDTO;
import edu.icet.ecom.model.entity.Customer;
import edu.icet.ecom.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/customer")
@RestController
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/getCustomer/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable String id){
//        return customerService.getCustomer(id);
        CustomerDTO customerDTO = customerService.getCustomer(id);
        if(customerDTO != null)
            return ResponseEntity.status(HttpStatus.OK).body(customerDTO);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @PostMapping("/putCustomer")
    public CustomerDTO putCustomer(@RequestBody CustomerDTO customerDTO){
        return customerService.putCustomer(customerDTO);
    }

    @PostMapping("/updateCustomer")
    public CustomerDTO updateCustomer(@RequestBody CustomerDTO customerDTO){
        return customerService.updateCustomer(customerDTO);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCustomer(@PathVariable String id){
        try {
            CustomerDTO customerDTO = customerService.deleteCustomer(id);

            if (customerDTO != null) {
                return ResponseEntity.status(HttpStatus.OK).body("Customer deleted successfully: " + customerDTO.getCustomerName());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid ID format");
        }
    }
}
