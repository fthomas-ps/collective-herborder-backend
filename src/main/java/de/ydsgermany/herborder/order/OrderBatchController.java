package de.ydsgermany.herborder.order;

import static java.lang.String.format;

import de.ydsgermany.herborder.order_batch.OrderBatch;
import de.ydsgermany.herborder.order_batch.OrderBatchDto;
import jakarta.persistence.EntityNotFoundException;
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
public class OrderBatchController {

    private final OrderBatchesRepository orderBatchesRepository;

    @Autowired
    public OrderBatchController(
        OrderBatchesRepository orderBatchesRepository) {
        this.orderBatchesRepository = orderBatchesRepository;
    }

    @GetMapping(path = "/{externalOrderBatchId}")
    public ResponseEntity<OrderBatchDto> getOrderBatch(@PathVariable String externalOrderBatchId) {
        OrderBatch orderBatch = orderBatchesRepository.findAll().stream().findFirst()
            .orElseThrow(() -> new EntityNotFoundException(format("Order Batch %s not found", externalOrderBatchId)));
        OrderBatchDto orderBatchDto = OrderBatchDto.from(orderBatch);
        return ResponseEntity.ok()
            .body(orderBatchDto);
    }

}
