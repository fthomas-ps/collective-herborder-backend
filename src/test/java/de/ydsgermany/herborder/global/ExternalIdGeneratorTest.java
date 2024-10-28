package de.ydsgermany.herborder.global;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.ydsgermany.herborder.order.Order;
import de.ydsgermany.herborder.order.OrdersRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExternalIdGeneratorTest {

    private static final Set<Character> BASE62_CHARACTERS = Set.of(
        'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
        'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
        '0','1','2','3','4','5','6','7','8','9');

    private ExternalIdGenerator externalIdGenerator;

    @Mock
    private OrdersRepository ordersRepository;

    @BeforeEach
    void setup() {
        this.externalIdGenerator = new ExternalIdGenerator(ordersRepository);
    }

    @Test
    void testGenerateGeneratesABas62String() {
        when(ordersRepository.findByExternalId(any(String.class)))
            .thenReturn(Optional.empty());

        String result = externalIdGenerator.generate();

        assertThat(result).hasSize(ExternalIdGenerator.ID_LENGTH);
        assertThat(isBase62(result)).isTrue();
    }

    @Test
    void testGenerateGeneratesDifferentStrings() {
        when(ordersRepository.findByExternalId(any(String.class)))
            .thenReturn(Optional.empty());

        String result1 = externalIdGenerator.generate();
        String result2 = externalIdGenerator.generate();

        assertThat(result2).isNotEqualTo(result1);
    }

    @Test
    void testGenerateGeneratesUntilItGeneratesANonExistingExternalId() {
        when(ordersRepository.findByExternalId(any(String.class)))
            .thenReturn(Optional.of(Order.builder().build()), Optional.empty());

        String result = externalIdGenerator.generate();

        assertThat(result).hasSize(ExternalIdGenerator.ID_LENGTH);
        assertThat(isBase62(result)).isTrue();
    }

    private boolean isBase62(String s) {
        for (char c: s.toCharArray()) {
            if (!BASE62_CHARACTERS.contains(c)) {
                return false;
            }
        }
        return true;
    }

}
