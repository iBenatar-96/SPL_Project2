package bgu.spl.mics.application.passiveObjects;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.*;

public class EwokTest {

    private Ewok ewok;

    @BeforeEach
    public void setUp() throws Exception {
        ewok = new Ewok(1);
    }

    @Test
    public void testAcquire() {
        assertTrue(ewok.available);
        int beforeChange = ewok.serialNumber;
        ewok.acquire();
        assertFalse(ewok.available); //checking that availability was changed to false
        assertEquals(ewok.serialNumber, beforeChange); //checking that serial number wasn't changed
    }

    @Test
    public void testRelease() {
        ewok.acquire();
        int beforeChange = ewok.serialNumber;
        ewok.release();
        assertTrue(ewok.available); //checking that availability was changed to true
        assertEquals(ewok.serialNumber, beforeChange); //checking that the serial number wasn't changed
    }
}