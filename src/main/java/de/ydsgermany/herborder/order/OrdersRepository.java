package de.ydsgermany.herborder.order;

import de.ydsgermany.herborder.global.ExternalIdRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Long>, ExternalIdRepository<Order> {

}
