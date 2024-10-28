package de.ydsgermany.herborder.shipment_receival;

import jakarta.annotation.Nonnull;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record ShipmentDto(

    Long id,

    @Nonnull
    LocalDate date,

    List<ShipmentHerbItemDto> herbs

) {

    public static ShipmentDto from(Shipment shipment) {
        return ShipmentDto.builder()
            .id(shipment.getId())
            .date(shipment.getDate())
            .herbs(herbsFrom(shipment.getHerbs()))
            .build();
    }

    private static List<ShipmentHerbItemDto> herbsFrom(List<ShipmentHerbItem> herbs) {
        return herbs.stream()
            .map(ShipmentHerbItemDto::from)
            .toList();
    }

}
