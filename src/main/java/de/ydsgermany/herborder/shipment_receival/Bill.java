package de.ydsgermany.herborder.shipment_receival;

import de.ydsgermany.herborder.order_batch.OrderBatch;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bills_generator")
    @SequenceGenerator(name = "bills_generator", sequenceName = "bills_seq", allocationSize = 1)
    Long id;

    @OneToOne
    @JoinColumn(name = "order_batch_id")
    OrderBatch orderBatch;

    LocalDate date;
    int vat;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = BillHerbItem.class)
    List<BillHerbItem> herbs;

}
