package org.aedificatores.libspeedydoge.VF.Objects.Skystone;

import org.aedificatores.libspeedydoge.Universal.Math.Pose;
import org.aedificatores.libspeedydoge.VF.VectorShapes.VectorRectangle;

public class Stone extends VectorRectangle {
    public boolean skystone = false;
    public Stone(Pose pose){
        super(pose, 4,8, 24, 0.01);
    }


}
