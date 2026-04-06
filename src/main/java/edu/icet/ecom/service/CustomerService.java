package edu.icet.ecom.service;

import edu.icet.ecom.model.dto.CustomerDTO;
import edu.icet.ecom.model.entity.Customer;
import edu.icet.ecom.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service

public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerDTO getCustomer(String id) {
        if (id == null || !id.matches("C\\d{3,}")) {
            throw new IllegalArgumentException("Invalid Customer ID Format");
        }

        Long dbId = Long.parseLong(id.substring(1));

        Optional<Customer> customer = customerRepository.findById(dbId);

        if(customer.isPresent()){
            Customer cus = customer.get();

            return new CustomerDTO(
                    formatId(cus.getCustomerId()),
                    cus.getCustomerName(),
                    cus.getAddress(),
                    cus.getEmail()
            );
        }

        return null;
    }

    public CustomerDTO putCustomer(CustomerDTO customerDTO) {

        Customer customer = Customer.builder()
                        .customerName(customerDTO.getCustomerName())
                                .address(customerDTO.getAddress())
                                        .email(customerDTO.getEmail())
                                                .build();

        Customer saved  = customerRepository.save(customer);

        return new CustomerDTO(
                formatId(saved.getCustomerId()),
                saved.getCustomerName(),
                saved.getAddress(),
                saved.getEmail()
        );
    }



    public String formatId(Long id){
        return String.format("C%03d" , id);

    }

    @Transactional
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        long cusId =Long.parseLong(customerDTO.getCustomerId().substring(1));

        Customer customer = customerRepository.findById(cusId).orElseThrow(
                () -> new RuntimeException("Customer not Found!..")
        );

        customer.setCustomerName(customerDTO.getCustomerName());
        customer.setAddress(customerDTO.getAddress());
        customer.setEmail(customerDTO.getEmail());


        return new CustomerDTO(
                formatId(customer.getCustomerId()),
                customer.getCustomerName(),
                customer.getAddress(),
                customer.getEmail()
        );
    }

    public CustomerDTO deleteCustomer(String id) {
        Long dbId = Long.parseLong(id.substring(1));
        Optional<Customer> optionalCustomer = customerRepository.findById(dbId);

        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();

            customerRepository.delete(customer);

            return new CustomerDTO(
                    formatId(customer.getCustomerId()),
                    customer.getCustomerName(),
                    customer.getAddress(),
                    customer.getEmail()
            );
        }

        return null;
    }

//    public String idGen(){
//        List<Customer> customers = customerRepository.findAll();
//        int maxId = 1;
//        String id = "C001";
//
//        if (customers.isEmpty()){
//            return id;
//        }
//
//        for(Customer c : customers){
//            int num = Integer.parseInt(c.getCustomerId().substring(1));
//
//            if (num > maxId){
//                maxId = num;
//            }
//        }
//
//        return id = String.format("C%03d", maxId + 1);
//
//    }
}
