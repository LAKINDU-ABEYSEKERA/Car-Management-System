package edu.icet.ecom.service.impl;

import edu.icet.ecom.model.entity.SystemLog;
import edu.icet.ecom.repository.SystemLogRepository;
import edu.icet.ecom.service.AuditService;
import edu.icet.ecom.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuditServiceImpl implements AuditService {

    private final SystemLogRepository logRepository;
    private final Random random = new Random();

    @Override
    @Transactional(readOnly = true)
    public List<SystemLog> getRecentLogs() {
        return logRepository.findTop100ByOrderByTimestampAsc();
    }

    @Override
    public void logEvent(String level, String service, String message) {
        String currentUser = SecurityUtil.getCurrentUserEmail(); // Automatically grabs who is logged in!

        SystemLog sysLog = SystemLog.builder()
                .eventId("EVT-" + (10000 + random.nextInt(90000)))
                .timestamp(LocalDateTime.now())
                .logLevel(level)
                .service(service)
                .message(message)
                .username(currentUser != null ? currentUser : "SYSTEM")
                .build();

        logRepository.save(sysLog);
    }

    @Override
    public void processManualRefund(String accountId, Double amount) {
        // Here is where you would normally interact with a Stripe/PayPal API
        log.warn("Executing manual override refund for {} amount {}", accountId, amount);

        logEvent("REFUND", "FINANCE_ENGINE",
                String.format("OVERRIDE: Manual $%.2f refund processed to account %s.", amount, accountId));
    }

    @Override
    public void clearBuffer() {
        logRepository.deleteAll();
        logEvent("CRITICAL", "SYS_ADMIN", "Global log buffer was manually purged.");
    }
}