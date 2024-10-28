package de.ydsgermany.herborder.order;

import de.ydsgermany.herborder.order_batch.OrderBatch;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_generator")
    @SequenceGenerator(name = "orders_generator", sequenceName = "orders_seq", allocationSize = 1)
    Long id;

    String externalId;

    @ManyToOne
    @JoinColumn(name = "order_batch_id")
    OrderBatch orderBatch;

    String firstName;
    String lastName;
    String mail;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = HerbQuantity.class)
    List<HerbQuantity> herbs;

}
