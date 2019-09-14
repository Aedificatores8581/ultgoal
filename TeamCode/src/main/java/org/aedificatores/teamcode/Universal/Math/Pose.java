package org.aedificatores.teamcode.Universal.Math;

public class Pose {
	public double x, y, angle;

	public Pose() {
		x = 0;
		y = 0;
		angle = 0;
	}

	public Pose(double x, double y) {
		this(x, y, 0);
	}

	public Pose(Pose p) {
		this(p.x, p.y, p.angle);
	}

	public Pose(double x, double y, double angle) {
		this.x = x;
		this.y = y;
		this.angle = angle;
	}

	public void add(Pose p) {
		x += p.x;
		y += p.y;
		angle += p.angle;
	}

	public Vector2 toVector() {
		return new Vector2(x, y);
	}

	public double radius() {
		return Math.hypot(x, y);
	}

	public double angleOfVector() {
		return Math.atan2(y, x);
	}

	public Pose clone() {
		return new Pose(x, y, angle);
	}

	public String toString() {
		return "Pose(" + x + ", " + y + ", " + angle + ")";
	}
}
