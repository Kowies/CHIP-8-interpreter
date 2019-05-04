package chip8;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MemoryTest {

    Memory memory;

    @Before
    public void initialize() {
        memory = new Memory();
    }

    @Test
    public void constructor() {

        assertArrayEquals(memory.getRAM(), new byte[4096]);
        assertArrayEquals(memory.getV(), new byte[16]);
        assertEquals(memory.getI(), 0);
        assertEquals(memory.getPC(), 0);

        assertArrayEquals(memory.getStack(), new char[16]);
        assertEquals(memory.getSP(), 0);

        assertEquals(memory.getDelayTimer(), 0);
        assertEquals(memory.getSoundTimer(), 0);
    }

    @Test
    public void setRAM() {

        byte tempRAM[] = memory.getRAM();
        tempRAM[0] = 0x01;
        tempRAM[1] = 0x23;

        memory.setRAM(tempRAM);

        assertEquals(memory.getRAM(), tempRAM);
    }

    @Test
    public void setV() {

        byte tempV[] = memory.getV();
        tempV[0] = 0x45;
        tempV[1] = 0x67;

        memory.setV(tempV);

        assertEquals(memory.getV(), tempV);
    }

    @Test
    public void setStack() {

        char tempStack[] = memory.getStack();
        tempStack[0] = 0x89;
        tempStack[1] = 0xAB;

        memory.setStack(tempStack);

        assertEquals(memory.getStack(), tempStack);
    }


}