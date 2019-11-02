package org.aedificatores.libspeedydoge.VF.Feilds;

import org.aedificatores.libspeedydoge.Universal.Math.Geometry.Rectangle;
import org.aedificatores.libspeedydoge.Universal.Math.Pose;
import org.aedificatores.libspeedydoge.VF.Boundary;
import org.aedificatores.libspeedydoge.VF.Objects.FieldWall;
import org.aedificatores.libspeedydoge.VF.VectorField;
import org.aedificatores.libspeedydoge.VF.VectorFieldComponent;
import org.aedificatores.libspeedydoge.VF.VectorShapes.VectorCircle;

import java.util.ArrayList;

public class TestField extends VectorField {

    public TestField(){
        super(new ArrayList<VectorFieldComponent>(), new ArrayList<Boundary>());
        FieldWall wall= new FieldWall();
        boundaries.add(wall);
        Rectangle rectangle = new Rectangle(new Pose(), 23, 23);
        obstacles.add(new VectorCircle(new Pose(-25, 0, 0), 2.75, 24, 0.01));
    }
}
