package com.example.sobelexercise.presentation.resultScreenActivity.view;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.sobelexercise.R;
import com.example.sobelexercise.databinding.ActivityMainBinding;
import com.example.sobelexercise.databinding.ActivityResultScreenBinding;
import com.example.sobelexercise.presentation.mainScreenActivity.view.MainScreenActivity;
import com.example.sobelexercise.presentation.mainScreenActivity.viewModel.MainScreenViewModel;
import com.example.sobelexercise.presentation.resultScreenActivity.viewModel.ResultScreenViewModel;

import java.io.OutputStream;

public class ResultScreenActivity extends AppCompatActivity {

    private ActivityResultScreenBinding binding;

    private ResultScreenViewModel viewModel;
    Bitmap originalImage;
    Bitmap sobelImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityResultScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ResultScreenViewModel.class);

        loadImages();

        binding.saveImageButton.setOnClickListener(v -> saveImageSobel());
        binding.changeImageButton.setOnClickListener(v -> goToMainScreen());

        binding.sobelImage.setOnClickListener(v -> makeImageBigger(sobelImage));
        binding.normalImage.setOnClickListener(v -> makeImageBigger(originalImage));
        binding.bigImage.setOnClickListener(v -> makeImageSmaller());
    }

    private void loadImages() {
        originalImage = viewModel.getOriginalImageBitmap();
        binding.normalImage.setImageBitmap(originalImage);

        sobelImage = viewModel.getSobelImageBitmap();
        binding.sobelImage.setImageBitmap(sobelImage);
    }

    private void saveImageSobel(){
        ContentResolver resolver = this.getContentResolver();
        boolean isCorrectlySaved = viewModel.saveSobelImage(sobelImage, resolver);

        if (isCorrectlySaved)
            Toast.makeText(this, "Imagen guardada", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Error al guardar imagen", Toast.LENGTH_SHORT).show();
    }

    private void goToMainScreen(){
        Intent intent = new Intent(this, MainScreenActivity.class);
        startActivity(intent);
    }

    private void makeImageBigger(Bitmap image){
        binding.bigImage.setImageBitmap(image);
        binding.resultContent.setVisibility(View.GONE);
        binding.bigImage.setVisibility(View.VISIBLE);
    }

    private void makeImageSmaller(){
        binding.bigImage.setVisibility(View.GONE);
        binding.resultContent.setVisibility(View.VISIBLE);
    }
}