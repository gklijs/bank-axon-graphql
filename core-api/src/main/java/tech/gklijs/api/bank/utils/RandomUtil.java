package tech.gklijs.api.bank.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
class RandomUtil {

    private static final Random random = new Random();

    Random getRandom() {
        return random;
    }
}
