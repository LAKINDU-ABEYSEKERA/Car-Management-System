package edu.icet.ecom.service.impl;

import edu.icet.ecom.exception.BusinessException;
import edu.icet.ecom.model.dto.CustomerDTO;
import edu.icet.ecom.model.dto.PaginatedResponse;
import edu.icet.ecom.model.entity.Customer;
import edu.icet.ecom.repository.CustomerRepository;
import edu.icet.ecom.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    // =========================================================
    // PAGINATION (Industry Standard)
    // =========================================================
    @Override
    public PaginatedResponse<CustomerDTO> getAllCustomers(int page, int size, String search) {
        log.info("Fetching customers - Page: {}, Size: {}, Search: '{}'", page, size, search);

        Pageable pageable = PageRequest.of(page, size, Sort.by("customerId").descending());
        Page<Customer> customerPage;

        // THE SEARCH LOGIC
        if (search != null && !search.trim().isEmpty()) {
            customerPage = customerRepository.findByCustomerNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);
        } else {
            customerPage = customerRepository.findAll(pageable);
        }

        List<CustomerDTO> dtoList = customerPage.getContent().stream().map(this::mapToDTO).toList();

        return PaginatedResponse.<CustomerDTO>builder()
                .content(dtoList)
                .pageNumber(customerPage.getNumber())
                .pageSize(customerPage.getSize())
                .totalElements(customerPage.getTotalElements())
                .totalPages(customerPage.getTotalPages())
                .isLast(customerPage.isLast())
                .build();
    }

    // =========================================================
    // CREATE
    // =========================================================
    @Override
    public CustomerDTO addCustomer(CustomerDTO dto) {
        log.info("Adding new customer: {}", dto.getCustomerName());

        Customer saved = customerRepository.save(mapToEntity(dto));
        return mapToDTO(saved);
    }

    // =========================================================
    // READ
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomer(String id) {
        Long actualId = extractId(id);
        Customer customer = customerRepository.findById(actualId)
                .orElseThrow(() -> new BusinessException("Customer not found with ID: " + id));

        return mapToDTO(customer);
    }

    // =========================================================
    // UPDATE
    // =========================================================
    @Override
    public CustomerDTO updateCustomer(CustomerDTO dto) {
        Long actualId = extractId(dto.getCustomerId());

        Customer existing = customerRepository.findById(actualId)
                .orElseThrow(() -> new BusinessException("Customer not found with ID: " + dto.getCustomerId()));

        existing.setCustomerName(dto.getCustomerName());
        existing.setAddress(dto.getAddress());
        existing.setEmail(dto.getEmail());

        Customer updated = customerRepository.save(existing);
        return mapToDTO(updated);
    }

    // =========================================================
    // DELETE
    // =========================================================
    @Override
    public CustomerDTO deleteCustomer(String id) {
        Long actualId = extractId(id);

        Customer existing = customerRepository.findById(actualId)
                .orElseThrow(() -> new BusinessException("Cannot delete. Customer not found with ID: " + id));

        customerRepository.delete(existing);
        return mapToDTO(existing);
    }

    // =========================================================
    // CUSTOMER ANALYTICS (Like Fleet Telemetry)
    // =========================================================
    @Override
    public Map<String, Object> getCustomerInsights() {
        List<Customer> customers = customerRepository.findAll();

        long totalCustomers = customers.size();
        long gmailUsers = customers.stream()
                .filter(c -> c.getEmail() != null && c.getEmail().contains("@gmail"))
                .count();

        long corporateUsers = customers.stream()
                .filter(c -> c.getEmail() != null && !c.getEmail().contains("@gmail"))
                .count();

        return Map.of(
                "totalCustomers", totalCustomers,
                "gmailUsers", gmailUsers,
                "corporateUsers", corporateUsers
        );
    }

    // =========================================================
    // PRIVATE HELPERS (CRITICAL FOR CLEAN CODE)
    // =========================================================

    private Customer mapToEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setCustomerName(dto.getCustomerName());
        customer.setAddress(dto.getAddress());
        customer.setEmail(dto.getEmail());
        return customer;
    }

    private CustomerDTO mapToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(formatId(customer.getCustomerId()));
        dto.setCustomerName(customer.getCustomerName());
        dto.setAddress(customer.getAddress());
        dto.setEmail(customer.getEmail());
        return dto;
    }

    private Long extractId(String formattedId) {
        if (formattedId == null || !formattedId.startsWith("C"))
            throw new BusinessException("Invalid Customer ID format");

        return Long.parseLong(formattedId.substring(1));
    }

    private String formatId(Long id) {
        return String.format("C%03d", id);
    }
}