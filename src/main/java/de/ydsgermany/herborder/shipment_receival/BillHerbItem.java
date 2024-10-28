package de.ydsgermany.herborder.shipment_receival;

import de.ydsgermany.herborder.herbs.Herb;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bill_herb_items")
@IdClass(BillHerbItemId.class)
public class BillHerbItem {

    @ManyToOne
    @Id
    Bill bill;

    @ManyToOne
    @JoinColumn(name = "herb_id")
    @Id
    Herb herb;

    Integer unitPrice;

    Long quantity;

}
