package org.aedificatores.libspeedydoge.VF.VectorShapes;

import org.aedificatores.libspeedydoge.Universal.Math.Geometry.Circle;
import org.aedificatores.libspeedydoge.Universal.Math.Pose;
import org.aedificatores.libspeedydoge.VF.VectorShapes.VectorShape;


/**
 * VectorShape in the form of a circle
 */
public class VectorCircle extends VectorShape {

    public VectorCircle(){
        super(new Pose(), 1, 1);
        shape = new Circle(new Pose(), 1);
    }
    public VectorCircle(Pose location, double radius, double strength, double falloff){
        super(location, strength, falloff);
        shape = new Circle(location, radius);
    }


}
