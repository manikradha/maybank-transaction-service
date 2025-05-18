package com.maybank.transaction.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AccountSearchDTO extends PaginationDTO {
    private @NotEmpty List<@NotEmpty String> accountNumbers;
}
