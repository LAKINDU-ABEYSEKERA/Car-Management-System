package edu.icet.ecom.service.impl;

import edu.icet.ecom.model.dto.CarDTO;
import edu.icet.ecom.model.entity.Car;
import edu.icet.ecom.repository.CarRepository;
import edu.icet.ecom.service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional // Ensures database safety. If an error happens halfway through, it rolls back changes!
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Override
    public CarDTO addCar(CarDTO carDTO) {
        log.info("Translating CarDTO to Entity to save in database");
        Car car = mapToEntity(carDTO); // Ignore ID, let DB auto-generate

        Car savedCar = carRepository.save(car);

        return mapToDTO(savedCar); // Convert back and add the "CAR" prefix
    }

    @Override
    public CarDTO getCar(String id) {
        Long actualId = extractId(id);
        Car car = carRepository.findById(actualId).orElse(null);
        return car != null ? mapToDTO(car) : null;
    }

    @Override
    public CarDTO updateCar(CarDTO carDTO) {
        Long actualId = extractId(carDTO.getCarId());

        // Find the existing car first
        Car existingCar = carRepository.findById(actualId)
                .orElseThrow(() -> new RuntimeException("Car not found with ID: " + carDTO.getCarId()));

        // Update its fields
        existingCar.setBrand(carDTO.getBrand());
        existingCar.setModel(carDTO.getModel());
        existingCar.setSeatingCapacity(carDTO.getSeatingCapacity());
        existingCar.setFuelType(carDTO.getFuelType());
        existingCar.setPricePerDay(carDTO.getPricePerDay());
        existingCar.setStatus(carDTO.getStatus());

        Car updatedCar = carRepository.save(existingCar);
        return mapToDTO(updatedCar);
    }

    @Override
    public CarDTO deleteCar(String id) {
        Long actualId = extractId(id);
        Car existingCar = carRepository.findById(actualId).orElse(null);

        if (existingCar != null) {
            carRepository.delete(existingCar);
            return mapToDTO(existingCar);
        }
        return null;
    }

    // =========================================================================
    // PRIVATE HELPER METHODS (The Translators)
    // =========================================================================

    private Car mapToEntity(CarDTO dto) {
        Car car = new Car();
        // Notice we do NOT set the carId here. MySQL will auto-generate it!
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setSeatingCapacity(dto.getSeatingCapacity());
        car.setFuelType(dto.getFuelType());
        car.setPricePerDay(dto.getPricePerDay());
        car.setStatus(dto.getStatus());
        return car;
    }

    private CarDTO mapToDTO(Car car) {
        CarDTO dto = new CarDTO();
        // Convert pure number 1 to "CAR001"
        dto.setCarId(formatId(car.getCarId()));
        dto.setBrand(car.getBrand());
        dto.setModel(car.getModel());
        dto.setSeatingCapacity(car.getSeatingCapacity());
        dto.setFuelType(car.getFuelType());
        dto.setPricePerDay(car.getPricePerDay());
        dto.setStatus(car.getStatus());
        return dto;
    }

    private Long extractId(String formattedId) {
        if (formattedId == null || !formattedId.startsWith("CAR")) {
            throw new IllegalArgumentException("Invalid Car ID format. Must start with 'CAR'");
        }
        // Substring(3) chops off "CAR" leaving just the numbers
        return Long.parseLong(formattedId.substring(3));
    }

    private String formatId(Long id) {
        // Formats the pure number back into CAR001, CAR015, etc.
        return String.format("CAR%03d", id);
    }
}