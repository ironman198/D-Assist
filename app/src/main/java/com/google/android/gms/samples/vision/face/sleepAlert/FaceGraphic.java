/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.samples.vision.face.sleepAlert;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.samples.vision.face.sleepAlert.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;
    private Paint LeftEyeBrush ;

    private static final int COLOR_CHOICES[] = {
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.MAGENTA,
        Color.RED,
        Color.WHITE,
        Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;
    Button blackButton ;
    MediaPlayer player ;
    Context ctx;
    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;
    Timer timerr ;
    int eyesClosedCounter ;
    boolean rightEyeClosed= false;
    boolean leftEyeClosed = false;
    boolean musicStopped  = true;
    AssetManager am;


    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        this.LeftEyeBrush = new Paint();
        LeftEyeBrush.setStyle(Paint.Style.STROKE);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
        timerr = new Timer();
    }

    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }
void stopMusic()
{
    System.out.println("\n Srini setting stopMusic to True");
    this.musicStopped = true ;
    this.player.stop();
    this.player.reset();
    AssetFileDescriptor afd = null;
    try {
        afd = am.openFd("Song.mp3");
        this.player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        this.player.prepare();
    } catch (IOException e) {
        System.out.println("\n Srini inside Exception");
        e.printStackTrace();
    }
}

    void addMedianButtion(MediaPlayer mp,Button btn,AssetManager am,Context ctx)
    {
        this.player = mp ;
        this.blackButton = btn ;
        this.am = am ;
        this.ctx = ctx ;
        AssetFileDescriptor afd = null;
        try {
            afd = am.openFd("Song.mp3");
            this.player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            this.player.prepare();
        } catch (IOException e) {
            System.out.println("\n Srini inside Exception");
            e.printStackTrace();
        }
        System.out.println("Srini with list" + am.getLocales().toString());
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }
        boolean left_eye_closed = false;
        boolean right_eye_closed = false;
        for (Landmark landmark : this.mFace.getLandmarks()) {
            float px;
            float py;
            if (landmark.getType() == 4) {
                System.out.println("Srini with value of face right eye" + face.getIsLeftEyeOpenProbability());
                System.out.println("Srini with value of face position x eye" + landmark.getPosition().x);
                System.out.println("Srini with value of face position y eye" + landmark.getPosition().y);
                if (face.getIsLeftEyeOpenProbability()< 0.6f)
                {
                    px = landmark.getPosition().x;
                    py = landmark.getPosition().y;
                    this.LeftEyeBrush.setColor(-1);
//                canvas.drawCircle((float) this.mFaceArray[i].leftEye.x, (float) this.mFaceArray[i].leftEye.y, 21.0f, this.LeftEyeBrush);
                    canvas.drawCircle(px, py, 21.0f, LeftEyeBrush);
//                    RectF tmpRect = new RectF();
//                    tmpRect.top = 0;
//                    tmpRect.bottom = 21;
//                    tmpRect.left = 0;
//                    tmpRect.right = 21;
//                    canvas.drawOval(tmpRect, LeftEyeBrush);
                    left_eye_closed = true ;
//                    startMusic();
                }
                else
                {
                    px = landmark.getPosition().x;
                    py = landmark.getPosition().y;
                    this.LeftEyeBrush.setColor(-1);
//                canvas.drawCircle((float) this.mFaceArray[i].leftEye.x, (float) this.mFaceArray[i].leftEye.y, 21.0f, this.LeftEyeBrush);
                    canvas.drawCircle(px, py, 5.0f, LeftEyeBrush);
                    RectF tmpRect = new RectF();
                    tmpRect.top = 0;
                    tmpRect.bottom = 21;
                    tmpRect.left = 0;
                    tmpRect.right = 21;
                    canvas.drawOval(tmpRect, LeftEyeBrush);

                }


            } else if (landmark.getType() == 10) {
                if (face.getIsRightEyeOpenProbability()<0.6f)
                {
                    px = translateX(landmark.getPosition().y);
                    py = translateY(landmark.getPosition().x);
                    this.LeftEyeBrush.setColor(-1);
//                canvas.drawCircle((float) this.mFaceArray[i].leftEye.x, (float) this.mFaceArray[i].leftEye.y, 21.0f, this.LeftEyeBrush);
                    canvas.drawCircle(py, px, 21.0f, LeftEyeBrush);
                    right_eye_closed = true ;

                }
                else
                {
                    px = translateX(landmark.getPosition().x);
                    py = translateY(landmark.getPosition().y);
                    this.LeftEyeBrush.setColor(-1);
//                canvas.drawCircle((float) this.mFaceArray[i].leftEye.x, (float) this.mFaceArray[i].leftEye.y, 21.0f, this.LeftEyeBrush);
                    canvas.drawCircle(py, px, 5.0f, LeftEyeBrush);

                }
            }
        }
//                // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
//    canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
//        canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
//        canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
//        canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
//        canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - ID_X_OFFSET*2, y - ID_Y_OFFSET*2, mIdPaint);

        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
//        canvas.drawRect(left, top, right, bottom, mBoxPaint);
        System.out.println("Srini with rightEyeClosed"+rightEyeClosed);
        System.out.println("Srini with leftEyeClosed"+leftEyeClosed);
        System.out.println("Srini with musicStopped"+musicStopped);
        if (right_eye_closed == true && left_eye_closed == true && this.musicStopped == true)
        {
            eyesClosedCounter = eyesClosedCounter + 1 ;
            if (eyesClosedCounter > 4)
            {
                startMusic();
                eyesClosedCounter = 0;
            }
        }
    }
    public void startMusic()
    {
        Log.wtf("Srini after prepare", "");
//                            this.blackButton = (Button)findViewById(R.id.Button01);
        System.out.println("\n Srini inside StartMusic");
//        System.out.print("\n Srini wiht "+blackButton.getEditableText().toString());

//        this.player = new MediaPlayer();
//        AssetManager am = ctx.getAssets();

        this.player.start();

        startRandomButton();
        musicStopped = false ;
    }
    private void startRandomButton() {

        setButtonRandomPosition();
//        timerr.schedule(new TimerTask() {
//            @Override
//            public void run() {
//
//            }
//        }, 0, 2500);//Update button every 2.5 second
    }
        private void setButtonRandomPosition(){

        int randomX = new Random().nextInt(getDisplaySize(ctx).x);
        int randomY = new Random().nextInt(getDisplaySize(ctx).y);
            System.out.println(" randomX"+randomX);
            System.out.println(" randomY" + randomY);
            if(randomX > 400)
            {
                randomX = 400;
            }
            if (randomY >800)
            {
                randomY = 800 ;
            }
        this.blackButton.setX(randomX);
        this.blackButton.setY(randomY);
        this.blackButton.setVisibility(View.VISIBLE);
        System.out.println(" Srini wth " + this.blackButton.isEnabled());

    }


    public static Point getDisplaySize( Context context) {
        Point point = new Point();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getSize(point);
        return point;
    }
}
