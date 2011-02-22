package net.refractions.udig.project.render;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import net.refractions.udig.project.tests.support.AbstractProjectTestCase;

import com.vividsolutions.jts.geom.Envelope;

public class TileCalculatorTest extends AbstractProjectTestCase {

	private TileCalculator tileCalculator;

	@Override
	protected void setUp() throws Exception {
        super.setUp();

		this.tileCalculator=new TileCalculator(AffineTransform.getTranslateInstance(-200,-200), new Dimension(50,50));
		tileCalculator.setBounds(new Envelope( 100,600,100,600 ) );
	}


	/*
	 * Test method for 'net.refractions.udig.project.render.TileCalculator.numXTiles()'
	 */
	public void testNumXTiles() {
		assertEquals(10, tileCalculator.numXTiles());
		tileCalculator.setBounds(new Envelope(100,580,100,110));
		assertEquals(10, tileCalculator.numXTiles());
		tileCalculator.setBounds(new Envelope(100,110,0,600));
		assertEquals(1, tileCalculator.numXTiles());

		tileCalculator.setBounds(new Envelope( 100,200,100,200 ) );
		tileCalculator.setTileSize(new Dimension( 10,50 ));
		assertEquals(10, tileCalculator.numXTiles());
	}

	/*
	 * Test method for 'net.refractions.udig.project.render.TileCalculator.numYTiles()'
	 */
	public void testNumYTiles() {
		assertEquals(10, tileCalculator.numYTiles());
		tileCalculator.setBounds(new Envelope(100,580,100,110));
		assertEquals(1, tileCalculator.numYTiles());
		tileCalculator.setBounds(new Envelope(100,110,110,580));
		assertEquals(10, tileCalculator.numYTiles());

		tileCalculator.setBounds(new Envelope( 100,200,100,200 ) );
		tileCalculator.setTileSize(new Dimension( 50,10 ));
		assertEquals(10, tileCalculator.numYTiles());
	}

	/*
	 * Test method for 'net.refractions.udig.project.render.TileCalculator.getWorldTile(int, int)'
	 */
	public void testGetWorldTile() {
		Envelope tile = tileCalculator.getWorldTile(0,0);
		assertEquals(new Envelope(100,150,100,150), tile);
		tile = tileCalculator.getWorldTile(2,1);
		assertEquals(new Envelope(200,250,150,200), tile);
		tile = tileCalculator.getWorldTile(2,2);
		assertEquals(new Envelope(200,250,200,250), tile);
		tileCalculator.setBounds(new Envelope( 100,110,100,200 ) );
		tile = tileCalculator.getWorldTile(0,0);
		assertEquals(new Envelope(100,110,100,150), tile);

		tileCalculator.setBounds(new Envelope( 100,200,100,200 ) );
		tileCalculator.setTileSize(new Dimension( 10,50 ));
		tile = tileCalculator.getWorldTile(0,0);
		assertEquals(new Envelope(100,110,100,150), tile);
	}

	/*
	 * Test method for 'net.refractions.udig.project.render.TileCalculator.getScreenTile(int, int)'
	 */
	public void testGetScreenTile() {
		Rectangle tile = tileCalculator.getScreenTile(0,0);
		assertEquals(new Rectangle(-100,-100,50,50), tile);

		tile = tileCalculator.getScreenTile(2,1);
		assertEquals(new Rectangle(0,-50,50,50), tile);

		tile = tileCalculator.getScreenTile(2,2);
		assertEquals(new Rectangle(0,0,50,50), tile);

		tileCalculator.setBounds(new Envelope( 100,110,100,200 ) );
		tile = tileCalculator.getScreenTile(0,0);
		assertEquals(new Rectangle(-100,-100,10,50), tile);

		tileCalculator.setBounds(new Envelope( 100,200,100,200 ) );
		tileCalculator.setTileSize(new Dimension( 10,50 ));
		tile = tileCalculator.getScreenTile(0,0);
		assertEquals(new Rectangle(-100,-100,10,50), tile);
	}

	public void testGetWorldRandom() throws Exception {
		tileCalculator.setTileSize(new Dimension(250,250));

		Envelope tile1 = tileCalculator.getWorldRandom();
		Envelope tile2 = tileCalculator.getWorldRandom();
		Envelope tile3 = tileCalculator.getWorldRandom();
		Envelope tile4 = tileCalculator.getWorldRandom();

		assertNull(tileCalculator.getWorldRandom());
		assertNull(tileCalculator.getScreenRandom());

		assertFalse( tile1.equals(tile2));
		assertFalse( tile1.equals(tile3));
		assertFalse( tile1.equals(tile4));

		assertFalse( tile2.equals(tile1));
		assertFalse( tile2.equals(tile3));
		assertFalse( tile2.equals(tile4));

		assertFalse( tile3.equals(tile2));
		assertFalse( tile3.equals(tile1));
		assertFalse( tile3.equals(tile4));

		assertFalse( tile4.equals(tile2));
		assertFalse( tile4.equals(tile3));
		assertFalse( tile4.equals(tile1));
	}

	public void testGetScreenRandom() throws Exception {
		tileCalculator.setTileSize(new Dimension(250,250));

		Rectangle tile1 = tileCalculator.getScreenRandom();
		Rectangle tile2 = tileCalculator.getScreenRandom();
		Rectangle tile3 = tileCalculator.getScreenRandom();
		Rectangle tile4 = tileCalculator.getScreenRandom();

		assertNull(tileCalculator.getWorldRandom());
		assertNull(tileCalculator.getScreenRandom());

		assertFalse( tile1.equals(tile2));
		assertFalse( tile1.equals(tile3));
		assertFalse( tile1.equals(tile4));

		assertFalse( tile2.equals(tile1));
		assertFalse( tile2.equals(tile3));
		assertFalse( tile2.equals(tile4));

		assertFalse( tile3.equals(tile2));
		assertFalse( tile3.equals(tile1));
		assertFalse( tile3.equals(tile4));

		assertFalse( tile4.equals(tile2));
		assertFalse( tile4.equals(tile3));
		assertFalse( tile4.equals(tile1));
	}

}
