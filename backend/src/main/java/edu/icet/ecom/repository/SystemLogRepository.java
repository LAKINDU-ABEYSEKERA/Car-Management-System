package edu.icet.ecom.repository;

import edu.icet.ecom.model.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    // Fetches the most recent 100 logs in chronological order
    List<SystemLog> findTop100ByOrderByTimestampAsc();
}