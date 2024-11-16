package de.ydsgermany.herborder.order_batch;

import de.ydsgermany.herborder.global.ExternalIdRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminOrderBatchesRepository extends JpaRepository<OrderBatch, Long>, ExternalIdRepository<OrderBatch> {

}
