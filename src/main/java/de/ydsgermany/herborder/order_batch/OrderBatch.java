package de.ydsgermany.herborder.order_batch;

import de.ydsgermany.herborder.order.Order;
import de.ydsgermany.herborder.shipment_receival.Bill;
import de.ydsgermany.herborder.shipment_receival.Shipment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(exclude = {"orders", "bill", "shipments"})
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_batches")
public class OrderBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_batches_generator")
    @SequenceGenerator(name = "order_batches_generator", sequenceName = "order_batches_seq", allocationSize = 1)
    Long id;

    String externalId;

    String name;

    @Enumerated(EnumType.STRING)
    OrderState orderState;

    @OneToMany(mappedBy = "orderBatch")
    Set<Order> orders;

    @OneToOne(mappedBy = "orderBatch", cascade = CascadeType.PERSIST)
    Bill bill;

    @OneToMany(mappedBy = "orderBatch")
    Set<Shipment> shipments;

}
