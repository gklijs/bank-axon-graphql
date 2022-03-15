package nl.openweb.api.utils;

import nl.openweb.api.bank.utils.IbanUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IbanUtilsTest {

    @Test
    void testIbanCheck() {
        assertTrue(IbanUtil.isValidOpenIban("NL09AXON0000000000"));
    }
}
