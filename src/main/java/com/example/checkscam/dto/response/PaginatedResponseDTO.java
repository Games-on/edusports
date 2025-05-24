package com.example.checkscam.dto.response;


import lombok.Data;

import java.util.List;

@Data
public class PaginatedResponseDTO<T> {
    private boolean success;
    private DataWrapper<T> data;

    @Data
    public static class DataWrapper<T> {
        private List<T> tournaments;
        private Pagination pagination;
    }

    @Data
    public static class Pagination {
        private int currentPage;
        private int totalPages;
        private long totalItems;
        private int itemsPerPage;
    }
}
