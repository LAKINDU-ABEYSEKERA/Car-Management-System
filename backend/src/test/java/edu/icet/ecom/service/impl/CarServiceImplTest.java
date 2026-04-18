package edu.icet.ecom.service.impl;

import edu.icet.ecom.exception.BusinessException;
import edu.icet.ecom.model.dto.CarDTO;
import edu.icet.ecom.model.entity.Car;
import edu.icet.ecom.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// 1. Tell JUnit to enable Mockito!
@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    // 2. We create a FAKE Repository. It has no connection to MySQL.
    @Mock
    private CarRepository carRepository;

    // 3. We inject that FAKE repository into our REAL Service.
    @InjectMocks
    private CarServiceImpl carService;

    // ====================================================================
    // TEST 1: The Happy Path (Car is found)
    // ====================================================================
    @Test
    void getCar_WhenCarExists_ShouldReturnCarDTO() {
        // GIVEN (Arrange)
        String requestedId = "CAR001";
        Long extractedId = 1L;

        Car fakeCarFromDatabase = new Car();
        fakeCarFromDatabase.setCarId(extractedId);
        fakeCarFromDatabase.setBrand("Tesla");
        fakeCarFromDatabase.setModel("Model 3");
        fakeCarFromDatabase.setPricePerDay(15000.0);

        // We train the fake repository: "If anyone asks for ID 1, give them the Tesla."
        when(carRepository.findById(extractedId)).thenReturn(Optional.of(fakeCarFromDatabase));

        // WHEN (Act)
        // We actually execute the business logic
        CarDTO result = carService.getCar(requestedId);

        // THEN (Assert)
        // We verify the Service correctly translated the Entity into a DTO!
        assertNotNull(result);
        assertEquals("CAR001", result.getCarId());
        assertEquals("Tesla", result.getBrand());

        // We verify that the repository was called exactly once
        verify(carRepository, times(1)).findById(extractedId);
    }

    // ====================================================================
    // TEST 2: The Sad Path (Car is NOT found)
    // ====================================================================
    @Test
    void getCar_WhenCarDoesNotExist_ShouldThrowBusinessException() {
        // GIVEN (Arrange)
        String requestedId = "CAR999";
        Long extractedId = 999L;

        // We train the fake repository: "If anyone asks for ID 999, return absolutely nothing."
        when(carRepository.findById(extractedId)).thenReturn(Optional.empty());

        // WHEN & THEN (Act & Assert combined for Exceptions)
        // We verify that executing this logic triggers our custom BusinessException
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            carService.getCar(requestedId);
        });

        // We verify the exception message is perfectly accurate
        assertEquals("Car not found with ID: CAR999", exception.getMessage());

        verify(carRepository, times(1)).findById(extractedId);
    }
}