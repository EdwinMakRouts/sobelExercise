package com.example.sobelexercise.presentation.resultScreenActivity.viewModel;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.example.sobelexercise.domain.model.ImageProcessorModel;
import com.example.sobelexercise.domain.repository.ImageProcessorRepository;

import java.io.OutputStream;

public class ResultScreenViewModel extends ViewModel {

    private final ImageProcessorModel processor;

    public ResultScreenViewModel() {
        ImageProcessorRepository imageRepository = ImageProcessorRepository.getInstance();
        processor = imageRepository.getProcessorModel();
    }

    public Bitmap getOriginalImageBitmap() {
        return processor.getOriginalImageBitmap();
    }

    public Bitmap getSobelImageBitmap() {
        return processor.getSobelImageBitmap();
    }

    public boolean saveSobelImage(Bitmap sobelImage, ContentResolver resolver) {
        OutputStream fos;
        ContentValues contentValues = new ContentValues();

        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "SobelImage" + ".png");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyApp");

        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        try {
            if (imageUri != null) {
                fos = resolver.openOutputStream(imageUri);
                sobelImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();

                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }
}
