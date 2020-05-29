package com.example.warhawk;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import static com.example.warhawk.GameView.screenRatioX;
import static com.example.warhawk.GameView.screenRatioY;

public class Flight {
    int toShoot = 0;
    int x, y, width, height, wingCounter = 0;
    Bitmap flight1, flight2, dead;
    private GameView gameView;

    Flight(GameView gameView, int screenX, int screenY, Resources res) {
        flight1 = BitmapFactory.decodeResource(res, R.drawable.spaceship_fly1);
        flight2 = BitmapFactory.decodeResource(res, R.drawable.spaceship_fly2);
        dead = BitmapFactory.decodeResource(res, R.drawable.dead);

        this.gameView = gameView;

        width = flight1.getWidth();
        height = flight1.getHeight();

        width /= 3;
        height /= 3;

        width = Math.round(screenRatioX * width);
        height = Math.round(screenRatioY * height);

        flight1 = Bitmap.createScaledBitmap(flight1, width, height, false);
        flight2 = Bitmap.createScaledBitmap(flight2, width, height, false);
        dead = Bitmap.createScaledBitmap(dead, width, height, false);

        x = (screenX /2) - (width / 2);
        y = screenY - (64 * (int) screenRatioY) - height;
    }

    Bitmap getFlight() {

        if(toShoot != 0){
            toShoot = 0;
            gameView.newBullet();
        }

        if(wingCounter == 0){
            wingCounter++;
            return flight1;
        }

        wingCounter--;

        return flight2;
    }

    Rect getCollisionShape () {
        return new Rect(x, y, x + width, y + height);
    }

    Bitmap getDead() {
        return dead;
    }
}
