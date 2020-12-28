package nl.openweb.api.bank.utils;

import lombok.experimental.UtilityClass;

import java.math.BigInteger;
import java.util.stream.IntStream;

@UtilityClass
public class IbanUtil {

    public String getIban() {
        StringBuilder builder = new StringBuilder();
        IntStream.range(0, 10).forEach(x -> builder.append(RandomUtil.getRandom().nextInt(10)));
        return toIban(builder.toString());
    }

    private String toIban(String digits) {
        int checkNr = new BigInteger("24251423" + digits + "232100").remainder(BigInteger.valueOf(97)).intValue();
        String checkPart = Integer.toString(98 - checkNr);
        if (checkPart.length() == 1) {
            checkPart = '0' + checkPart;
        }
        return "NL" + checkPart + "OPEN" + digits;
    }

    public boolean isValidOpenIban(String iban){
        if(iban.length() != 18){
            return false;
        }
        return iban.equals(toIban(iban.substring(8,18)));
    }

    public boolean invalidFrom(String iban){
        if (iban.equals("cash")){
            return false;
        }
        return ! isValidOpenIban(iban);
    }
}
