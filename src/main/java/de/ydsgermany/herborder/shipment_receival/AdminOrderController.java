package de.ydsgermany.herborder.shipment_receival;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import de.ydsgermany.herborder.global.ExternalIdGenerator;
import de.ydsgermany.herborder.herbs.Herb;
import de.ydsgermany.herborder.herbs.HerbsRepository;
import de.ydsgermany.herborder.order.HerbQuantity;
import de.ydsgermany.herborder.order.Order;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin/orders")
@Slf4j
public class AdminOrderController {

    public static final String MAIL_TITLE = "Kräuterbestellung 2025";
    public static final String BCC_ADDRESS = "florianthomas138@gmail.com";
    public static final String MAIL_BODY_TEMPLATE_CREATE_ORDER = """
        Jai Swaminarayan Das na Das %s,
        
        Vielen Dank für deine Kräuterbestellung. Wir haben die folgende Bestellung von dir erhalten:
        
        %s
        
        Deine Bestellung kannst du jederzeit ändern. Gehe dazu einfach auf https://meine-kraeuterbestellung.online/order/%s.
        
        Viele Grüße
        Shivam und Amrut
        
        """;

    public static final String MAIL_BODY_TEMPLATE_UPDATE_ORDER = """
        Jai Swaminarayan Das na Das %s,
        
        Deine Kräuterbestellung wurde aktualisiert. Im Folgenden siehst du noch einmal deine aktualisierte Bestellung:
        
        %s
        
        Deine Bestellung kannst du jederzeit ändern. Gehe dazu einfach auf https://meine-kraeuterbestellung.online/order/%s.
 
        Viele Grüße
        Shivam und Amrut
        
        """;
    public static final String HERBS_FORMAT = "%s: %d";

    public static final String MAIL_BODY_TEMPLATE_PRICE = """
        Jai Swaminarayan Das na Das %s,
        
        Es ist soweit. Für deine Kräuterbestellung sind Kosten in Höhe von %s € angefallen.

        Bitte überweise diesen Betrag auf mein Konto:

        Marion Zehe

        IBAN:
        DE07 1009 0000 5822 7130 00

        BIC:
        BEVODEBBXXX

        Verwendungszweck:
        Fremdgeld Ayushakti

        Ganz liebe Grüße
        Shivam und Amrut
        """;

    private final AdminOrdersRepository ordersRepository;
    private final ExternalIdGenerator externalIdGenerator;
    private final HerbsRepository herbsRepository;
    private final BillRepository billRepository;
    private final JavaMailSender mailSender;
    private final Validator validator;

    @Autowired
    public AdminOrderController(
        AdminOrdersRepository ordersRepository,
        @Qualifier("ordersExternalIdGenerator") ExternalIdGenerator externalIdGenerator,
        HerbsRepository herbsRepository,
        BillRepository billRepository,
        JavaMailSender mailSender,
        Validator validator) {
        this.ordersRepository = ordersRepository;
        this.externalIdGenerator = externalIdGenerator;
        this.herbsRepository = herbsRepository;
        this.billRepository = billRepository;
        this.mailSender = mailSender;
        this.validator = validator;
    }

    @PutMapping(consumes = "application/json", path = "/{externalOrderId}")
    @Transactional
    public ResponseEntity<AdminOrderDto> updateOrder(@RequestBody AdminOrderDto orderDto, @PathVariable String externalOrderId) {
        Set<ConstraintViolation<AdminOrderDto>> violations = validator.validate(orderDto);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations.toString());
        }
        Order foundOrder = ordersRepository.findByExternalId(externalOrderId)
            .orElseThrow(() -> new EntityNotFoundException(format("Order %s not found", externalOrderId)));
        Order savedOrder = addOrUpdateOrder(orderDto, foundOrder);
        AdminOrderDto savedOrderDto = AdminOrderDto.from(savedOrder, null);
        return ResponseEntity
            .ok(savedOrderDto);
    }

    private Order addOrUpdateOrder(AdminOrderDto orderDto, Order oldOrder) {
        Order order;
        if (oldOrder == null) {
            order = createOrderFrom(orderDto);
            order.setId(null);
            order.setExternalId(externalIdGenerator.generate());
        } else {
            order = oldOrder;
            order.setFirstName(orderDto.firstName());
            order.setLastName(orderDto.lastName());
            order.setMail(orderDto.mail());
            // Hibernate requires us to retain the existing collection instance.
            // If we call order.setHerbs(<updated herbs list>), the update will fail
            order.getHerbs().clear();
            order.getHerbs().addAll(herbsFrom(order, orderDto.herbs()));
            order.setPaidAmount(orderDto.paidAmount());
        }
        return ordersRepository.save(order);
    }

    public Order createOrderFrom(AdminOrderDto orderDto) {
        Order order = Order.builder()
            .firstName(orderDto.firstName())
            .lastName(orderDto.lastName())
            .mail(orderDto.mail())
            .build();
        order.setHerbs(herbsFrom(order, orderDto.herbs()));
        return order;
    }

    private List<HerbQuantity> herbsFrom(Order order, List<AdminHerbQuantityDto> herbDtos) {
        return herbDtos.stream()
            .map(herbQuantityDto -> {
                Herb herb = herbsRepository.findById(herbQuantityDto.herbId())
                    .orElseThrow(() -> new EntityNotFoundException(
                        format("Herb with id %s not found", herbQuantityDto.herbId())));
                return new HerbQuantity(order, herb, herbQuantityDto.quantity(), herbQuantityDto.packedQuantity());
            })
            .toList();
    }

    @PostMapping(consumes = "application/json", path = "/{externalOrderId}")
    @Transactional
    public ResponseEntity<Void> sendConfirmationMail(@PathVariable String externalOrderId) {
        Order foundOrder = ordersRepository.findByExternalId(externalOrderId)
            .orElseThrow(() -> new EntityNotFoundException(format("Order %s not found", externalOrderId)));
        sendConfirmationMail(foundOrder, true);
        return ResponseEntity
            .ok()
            .build();
    }

    private void sendConfirmationMail(Order order, boolean isUpdate) {
        String mailBody = generateMailBody(order, isUpdate);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(order.getMail());
        message.setBcc(BCC_ADDRESS);
        message.setSubject(MAIL_TITLE);
        message.setText(mailBody);
        mailSender.send(message);
    }

    private static String generateMailBody(Order order, boolean isUpdate) {
        String mailBodyTemplate = getOperationSpecificTemplate(isUpdate);
        String herbsList = generateHerbsList(order);
        return mailBodyTemplate.formatted(order.getFirstName(), herbsList, order.getExternalId());
    }

    private static String getOperationSpecificTemplate(boolean isUpdate) {
        if (isUpdate) {
            return MAIL_BODY_TEMPLATE_UPDATE_ORDER;
        }
        return MAIL_BODY_TEMPLATE_CREATE_ORDER;
    }

    private void sendPriceMail(Order order, Long price) {
        String formattedPrice = formatPrice(price);
        String mailBody = MAIL_BODY_TEMPLATE_PRICE.formatted(order.getFirstName(), formattedPrice);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(order.getMail());
        message.setBcc(BCC_ADDRESS);
        message.setSubject(MAIL_TITLE);
        message.setText(mailBody);
        mailSender.send(message);
    }

    private static String formatPrice(Long price) {
        String priceString = price.toString();
        String paddedPriceString = prefixWithZerosIfRequired(priceString);
        return new StringBuilder(paddedPriceString)
            .insert(paddedPriceString.length() - 2, ',')
            .toString();
    }

    private static String prefixWithZerosIfRequired(String priceString) {
        StringBuilder priceStringBuilder = new StringBuilder(priceString);
        while (priceStringBuilder.length() < 3) {
            priceStringBuilder.insert(0, '0');
        }
        return priceStringBuilder.toString();
    }

    private static String generateHerbsList(Order order) {
        return order.getHerbs().stream()
            .map(herbQuantity -> String.format(HERBS_FORMAT, herbQuantity.getHerb().getName(), herbQuantity.getQuantity()))
            .collect(joining("\n"));
    }

    @GetMapping(path = "/{externalOrderId}")
    public ResponseEntity<AdminOrderDto> getHerbOrder(@PathVariable String externalOrderId) {
        Optional<Bill> billOpt = billRepository.findAll().stream().findFirst();
        Map<Long, BillHerbItem> billHerbItems = billOpt
            .map(bill ->
                bill.getHerbs().stream()
                    .collect(toMap(herbItem -> herbItem.getHerb().getId(), herbItem -> herbItem))
            )
            .orElseGet(HashMap::new);

        Order order = ordersRepository.findByExternalId(externalOrderId)
            .orElseThrow(() -> new EntityNotFoundException(format("Order %s not found", externalOrderId)));
        Long totalPriceWithVat = calculatePrice(order, billHerbItems, billOpt);
        AdminOrderDto orderDto = AdminOrderDto.from(order, totalPriceWithVat);
        return ResponseEntity.ok()
            .body(orderDto);
    }

    private static final BillHerbItem EMPTY_BILL_HERB_ITEM = new BillHerbItem(null, null, 0, null);

    @GetMapping
    public ResponseEntity<List<AdminOrderDto>> getHerbOrders() {
        Optional<Bill> billOpt = billRepository.findAll().stream().findFirst();
        Map<Long, BillHerbItem> billHerbItems = billOpt
            .map(bill ->
                bill.getHerbs().stream()
                    .collect(toMap(herbItem -> herbItem.getHerb().getId(), herbItem -> herbItem))
            )
            .orElseGet(HashMap::new);

        List<AdminOrderDto> orderDtos = ordersRepository.findAll()
            .stream()
            .map(order -> {
                Long totalPriceWithVat = calculatePrice(order, billHerbItems, billOpt);
                return AdminOrderDto.from(order, totalPriceWithVat);
            })
            .toList();
        return ResponseEntity.ok()
            .body(orderDtos);
    }

    private static Long calculatePrice(Order order, Map<Long, BillHerbItem> billHerbItems,
        Optional<Bill> billOpt) {
        if (billOpt.isEmpty()) {
            return null;
        }
        long totalPriceWithoutVat = order.getHerbs().stream()
            .map(orderHerbItem -> orderHerbItem.getQuantity() * billHerbItems.getOrDefault(
                orderHerbItem.getHerb().getId(), EMPTY_BILL_HERB_ITEM).getUnitPrice())
            .filter(Objects::nonNull)
            .mapToLong(unitPrice -> unitPrice)
            .sum();
        return Math.round(totalPriceWithoutVat * ((double) (100 + billOpt.map(Bill::getVat).orElse(0)) / 100));
    }

    @PostMapping(path = "/{externalOrderId}/price-mail")
    public ResponseEntity<Void> sendPriceMail(@PathVariable String externalOrderId) {
        Order foundOrder = ordersRepository.findByExternalId(externalOrderId)
            .orElseThrow(() -> new EntityNotFoundException(format("Order %s not found", externalOrderId)));
        Optional<Bill> billOpt = billRepository.findAll().stream().findFirst();
        Map<Long, BillHerbItem> billHerbItems = billOpt
            .map(bill ->
                bill.getHerbs().stream()
                    .collect(toMap(herbItem -> herbItem.getHerb().getId(), herbItem -> herbItem))
            )
            .orElseGet(HashMap::new);
        Long totalPriceWithVat = calculatePrice(foundOrder, billHerbItems, billOpt);
        sendPriceMail(foundOrder, totalPriceWithVat);
        return ResponseEntity
            .ok()
            .build();
    }

    @PostMapping(path = "/price-mails")
    public ResponseEntity<Void> sendPriceMails() {
        Optional<Bill> billOpt = billRepository.findAll().stream().findFirst();
        Map<Long, BillHerbItem> billHerbItems = billOpt
            .map(bill ->
                bill.getHerbs().stream()
                    .collect(toMap(herbItem -> herbItem.getHerb().getId(), herbItem -> herbItem))
            )
            .orElseGet(HashMap::new);

        ordersRepository.findAll()
            .forEach(order -> {
                Long totalPriceWithVat = calculatePrice(order, billHerbItems, billOpt);
                sendPriceMail(order, totalPriceWithVat);
            });
        return ResponseEntity.ok().build();
    }

}
