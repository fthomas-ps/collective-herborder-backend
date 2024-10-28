package de.ydsgermany.herborder.order_batch;

import jakarta.annotation.Nonnull;
import java.util.List;
import lombok.Builder;

@Builder
public record OrderBatchDto(

    String externalId,

    @Nonnull
    String name,

    @Nonnull
    OrderState orderState

) {

    public static List<OrderBatchDto> from(List<OrderBatch> orderBatches) {
        return orderBatches
            .stream()
            .map(OrderBatchDto::from)
            .toList();
    }

    public static OrderBatchDto from(OrderBatch orderBatch) {
        return OrderBatchDto.builder()
            .externalId(orderBatch.getExternalId())
            .name(orderBatch.getName())
            .orderState(orderBatch.getOrderState())
            .build();
    }

}
