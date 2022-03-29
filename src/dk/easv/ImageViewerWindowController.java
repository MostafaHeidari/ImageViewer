package dk.easv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Path;
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
    private int currentImageIndex = 0;


    @FXML
    Parent root;

    @FXML
    private ImageView imageView;

    private double time = 2000; // hver 1000 svare til 1 sek

    int count; //declared as global variable
    private Task task;
    private Thread thread;
    private List<File> files;
    private String filesPath;

    public ImageViewerWindowController() {
        slideshowSlider = new Slider();

        // The minimum value.
        slideshowSlider.setMin(1000);

        // The maximum value.
        slideshowSlider.setMax(5000);

        // Current value
        slideshowSlider.setValue(time);

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
        if(thread == null || !thread.isAlive()){
            task = getTask();
            thread = new Thread(task);
            thread.start();
        }
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
                time = slideshowSlider.getValue() * 1000;
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
        }
    }

    public void stopBtn(ActionEvent event) {
        if (thread.isAlive()){
            thread.interrupt();
            stopBtn.setText("Start SlideShow");
        }
        else {
            task = getTask();
            thread = new Thread(task);
            thread.start();
            stopBtn.setText("Stop SlideShow");
        }
    }

    private Task<Void> getTask(){
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




