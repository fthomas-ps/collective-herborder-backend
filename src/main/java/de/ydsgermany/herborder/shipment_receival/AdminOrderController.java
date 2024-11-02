package de.ydsgermany.herborder.shipment_receival;

import de.ydsgermany.herborder.order.AggregatedHerbQuantityDto;
import de.ydsgermany.herborder.order.OrderDto;
import de.ydsgermany.herborder.order.OrdersRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin/orders")
@Slf4j
public class AdminOrderController {

    private final OrdersRepository ordersRepository;

    @Autowired
    public AdminOrderController(
        OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getHerbOrders() {
        List<OrderDto> orderDtos = ordersRepository.findAll()
            .stream()
            .map(OrderDto::from)
            .toList();
        return ResponseEntity.ok()
            .body(orderDtos);
    }

    @GetMapping(path = "/aggregated")
    public ResponseEntity<List<AggregatedHerbQuantityDto>> getAggregatedOrder() {
        List<AggregatedHerbQuantityDto> herbQuantities = ordersRepository.aggregateOrders();
        return ResponseEntity
            .ok(herbQuantities);
    }

}
