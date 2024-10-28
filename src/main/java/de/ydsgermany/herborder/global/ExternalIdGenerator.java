package de.ydsgermany.herborder.global;

import java.util.Random;
import java.util.stream.IntStream;

public class ExternalIdGenerator {

    static final int ID_LENGTH = 32;
    private static final char[] BASE62_CHARACTERS = new char[]{
        'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
        'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
        '0','1','2','3','4','5','6','7','8','9'
    };

    private final ExternalIdRepository entityRepository;
    private final Random random;

    public ExternalIdGenerator(ExternalIdRepository entityRepository) {
        this.entityRepository = entityRepository;
        this.random = new Random();
    }

    public String generate() {
        String externalId;
        do {
            externalId = generateExternalId();
        } while (entityRepository.findByExternalId(externalId).isPresent());
        return externalId;
    }

    private String generateExternalId() {
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, ID_LENGTH).forEach(
            i -> sb.append(generateRandomCharacter())
        );
        return sb.toString();
    }

    private char generateRandomCharacter() {
        int randomNumber = random.nextInt(BASE62_CHARACTERS.length);
        return BASE62_CHARACTERS[randomNumber];
    }

}
