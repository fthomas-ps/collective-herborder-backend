package de.ydsgermany.herborder.order;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record OrderDto(

    String externalId,

    @NotNull
    String firstName,

    @NotNull
    String lastName,

    @NotNull
    String mail,

    @NotNull
    List<HerbQuantityDto> herbs

) {

    public static OrderDto from(Order order) {
        return OrderDto.builder()
            .externalId(order.getExternalId())
            .firstName(order.getFirstName())
            .lastName(order.getLastName())
            .mail(order.getMail())
            .herbs(herbsFrom(order.getHerbs()))
            .build();
    }

    private static List<HerbQuantityDto> herbsFrom(List<HerbQuantity> herbs) {
        return herbs.stream()
            .map(HerbQuantityDto::from)
            .toList();
    }


}
