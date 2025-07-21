package com.example.sobelexercise.presentation.mainScreenActivity.viewModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sobelexercise.domain.model.ImageProcessorModel;
import com.example.sobelexercise.domain.repository.ImageProcessorRepository;

public class MainScreenViewModel extends ViewModel {

    private final ImageProcessorModel processor;

    public MainScreenViewModel() {
        ImageProcessorRepository imageRepository = ImageProcessorRepository.getInstance();
        processor = imageRepository.getProcessorModel();
    }

    private final MutableLiveData<Bitmap> selectedImage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> processedImage = new MutableLiveData<>();

    public LiveData<Bitmap> getSelectedImage() {
        return selectedImage;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Bitmap> getProcessedImage() {
        return processedImage;
    }


    public void handleImageResult(Context context, Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            selectedImage.setValue(bitmap);
            loading.setValue(true);

            new Thread(() -> {
                try {
                    Bitmap result = processor.processImage(bitmap);
                    processedImage.postValue(result);
                } catch (Exception e) {
                    errorMessage.setValue("Error al procesar el filtro SOBEL en la imagen");
                }

            }).start();
        } catch (Exception e) {
            errorMessage.setValue("Error al procesar la imagen");
            loading.setValue(false);
        }
    }
}
