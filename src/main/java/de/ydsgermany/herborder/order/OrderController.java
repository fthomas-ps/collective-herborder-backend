package de.ydsgermany.herborder.order;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import de.ydsgermany.herborder.global.ExternalIdGenerator;
import de.ydsgermany.herborder.herbs.Herb;
import de.ydsgermany.herborder.herbs.HerbsRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
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
@RequestMapping(path = "/orders")
@Slf4j
public class OrderController {

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

    private final OrdersRepository ordersRepository;
    private final HerbsRepository herbsRepository;
    private final ExternalIdGenerator externalIdGenerator;
    private final JavaMailSender mailSender;
    private final Validator validator;

    @Autowired
    public OrderController(
        OrdersRepository ordersRepository, HerbsRepository herbsRepository,
        @Qualifier("ordersExternalIdGenerator") ExternalIdGenerator externalIdGenerator,
        JavaMailSender javaMailSender) {
        this.ordersRepository = ordersRepository;
        this.herbsRepository = herbsRepository;
        this.externalIdGenerator = externalIdGenerator;
        this.mailSender = javaMailSender;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @PostMapping(consumes = "application/json")
    @Transactional
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
        Set<ConstraintViolation<OrderDto>> violations = validator.validate(orderDto);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations.toString());
        }
        Order savedOrder = addOrUpdateOrder(orderDto, null);
        sendConfirmationMail(savedOrder, false);
        OrderDto savedOrderDto = OrderDto.from(savedOrder);
        return ResponseEntity
            .created(URI.create("https://meine-kraeuterbestellung.online/api/orders/" + savedOrderDto.externalId()))
            .body(savedOrderDto);
    }

    @PutMapping(consumes = "application/json", path = "/{externalOrderId}")
    @Transactional
    public ResponseEntity<OrderDto> updateOrder(@RequestBody OrderDto orderDto, @PathVariable String externalOrderId) {
        Set<ConstraintViolation<OrderDto>> violations = validator.validate(orderDto);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations.toString());
        }
        Order foundOrder = ordersRepository.findByExternalId(externalOrderId)
            .orElseThrow(() -> new EntityNotFoundException(format("Order %s not found", externalOrderId)));
        Order savedOrder = addOrUpdateOrder(orderDto, foundOrder);
        sendConfirmationMail(savedOrder, true);
        OrderDto savedOrderDto = OrderDto.from(savedOrder);
        return ResponseEntity
            .ok(savedOrderDto);
    }

    private Order addOrUpdateOrder(OrderDto orderDto, Order oldOrder) {
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
        }
        return ordersRepository.save(order);
    }

    public Order createOrderFrom(OrderDto orderDto) {
        Order order = Order.builder()
            .firstName(orderDto.firstName())
            .lastName(orderDto.lastName())
            .mail(orderDto.mail())
            .build();
        order.setHerbs(herbsFrom(order, orderDto.herbs()));
        return order;
    }

    private List<HerbQuantity> herbsFrom(Order order, List<HerbQuantityDto> herbDtos) {
        Map<Long, HerbQuantity> herbQuantities = order.getHerbs().stream()
            .collect(Collectors.toMap(herbQuantity -> herbQuantity.getHerb().getId(), Function.identity()));
        return herbDtos.stream()
            .map(herbQuantityDto -> {
                Herb herb = herbsRepository.findById(herbQuantityDto.herbId())
                    .orElseThrow(() -> new EntityNotFoundException(
                        format("Herb with id %s not found", herbQuantityDto.herbId())));
                return new HerbQuantity(order,
                    herb,
                    herbQuantityDto.quantity(),
                    herbQuantities.get(herbQuantityDto.herbId()).getPackedQuantity());
            })
            .toList();
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

    private static String generateHerbsList(Order order) {
        return order.getHerbs().stream()
            .map(herbQuantity -> String.format(HERBS_FORMAT, herbQuantity.getHerb().getName(), herbQuantity.getQuantity()))
            .collect(joining("\n"));
    }

    @GetMapping(path = "/{externalOrderId}")
    public ResponseEntity<OrderDto> getHerbOrder(@PathVariable String externalOrderId) {
        Order order = ordersRepository.findByExternalId(externalOrderId)
            .orElseThrow(() -> new EntityNotFoundException(format("Order %s not found", externalOrderId)));
        OrderDto orderDto = OrderDto.from(order);
        return ResponseEntity.ok()
            .body(orderDto);
    }

}
