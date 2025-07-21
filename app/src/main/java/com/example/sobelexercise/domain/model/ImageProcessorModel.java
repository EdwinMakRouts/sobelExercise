package com.example.sobelexercise.domain.model;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ImageProcessorModel {
    private Bitmap originalImage;
    private Bitmap sobelImage;

    static {
        System.loadLibrary("sobelexercise");
    }
    private native int[] transformImage (int[] pixels, int width, int height);

    public Bitmap processImage(Bitmap originalImage) {
        this.originalImage = originalImage;

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int[] pixels = new int [width * height];

        //Here we add in the array pixels all the pixels from the original image
        originalImage.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] resultTransformation = transformImage(pixels, width, height);

        sobelImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        sobelImage.setPixels(resultTransformation, 0, width, 0, 0, width, height);

        return sobelImage;
    }

    public Bitmap getOriginalImageBitmap() {
        return originalImage;
    }

    public Bitmap getSobelImageBitmap() {
        return sobelImage;
    }

}
