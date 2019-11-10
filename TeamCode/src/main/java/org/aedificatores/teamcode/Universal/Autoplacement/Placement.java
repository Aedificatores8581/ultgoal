package org.aedificatores.teamcode.Universal.Autoplacement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class Placement {
	public enum BlockOrientationAlias {
		UP, DOWN, LEFT, RIGHT, STUB
	}

	public class BlockPose {
		BlockOrientationAlias orientation;
		int x = 0;
		int y = 0;
		int z = 0;

		BlockPose(int x, int y, int z, BlockOrientationAlias o) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.orientation = o;
		}

		public String toString() {
			return "(" + x + "," + y + "," + z + "), " + orientation;
		}
	}

	Stack<BlockPose> linear = new Stack<BlockPose>();

	Placement(String exchangeString) {
		String delimiter = "[,]+";
		String[] tokens = exchangeString.split(delimiter);
		for (int i = 0; i < tokens.length; i += 4) {
			BlockOrientationAlias asAlias;

			switch (Integer.parseInt(tokens[i + 3])) {
				case 0:
					asAlias = BlockOrientationAlias.DOWN;
					break;
				case 1:
					asAlias = BlockOrientationAlias.UP;
					break;
				case 2:
					asAlias = BlockOrientationAlias.LEFT;
					break;
				case 3:
					asAlias = BlockOrientationAlias.RIGHT;
					break;
				default:
					asAlias = BlockOrientationAlias.STUB;
			}

			linear.push(new BlockPose(Integer.parseInt(tokens[i]), Integer.parseInt(tokens[i + 1]), Integer.parseInt(tokens[i + 2]), asAlias));
		}
	}

	private static String readFileAsString(String fileName) throws Exception {
		StringBuilder s = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			// DISGUSTING
			String line;
			while ((line = br.readLine()) != null) {
				s.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return s.toString();
	}
}