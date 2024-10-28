package de.ydsgermany.herborder.herbs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HerbsRepository extends JpaRepository<Herb, Long> {

}
