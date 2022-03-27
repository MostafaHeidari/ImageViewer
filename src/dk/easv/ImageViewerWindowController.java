package dk.easv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ImageViewerWindowController {
    private final List<Image> images = new ArrayList<>();
    public Button stopBtn;
    private int currentImageIndex = 0;

    @FXML
    Parent root;

    @FXML
    private ImageView imageView;

    private static int time = 2500;

    int count; //declared as global variable
    private Task task;
    private Thread thread;
    private List<File> files;

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
                            count++;
                            if (count >= images.size()) {
                                count = 0;
                            }
                        }
                    });

                    Thread.sleep(time);
                }
                return null;
            }
        };
    }
}




