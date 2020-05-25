package com.example.warhawk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable{

    private Thread thread;
    private boolean isPlaying, isGameOver = false;
    private int screenX, screenY, score = 0;
    public static float screenRatioX, screenRatioY;
    private Paint paint;
    private Enemy[] enemies;
    private SharedPreferences prefs;
    private Random random;
    private SoundPool soundPool;
    private List<Bullet> bullets;
    private int sound;
    private Flight flight;
    private GameActivity activity;
    private Background background1, background2;

    private int imageSizeX = 2380, imageSizeY = 2440;

    public GameView(GameActivity activity, int screenX, int screenY){
        super(activity);

        this.activity = activity;

        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        sound = soundPool.load(activity, R.raw.shoot, 1);

        this.screenX = screenX;
        this.screenY = screenY;

        screenRatioX = 1080f / screenX;
        screenRatioY = 1920f / screenY;

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        flight = new Flight(this, screenX, screenY, getResources());

        bullets = new ArrayList<>();

        background2.y = 0 - screenY;

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);

        enemies = new Enemy[6];

        for(int i=0; i < 6; i++){
            Enemy enemy = new Enemy(getResources());
            enemies[i] = enemy;
        }

        random = new Random();

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

        List<Bullet> trash = new ArrayList<>();

        for(Bullet bullet: bullets) {
            if(bullet.y < 0) {
                trash.add(bullet);
            }
            bullet.y -= 50 * screenRatioY;

            for(Enemy enemy : enemies){
                if (Rect.intersects(enemy.getCollisionShape(), bullet.getCollisionShape())) {
                    score++;
                    enemy.y = screenY + 500;
                    bullet.x = -500;
                    enemy.wasShot = true;
                }
            }
        }

        for(Bullet bullet : trash) {
            bullets.remove(bullet);
        }

        for(Enemy enemy : enemies){
            enemy.y += enemy.speed;

            if (enemy.y + enemy.height > screenY){

                if(!enemy.wasShot){
                    isGameOver = true;
                    return;
                }

                int bound = (int) (30 * screenRatioY);
                enemy.speed = random.nextInt(bound);

                if(enemy.speed < 10 * screenRatioY){
                    enemy.speed = (int) (10 * screenRatioY);
                }

                enemy.y = -800;
                enemy.x = random.nextInt(screenX - enemy.width);

                enemy.wasShot = false;
            }

            if(Rect.intersects(enemy.getCollisionShape(), flight.getCollisionShape())){
                isGameOver = true;
                return;
            }
        }
    }

    private void draw() {

        if(getHolder().getSurface().isValid()){
            Canvas canvas = getHolder().lockCanvas();

            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            for(Enemy enemy : enemies){
                canvas.drawBitmap(enemy.getEnemy(), enemy.x, enemy.y, paint);
            }

            canvas.drawText(score + "", screenX /2f, 164, paint);

            if(isGameOver){
                isPlaying = false;
                canvas.drawBitmap(flight.getDead(), flight.x, flight.y, paint);
                canvas.drawText("DEAD", screenX / 2f - 100, screenY / 2f, paint);
                getHolder().unlockCanvasAndPost(canvas);
                saveIfHighScore();
                waitBeforeExit();
                return;
            }


            canvas.drawBitmap(flight.getFlight(), flight.x, flight.y, paint);

            for(Bullet bullet : bullets){
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);
            }

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void waitBeforeExit() {
        try {
            Thread.sleep(3000);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveIfHighScore() {
        if(prefs.getInt("highscore", 0) < score){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highscore", score);
            editor.apply();
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

                    flight.toShoot++;
                }
        }
        return true;
    }

    public void newBullet() {

        if(!prefs.getBoolean("isMute", false)){
            soundPool.play(sound, 1, 1, 0, 0, 1);
        }

        Bullet bullet = new Bullet(getResources());
        bullet.x = flight.x + (flight.width / 2) - 13;
        bullet.y = flight.y - 50;
        bullets.add(bullet);
    }
}
