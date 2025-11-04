package de.ydsgermany.herborder.global;

import java.util.Optional;

public interface ExternalIdRepository<T> {

    Optional<T> findByExternalId(String externalId);

}
