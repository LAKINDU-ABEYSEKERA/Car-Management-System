package edu.icet.ecom.service.impl;

import edu.icet.ecom.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Map;
import edu.icet.ecom.model.dto.PaginatedResponse;

import edu.icet.ecom.model.dto.CarDTO;
import edu.icet.ecom.model.entity.Car;
import edu.icet.ecom.model.enums.CarStatus;
import edu.icet.ecom.repository.CarRepository;
import edu.icet.ecom.service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Override
    public PaginatedResponse<CarDTO> getAllCars(int page, int size) {
        log.info("Fetching cars - Page: {}, Size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("carId").descending());
        Page<Car> carPage = carRepository.findAll(pageable);

        List<CarDTO> dtoList = carPage.getContent().stream().map(this::mapToDTO).toList();

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
        Car car = mapToEntity(carDTO);
        Car savedCar = carRepository.save(car);
        return mapToDTO(savedCar);
    }

    @Override
    public CarDTO getCar(String id) {
        Long actualId = extractId(id);
        Car car = carRepository.findById(actualId).orElseThrow(() -> new BusinessException("Car not found with ID: " + id));
        return mapToDTO(car);
    }

    @Override
    public CarDTO updateCar(CarDTO carDTO) {
        Long actualId = extractId(carDTO.getCarId());
        Car existingCar = carRepository.findById(actualId)
                .orElseThrow(() -> new BusinessException("Car not found with ID: " + carDTO.getCarId()));

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
    // NEW: THE GLOBAL TELEMETRY ENGINE
    // =========================================================================
    @Override
    public Map<String, Object> getFleetTelemetry() {
        List<Car> allCars = carRepository.findAll();

        long totalFleet = allCars.size();
        long activeHolds = allCars.stream().filter(c -> c.getStatus() == CarStatus.BOOKED).count();
        double dailyRevenue = allCars.stream()
                .filter(c -> c.getStatus() == CarStatus.BOOKED)
                .mapToDouble(Car::getPricePerDay)
                .sum();

        long utilizationRate = totalFleet == 0 ? 0 : Math.round(((double) activeHolds / totalFleet) * 100);

        return Map.of(
                "totalFleet", totalFleet,
                "activeHolds", activeHolds,
                "utilizationRate", utilizationRate,
                "dailyRevenue", dailyRevenue
        );
    }

    // =========================================================================
    // PRIVATE HELPER METHODS
    // =========================================================================
    private Car mapToEntity(CarDTO dto) {
        Car car = new Car();
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
        if (formattedId == null || !formattedId.startsWith("CAR")) throw new BusinessException("Invalid format.");
        return Long.parseLong(formattedId.substring(3));
    }

    private String formatId(Long id) { return String.format("CAR%03d", id); }
}