package VF.VectorShapes;

import org.aedificatores.libspeedydoge.Universal.Math.Geometry.Ellipse;
import org.aedificatores.libspeedydoge.Universal.Math.Pose;
import org.aedificatores.libspeedydoge.VF.VectorShapes.VectorShape;


/**
 * VectorShape in the form of an Ellipse
 */
public class VectorEllipse extends VectorShape {

    public VectorEllipse(){
        super(new Pose(), 1, 1);
        shape = new Ellipse(new Pose(), 1, 1);
    }
    public VectorEllipse(Pose location, double r1, double r2, double strength, double falloff){
        super(location, strength, falloff);
        shape = new Ellipse(location, r1, r2);
    }
}
