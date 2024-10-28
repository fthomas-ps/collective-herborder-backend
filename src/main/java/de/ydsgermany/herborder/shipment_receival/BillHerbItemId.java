package de.ydsgermany.herborder.shipment_receival;

import de.ydsgermany.herborder.herbs.Herb;
import java.io.Serializable;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BillHerbItemId implements Serializable {

    private Bill bill;
    private Herb herb;

}
