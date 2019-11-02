package org.aedificatores.libspeedydoge.Universal.Math.Geometry;

import org.aedificatores.libspeedydoge.Universal.Math.Pose;
import org.aedificatores.libspeedydoge.Universal.Math.Vector2;

/**
 * Superclass for any geometric object
 */
public abstract class Shape {
    public Pose location = new Pose();

    /*
    returns the point on perimeter of the Shape closest to the input point
     */
    public abstract Vector2 getClosestPoint(Vector2 point);

}
