package de.ydsgermany.herborder.shipment_receival;

import de.ydsgermany.herborder.order_batch.OrderBatch;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderBatchesAggregationRepository extends JpaRepository<OrderBatch, Long> {

    @Query("SELECT new de.ydsgermany.herborder.shipment_receival.AggregatedHerbItemsDto(oa.herb_id, oa.quantity, bi.quantity, sa.quantity) "
        + "FROM "
        + "  Order o "
        + "  JOIN ("
        + "    SELECT"
        + "      oi.herb.id AS herb_id,"
        + "      SUM(oi.quantity) AS quantity "
        + "    FROM HerbQuantity oi "
        + "    WHERE oi.order = o "
        + "    GROUP BY oi.herb.id "
        + "  ) oa"
        + "  JOIN Bill b ON :externalOrderBatchId = b.orderBatch.externalId"
        + "  JOIN BillHerbItem bi ON b = bi.bill AND oa.herb_id = bi.herb.id"
        + "  JOIN Shipment s ON :externalOrderBatchId = s.orderBatch.externalId "
        + "  JOIN ( "
        + "    SELECT "
        + "      si.herb.id AS herb_id, "
        + "      SUM(si.quantity) AS quantity "
        + "    FROM ShipmentHerbItem si "
        + "    WHERE si.shipment = s "
        + "    GROUP BY si.herb.id"
        + "  ) sa ON oa.herb_id = sa.herb_id"
    )
    List<AggregatedHerbItemsDto> aggregateOrders(String externalOrderBatchId);

}
