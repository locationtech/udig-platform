package net.refractions.udig.mapgraphic.scalebar;

import static net.refractions.udig.mapgraphic.scalebar.Unit.*;
import static org.junit.Assert.*;

import net.refractions.udig.core.IProviderWithParam;
import net.refractions.udig.core.Pair;

import org.junit.Test;

public class ScalebarMapGraphicTest {
    private IProviderWithParam<Integer, Integer> range = new IProviderWithParam<Integer, Integer>(){

        public Integer get( Integer param ) {
            if( param.intValue() == -1){
                return 1;
            }
            if( param < 10000){
                return param*10;
            }
            return null;
        }

    };

    @Test
    public void testClosestIntMeter1() {
        int idealDistance = 10;
        int closest = ScalebarMapGraphic.closestInt(idealDistance, range);
        assertEquals( 10, closest );
    }

    @Test
    public void testClosestIntMeter2() {
        int idealDistance = 20;
        int closest = ScalebarMapGraphic.closestInt(idealDistance, range);
        assertEquals( 10, closest );
    }

    @Test
    public void testClosestIntMeter3() {
        int idealDistance = 90;
        int closest = ScalebarMapGraphic.closestInt(idealDistance, range);
        assertEquals( 10, closest );
    }

    @Test
    public void testClosestIntMeter4() {
        int idealDistance = 120;
        int closest = ScalebarMapGraphic.closestInt(idealDistance, range);
        assertEquals( 100, closest );
    }


    @Test
    public void testClosestIntKilometer1() {
        int idealDistance = 5342;
        int closest = ScalebarMapGraphic.closestInt(idealDistance, range);
        assertEquals( 1000, closest );
    }

    @Test
    public void testCalculateUnitAndLength1() throws Exception {
        Pair<Integer, Pair<Integer, Unit>> result = ScalebarMapGraphic.calculateUnitAndLength(1, 100, KILOMETER, METER );
        assertExpected( 100, 100, METER, result.getLeft(), result.getRight().getLeft(), result.getRight().getRight());
    }

    @Test
    public void testCalculateUnitAndLength2() throws Exception {
        Pair<Integer, Pair<Integer, Unit>> result = ScalebarMapGraphic.calculateUnitAndLength(100, 100, KILOMETER, METER);
        assertExpected( 100, 10, KILOMETER, result.getLeft(), result.getRight().getLeft(), result.getRight().getRight());
    }
    @Test
    public void testCalculateUnitAndLengthImperial() throws Exception {
        Pair<Integer, Pair<Integer, Unit>> result = ScalebarMapGraphic.calculateUnitAndLength(1, 100, YARD, FOOT);
        assertExpected( 91, 100, YARD, result.getLeft(), result.getRight().getLeft(), result.getRight().getRight());
    }

    private void assertExpected( int expectedLength, int expectedDistance, Unit expectedUnit, Integer barLengthPixels, Integer distance, Unit unit ) {
        assertEquals( expectedLength, barLengthPixels.intValue());
        assertEquals(expectedDistance, distance.intValue());
        assertEquals(expectedUnit, unit);
    }
}
