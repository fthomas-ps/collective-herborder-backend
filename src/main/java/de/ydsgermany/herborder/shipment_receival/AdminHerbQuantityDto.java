package de.ydsgermany.herborder.shipment_receival;

import de.ydsgermany.herborder.order.HerbQuantity;
import jakarta.validation.constraints.NotNull;

public record AdminHerbQuantityDto(

    @NotNull(message = "Herb ID must not be null")
    Long herbId,

    @NotNull(message = "Quantity must not be null")
    Integer quantity,

    Integer packedQuantity

) {

    public static AdminHerbQuantityDto from(HerbQuantity herbQuantity) {
        return new AdminHerbQuantityDto(
            herbQuantity.getHerb().getId(),
            herbQuantity.getQuantity(),
            herbQuantity.getPackedQuantity());
    }

}
