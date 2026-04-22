package edu.icet.ecom.controller;

import edu.icet.ecom.model.dto.CarDTO;
import edu.icet.ecom.model.dto.PaginatedResponse;
import edu.icet.ecom.service.CarService;
import edu.icet.ecom.util.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/car")
public class CarController {

    // Injecting the service layer!
    private final CarService carService;

    @PostMapping("/addCar")
    public ResponseEntity<StandardResponse> addCar(@Valid @RequestBody CarDTO carDTO) {
        log.info("Request received to add new car: {} {}", carDTO.getBrand(), carDTO.getModel());

        CarDTO savedCar = carService.addCar(carDTO); // Assuming your service method is named addCar

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new StandardResponse(HttpStatus.CREATED.value(), "Car added successfully", savedCar));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/getCar/{id}")
    public ResponseEntity<StandardResponse> getCar(@PathVariable String id) {
        log.info("Fetching car with ID: {}", id);
        CarDTO carDTO = carService.getCar(id);

        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "Car retrieved successfully", carDTO)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/getAllCars")
    public ResponseEntity<StandardResponse> getAllCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Fetching all cars - Page: {}, Size: {}", page, size);
        PaginatedResponse<CarDTO> paginatedCars = carService.getAllCars(page, size);

        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "Cars retrieved successfully", paginatedCars)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/telemetry")
    public ResponseEntity<StandardResponse> getTelemetry() {
        Map<String, Object> stats = carService.getFleetTelemetry();
        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "Live telemetry retrieved", stats)
        );
    }





    @PutMapping("/updateCar")
    public ResponseEntity<StandardResponse> updateCar(@Valid @RequestBody CarDTO carDTO) {
        log.info("Updating car ID: {}", carDTO.getCarId());
        CarDTO updatedCar = carService.updateCar(carDTO);

        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "Car updated successfully", updatedCar)
        );
    }

    @DeleteMapping("/deleteCar/{id}")
    public ResponseEntity<StandardResponse> deleteCar(@PathVariable String id) {
        log.info("Attempting to delete car ID: {}", id);
        CarDTO deletedCar = carService.deleteCar(id);

        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "Car deleted successfully", deletedCar.getCarId())
        );
    }


}