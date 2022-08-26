package tech.gklijs.api.utils;

import org.junit.jupiter.api.*;
import tech.gklijs.api.bank.utils.IbanUtil;

import static org.junit.jupiter.api.Assertions.*;

class IbanUtilsTest {

    @Test
    void testIbanCheck() {
        assertTrue(IbanUtil.isValidIban("NL09AXON0000000000"));
    }

    @Test
    void testAxonCheck() {
        assertTrue(IbanUtil.isAxonIban("NL09AXON0000000000"));
    }

    @Test
    void testIbanCheckWontThrowWhenInvalid() {
        assertFalse(IbanUtil.isValidIban("NL09AXON0001000000"));
    }
    @Test
    void testAxonCheckWontThrowWhenInvalid() {
        assertFalse(IbanUtil.isAxonIban("NL09AXON0001000000"));
    }
}
