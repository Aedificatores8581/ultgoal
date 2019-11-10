package org.aedificatores.teamcode.Universal.Autoplacement;

import org.aedificatores.teamcode.Universal.Math.Vector3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;
import java.nio.file.*;

public class Placement {
	Stack<Vector3> linear = new Stack<Vector3>();

	class Vector3Alias {
		int x, y;
		BlockOrientationAlias boa;

		Vector3Alias(Vector3 vector) {
			x = (int) vector.x;
			y = (int) vector.y;

			switch ((int) vector.z) {
				case 1:
					boa = BlockOrientationAlias.UP;
					break;
				case 0:
					boa = BlockOrientationAlias.DOWN;
					break;
				case 3:
					boa = BlockOrientationAlias.LEFT;
					break;
				case 4:
					boa = BlockOrientationAlias.RIGHT;
					break;
				default:
					boa = BlockOrientationAlias.STUB;
					break;
			}
		}
	}

	public enum BlockOrientationAlias {
		UP, DOWN, LEFT, RIGHT, STUB
	}

	Placement(String exchangeString) {
		String delimiter = "[,]+";
		String[] tokens = exchangeString.split(delimiter);
		for (int i = 0; i < tokens.length; i += 3) {
			// TODO: Does this work without casting, furthermore would it be faster?
			linear.push(new Vector3(
					(double) Integer.parseInt(tokens[i]),
					(double) Integer.parseInt(tokens[i + 1]),
					(double) Integer.parseInt(tokens[i + 2]))
			);
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

	Placement(String file, int _) {
	}
}
