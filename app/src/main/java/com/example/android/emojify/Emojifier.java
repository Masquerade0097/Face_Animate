package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import static com.example.android.emojify.Emojifier.Emoji.CLOSED_EYE_FROWN;
import static com.example.android.emojify.Emojifier.Emoji.CLOSED_EYE_SMILE;
import static com.example.android.emojify.Emojifier.Emoji.FROWN;
import static com.example.android.emojify.Emojifier.Emoji.LEFT_WINK;
import static com.example.android.emojify.Emojifier.Emoji.LEFT_WINK_FROWN;
import static com.example.android.emojify.Emojifier.Emoji.RIGHT_WINK;
import static com.example.android.emojify.Emojifier.Emoji.RIGHT_WINK_FROWN;
import static com.example.android.emojify.Emojifier.Emoji.SMILE;


/**
 * Created by priyanshu on 13/8/17.
 */

public class Emojifier {

    private static final String LOG_TAG = "GetClassification";
    private static final float EMOJI_SCALE_FACTOR = .9f;

    public static boolean smiling,leftEyeClosed,rightEyeClosed;

    public static Bitmap detectFacesAndOverlayEmoji(Context context, Bitmap bitmap){
        Bitmap resultBitmap = bitmap;

        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        //Given a bitmap, we can create Frame instance from the bitmap to supply to the detector
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        //The detector can be called synchronously with a frame to detect faces:
        SparseArray<Face> faces = detector.detect(frame);
        int numberOfFaces = faces.size();

        if(numberOfFaces == 0){
//            Toast.makeText(this,"No Face Detected",Toast.LENGTH_LONG).show();
            Log.v(LOG_TAG,"No Face detected");
        }else if(numberOfFaces < 0){
            Log.v(LOG_TAG,"Error in detecting faces");
        } else{
            Log.v(LOG_TAG,numberOfFaces + "Face detected");
        }


        for(int i=0; i<faces.size();i++){
            Face faceAtI = faces.valueAt(i);

            Bitmap emojiBitmap;
            switch(whichEmoji(faceAtI)){
                case LEFT_WINK :
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.leftwink);
                    break;
                case RIGHT_WINK:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.rightwink);
                    break;
                case CLOSED_EYE_SMILE:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.closed_frown);
                    break;
                case RIGHT_WINK_FROWN:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.rightwinkfrown);
                    break;
                case LEFT_WINK_FROWN:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.leftwinkfrown);
                    break;
                case CLOSED_EYE_FROWN:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.closed_frown);
                    break;
                case FROWN:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.frown);
                    break;
                case SMILE:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.smile);
                    break;
                default:
                    emojiBitmap = null;
                    Toast.makeText(context, R.string.no_emoji, Toast.LENGTH_SHORT).show();
            }
            //Add the emoji to face
            resultBitmap = addBitmapToFace(resultBitmap,emojiBitmap,faceAtI);
        }

        detector.release();
        return resultBitmap;
    }


    public enum Emoji {
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
                emoji = LEFT_WINK;
            }  else if(rightEyeClosed && !leftEyeClosed){
                emoji = RIGHT_WINK;
            } else if (leftEyeClosed){
                emoji = CLOSED_EYE_SMILE;
            } else {
                emoji = SMILE;
            }
        } else {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = LEFT_WINK_FROWN;
            }  else if(rightEyeClosed && !leftEyeClosed){
                emoji = RIGHT_WINK_FROWN;
            } else if (leftEyeClosed){
                emoji = CLOSED_EYE_FROWN;
            } else {
                emoji = FROWN;
            }
        }

        // Log the chosen Emoji
        Log.d(LOG_TAG, "whichEmoji: " + emoji.name());
        return emoji;
    }

    /**
     * Combines the original picture with the emoji bitmaps
     *
     * @param backgroundBitmap The original picture
     * @param emojiBitmap      The chosen emoji
     * @param face             The detected face
     * @return The final bitmap, including the emojis over the faces
     */
    private static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, Face face) {

        // Initialize the results bitmap to be a mutable copy of the original image
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());

        // Scale the emoji so it looks better on the face
        float scaleFactor = EMOJI_SCALE_FACTOR;

        // Determine the size of the emoji to match the width of the face and preserve aspect ratio
        int newEmojiWidth = (int) (face.getWidth() * scaleFactor);
        int newEmojiHeight = (int) (emojiBitmap.getHeight() *
                newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);


        // Scale the emoji
        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);

        // Determine the emoji position so it best lines up with the face
        float emojiPositionX =
                (face.getPosition().x + face.getWidth() / 2) - emojiBitmap.getWidth() / 2;
        float emojiPositionY =
                (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;

        // Create the canvas and draw the bitmaps to it
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);

        return resultBitmap;
    }

}

