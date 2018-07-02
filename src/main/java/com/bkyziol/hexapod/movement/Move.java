package com.bkyziol.hexapod.movement;

@FunctionalInterface
public interface Move {
	void execute() throws InterruptedException, BodyMovementException;
}
