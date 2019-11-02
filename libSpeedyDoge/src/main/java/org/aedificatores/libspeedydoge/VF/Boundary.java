package org.aedificatores.libspeedydoge.VF;

import org.aedificatores.libspeedydoge.Universal.Math.Pose;
import org.aedificatores.libspeedydoge.Universal.Math.Vector2;

/**
 * Defines points in a VectorField with an undefined output
 */
public interface Boundary extends ActivatableComponent {
    /*
    if position is inside the boundary region, returns a vector that points parallel to or away from the boundary, otherwise return destination
     */
    Vector2 interact(Pose position, Vector2 destination);
}
