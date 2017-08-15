package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

/**
 * Created by priyanshu on 13/8/17.
 */

public class Emojifier {

    private static final String LOG_TAG = "GetClassification";

    public static int detectFaces(Context context, Bitmap bitmap){

        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        //Given a bitmap, we can create Frame instance from the bitmap to supply to the detector
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        //The detector can be called synchronously with a frame to detect faces:
        SparseArray<Face> faces = detector.detect(frame);

        for(int i=0; i<faces.size();i++){
            Face faceAtI = faces.valueAt(i);
            getClassifications(faceAtI);
        }

        detector.release();
        return faces.size();
    }

    public static void getClassifications(Face face){

        float smile = face.getIsSmilingProbability();
        float leftEye = face.getIsLeftEyeOpenProbability();
        float rightEye = face.getIsRightEyeOpenProbability();
        Log.v(LOG_TAG,"Smiling Probability - " + String.valueOf(smile));
        Log.v(LOG_TAG,"LeftEye open Probability - " + String.valueOf(leftEye));
        Log.v(LOG_TAG,"RightEye open Probability - " + String.valueOf(rightEye));
    }


}

