package edu.icet.ecom.service;

import edu.icet.ecom.model.dto.DriverDTO;
import java.util.List;

public interface DriverService {
    DriverDTO addDriver(DriverDTO driverDTO);
    DriverDTO getDriver(String driverId);
    List<DriverDTO> getAllDrivers();
    DriverDTO updateDriver(String driverId, DriverDTO driverDTO);
    void deleteDriver(String driverId);
}