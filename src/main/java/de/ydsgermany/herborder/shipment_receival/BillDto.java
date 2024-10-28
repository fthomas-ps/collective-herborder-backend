package de.ydsgermany.herborder.shipment_receival;

import jakarta.annotation.Nonnull;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record BillDto(

    Long id,

    @Nonnull
    LocalDate date,

    @Nonnull
    int vat,

    List<BillHerbItemDto> herbs

) {

    public static BillDto from(Bill bill) {
        return BillDto.builder()
            .id(bill.getId())
            .date(bill.getDate())
            .vat(bill.getVat())
            .herbs(herbsFrom(bill.getHerbs()))
            .build();
    }

    private static List<BillHerbItemDto> herbsFrom(List<BillHerbItem> herbs) {
        return herbs.stream()
            .map(BillHerbItemDto::from)
            .toList();
    }


}
