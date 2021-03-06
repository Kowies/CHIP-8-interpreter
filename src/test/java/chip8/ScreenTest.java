package chip8;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ScreenTest {

    Screen screen;

    @Before
    public void initialize() {
        screen = new Screen();
    }

    @Test
    public void constructor() {
        for (int i = 0; i < Screen.HEIGHT; ++i)
            for (int j = 0; j < Screen.WIDTH; ++j)
                assertEquals(screen.getPixel(j, i),false);

    }

    @Test
    public void setPixel() {
        int x = 12;
        int y = 25;

        screen.setPixel(x, y);
        assertEquals(screen.getPixel(x, y), true);

        screen.setPixel(x, y);
        assertEquals(screen.getPixel(x, y), true);

        assertEquals(screen.getPixel(x + 1, y), false);
        assertEquals(screen.getPixel(x + 1, y + 1), false);
        assertEquals(screen.getPixel(x, y + 1), false);
        assertEquals(screen.getPixel(x - 1, y), false);
    }

    @Test
    public void clear() {
        screen.setPixel(12, 25);
        screen.setPixel(3,12);
        screen.setPixel(27,1);

        screen.clear();

        for (int i = 0; i < Screen.HEIGHT; ++i)
            for (int j = 0; j < Screen.WIDTH; ++j)
                assertEquals(screen.getPixel(j, i),false);
    }

    @Test
    public void flipPixel() {
        int x = 12;
        int y = 25;

        screen.flipPixel(x, y);
        assertEquals(screen.getPixel(x, y), true);

        screen.flipPixel(x, y);
        assertEquals(screen.getPixel(x, y), false);

        assertEquals(screen.getPixel(x + 1, y), false);
        assertEquals(screen.getPixel(x + 1, y + 1), false);
        assertEquals(screen.getPixel(x, y + 1), false);
        assertEquals(screen.getPixel(x - 1, y), false);

        screen.flipPixel(x, y);
        assertEquals(screen.getPixel(x, y), true);
    }
}