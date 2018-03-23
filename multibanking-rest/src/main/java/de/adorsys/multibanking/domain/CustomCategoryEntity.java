package de.adorsys.multibanking.domain;

import lombok.Data;

@Data
public class CustomCategoryEntity extends CategoryEntity {
    private String userId;
    private boolean released;
}
