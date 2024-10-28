package de.ydsgermany.herborder.order;

import jakarta.annotation.Nonnull;

public record AggregatedHerbQuantityDto(

    @Nonnull
    Long herbId,

    @Nonnull
    Long quantity

) {

}
