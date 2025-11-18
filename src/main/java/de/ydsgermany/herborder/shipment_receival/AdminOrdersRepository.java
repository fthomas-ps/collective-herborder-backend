package de.ydsgermany.herborder.shipment_receival;

import de.ydsgermany.herborder.global.ExternalIdRepository;
import de.ydsgermany.herborder.order.Order;
import de.ydsgermany.herborder.order_batch.OrderBatch;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminOrdersRepository extends JpaRepository<Order, Long>, ExternalIdRepository<Order> {

    List<Order> findByOrderBatch(OrderBatch orderBatch);
}
