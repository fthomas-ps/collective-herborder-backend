package de.ydsgermany.herborder.shipment_receival;

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
import java.time.LocalDate;
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
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shipments_generator")
    @SequenceGenerator(name = "shipments_generator", sequenceName = "shipments_seq", allocationSize = 1)
    Long id;

    @ManyToOne
    @JoinColumn(name = "order_batch_id")
    OrderBatch orderBatch;

    LocalDate date;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = ShipmentHerbItem.class)
    List<ShipmentHerbItem> herbs;

}
