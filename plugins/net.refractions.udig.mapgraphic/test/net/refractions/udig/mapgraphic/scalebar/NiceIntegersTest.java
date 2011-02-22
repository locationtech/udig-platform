package net.refractions.udig.mapgraphic.scalebar;

import static org.junit.Assert.*;

import org.junit.Test;

public class NiceIntegersTest {

    @Test
    public void testGet() {
        NiceIntegers obj = new NiceIntegers();
        assertEquals(1, obj.get(-1));
        assertEquals(2, obj.get(1));
        assertEquals(5, obj.get(2));
        assertEquals(10, obj.get(5));
        assertEquals(20, obj.get(10));
        assertEquals(50, obj.get(20));
        assertEquals(100, obj.get(50));
        assertEquals(200, obj.get(100));
        assertEquals(10, obj.get(7));
        assertEquals(50, obj.get(22));
    }

}
