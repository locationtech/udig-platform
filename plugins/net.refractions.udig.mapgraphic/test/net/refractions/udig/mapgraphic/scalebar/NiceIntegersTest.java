package net.refractions.udig.mapgraphic.scalebar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NiceIntegersTest {

    @Test
    public void testGet() {
        NiceIntegers obj = new NiceIntegers();
        assertEquals("1", 1, (int) obj.get(-1));
        assertEquals(2, (int) obj.get(1));
        assertEquals(5, (int) obj.get(2));
        assertEquals(10, (int) obj.get(5));
        assertEquals(20, (int) obj.get(10));
        assertEquals(50, (int) obj.get(20));
        assertEquals(100, (int) obj.get(50));
        assertEquals(200, (int) obj.get(100));
        assertEquals(10, (int) obj.get(7));
        assertEquals(50, (int) obj.get(22));
    }

}
