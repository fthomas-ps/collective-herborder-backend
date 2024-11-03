package de.ydsgermany.herborder.shipment_receival;

import jakarta.validation.constraints.NotNull;

public record LoginDto(

    @NotNull
    String username,

    @NotNull
    String password

) {
}
