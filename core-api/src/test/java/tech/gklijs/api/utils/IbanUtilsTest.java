package tech.gklijs.api.utils;

import org.junit.jupiter.api.*;
import tech.gklijs.api.bank.utils.IbanUtil;

import static org.junit.jupiter.api.Assertions.*;

class IbanUtilsTest {

    @Test
    void testIbanCheck() {
        assertTrue(IbanUtil.isValidAxonIban("NL09AXON0000000000"));
    }
}
