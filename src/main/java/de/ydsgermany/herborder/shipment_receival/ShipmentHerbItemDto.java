package de.ydsgermany.herborder.shipment_receival;

import jakarta.annotation.Nonnull;

public record ShipmentHerbItemDto(

    @Nonnull
    Long herbId,

    @Nonnull
    Long quantity

) {

    public static ShipmentHerbItemDto from(ShipmentHerbItem shipmentHerbItem) {
        return new ShipmentHerbItemDto(
            shipmentHerbItem.getHerb().getId(),
            shipmentHerbItem.getQuantity());
    }

}
