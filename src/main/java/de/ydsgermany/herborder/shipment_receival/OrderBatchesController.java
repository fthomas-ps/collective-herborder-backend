package de.ydsgermany.herborder.shipment_receival;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/order_batches")
@Slf4j
public class OrderBatchesController {

    private final OrderBatchesAggregationRepository orderBatchesAggregationRepository;

    @Autowired
    public OrderBatchesController(
        OrderBatchesAggregationRepository orderBatchesAggregationRepository
    ) {
        this.orderBatchesAggregationRepository = orderBatchesAggregationRepository;
    }

    //@GetMapping(path = "/{externalOrderBatchId}/shipment_receival_stats")
    //public ResponseEntity<List<AggregatedHerbItemsDto>> getShipmentReceivalOverview(
    //    @PathVariable String externalOrderBatchId) {
    //    List<AggregatedHerbItemsDto> aggregatedHerbItemsDtos = orderBatchesAggregationRepository.aggregateOrders(
    //        externalOrderBatchId);
    //    return ResponseEntity.ok()
    //        .body(aggregatedHerbItemsDtos);
    //}

}
