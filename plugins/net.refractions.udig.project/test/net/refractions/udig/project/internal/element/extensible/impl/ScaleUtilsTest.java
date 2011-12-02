package net.refractions.udig.project.internal.element.extensible.impl;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import net.refractions.udig.project.internal.render.impl.ScaleUtils;

import org.junit.Test;

public class ScaleUtilsTest {
	private static SortedSet<Double> SCALES = new TreeSet<Double>(Arrays.asList(10.0,100.0,1000.0,10000.0,100000.0,1000000.0));
	@Test
	public void calculateClosestScale() {
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 0.0, .5),0.0001);
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 10.0, .5),0.0001);
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 9.0, .5),0.0001);
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 5.1, .5),0.0001);
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 54, .5),0.0001);
		assertEquals(100.0, ScaleUtils.calculateClosestScale(SCALES, 56, .5),0.0001);
		assertEquals(100.0, ScaleUtils.calculateClosestScale(SCALES, 99.0, .5),0.0001);
		assertEquals(1000000.0, ScaleUtils.calculateClosestScale(SCALES, 10000000.0, .5),0.0001);
		
		assertEquals(1000000.0, ScaleUtils.calculateClosestScale(SCALES, 10000000.0, 0),0.0001);
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 99, 0),0.0001);
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 0, 0),0.0001);
		assertEquals(100.0, ScaleUtils.calculateClosestScale(SCALES, 101, 0),0.0001);
		assertEquals(100.0, ScaleUtils.calculateClosestScale(SCALES, 150, 0),0.0001);
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 0, 1),0.0001);
		assertEquals(100.0, ScaleUtils.calculateClosestScale(SCALES, 11, 1),0.0001);
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 10, 1),0.0001);
		assertEquals(1000000.0, ScaleUtils.calculateClosestScale(SCALES, 10000000.0, 1),0.0001);
		
		assertEquals(100.0, ScaleUtils.calculateClosestScale(SCALES, 51.0, .7),0.0001);
		assertEquals(10.0, ScaleUtils.calculateClosestScale(SCALES, 30.0, .7),0.0001);
		
	}
}
