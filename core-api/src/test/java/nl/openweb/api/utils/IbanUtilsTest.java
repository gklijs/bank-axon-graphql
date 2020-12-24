package nl.openweb.api.utils;

import nl.openweb.api.bank.utils.IbanUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IbanUtilsTest {

    @Test
    void testIbanCheck() {
        assertTrue(IbanUtil.isValidOpenIban("NL66OPEN0000000000"));
    }
}
