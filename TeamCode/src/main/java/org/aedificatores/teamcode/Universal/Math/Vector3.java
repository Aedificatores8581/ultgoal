package org.aedificatores.teamcode.Universal.Math;

public class Vector3 {
	public double x, y, z;

	public Vector3() {
		x = 0;
		y = 0;
		z = 0;
	}

	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3(double i, double j, double k, double magnitude){
		final double v = i * i + k * k + j * j;
		x = magnitude / Math.sqrt(v) * i;
        y = magnitude / Math.sqrt(v) * j;
        z = magnitude / Math.sqrt(v) * k;
    }

    public Vector3(Vector3 componentForm, double magnitude){
        x = magnitude / componentForm.magnitude() * componentForm.x;
        y = magnitude / componentForm.magnitude() * componentForm.y;
        z = magnitude / componentForm.magnitude() * componentForm.z;
    }

    public void setFromComponentForm(Vector3 componentForm, double magnitude){
        componentForm.scalarMultiply(magnitude / componentForm.magnitude());
        componentForm.copyTo(this);
    }

	public void add(Vector3 vector) {
        x += vector.x;
        y += vector.y;
        z += vector.z;
    }

	public void subtract(Vector3 vector) {
        vector.scalarMultiply(-1);
        add(vector);
    }

	public void scalarMultiply(double a) {
        x *= a;
        y *= a;
        z *= a;
    }

	public double magnitude() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public double dot(Vector3 vector) {
        return vector.x * this.x + vector.y * this.y + vector.z * this.z;
    }

	public Vector3 cross(Vector3 vector) {
        Vector3 result = new Vector3();

        result.x = (this.y * vector.z) - (this.z * vector.y);
        result.y = (this.z * vector.x) - (this.x * vector.z);
        result.z = (this.x * vector.y) - (this.y * vector.x);

        return result;
    }

	public double angleBetween(Vector3 vector) {
        return Math.acos(this.dot(vector) / (this.magnitude() * vector.magnitude()));
    }

	public void rotateX(double angle){
        Vector2 temp = new Vector2(y, z);
        temp.rotate(angle);
        y = temp.x;
        z = temp.y;
    }

	 public void rotateY(double angle){
        Vector2 temp = new Vector2(x, z);
        temp.rotate(angle);
        x = temp.x;
        z = temp.y;
    }

    public void rotateZ(double angle){
        Vector2 temp = new Vector2(x, y);
        temp.rotate(angle);
        x = temp.x;
        y = temp.y;
    }

    public void copyTo(Vector3 intendedVector){
        intendedVector.x = x;
        intendedVector.y = y;
        intendedVector.z = z;
    }

	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
