package de.ydsgermany.herborder.shipment_receival;

import de.ydsgermany.herborder.global.ExternalIdRepository;
import de.ydsgermany.herborder.order.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminOrdersRepository extends JpaRepository<Order, Long>, ExternalIdRepository<Order> {

    @Query("SELECT new de.ydsgermany.herborder.shipment_receival.AggregatedHerbQuantityDto(q.herb.id, SUM(q.quantity)) "
        + "FROM HerbQuantity q "
        + "GROUP BY q.herb.id")
    List<AggregatedHerbQuantityDto> aggregateOrders();

}
