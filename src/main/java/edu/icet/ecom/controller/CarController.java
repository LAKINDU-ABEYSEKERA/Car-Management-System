package edu.icet.ecom.controller;

import edu.icet.ecom.model.dto.CarDTO;
import edu.icet.ecom.service.CarService;
import edu.icet.ecom.util.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
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

    @GetMapping("/getCar/{id}")
    public ResponseEntity<StandardResponse> getCar(@PathVariable String id) {
        log.info("Fetching car with ID: {}", id);
        CarDTO carDTO = carService.getCar(id);

        if (carDTO == null) {
            throw new RuntimeException("Car not found with ID: " + id);
        }

        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "Car retrieved successfully", carDTO)
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

        if (deletedCar == null) {
            throw new RuntimeException("Cannot delete. Car not found with ID: " + id);
        }

        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "Car deleted successfully", deletedCar.getCarId())
        );
    }
}