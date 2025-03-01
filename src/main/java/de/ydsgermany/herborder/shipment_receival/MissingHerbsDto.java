package de.ydsgermany.herborder.shipment_receival;

import jakarta.annotation.Nonnull;

public record MissingHerbsDto(

	@Nonnull
	String externalOrderId,
	
    @Nonnull
    String firstName,

    @Nonnull
    String lastName,

    @Nonnull
    Long herbId,

    @Nonnull
    String herbName,

    @Nonnull
    Integer quantityOrdered,

    @Nonnull
    Integer quantityShipped

) {

}
