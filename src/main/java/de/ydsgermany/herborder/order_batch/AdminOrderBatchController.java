package de.ydsgermany.herborder.order_batch;

import static java.lang.String.format;

import de.ydsgermany.herborder.global.ExternalIdGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin/order_batches")
@Slf4j
public class AdminOrderBatchController {

    private final AdminOrderBatchesRepository orderBatchesRepository;
    private final ExternalIdGenerator externalIdGenerator;

    @Autowired
    public AdminOrderBatchController(
        AdminOrderBatchesRepository orderBatchesRepository,
        @Qualifier("orderBatchesExternalIdGenerator") ExternalIdGenerator externalIdGenerator) {
        this.orderBatchesRepository = orderBatchesRepository;
        this.externalIdGenerator = externalIdGenerator;
    }

    @PostMapping(consumes = "application/json")
    @Transactional
    public ResponseEntity<OrderBatchDto> createOrderBatch(@RequestBody OrderBatchDto orderBatchDto) {
        OrderBatchDto savedOrderBatchDto = addOrUpdateOrderBatch(orderBatchDto, null);
        return ResponseEntity
            .created(URI.create("https://localhost:8080/orders/" + savedOrderBatchDto.externalId()))
            .body(savedOrderBatchDto);
    }

    @PutMapping(consumes = "application/json", path = "/{externalOrderBatchId}")
    @Transactional
    public ResponseEntity<OrderBatchDto> updateOrderBatch(@RequestBody OrderBatchDto orderBatchDto, @PathVariable String externalOrderBatchId) {
        OrderBatch foundOrder = orderBatchesRepository.findByExternalId(externalOrderBatchId)
            .orElseThrow(() -> new EntityNotFoundException(format("Order Batch %s not found", externalOrderBatchId)));
        OrderBatchDto savedOrderDto = addOrUpdateOrderBatch(orderBatchDto, foundOrder);
        return ResponseEntity
            .ok()
            .body(savedOrderDto);
    }

    private OrderBatchDto addOrUpdateOrderBatch(OrderBatchDto orderBatchDto, OrderBatch oldOrderBatch) {
        OrderBatch newOrderBatch = createOrderBatchFrom(orderBatchDto);
        if (oldOrderBatch == null) {
            newOrderBatch.setId(null);
            newOrderBatch.setExternalId(externalIdGenerator.generate());
            newOrderBatch.setOrderState(OrderState.CREATED);
        } else {
            newOrderBatch.setId(oldOrderBatch.getId());
            newOrderBatch.setExternalId(oldOrderBatch.getExternalId());
        }
        OrderBatch savedOrderBatch = orderBatchesRepository.save(newOrderBatch);
        return OrderBatchDto.from(savedOrderBatch);
    }

    public OrderBatch createOrderBatchFrom(OrderBatchDto orderBatchDto) {
        return OrderBatch.builder()
            .externalId(orderBatchDto.externalId())
            .name(orderBatchDto.name())
            .orderState(orderBatchDto.orderState())
            .build();
    }

    @GetMapping(path = "/{externalOrderBatchId}")
    public ResponseEntity<OrderBatchDto> getOrderBatch(@PathVariable String externalOrderBatchId) {
        OrderBatch orderBatch = orderBatchesRepository.findByExternalId(externalOrderBatchId)
            .orElseThrow(() -> new EntityNotFoundException(format("Order Batch %s not found", externalOrderBatchId)));
        OrderBatchDto orderBatchDto = OrderBatchDto.from(orderBatch);
        return ResponseEntity.ok()
            .body(orderBatchDto);
    }

    @GetMapping
    public ResponseEntity<List<OrderBatchDto>> getOrderBatches() {
        List<OrderBatch> orderBatches = orderBatchesRepository.findAll();
        List<OrderBatchDto> orderBatchDtos = OrderBatchDto.from(orderBatches);
        return ResponseEntity.ok()
            .body(orderBatchDtos);
    }

}
