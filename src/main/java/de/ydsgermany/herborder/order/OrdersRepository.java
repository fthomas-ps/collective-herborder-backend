package de.ydsgermany.herborder.order;

import de.ydsgermany.herborder.global.ExternalIdRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Long>, ExternalIdRepository<Order> {

    @Query("SELECT new de.ydsgermany.herborder.order.AggregatedHerbQuantityDto(q.herb.id, SUM(q.quantity)) "
        + "FROM HerbQuantity q "
        + "GROUP BY q.herb.id")
    List<AggregatedHerbQuantityDto> aggregateOrders();

}
