package org.aedificatores.teamcode.Universal.Autoplacement;

import java.util.Arrays;

// @Untested-Functional

/**
 * Written by Theodore Lovinski 6/10/2019
 *
 * Visual placement representation for intended block placement in auto.
 *
 * Keep in mind that (0, 0) is functionally (1, 1), where (1, 1, 0) and (1, 1, 1) are different.
 */

public class TextRepresentation {
	private enum OrientationFromOrigin { UP, DOWN, LEFT, RIGHT }
	private static int maxAllowedHeight;
	public static boolean allowFloatingBlocks = false;
	public static boolean orientViewHorizontally = false;
	public static int currentZLevel = 0;
	private static final byte IS_EDITING_ALIAS = 0x30;
	private static byte representation[] = new byte[32 * maxAllowedHeight];

	TextRepresentation(int mah) {
		maxAllowedHeight = mah;
		Arrays.fill(representation, (byte) 0x00);
	}

	// @Overload
	TextRepresentation(int mah, byte[] existingRepresentation) {
		maxAllowedHeight = mah;
		representation = existingRepresentation;
	}

	private int twoToOneDimensions(int x, int y, int z) {
		return (x * 4) + (y * 8) + (z * maxAllowedHeight);
	}

	public void placeStone(OrientationFromOrigin orientation, int x, int y, int z, byte r) {
		int auxX = x;
		int auxY = y;
		switch (orientation) {
			case UP:
				auxY = y - 1;
				break;
			case DOWN:
				auxY = y + 1;
				break;
			case LEFT:
				auxX = x - 1;
				break;
			case RIGHT:
				auxX = x + 1;
				break;
		}

		if (!allowFloatingBlocks && (representation[twoToOneDimensions(x, y, z - 1)] == 0x01 || representation[twoToOneDimensions(auxX, auxY, z - 1)] == 0x01)) {
			representation[twoToOneDimensions(x, y, z)] = r;
			representation[twoToOneDimensions(auxX, auxY, z)] = r;
		}
	}

	// @Overload @CouldBeBetter
	public void placeStone(OrientationFromOrigin orientation, int x, int y, byte r) {
		placeStone(orientation, x, y, currentZLevel, r);
	}

	public String display(String i1, String i2) {
		StringBuffer sRepresentation = new StringBuffer();
		for (int i = 0; i < representation.length; i++) {
			sRepresentation.append(representation[i] == 0x01 ? i1 : i2);
			if (i % (orientViewHorizontally ? 4 : 8) == 0) sRepresentation.append("\n");
		}
		return sRepresentation.toString();
	}
}
