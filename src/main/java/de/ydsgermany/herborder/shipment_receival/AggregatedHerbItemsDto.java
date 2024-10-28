package de.ydsgermany.herborder.shipment_receival;

import jakarta.annotation.Nonnull;

public record AggregatedHerbItemsDto(

    @Nonnull
    Long herbId,

    @Nonnull
    Long quantityOrders,

    @Nonnull
    Long quantityBill,

    @Nonnull
    Long quantityShipments

) {

}
