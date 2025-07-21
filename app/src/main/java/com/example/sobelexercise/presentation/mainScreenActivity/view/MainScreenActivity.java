package com.example.sobelexercise.presentation.mainScreenActivity.view;

import static android.app.PendingIntent.getActivity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.sobelexercise.databinding.ActivityMainBinding;
import com.example.sobelexercise.domain.model.ImageProcessorModel;
import com.example.sobelexercise.presentation.mainScreenActivity.viewModel.MainScreenViewModel;
import com.example.sobelexercise.presentation.resultScreenActivity.view.ResultScreenActivity;

public class MainScreenActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private MainScreenViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainScreenViewModel.class);

        addObserverForViewModel();

        registerResultLauncher();

        stopLoadingScreen();

        binding.selectImageButton.setOnClickListener(v -> getImageFromGallery());
        binding.cameraButton.setOnClickListener(v -> getImageFromGallery());
    }

    private void addObserverForViewModel() {
        //Make appear or disappear the loading bar
        viewModel.getLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                if (isLoading) {
                    startLoadingScreen();
                } else {
                    stopLoadingScreen();
                }
            }
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                //In case there is an error, when we are getting the bitmap of the image, avise to the user
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getSelectedImage().observe(this, bitmap -> {
            if (bitmap != null) {
                Toast.makeText(this, "Imagen cargada correctamente", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getProcessedImage().observe(this, sobelImage -> {
            if (sobelImage != null) {
                Toast.makeText(this, "Imagen sobel generada", Toast.LENGTH_SHORT).show();
                //Navigate to result activity
                Intent intent = new Intent(this, ResultScreenActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerResultLauncher() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();

                    //We pass the context of Screen and the imageUri to be processed
                    viewModel.handleImageResult(binding.getRoot().getContext(), imageUri);
                } else {
                    Toast.makeText(this, "No se ha seleccionado una imagen", Toast.LENGTH_LONG).show();
                }
            }
        );
    }

    private void getImageFromGallery() {
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        imagePickerLauncher.launch(intent);
    }

    private void startLoadingScreen() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.content.setVisibility(View.INVISIBLE);
    }

    private void stopLoadingScreen() {
        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.content.setVisibility(View.VISIBLE);
    }
}