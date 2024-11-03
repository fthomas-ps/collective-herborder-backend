package de.ydsgermany.herborder.shipment_receival;


import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin/login")
public class LoginController {

    private final String username;
    private final String password;

    public LoginController(
        @Value("${spring.security.user.name}") String username,
        @Value("${spring.security.user.password}") String password) {
        this.username = username;
        this.password = password;
    }

    @PostMapping
    public ResponseEntity<Void> login(@RequestBody LoginDto loginDto) {
        if (!(Objects.equals(loginDto.username(), username)
            && Objects.equals(loginDto.password(), password))) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }

}
