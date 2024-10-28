package de.ydsgermany.herborder.order;

import jakarta.validation.constraints.NotNull;

public record HerbQuantityDto(

    @NotNull(message = "Herb ID must not be null")
    Long herbId,

    @NotNull(message = "Quantity must not be null")
    Integer quantity

) {

    public static HerbQuantityDto from(HerbQuantity herbQuantity) {
        return new HerbQuantityDto(
            herbQuantity.getHerb().getId(),
            herbQuantity.getQuantity());
    }

}
