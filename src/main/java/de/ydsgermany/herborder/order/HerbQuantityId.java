package de.ydsgermany.herborder.order;

import de.ydsgermany.herborder.herbs.Herb;
import java.io.Serializable;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HerbQuantityId implements Serializable {

    private Order order;
    private Herb herb;

}
