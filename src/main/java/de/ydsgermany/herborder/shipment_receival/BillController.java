package de.ydsgermany.herborder.shipment_receival;

import static java.lang.String.format;

import de.ydsgermany.herborder.herbs.Herb;
import de.ydsgermany.herborder.herbs.HerbsRepository;
import de.ydsgermany.herborder.order_batch.AdminOrderBatchesRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin/order_batches/{externalOrderBatchId}/bill")
@Slf4j
public class BillController {

    private final AdminOrderBatchesRepository orderBatchesRepository;
    private final BillRepository billRepository;
    private final HerbsRepository herbsRepository;

    @Autowired
    public BillController(
        AdminOrderBatchesRepository orderBatchesRepository,
        BillRepository billRepository, HerbsRepository herbsRepository) {
        this.orderBatchesRepository = orderBatchesRepository;
        this.billRepository = billRepository;
        this.herbsRepository = herbsRepository;
    }

    @PutMapping(consumes = "application/json")
    @Transactional
    public ResponseEntity<BillDto> updateBill(@PathVariable String externalOrderBatchId, @RequestBody BillDto billDto) {
        Bill foundBill = billRepository.findByOrderBatchExternalId(externalOrderBatchId)
            .orElseThrow(() -> new EntityNotFoundException(format("Bill for Order Batch %s not found", externalOrderBatchId)));
        BillDto savedBillDto = addOrUpdateBill(billDto, foundBill);
        return ResponseEntity
            .ok()
            .body(savedBillDto);
    }

    private BillDto addOrUpdateBill(BillDto billDto, Bill oldBill) {
        Bill bill = createBillFrom(billDto);
        bill.setId(oldBill.getId());
        bill.setOrderBatch(oldBill.getOrderBatch());
        Bill savedBill = billRepository.save(bill);
        return BillDto.from(savedBill);
    }

    public Bill createBillFrom(BillDto billDto) {
        Bill bill = Bill.builder()
            .date(billDto.date())
            .vat(billDto.vat())
            .build();
        bill.setHerbs(herbsFrom(bill, billDto.herbs()));
        return bill;
    }

    private List<BillHerbItem> herbsFrom(Bill bill, List<BillHerbItemDto> herbDtos) {
        return herbDtos.stream()
            .map(billHerbItemDto -> {
                Herb herb = herbsRepository.findById(billHerbItemDto.herbId())
                    .orElseThrow(() -> new EntityNotFoundException(
                        format("Herb with id %s not found", billHerbItemDto.herbId())));
                return new BillHerbItem(bill, herb, billHerbItemDto.unitPrice(), billHerbItemDto.quantity());
            })
            .toList();
    }

    @GetMapping
    public ResponseEntity<BillDto> getBill(@PathVariable String externalOrderBatchId) {
        // Since there is only one batch order right now, there is also only one bill. So, just take it.
        Bill bill = billRepository.findByOrderBatchExternalId(externalOrderBatchId)
            .orElseThrow(() -> new EntityNotFoundException(format("Bill for Order Batch %s not found", externalOrderBatchId)));
        BillDto billDto = BillDto.from(bill);
        return ResponseEntity.ok()
            .body(billDto);
    }

}
