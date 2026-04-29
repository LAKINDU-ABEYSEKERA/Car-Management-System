package edu.icet.ecom.service;

import edu.icet.ecom.model.entity.SystemLog;
import java.util.List;

public interface AuditService {
    List<SystemLog> getRecentLogs();
    void logEvent(String level, String service, String message);
    void processManualRefund(String accountId, Double amount);
    void clearBuffer();
}