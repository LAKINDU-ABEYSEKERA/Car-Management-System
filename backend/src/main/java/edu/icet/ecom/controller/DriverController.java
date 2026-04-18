package edu.icet.ecom.controller;

import edu.icet.ecom.model.dto.DriverDTO;
import edu.icet.ecom.service.DriverService;
import edu.icet.ecom.util.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    // =========================================================================
    // OPERATIONS FOR ALL EMPLOYEES (ADMIN & STAFF)
    // =========================================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping("/addDriver")
    public ResponseEntity<StandardResponse> addDriver(@Valid @RequestBody DriverDTO driverDTO) {
        log.info("Request received to add a new driver");
        DriverDTO savedDriver = driverService.addDriver(driverDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new StandardResponse(HttpStatus.CREATED.value(), "Driver added successfully", savedDriver));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/getDriver/{driverId}")
    public ResponseEntity<StandardResponse> getDriver(@PathVariable String driverId) {
        DriverDTO driver = driverService.getDriver(driverId);
        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "Driver retrieved successfully", driver)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/getAllDrivers")
    public ResponseEntity<StandardResponse> getAllDrivers() {
        List<DriverDTO> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "All drivers retrieved", drivers)
        );
    }

    // =========================================================================
    // RESTRICTED OPERATIONS (ADMIN ONLY)
    // =========================================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateDriver/{driverId}")
    public ResponseEntity<StandardResponse> updateDriver(
            @PathVariable String driverId,
            @Valid @RequestBody DriverDTO driverDTO) {

        log.info("Admin request to update driver: {}", driverId);
        DriverDTO updatedDriver = driverService.updateDriver(driverId, driverDTO);
        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "Driver updated successfully", updatedDriver)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteDriver/{driverId}")
    public ResponseEntity<StandardResponse> deleteDriver(@PathVariable String driverId) {
        log.info("Admin request to delete driver: {}", driverId);
        driverService.deleteDriver(driverId);
        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "Driver deleted successfully", null)
        );
    }
}