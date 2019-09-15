package org.aedificatores.teamcode.Mechanisms;

import org.aedificatores.teamcode.Universal.Math.Pose;
import org.aedificatores.teamcode.Universal.Math.Vector2;

public class OdometryWheels {
	public class Wheels3 {
		public double encPerInch;
		public Pose wheel1, wheel2, wheel3 = new Pose();
		public Wheels3(Pose p1, p2, p3) {
			wheel1 = p1;
			wheel2 = p2;
			wheel3 = p4;
		}
	}

	public Pose standardPositionTrack(Pose currentPos, double x/*read3*/, double l/*read2*/, double r/*read1*/){
		double diff1 = Math.cos(wheel1.angleOfVector() - wheel1.angle);
		double diff2 = Math.cos(wheel2.angleOfVector() - wheel2.angle);
		double diff3 = Math.cos(wheel3.angleOfVector() - wheel3.angle);
		double xDiff = wheel1.radius() / wheel2.radius();
		double angle = (r / diff1 - l / diff2 * xDiff) / ((wheel1.radius() * 2) * encPerInch);

		x -= angle * wheel3.radius() * diff3;
		r -= angle * wheel1.radius() * diff1;
		l -= angle * wheel2.radius() * diff2;

		//assuming the calculations were done correctly, l and r should now be equal
		Vector2 velocity = new Vector2(x, l);
		Vector2 velocity2 = new Vector2();

		if (angle != 0) {
			double rad = velocity.magnitude() / angle;
			velocity2.setFromPolar(rad, angle);
			velocity2.x -= rad;
			velocity2.rotate(velocity.angle());
		} else {
			velocity2 = velocity;
		}

		currentPos.x += velocity2.x;
		currentPos.y += velocity2.y;
		currentPos.angle += angle;

		return currentPos;
	}
}
