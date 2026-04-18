package edu.icet.ecom.service.impl;

import edu.icet.ecom.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;
import edu.icet.ecom.model.dto.PaginatedResponse;

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
    public PaginatedResponse<CarDTO> getAllCars(int page, int size) {
        log.info("Fetching cars - Page: {}, Size: {}", page, size);

        // 1. Create the Pageable instructions (Sort by newest cars first)
        Pageable pageable = PageRequest.of(page, size, Sort.by("carId").descending());

        // 2. Fetch the secure Page from the database
        // (Remember, because of @SQLRestriction, this automatically hides deleted cars!)
        Page<Car> carPage = carRepository.findAll(pageable);

        // 3. Translate the List of Entities into a List of DTOs
        List<CarDTO> dtoList = carPage.getContent().stream()
                .map(this::mapToDTO)
                .toList();

        // 4. Wrap everything in our beautiful generic response
        return PaginatedResponse.<CarDTO>builder()
                .content(dtoList)
                .pageNumber(carPage.getNumber())
                .pageSize(carPage.getSize())
                .totalElements(carPage.getTotalElements())
                .totalPages(carPage.getTotalPages())
                .isLast(carPage.isLast())
                .build();
    }



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
        Car car = carRepository.findById(actualId).orElseThrow(() -> new BusinessException("Car not found with ID: " + id));
        return  mapToDTO(car);
    }

    @Override
    public CarDTO updateCar(CarDTO carDTO) {
        Long actualId = extractId(carDTO.getCarId());

        // Find the existing car first
        Car existingCar = carRepository.findById(actualId)
                .orElseThrow(() -> new BusinessException("Car not found with ID: " + carDTO.getCarId()));

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
        Car existingCar = carRepository.findById(actualId).orElseThrow(() -> new BusinessException("Cannot delete. Car not found with ID: " + id));

        carRepository.delete(existingCar);
        return mapToDTO(existingCar);
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
            throw new BusinessException("Invalid Car ID format. Must start with 'CAR'");
        }
        // Substring(3) chops off "CAR" leaving just the numbers
        return Long.parseLong(formattedId.substring(3));
    }

    private String formatId(Long id) {
        // Formats the pure number back into CAR001, CAR015, etc.
        return String.format("CAR%03d", id);
    }
}