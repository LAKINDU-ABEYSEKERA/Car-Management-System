package edu.icet.ecom.service;

import edu.icet.ecom.model.dto.CarDTO;
import edu.icet.ecom.model.dto.PaginatedResponse;

public interface CarService {
    CarDTO addCar(CarDTO carDTO);
    CarDTO getCar(String id);
    CarDTO updateCar(CarDTO carDTO);
    CarDTO deleteCar(String id);

    // NEW: The paginated fetch method!
    PaginatedResponse<CarDTO> getAllCars(int page, int size);
    java.util.Map<String, Object> getFleetTelemetry();
}