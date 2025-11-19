package de.ydsgermany.herborder.shipment_receival;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    List<Shipment> findByOrderBatchExternalId(String orderBatchExternalId);

    Optional<Shipment> findByOrderBatchExternalIdAndId(String orderBatchExternalId, Long shipmentId);

}
