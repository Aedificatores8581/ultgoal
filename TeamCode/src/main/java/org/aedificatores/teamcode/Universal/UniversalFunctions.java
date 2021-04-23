package org.aedificatores.teamcode.Universal;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.opencv.core.Point3;
import java.util.Arrays;

// TODO: Actually hasn't been cleaned up.

public class UniversalFunctions {
	public static double clamp(double min, double test, double max) {
        if (max < min) {
            double temp = min;
            min = max;
            max = temp;
        }
        return Math.max(Math.min(max, test), min);
    }

    public static Vector2d pos2vec(Pose2d pos) {
	    return new Vector2d(pos.getX(), pos.getY());
    }

    public static int clamp(int min, int test, int max) {
        if (max < min) {
            int temp = min;
            min = max;
            max = temp;
        }
        return Math.max(Math.min(max, test), min);
    }

    public static boolean withinTolerance(double test, double centerPoint, double lowerLimit, double upperLimit) {
        return test == clamp(centerPoint - lowerLimit, test, centerPoint + upperLimit);
    }

    public static boolean withinTolerance(double test, double lowerLimit, double upperLimit) {
        return test == clamp(lowerLimit, test, upperLimit);
    }

    public static double round(double d) {
        if (d < 0) {
            return Math.floor(d);
        }
        return Math.ceil(d);
    }

    public static double normalizeAngleDegrees(double angle, double newStartAngle) {
        angle -= newStartAngle;
        return normalizeAngleDegrees(angle);
    }

    public static double normalizeAngleDegrees(double angle) {
        double a2 = Math.abs(angle) % 360;
        if (Math.abs(angle) != angle) {
            return 360 - a2;
        }
        return a2;
    }

    public static double normalizeAngleRadians(double angle, double newStartAngle) {
        angle -= newStartAngle;
        return normalizeAngleRadians(angle);
    }

    public static double normalizeAngleRadians(double angle) {
        return Math.toRadians(normalizeAngleDegrees(Math.toDegrees(angle)));
    }

    public static double normalizeAngle180(double angle, double newStartAngle) {
        double ang = normalizeAngleDegrees(angle, newStartAngle);
        return normalizeAngle180(ang);
    }

    public static double normalizeAngle180(double angle) {
        double ang = normalizeAngleDegrees(angle);
        if(ang > 180){
            ang = -360 + ang;
        }
        return ang;
    }

    public static double normalizeAngle180Radians(double angle){
        return Math.toRadians(normalizeAngle180(Math.toDegrees(angle)));
    }

    public static double normalizeAngle180Radians(double angle, double newStartAngle){
        return Math.toRadians(normalizeAngle180(Math.toDegrees(angle), Math.toDegrees(newStartAngle)));
    }

    public static double max(double... ds) {
        switch(ds.length){
            case 0: return 0.0;
            case 1: return ds[0];
            case 2: return Math.max(ds[0], ds[1]);
            default: return Math.max(ds[0],
                    max(Arrays.copyOfRange(ds, 1, ds.length)));
        }
    }

    public static double maxAbs(double... ds) {
        for (int i = 0; i < ds.length; ++i){
            ds[i] = Math.abs(ds[i]);
        }
        switch(ds.length){
            case 0: return 0.0;
            case 1: return ds[0];
            case 2: return Math.max(ds[0], ds[1]);
            default: return Math.max(ds[0],
                    max(Arrays.copyOfRange(ds, 1, ds.length)));
        }
    }

    public static double min(double... ds){
        switch(ds.length){
            case 0: return 0.0;
            case 1: return ds[0];
            case 2: return Math.min(ds[0], ds[1]);
            default: return Math.min(ds[0],
                    min(Arrays.copyOfRange(ds,1, ds.length)));
        }
    }

    public static double minAbs(double... ds){
        for (int i = 0; i < ds.length; ++i){
            ds[i] = Math.abs(ds[i]);
        }
        switch(ds.length){
            case 0: return 0.0;
            case 1: return ds[0];
            case 2: return Math.min(ds[0], ds[1]);
            default: return Math.min(ds[0],
                    min(Arrays.copyOfRange(ds,1, ds.length)));
        }
    }

    public static String[] formatArrayStr(String str, int len){
        String[] ret = new String[len];
        int i = 0, ind = str.indexOf(","), next;
        while (ind != -1) {
            next = str.indexOf(",", ind + 1);
            if (next == -1)
                next = str.length();
            ret[i] = str.substring(ind, next);
        }
        return ret;
    }

    public static double getTimeInSeconds(){
        return System.nanoTime() / Math.pow(10, 9);
    }

    public static double[] sphericalToCartesian(double rad, double theta, double rho) {
        double  x = rad * Math.sin(theta) * Math.cos(rho),
                y = rad * Math.sin(theta) * Math.sin(rho),
                z = rad * Math.cos(theta);
        double[] cartesian = {x, y, z};
        return cartesian;
    }

    public static double[] cartesianToSpherical(double x, double y, double z){
        double  radius = Math.sqrt(x * x + y * y + z * z),
                theta  = Math.atan2(Math.hypot(x, y), z),
                rho    = Math.atan2(y, x);
        double[] spherical = {radius, theta, rho};
        return spherical;
    }

    public static Point3 subtract(Point3 point1, Point3 point2){
        return new Point3(point1.x - point2.x, point1.y - point2.y, point1.z - point2.z);
    }

    public static Point3 add(Point3 point1, Point3 point2){
        return new Point3(point1.x + point2.x, point1.y + point2.y, point1.z + point2.z);
    }

    public static double[] convertToArray(double x){
        double[] output = {x};
        return output;
    }
}
