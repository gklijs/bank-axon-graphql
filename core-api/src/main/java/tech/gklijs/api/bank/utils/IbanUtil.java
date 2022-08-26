package tech.gklijs.api.bank.utils;

import lombok.experimental.UtilityClass;
import nl.garvelink.iban.IBAN;
import nl.garvelink.iban.IBANFields;
import nl.garvelink.iban.Modulo97;
import nl.garvelink.iban.WrongChecksumException;

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
        int checkNr = new BigInteger("10332423" + digits + "232100").remainder(BigInteger.valueOf(97)).intValue();
        String checkPart = Integer.toString(98 - checkNr);
        if (checkPart.length() == 1) {
            checkPart = '0' + checkPart;
        }
        return "NL" + checkPart + "AXON" + digits;
    }

    public boolean isAxonIban(String iban) {
        try {
            return IBANFields.getBankIdentifier(IBAN.valueOf(iban))
                             .map(i -> i.equals("AXON"))
                             .orElse(false);
        }catch (WrongChecksumException e){
            return false;
        }
    }

    public boolean isValidIban(String iban) {
        return Modulo97.verifyCheckDigits(iban);
    }

    public boolean invalidFrom(String iban) {
        if (iban.equals("cash")) {
            return false;
        }
        return !isValidIban(iban);
    }
}
