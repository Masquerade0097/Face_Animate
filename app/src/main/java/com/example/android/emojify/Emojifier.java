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

    public static boolean smiling,leftEyeClosed,rightEyeClosed;

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
            whichEmoji(faceAtI);
        }

        detector.release();
        return faces.size();
    }


    private enum Emoji {
        LEFT_WINK,RIGHT_WINK,CLOSED_EYE_SMILE,RIGHT_WINK_FROWN,LEFT_WINK_FROWN,
        CLOSED_EYE_FROWN,FROWN,SMILE
    }

    public static Emoji whichEmoji(Face face){

        float smile = face.getIsSmilingProbability();
        float leftEyeC = face.getIsLeftEyeOpenProbability();
        float rightEyeC = face.getIsRightEyeOpenProbability();
        Log.v(LOG_TAG,"Smiling Probability - " + String.valueOf(smile));
        Log.v(LOG_TAG,"LeftEye open Probability - " + String.valueOf(leftEyeC));
        Log.v(LOG_TAG,"RightEye open Probability - " + String.valueOf(rightEyeC));

        if(smile>0.15){
            smiling = true;
        }
        if(leftEyeC<0.5){
            leftEyeClosed = true;
        }
        if(rightEyeC<0.5){
            rightEyeClosed = true;
        }


        // Determine and log the appropriate emoji
        Emoji emoji;
        if(smiling) {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK;
            }  else if(rightEyeClosed && !leftEyeClosed){
                emoji = Emoji.RIGHT_WINK;
            } else if (leftEyeClosed){
                emoji = Emoji.CLOSED_EYE_SMILE;
            } else {
                emoji = Emoji.SMILE;
            }
        } else {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK_FROWN;
            }  else if(rightEyeClosed && !leftEyeClosed){
                emoji = Emoji.RIGHT_WINK_FROWN;
            } else if (leftEyeClosed){
                emoji = Emoji.CLOSED_EYE_FROWN;
            } else {
                emoji = Emoji.FROWN;
            }
        }

        // Log the chosen Emoji
        Log.d(LOG_TAG, "whichEmoji: " + emoji.name());
        return emoji;
    }

}

