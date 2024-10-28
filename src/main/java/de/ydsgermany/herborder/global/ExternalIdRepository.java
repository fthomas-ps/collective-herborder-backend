package de.ydsgermany.herborder.global;

import de.ydsgermany.herborder.order.Order;
import java.util.Optional;

public interface ExternalIdRepository<T> {

    Optional<T> findByExternalId(String externalId);

}
