package org.aedificatores.teamcode.Universal.Math;

public class Vector2 {
	public double x, y;

	public Vector2() {
		x = 0.0;
		y = 0.0;
	}

	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void setFromPolar(double r, double theta) {
		this.x = Math.cos(theta) * r;
		this.y = Math.sin(theta) * r;
	}

	public void add(Vector2 vector) {
        x += vector.x;
        y += vector.y;
    }

    public void subtract(Vector2 vector) {
        vector.scalarMultiply(-1);
        add(vector);
    }

	public Vector2 scalarMultiply(double a) {
        x *= a;
        y *= a;
        return this;
    }

	public double magnitude() {
		return Math.hypot(x, y);
	}

	public double dot(Vector2 vector) {
        return vector.x * this.x + vector.y * this.y;
    }

	public Vector3 cross(Vector2 vector) {
        Vector3 a = new Vector3();
        Vector3 b = new Vector3();

        a.x = this.x;
        a.y = this.y;

        b.x = vector.x;
        b.y = vector.y;

        return a.cross(b);
    }

	public double angle() {
		return Math.atan2(y, x);
	}

	public double angleBetween(Vector2 vector) {
        return Math.acos(this.dot(vector) / (this.magnitude() * vector.magnitude()));
    }

	public void rotate(double angle){
        double tempX = x, tempY = y;
        x = Math.cos(angle) * tempX - Math.sin(angle) * tempY;
        y = Math.sin(angle) * tempX + Math.cos(angle) * tempY;
    }

	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
