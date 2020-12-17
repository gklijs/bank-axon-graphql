package nl.openweb.api.bank.utils;

import lombok.experimental.UtilityClass;

import java.util.stream.IntStream;

@UtilityClass
public class TokenUtil {
    public String getToken() {
        StringBuilder builder = new StringBuilder();
        IntStream.range(1,20).forEach(x -> builder.append(RandomUtil.getRandom().nextInt(10)));
        return builder.toString();
    }
}
