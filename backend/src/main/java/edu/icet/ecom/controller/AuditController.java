package edu.icet.ecom.controller;

import edu.icet.ecom.model.entity.SystemLog;
import edu.icet.ecom.service.AuditService;
import edu.icet.ecom.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") // The silent killer prevention!
public class AuditController {

    private final AuditService auditService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stream")
    public ResponseEntity<StandardResponse> getLogStream() {
        List<SystemLog> logs = auditService.getRecentLogs();
        return ResponseEntity.ok(new StandardResponse(HttpStatus.OK.value(), "Buffer Active", logs));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/force-refund")
    public ResponseEntity<StandardResponse> forceRefund(@RequestBody Map<String, Object> payload) {
        String account = (String) payload.get("accountId");
        Double amount = Double.valueOf(payload.get("amount").toString());

        auditService.processManualRefund(account, amount);

        return ResponseEntity.ok(new StandardResponse(HttpStatus.OK.value(), "Refund Executed", null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/clear")
    public ResponseEntity<StandardResponse> clearBuffer() {
        auditService.clearBuffer();
        return ResponseEntity.ok(new StandardResponse(HttpStatus.OK.value(), "Buffer Cleared", null));
    }
}