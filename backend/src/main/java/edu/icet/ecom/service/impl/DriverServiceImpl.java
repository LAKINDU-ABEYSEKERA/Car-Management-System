package edu.icet.ecom.service.impl;

import edu.icet.ecom.exception.BusinessException;
import edu.icet.ecom.model.dto.DriverDTO;
import edu.icet.ecom.model.entity.Driver;
import edu.icet.ecom.model.enums.DriverStatus;
import edu.icet.ecom.repository.DriverRepository;
import edu.icet.ecom.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    @Override
    public DriverDTO addDriver(DriverDTO request) {
        log.info("Adding new driver: {}", request.getName());

        Driver driver = new Driver();
        driver.setName(request.getName());
        driver.setLicenceNo(request.getLicenceNo());
        driver.setStatus(DriverStatus.AVAILABLE); // Default status on creation

        Driver savedDriver = driverRepository.save(driver);
        return mapToDTO(savedDriver);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverDTO getDriver(String driverId) {
        Long actualId = extractId(driverId, "D");
        Driver driver = driverRepository.findById(actualId)
                .orElseThrow(() -> new BusinessException("Driver not found with ID: " + driverId));
        return mapToDTO(driver);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverDTO> getAllDrivers() {
        return driverRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DriverDTO updateDriver(String driverId, DriverDTO request) {
        log.info("Admin updating driver: {}", driverId);
        Long actualId = extractId(driverId, "D");

        Driver existingDriver = driverRepository.findById(actualId)
                .orElseThrow(() -> new BusinessException("Driver not found with ID: " + driverId));

        existingDriver.setName(request.getName());
        existingDriver.setLicenceNo(request.getLicenceNo());
        if (request.getStatus() != null) {
            existingDriver.setStatus(request.getStatus());
        }

        Driver updatedDriver = driverRepository.save(existingDriver);
        return mapToDTO(updatedDriver);
    }

    @Override
    public void deleteDriver(String driverId) {
        log.info("Admin deleting driver: {}", driverId);
        Long actualId = extractId(driverId, "D");

        if (!driverRepository.existsById(actualId)) {
            throw new BusinessException("Driver not found with ID: " + driverId);
        }
        driverRepository.deleteById(actualId);
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    private DriverDTO mapToDTO(Driver driver) {
        DriverDTO dto = new DriverDTO();
        dto.setDriverId(String.format("D%03d", driver.getDriverId()));
        dto.setName(driver.getName());
        dto.setLicenceNo(driver.getLicenceNo());
        dto.setStatus(driver.getStatus());
        return dto;
    }

    private Long extractId(String formattedId, String prefix) {
        if (formattedId == null || !formattedId.startsWith(prefix)) {
            throw new BusinessException("Invalid ID format. Must start with '" + prefix + "'");
        }
        return Long.parseLong(formattedId.substring(prefix.length()));
    }
}