package org.aedificatores.teamcode.Vision;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class RingDetector extends OpenCvPipeline {
    public enum RingStackType {
        NONE(0), ONE(100000), QUAD(400000);

        long thresh;

        RingStackType(long thresh) {
            this.thresh = thresh;
        }

        public long getThresh() {
            return thresh;
        }
    }

    private Mat hsvImage;
    private Mat bgrImage;
    private Mat threshold;
    private Mat thresholdAfterROI;
    private Rect roi;

    private Mat colSum;



    private int[] colSumArray;

    private long imageSum = 0;
    private RingStackType ringStackType;

    private int screenWidth, screenHeight;
    private int H_MIN = 0,
            S_MIN = 185,
            V_MIN = 40,
            H_MAX = 70,
            S_MAX = 255,
            V_MAX = 255;

    public RingDetector(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        hsvImage = new Mat();
        bgrImage = new Mat();
        threshold = new Mat();
        thresholdAfterROI = new Mat();

        colSum = new Mat();
        colSumArray = new int[screenWidth];
        roi = new Rect(50, 40, 240, 100);
    }

    @Override
    public Mat processFrame(Mat input) {
        // For whatever reason, OpenCV requires you to do some weird acrobatics to convert
        // RGBA to HSV, that's what is done here
        Imgproc.cvtColor(input, bgrImage, Imgproc.COLOR_RGBA2BGR);
        Imgproc.cvtColor(bgrImage, hsvImage, Imgproc.COLOR_BGR2HSV_FULL);

        // Threshold hsv values
        Core.inRange(hsvImage,
                new Scalar(H_MIN, S_MIN, V_MIN),
                new Scalar(H_MAX, S_MAX, V_MAX),
                threshold);
        thresholdAfterROI = threshold.submat(roi);

        Imgproc.cvtColor(threshold, bgrImage, Imgproc.COLOR_GRAY2BGR);
        Imgproc.cvtColor(bgrImage, input, Imgproc.COLOR_BGR2RGBA);
        Imgproc.rectangle(input, roi, new Scalar(0,255,0), 4);

        Core.reduce(thresholdAfterROI, colSum, 0, Core.REDUCE_SUM, CvType.CV_32S);

        long sum = 0;
        colSum.get(0, 0, colSumArray);
        for (int i = 0; i < screenWidth; ++i) {
            sum += colSumArray[i];
        }

        this.setImageSum(sum);
        this.setRingStackType(sum);

        return input;
    }

    private void setRingStackType(long sum) {
        if (sum > RingStackType.QUAD.getThresh()) {
            ringStackType = RingStackType.QUAD;
        } else if (sum > RingStackType.ONE.getThresh()) {
            ringStackType = RingStackType.ONE;
        } else {
            ringStackType = RingStackType.NONE;
        }
    }

    public RingStackType getRingStackType() {
        return ringStackType;
    }

    public void setImageSum(long imageSum) {
        this.imageSum = imageSum;
    }

    public long getImageSum() {
        return imageSum;
    }

    public void close() {
        hsvImage.release();
        bgrImage.release();
        threshold.release();
        thresholdAfterROI.release();
        colSum.release();
    }
}