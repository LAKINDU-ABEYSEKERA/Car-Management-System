package edu.icet.ecom.controller;

import edu.icet.ecom.model.dto.CustomerDTO;
import edu.icet.ecom.model.dto.PaginatedResponse;
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
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    // =========================================================
    // CREATE
    // =========================================================
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/addCustomer")
    public ResponseEntity<StandardResponse> createCustomer(
            @Valid @RequestBody CustomerDTO dto) {

        log.info("Creating customer: {}", dto.getCustomerName());

        CustomerDTO saved = customerService.addCustomer(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new StandardResponse(
                        201,
                        "Customer created successfully",
                        saved
                ));
    }

    // =========================================================
    // READ ONE
    // =========================================================
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/getCustomer/{id}")
    public ResponseEntity<StandardResponse> getCustomer(@PathVariable String id) {

        log.info("Fetching customer with ID: {}", id);

        CustomerDTO customer = customerService.getCustomer(id);

        return ResponseEntity.ok(
                new StandardResponse(
                        200,
                        "Customer retrieved successfully",
                        customer
                )
        );
    }

    // =========================================================
    // READ ALL (PAGINATION & SEARCH)
    // =========================================================
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/getAllCustomers")
    public ResponseEntity<StandardResponse> getAllCustomers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", defaultValue = "") String search) { // <-- Explicit names added!

        PaginatedResponse<CustomerDTO> response = customerService.getAllCustomers(page, size, search);

        return ResponseEntity.ok(new StandardResponse(200, "Success", response));
    }

    // =========================================================
    // UPDATE
    // =========================================================
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/updateCustomer/{id}")
    public ResponseEntity<StandardResponse> updateCustomer(
            @PathVariable String id,
            @Valid @RequestBody CustomerDTO dto) {

        log.info("Updating customer with ID: {}", id);

        dto.setCustomerId(id);
        CustomerDTO updated = customerService.updateCustomer(dto);

        return ResponseEntity.ok(
                new StandardResponse(
                        200,
                        "Customer updated successfully",
                        updated
                )
        );
    }

    // =========================================================
    // DELETE
    // =========================================================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteCustomer/{id}")
    public ResponseEntity<StandardResponse> deleteCustomer(@PathVariable String id) {

        log.warn("Deleting customer with ID: {}", id);

        CustomerDTO deleted = customerService.deleteCustomer(id);

        return ResponseEntity.ok(
                new StandardResponse(
                        200,
                        "Customer deleted successfully",
                        deleted.getCustomerId()
                )
        );
    }

    // =========================================================
    // ANALYTICS
    // =========================================================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/insights")
    public ResponseEntity<StandardResponse> getCustomerInsights() {

        log.info("Fetching customer insights");

        return ResponseEntity.ok(
                new StandardResponse(
                        200,
                        "Customer insights generated successfully",
                        customerService.getCustomerInsights()
                )
        );
    }
}