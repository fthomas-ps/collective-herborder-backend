package de.ydsgermany.herborder.order;

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
@Table(name = "herb_quantities")
@IdClass(HerbQuantityId.class)
public class HerbQuantity {

    @ManyToOne
    @Id
    Order order;

    @ManyToOne
    @JoinColumn(name = "herb_id")
    @Id
    Herb herb;

    Integer quantity;

    Integer packedQuantity;

}

