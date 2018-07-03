package com.bkyziol.hexapod;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.bkyziol.hexapod.movement.FootPosition;

import static com.bkyziol.hexapod.movement.utils.CalculationUtils.*;

public class CalculationTest {

	private static double angle = 10;
	private static double x = 70;
	private static double y = 40;

	@BeforeClass
	public static void init() {
		angle = 10;
		x = 70;
		y = 40;
	}

	@Test
	public void testOutsideLegIncreaseAngle() throws Exception {
		FootPosition footPosition = calculateForTurnOutsideLeg(angle, x, y, true);
		assertTrue(new Double(46.86111943831526).equals(footPosition.getX()));
		assertTrue(new Double(58.61344575607814).equals(footPosition.getY()));
	}

	@Test
	public void testOutsideLegDecreaseAngle() throws Exception {
		FootPosition footPosition = calculateForTurnOutsideLeg(angle, x, y, false);
		assertTrue(new Double(89.55713538995165).equals(footPosition.getX()));
		assertTrue(new Double(17.653040208979817).equals(footPosition.getY()));
	}

	@Test
	public void testInsideLegIncreaseAngle() throws Exception {
		FootPosition footPosition = calculateForTurnInsideLeg(angle, x, y, true);
		assertTrue(new Double(61.266685795981104).equals(footPosition.getX()));
		assertTrue(new Double(59.87312732071319).equals(footPosition.getY()));
	}

	@Test
	public void testInsideLegDecreaseAngle() throws Exception {
		FootPosition footPosition = calculateForTurnInsideLeg(angle, x, y, false);
		assertTrue(new Double(75.15156903228574).equals(footPosition.getX()));
		assertTrue(new Double(18.912721773614663).equals(footPosition.getY()));
	}
}
