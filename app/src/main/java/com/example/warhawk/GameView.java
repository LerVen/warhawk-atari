package com.example.warhawk;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable{

    private Thread thread;
    private boolean isPlaying;
    private int screenX, screenY;
    public static float screenRatioX, screenRatioY;
    private Paint paint;
    private Flight flight;
    private Background background1, background2;

    private int imageSizeX = 2380, imageSizeY = 2440;

    public GameView(Context context, int screenX, int screenY){
        super(context);

        this.screenX = screenX;
        this.screenY = screenY;

        screenRatioX = 1080f / screenX;
        screenRatioY = 1920f / screenY;

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        flight = new Flight(screenX, screenY, getResources());

        background2.y = 0 - screenY;

        paint = new Paint();

    }

    @Override
    public void run() {

        while(isPlaying){
            update();
            draw();
            sleep();
        }

    }

    private void update(){
        background1.y += 10 * screenRatioY;
        background2.y += 10 * screenRatioY;

        if(background1.y + background1.background.getHeight() > screenY * 2){
            background1.y = 0 - screenY;
        }

        if(background2.y + background2.background.getHeight() > screenY * 2){
            background2.y = 0 - screenY;
        }
    }

    private void draw() {

        if(getHolder().getSurface().isValid()){
            Canvas canvas = getHolder().lockCanvas();

            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            canvas.drawBitmap(flight.getFlight(), flight.x, flight.y, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void sleep(){
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause() {
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_MOVE:

                if(event.getY() >= flight.y
                        && event.getY() <= flight.y + flight.height
                        && event.getX() >= flight.x
                        && event.getX() <= flight.x + flight.width
                ){
                    flight.x = (int) event.getX() - (flight.width / 2);
                    flight.y = (int) event.getY() - (flight.height / 2);
                }
        }
        return true;
    }
}
