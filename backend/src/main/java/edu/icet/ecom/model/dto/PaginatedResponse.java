package edu.icet.ecom.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponse<T> {
    private List<T> content;      // The actual data (e.g., the 10 cars)
    private int pageNumber;       // Current page (e.g., Page 0)
    private int pageSize;         // How many items per page (e.g., 10)
    private long totalElements;   // Total cars in the entire database (e.g., 500)
    private int totalPages;       // Total pages available (e.g., 50)
    private boolean isLast;       // Is this the very last page?
}