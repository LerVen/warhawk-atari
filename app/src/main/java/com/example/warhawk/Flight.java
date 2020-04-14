package com.example.warhawk;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import static com.example.warhawk.GameView.screenRatioX;
import static com.example.warhawk.GameView.screenRatioY;

public class Flight {
    int x, y, width, height, wingCounter = 0;
    Bitmap flight1, flight2;

    Flight(int screenX, int screenY, Resources res) {
        flight1 = BitmapFactory.decodeResource(res, R.drawable.spaceship_fly1);
        flight2 = BitmapFactory.decodeResource(res, R.drawable.spaceship_fly2);

        width = flight1.getWidth();
        height = flight1.getHeight();

        width /= 3;
        height /= 3;


        width *= (int) screenRatioX;
        height *= (int) screenRatioY;

        flight1 = Bitmap.createScaledBitmap(flight1, width, height, false);
        flight2 = Bitmap.createScaledBitmap(flight2, width, height, false);

        x = (screenX /2) - (width / 2);
        y = screenY - (64 * (int) screenRatioY) - height;
    }

    Bitmap getFlight() {
        if(wingCounter == 0){
            wingCounter++;
            return flight1;
        }

        wingCounter--;

        return flight2;
    }
}
