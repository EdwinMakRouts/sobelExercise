package com.example.sobelexercise.domain.repository;

import com.example.sobelexercise.domain.model.ImageProcessorModel;

public class ImageProcessorRepository {
    private static ImageProcessorRepository instance;
    private ImageProcessorModel processorModel = new ImageProcessorModel();

    private ImageProcessorRepository() {}

    public static synchronized ImageProcessorRepository getInstance() {
        if (instance == null) {
            instance = new ImageProcessorRepository();
        }
        return instance;
    }

    public ImageProcessorModel getProcessorModel() {
        return processorModel;
    }
}
