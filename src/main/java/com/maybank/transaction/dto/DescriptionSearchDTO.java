package com.maybank.transaction.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DescriptionSearchDTO extends PaginationDTO {
    @NotEmpty
    private String description;
}
