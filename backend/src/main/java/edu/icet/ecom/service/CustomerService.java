package edu.icet.ecom.service;

import edu.icet.ecom.model.dto.CustomerDTO;
import edu.icet.ecom.model.dto.PaginatedResponse;
import java.util.Map;

public interface CustomerService {

    CustomerDTO addCustomer(CustomerDTO dto);

    CustomerDTO getCustomer(String id);

    CustomerDTO updateCustomer(CustomerDTO dto);

    CustomerDTO deleteCustomer(String id);

    // The clean definition for our server-side pagination & search
    PaginatedResponse<CustomerDTO> getAllCustomers(int page, int size, String search);

    // Business analytics
    Map<String, Object> getCustomerInsights();
}