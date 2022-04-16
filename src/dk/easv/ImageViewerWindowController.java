package dk.easv;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;




public class ImageViewerWindowController {
    private final List<Image> images = new ArrayList<>();
    public Button stopBtn;
    public Slider slideshowSlider;
    public Text fileText;
    public Button btnNext;
    private int currentImageIndex = 0;

    private Image image = images.get(currentImageIndex);

    public double getImageHeight() {
        return image.getHeight();
    }
    public double getImageWidth() {
        return image.getWidth();
    }

    @FXML
    Parent root;

    @FXML
    private ImageView imageView;

    private long time = 2; // hver tal svare til 1 sek

    int count; //declared as global variable
    private Task task;
    private Thread thread;
    private List<File> files;
    private String filesPath;

    private ScheduledExecutorService executor;

    public ImageViewerWindowController() {
        slideshowSlider = new Slider();

        // The minimum value.
        slideshowSlider.setMin(1);

        // The maximum value.
        slideshowSlider.setMax(5);

        // Current value
        slideshowSlider.setValue(time);
        /*
        int n = 8; // Number of threads
        for (int i = 0; i < n; i++) {
            MultithreadingDemo object
                    = new MultithreadingDemo();
            object.start();

        }*/
    }


    @FXML
    private void handleBtnLoadAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image files");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images",
                "*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));
        files = fileChooser.showOpenMultipleDialog(new Stage());


        if (!files.isEmpty()) {
            files.forEach((File f) ->
            {
                images.add(new Image(f.toURI().toString()));
            });
        }

        start();
       /* ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            SideshowTask sideshowTask = new SideshowTask();
            try {
                sideshowTask.call();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });*/

       /* if(thread == null || !thread.isAlive()){
            task = getTask();
            thread = new Thread(task);
            thread.start();
        }
        */
    }

    /**
     * Initialize metode, som hovedsageligt viser alt vores data i vores tabels.
     * Udover det bruges vores volumeslider også i denne metode.
     *
     * @throws Exception
     */
    public void initialize() throws Exception {

       /* slideshowSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                slideshowSlider.set(time);
            }
        });*/


        slideshowSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                time = (long) slideshowSlider.getValue();
            };

        });
    }






    @FXML
    private void handleBtnPreviousAction() {
        if (!images.isEmpty()) {
            currentImageIndex =
                    (currentImageIndex - 1 + images.size()) % images.size();
            displayImage();
        }
    }

    @FXML
    private void handleBtnNextAction() {
        if (!images.isEmpty()) {
            currentImageIndex = (currentImageIndex + 1) % images.size();
            displayImage();
        }
    }

    private void displayImage() {
        if (!images.isEmpty()) {
            imageView.setImage(images.get(currentImageIndex));
            fileText.setText(images.get(currentImageIndex).getUrl());
        }
    }

    private void start() {
        Runnable task = () -> {
            //Put any code that needs to run here
            Platform.runLater(() -> {
                //Put even more code to be updated automatically
                btnNext.fire();
            });
        };

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(task, time, time, TimeUnit.SECONDS);
    }

    public void stopBtn(ActionEvent event) {
        if (!executor.isShutdown()){
            executor.shutdown();
            stopBtn.setText("Start SlideShow");
        }
        else {
            start();
            //task = getTask();
            //thread = new Thread(task);
            //thread.start();
            stopBtn.setText("Stop SlideShow");
        }
    }

    private Task<Void> getPixels(){
        return new Task<Void>() {
            @Override
            public Void call() throws Exception {

                double height = getImageHeight();
                double width = getImageWidth();

                int redCount = 0;
                int pixelCount = 0;

                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height ; y++) {

                        Color rgb = image.getPixelReader().getColor(x, y);


                        //get rgbs
                        //int alpha = (rgb >>> 24) & 0xFF;
                        Color red  = (rgb >> 16) & 0xFF;
                        Color green = (rgb >>  8) & 0xFF;
                        Color blue  = (rgb >>  0) & 0xFF;



                        if (red > green && red > blue) {
                            redCount++;
                        }

                        pixelCount++;
                    }
                }

                System.out.println("Red Pixel Count:" + redCount);
                System.out.println("Pixel Count:" + pixelCount);
                Thread.sleep((long) time);
                return null;
            }
        };

    }

    public ObservableList<PieChart.Data> pixelStats(Image image) {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        int counterPixelsRed = 0;
        int counterPixelsGreen = 0;
        int counterPixelsBlue = 0;

        int redColorRGB = Color.RED.getRGB();
        int blueColorRGB = Color.BLUE.getRGB();
        int greenColorRGB = Color.GREEN.getRGB();

        PixelReader pixelReader = image.getPixelReader();
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                int differencePixelBlue = Math.abs(pixelReader.getArgb(x, y) - blueColorRGB);
                int differencePixelRed = Math.abs(pixelReader.getArgb(x, y) - redColorRGB);
                int differencePixelGreen = Math.abs(pixelReader.getArgb(x, y) - greenColorRGB);

                if (differencePixelBlue > differencePixelGreen && differencePixelBlue > differencePixelRed)
                    counterPixelsBlue += 1;

                if (differencePixelGreen > differencePixelRed && differencePixelGreen > differencePixelBlue)
                    counterPixelsGreen += 1;

                if (differencePixelRed > differencePixelBlue && differencePixelRed > differencePixelGreen)
                    counterPixelsRed += 1;

            }
        }
        pieData.add(new PieChart.Data("blue", counterPixelsBlue));
        pieData.add(new PieChart.Data("green", counterPixelsGreen));
        pieData.add(new PieChart.Data("red", counterPixelsRed));

        return pieData;
    }
}


   /* private Task<Void> getTask(){
        return new Task<Void>() {
            @Override
            public Void call() throws Exception {
                for (int i = 0; count < images.size(); i++) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImage(images.get(count));
                            fileText.setText(filesPath);
                            count++;
                            if (count >= images.size()) {
                                count = 0;
                            }
                        }
                    });

                    Thread.sleep((long) time);
                }
                return null;
            }
        };

    }
}

    */

class MultithreadingDemo extends Thread {
    public void run()
    {
        try {
            // Displaying the thread that is running
            System.out.println(
                    "Thread " + Thread.currentThread().getId()
                            + " is running");
        }
        catch (Exception e) {
            // Throwing an exception
            System.out.println("Exception is caught");
        }
    }

}




