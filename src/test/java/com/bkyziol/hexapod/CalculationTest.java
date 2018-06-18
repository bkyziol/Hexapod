package com.bkyziol.hexapod;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bkyziol.hexapod.movement.FootPosition;

import static com.bkyziol.hexapod.movement.utils.CalculationUtils.*;

public class CalculationTest {

	private final double angle = 10;
	private final double x = 60;
	private final double y = 60;

	@Test
	public void test() throws Exception {
		FootPosition footPosition = calculateForTurn(angle, x, y);
		assertTrue(new Double(59.91243774155651).equals(footPosition.getX()));
		assertTrue(new Double(34.998648579255).equals(footPosition.getY()));
	}
}
