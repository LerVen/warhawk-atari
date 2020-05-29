package com.example.warhawk;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.warhawk.GameView.screenRatioX;
import static com.example.warhawk.GameView.screenRatioY;

public class Enemy {
    public int speed = 20;
    public boolean wasShot = true;
    int x, y=0, width, height, enemyCounter = 1;

    Bitmap enemy;

    Enemy(Resources res){
        enemy = BitmapFactory.decodeResource(res, R.drawable.enemy);

        width = enemy.getWidth();
        height = enemy.getHeight();

        width /= 3;
        height /= 3;

        width = Math.round(screenRatioX * width);
        height = Math.round(screenRatioY * height);

        enemy = Bitmap.createScaledBitmap(enemy, width, height, false);

        x= -width;
    }

    // for animation
    Bitmap getEnemy () {
//        if(enemyCounter == 1){
//            enemyCounter++;
//            return enemy;
//        }

        return enemy;
    }

    Rect getCollisionShape () {
        return new Rect(x, y, x + width, y + height);
    }
}
