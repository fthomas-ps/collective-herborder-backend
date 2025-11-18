package de.ydsgermany.herborder.shipment_receival;

import de.ydsgermany.herborder.global.ExternalIdRepository;
import de.ydsgermany.herborder.order_batch.OrderBatch;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderBatchesAggregationRepository extends JpaRepository<OrderBatch, Long>, ExternalIdRepository<OrderBatch> {

    @Query("SELECT new de.ydsgermany.herborder.shipment_receival.AggregatedHerbItemsDto(h.name, SUM(hqa.herb_quantity), SUM(bi.quantity), SUM(si.shipped_quantity)) "
        + "FROM "
        + "  OrderBatch ob"
        + "  CROSS JOIN Herb h "
        + "  LEFT JOIN ("
        + "     SELECT hq.herb.id AS herb_id,SUM(hq.quantity) AS herb_quantity"
        + "     FROM HerbQuantity hq"
        + "         JOIN Order o ON o.id = hq.order.id AND o.orderBatch.id = :orderBatchId"
        + "     GROUP BY hq.herb.id ) hqa"
        + "         ON h.id = hqa.herb_id "
        + "  LEFT JOIN Bill bill ON ob.id = bill.orderBatch.id "
        + "  FULL OUTER JOIN BillHerbItem bi ON bill.id = bi.bill.id AND h.id = bi.herb.id "
        + "  FULL OUTER JOIN ("
        + "     SELECT shi.herb.id AS herb_id,SUM(shi.quantity) AS shipped_quantity"
        + "     FROM ShipmentHerbItem shi"
        + "         JOIN Shipment s ON shi.shipment.id = s.id AND s.orderBatch.id = :orderBatchId"
        + "     GROUP BY shi.herb.id ) "
        + " si ON h.id = si.herb_id "
        + "WHERE "
        + "  ob.id = :orderBatchId "
        + "  AND (hqa.herb_quantity IS NOT NULL "
        + "  OR bi.quantity IS NOT NULL "
        + "  OR si.shipped_quantity IS NOT NULL) "
        + "GROUP BY h.id, h.name "
    )
    List<AggregatedHerbItemsDto> aggregateOrders(Long orderBatchId);

    @Query("SELECT new de.ydsgermany.herborder.shipment_receival.MissingHerbsDto(o.externalId, o.firstName, o.lastName, h.id, h.name, hq.quantity, hq.packedQuantity) "
        + "FROM "
        + "  HerbQuantity hq"
        + "  LEFT JOIN Herb h"
        + "    ON h.id = hq.herb.id "
        + "  LEFT JOIN Order o"
        + "    ON o.id = hq.order.id "
        + "  LEFT JOIN OrderBatch orderBatch "
        + "    ON orderBatch.id = o.orderBatch.id "
        + "WHERE "
        + "  (hq.packedQuantity < hq.quantity "
        + "  OR hq.packedQuantity IS NULL) "
        + "  AND orderBatch.externalId = :externalOrderBatchId "
    )
    List<MissingHerbsDto> findMissingHerbs(String externalOrderBatchId);

}
