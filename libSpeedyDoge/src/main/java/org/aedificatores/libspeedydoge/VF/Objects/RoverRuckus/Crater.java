package org.aedificatores.libspeedydoge.VF.Objects.RoverRuckus;

import org.aedificatores.libspeedydoge.Universal.Math.Pose;



/**
 * Generates the vector field eminating from the crater lip
 * Field: Rover Ruckus
 **/
public class Crater extends VF.VectorShapes.VectorEllipse {
    public Crater(Pose location){
        super(location, 46.4, 51.4, 12, 0.1);
    }
    
    public Crater(Pose location, double strength, double falloff) {
        super(location, 46.4, 51.4, strength, falloff);
    }
}
