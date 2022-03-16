package tech.gklijs.graphql_endpoint.util;

import lombok.experimental.UtilityClass;

import java.text.NumberFormat;
import java.util.Locale;

@UtilityClass
public class CurrencyUtil {

    private final NumberFormat CURRENCY_INSTANCE =
            NumberFormat.getCurrencyInstance(new Locale("nl", "NL"));

    public String toCurrency(Long amount) {
        return CURRENCY_INSTANCE.format((amount * 1.0) / 100);
    }
}
