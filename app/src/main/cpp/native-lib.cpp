#include <jni.h>
#include <string>
#include <iostream>
#include <cmath>
#include <omp.h>
#include <ctime>
#include <android/log.h>

extern "C" JNIEXPORT jintArray JNICALL
Java_com_example_sobelexercise_domain_model_ImageProcessorModel_transformImage(
    JNIEnv* env,
    jobject,
    jintArray inputArray,
    jint width,
    jint height
) {
    int NUM_THREADS = 2;

    //To access an specific pixel: pixels[y * width + x]
    jint* pixels = env -> GetIntArrayElements(inputArray, nullptr);

    //Sobel operator
    int Gx [3][3] = {{1, 0, -1},{2, 0, -2},{1, 0, -1}};
    int Gy [3][3] = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};

    //Change the boundaries to 0 and put the rest of pixels in a scale of grey
    auto getAlpha = [](jint pixel) -> int { return (pixel >> 24) & 0xFF; };
    auto getRed = [](jint pixel) -> int { return (pixel >> 16) & 0xFF; };
    auto getGreen = [](jint pixel) -> int { return (pixel >> 8) & 0xFF; };
    auto getBlue = [](jint pixel) -> int { return pixel & 0xFF; };

    jint* grayPixels = new jint[width * height];

    //Timer start
    clock_t start = clock();

    omp_set_num_threads(NUM_THREADS);
    #pragma omp parallel
    {
        int threadCount = omp_get_num_threads();
        int threadID = omp_get_thread_num();
        if (threadID == 0) {
            __android_log_print(ANDROID_LOG_INFO, "SobelThreads", "Número de hilos activos: %d", threadCount);
        }

        #pragma omp for
        for (int y = 0; y < height; y++) {
            if (y == 0 || y == height - 1) {
                //Setting the first and last row as 0
                for (int x = 0; x < width; x++) {
                    pixels[y * width + x] = 0;
                }
            } else {
                //Setting the first and last column as 0
                pixels[y * width] = 0;
                pixels[y * width + (width - 1)] = 0;

                //Set pixels in a scale of gray in an auxiliar array
                for (int x = 1; x < width - 1; x++) {
                    int positionPixel = y * width + x;
                    int pixel = pixels[positionPixel];

                    int red = getRed(pixel);
                    int green = getGreen(pixel);
                    int blue = getBlue(pixel);
                    int alpha = getAlpha(pixel);

                    int gray = (red + green + blue) / 3;

                    grayPixels[positionPixel] = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
                }
            }
        }

        //Operate the sobel algorithm
        #pragma omp for
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int sumX = 0;
                int sumY = 0;

                //Convolution
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int neighbourX = x + kx;
                        int neighbourY = y + ky;
                        int pixel = grayPixels[neighbourY * width + neighbourX];

                        sumX += Gx[ky + 1][kx + 1] * (pixel & 0xFF);
                        sumY += Gy[ky + 1][kx + 1] * (pixel & 0xFF);
                    }
                }

                //Calculate the magnitude using the convolution results - Approximation 1
                double magnitude = std::sqrt((sumX * sumX) + (sumY * sumY));
                int magnitudeValue = static_cast<int>(magnitude);
                if (magnitudeValue > 255) magnitudeValue = 255;

                /*
                //Calculate the magnitude using the convolution results - Approximation 2
                int magnitudeValue = std::abs(sumX) + std::abs(sumY);
                if (magnitudeValue > 255) magnitudeValue = 255;
                */

                // Set pixel with the calculated magnitude
                pixels[y * width + x] = (0xFF << 24) | (magnitudeValue << 16) | (magnitudeValue << 8) | magnitudeValue;
            }
        }
    }

    //timerend
    clock_t end = clock();
    double elapsed_ms = 1000.0 * (end - start) / CLOCKS_PER_SEC;
    __android_log_print(ANDROID_LOG_INFO, "SobelTime", "Tiempo de ejecución: %.2f ms",
                        elapsed_ms);

    //Liberate memory
    jintArray resultArray = env->NewIntArray(width * height);
    env -> SetIntArrayRegion(resultArray, 0, width * height, pixels);
    env -> ReleaseIntArrayElements(inputArray, pixels, 0);
    delete[] grayPixels;

    return resultArray;
}

