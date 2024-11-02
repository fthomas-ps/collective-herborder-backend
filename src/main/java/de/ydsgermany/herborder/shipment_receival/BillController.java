package de.ydsgermany.herborder.shipment_receival;

import static java.lang.String.format;

import de.ydsgermany.herborder.herbs.Herb;
import de.ydsgermany.herborder.herbs.HerbsRepository;
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
@RequestMapping(path = "/admin/bills")
@Slf4j
public class BillController {

    private final BillRepository billRepository;
    private final HerbsRepository herbsRepository;

    @Autowired
    public BillController(
        BillRepository billRepository, HerbsRepository herbsRepository) {
        this.billRepository = billRepository;
        this.herbsRepository = herbsRepository;
    }

//    @PostMapping(consumes = "application/json")
//    @Transactional
//    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
//        OrderDto savedOrderDto = addOrUpdateOrder(orderDto, null);
//        return ResponseEntity
//            .created(URI.create("https://localhost:8080/orders/" + savedOrderDto.externalId()))
//            .body(savedOrderDto);
//
//    }

    @PutMapping(consumes = "application/json", path = "/{billId}")
    @Transactional
    public ResponseEntity<BillDto> updateBill(@RequestBody BillDto billDto, @PathVariable Long billId) {
        Bill foundBill = billRepository.findById(billId)
            .orElseThrow(() -> new EntityNotFoundException(format("Bill %s not found", billId)));
        BillDto savedBillDto = addOrUpdateBill(billDto, foundBill);
        return ResponseEntity
            .ok()
            .body(savedBillDto);
    }

    private BillDto addOrUpdateBill(BillDto billDto, Bill oldBill) {
        Bill bill = createBillFrom(billDto);
        bill.setId(oldBill.getId());
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

    //@GetMapping(path = "/{id}")
    //public ResponseEntity<BillDto> getBill(@PathVariable Long id) {
    //    Bill bill = billRepository.findById(id)
    //        .orElseThrow(() -> new EntityNotFoundException(format("Bill %d not found", id)));
    //    BillDto billDto = BillDto.from(bill);
    //    return ResponseEntity.ok()
    //        .body(billDto);
    //}

}
