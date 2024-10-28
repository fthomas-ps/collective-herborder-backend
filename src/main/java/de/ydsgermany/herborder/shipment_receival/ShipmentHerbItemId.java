package de.ydsgermany.herborder.shipment_receival;

import de.ydsgermany.herborder.herbs.Herb;
import java.io.Serializable;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ShipmentHerbItemId implements Serializable {

    private Shipment shipment;
    private Herb herb;

}
