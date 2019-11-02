package org.aedificatores.libspeedydoge.VF.Objects.RoverRuckus;

import org.aedificatores.libspeedydoge.Universal.Math.Pose;
import org.aedificatores.libspeedydoge.VF.VectorShapes.VectorCircle;

/**
 * Generates the vector field eminating from silver samples
 * Field: Rover Ruckus
 **/
public class SilverSample extends VectorCircle {
    public SilverSample(Pose location){
        super(location, 2.75/2, 12, 0.1);
    }
    
    public SilverSample(Pose location, double strength, double falloff){
        super(location, 2.75, strength, falloff);
    }
}
