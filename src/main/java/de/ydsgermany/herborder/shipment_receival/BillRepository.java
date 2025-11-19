package de.ydsgermany.herborder.shipment_receival;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByOrderBatchExternalId(String orderBatchExternalId);

}
