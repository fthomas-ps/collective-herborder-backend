package de.ydsgermany.herborder.shipment_receival;

import de.ydsgermany.herborder.order_batch.OrderBatch;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderBatchesAggregationRepository extends JpaRepository<OrderBatch, Long> {

    @Query("SELECT new de.ydsgermany.herborder.shipment_receival.AggregatedHerbItemsDto(h.name, SUM(hqa.herb_quantity), SUM(bi.quantity), SUM(si.shipped_quantity)) "
        + "FROM "
        + "  Herb h "
        + "  LEFT JOIN (SELECT hq.herb.id AS herb_id,SUM(hq.quantity) AS herb_quantity FROM HerbQuantity hq GROUP BY hq.herb.id )"
        + " hqa ON h.id = hqa.herb_id "
        + "  FULL OUTER JOIN BillHerbItem bi ON h.id = bi.herb.id "
        + "  FULL OUTER JOIN (SELECT shi.herb.id AS herb_id,SUM(shi.quantity) AS shipped_quantity FROM ShipmentHerbItem shi GROUP BY shi.herb.id ) "
        + " si ON h.id = si.herb_id "
        + "WHERE "
        + "  hqa.herb_quantity IS NOT NULL "
        + "  OR bi.quantity IS NOT NULL "
        + "  OR si.shipped_quantity IS NOT NULL "
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
        + "WHERE "
        + "  hq.packedQuantity < hq.quantity "
        + "  OR hq.packedQuantity IS NULL "
    )
    List<MissingHerbsDto> findMissingHerbs(Long orderBatchId);

}
