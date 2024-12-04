package de.ydsgermany.herborder.shipment_receival;

import jakarta.annotation.Nonnull;

public record AggregatedHerbQuantityDto(

    @Nonnull
    Long herbId,

    @Nonnull
    Long quantity

) {

}
