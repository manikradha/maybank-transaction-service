package com.maybank.transaction.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maybank.transaction.utils.SortDirection;
import com.maybank.transaction.utils.SortField;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Data
public class PaginationDTO {
    @Min(0)
    private int page = 0;

    @Max(100) @Min(1)
    private int size = 10;

    private SortField sortBy = SortField.TRX_DATE;
    private SortDirection direction = SortDirection.DESC;

    @JsonCreator
    public static PaginationDTO create(@JsonProperty("page") int page, @JsonProperty("size") int size, @JsonProperty("sortBy") String sortBy, @JsonProperty("direction") String direction) {
        PaginationDTO paginationDTO = new PaginationDTO();
        paginationDTO.setPage(page);
        paginationDTO.setSize(size);

        try {
            if (sortBy != null)
                paginationDTO.setSortBy(SortField.fromString(sortBy));

            if (direction != null)
                paginationDTO.setDirection(SortDirection.fromString(direction));

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return paginationDTO;
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size, Sort.by(direction == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy.getFieldName()));
    }
}
