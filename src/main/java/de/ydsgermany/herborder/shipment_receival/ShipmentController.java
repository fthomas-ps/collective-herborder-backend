package de.ydsgermany.herborder.shipment_receival;

import static java.lang.String.format;

import de.ydsgermany.herborder.herbs.Herb;
import de.ydsgermany.herborder.herbs.HerbsRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin/shipments")
@Slf4j
public class ShipmentController {

    private final ShipmentRepository shipmentRepository;
    private final HerbsRepository herbsRepository;

    @Autowired
    public ShipmentController(
        ShipmentRepository shipmentRepository, HerbsRepository herbsRepository) {
        this.shipmentRepository = shipmentRepository;
        this.herbsRepository = herbsRepository;
    }

    @PostMapping(consumes = "application/json")
    @Transactional
    public ResponseEntity<ShipmentDto> createShipment(@RequestBody ShipmentDto shipment) {
        ShipmentDto savedOrderDto = addOrUpdateShipment(shipment, null);
        return ResponseEntity
            .created(URI.create("https://localhost:8080/api/admin/shipments/" + savedOrderDto.id()))
            .body(savedOrderDto);

    }

    @PutMapping(consumes = "application/json", path = "/{shipmentId}")
    @Transactional
    public ResponseEntity<ShipmentDto> updateShipment(@RequestBody ShipmentDto shipmentDto, @PathVariable Long shipmentId) {
        Shipment foundShipment = shipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new EntityNotFoundException(format("Shipment %s not found", shipmentId)));
        ShipmentDto savedShipmentDto = addOrUpdateShipment(shipmentDto, foundShipment);
        return ResponseEntity
            .ok(savedShipmentDto);
    }

    private ShipmentDto addOrUpdateShipment(ShipmentDto shipmentDto, Shipment oldShipment) {
        Shipment shipment;
        if (oldShipment != null) {
            shipment = oldShipment;
            shipment.setDate(shipmentDto.date());
            // Hibernate requires us to retain the existing collection instance.
            // If we call shipment.setHerbs(<updated herbs list>), the update will fail
            shipment.getHerbs().clear();
        } else {
            shipment = createShipmentFrom(shipmentDto);
        }
        shipment.getHerbs().addAll(herbsFrom(shipment, shipmentDto.herbs()));
        Shipment savedShipment = shipmentRepository.save(shipment);
        return ShipmentDto.from(savedShipment);
    }

    public Shipment createShipmentFrom(ShipmentDto shipmentDto) {
        return Shipment.builder()
            .date(shipmentDto.date())
            .herbs(new LinkedList<>())
            .build();
    }

    private List<ShipmentHerbItem> herbsFrom(Shipment shipment, List<ShipmentHerbItemDto> herbDtos) {
        return herbDtos.stream()
            .map(shipmentHerbItemDto -> {
                Herb herb = herbsRepository.findById(shipmentHerbItemDto.herbId())
                    .orElseThrow(() -> new EntityNotFoundException(
                        format("Herb with id %s not found", shipmentHerbItemDto.herbId())));
                return new ShipmentHerbItem(shipment, herb, shipmentHerbItemDto.quantity());
            })
            .toList();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ShipmentDto> getShipment(@PathVariable Long id) {
        Shipment shipment = shipmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(format("Bill %d not found", id)));
        ShipmentDto shipmentDto = ShipmentDto.from(shipment);
        return ResponseEntity.ok()
            .body(shipmentDto);
    }

    @GetMapping
    public ResponseEntity<List<ShipmentDto>> getAllShipments() {
        List<ShipmentDto> shipmentDtos = shipmentRepository.findAll().stream()
            .map(ShipmentDto::from)
            .toList();
        return ResponseEntity.ok()
            .body(shipmentDtos);
    }

}
