package co.kirikiri.service;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class RandomNumberGenerator implements NumberGenerator {

    private static final int OFFSET = 1;
    private static final int MAX_NUMBER = 7;
    private static final Random random = new Random();

    @Override
    public int generate() {
        return random.nextInt(MAX_NUMBER) + OFFSET;
    }
}
