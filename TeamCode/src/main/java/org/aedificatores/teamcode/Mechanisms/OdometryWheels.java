package org.aedificatores.teamcode.Mechanisms;

import org.aedificatores.teamcode.Universal.Math.Pose;
import org.aedificatores.teamcode.Universal.Math.Vector2;

public class OdometryWheels {
    public double encPerInch;

    //the angles are the angles of the motors, not their wheels
    public Pose rightWheel;
    public Pose leftWheel;
    public Pose strafeWheel;

    public OdometryWheels(Pose p1, Pose p2, Pose p3, double encPerInch) {
        rightWheel = p1;
        leftWheel = p2;
        strafeWheel = p3;
        this.encPerInch = encPerInch;
    }

    public Pose standardPositionTrack(Pose currentPos, double x/*read3*/, double l/*read2*/, double r/*read1*/) {
        double rightDiff = Math.cos(rightWheel.angleOfVector() - rightWheel.angle),
                leftDiff = Math.cos(leftWheel.angleOfVector() - leftWheel.angle),
                strafeDiff = Math.cos(strafeWheel.angleOfVector() - strafeWheel.angle),
                xDiff = rightWheel.radius() / leftWheel.radius(),
                angle = (r / rightDiff - l / leftDiff * xDiff) / ((rightWheel.radius() * 2) * encPerInch);

        x -= angle * strafeWheel.radius() * strafeDiff;
        r -= angle * rightWheel.radius() * rightDiff;
        l -= angle * leftWheel.radius() * leftDiff;

        //assuming the calculations were done correctly, l and r should now be equal
        Vector2 velocity = new Vector2(x, l);
        Vector2 vel2 = new Vector2();

        if (angle != 0) {
            double rad = velocity.magnitude() / angle;
            vel2.setFromPolar(rad, angle);
            vel2.x -= rad;
            vel2.rotate(velocity.angle());
        } else {
            vel2 = velocity;
        }

        currentPos.x += vel2.x;
        currentPos.y += vel2.y;
        currentPos.angle += angle;

        return currentPos;
    }
}