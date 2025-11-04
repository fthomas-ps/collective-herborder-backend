package de.ydsgermany.herborder.shipment_receival;

import static java.lang.String.format;

import de.ydsgermany.herborder.order_batch.OrderBatch;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin/order_batches")
@Slf4j
public class OrderBatchesController {

    private final OrderBatchesAggregationRepository orderBatchesAggregationRepository;

    @Autowired
    public OrderBatchesController(
        OrderBatchesAggregationRepository orderBatchesAggregationRepository
    ) {
        this.orderBatchesAggregationRepository = orderBatchesAggregationRepository;
    }

    @GetMapping(path = "/{externalOrderBatchId}/stats")
    public ResponseEntity<List<AggregatedHerbItemsDto>> getShipmentReceivalOverview(
        @PathVariable String externalOrderBatchId) {
        OrderBatch orderBatch = orderBatchesAggregationRepository.findByExternalId(externalOrderBatchId)
            .orElseThrow(() -> new EntityNotFoundException(format("Order Batch %s not found", externalOrderBatchId)));
        List<AggregatedHerbItemsDto> aggregatedHerbItemsDtos = orderBatchesAggregationRepository.aggregateOrders(
            orderBatch.getId());
        return ResponseEntity.ok()
            .body(aggregatedHerbItemsDtos);
    }

    @GetMapping(path = "/{externalOrderBatchId}/missing-herbs")
    public ResponseEntity<List<MissingHerbsDto>> getMissingHerbs(
        @PathVariable String externalOrderBatchId) {
        OrderBatch orderBatch = orderBatchesAggregationRepository.findAll().stream().findFirst()
            .orElseThrow(() -> new EntityNotFoundException(format("Order Batch %s not found", externalOrderBatchId)));
        List<MissingHerbsDto> missingHerbsDto = orderBatchesAggregationRepository.findMissingHerbs(
            orderBatch.getId());
        return ResponseEntity.ok()
            .body(missingHerbsDto);
    }

}
