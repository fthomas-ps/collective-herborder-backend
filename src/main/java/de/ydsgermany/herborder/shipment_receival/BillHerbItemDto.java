package de.ydsgermany.herborder.shipment_receival;

import jakarta.annotation.Nonnull;

public record BillHerbItemDto(

    @Nonnull
    Long herbId,

    @Nonnull
    Integer unitPrice,

    @Nonnull
    Long quantity

) {

    public static BillHerbItemDto from(BillHerbItem billHerbItem) {
        return new BillHerbItemDto(
            billHerbItem.getHerb().getId(),
            billHerbItem.getUnitPrice(),
            billHerbItem.getQuantity());
    }

}
