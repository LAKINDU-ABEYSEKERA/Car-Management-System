package edu.icet.ecom.service;

import edu.icet.ecom.model.dto.CarDTO;

public interface CarService {
    CarDTO addCar(CarDTO carDTO);
    CarDTO getCar(String id);
    CarDTO updateCar(CarDTO carDTO);
    CarDTO deleteCar(String id);
}