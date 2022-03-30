package dk.easv;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SideshowTask extends Task<Image> {
    int count; //declared as global variable
    private final List<javafx.scene.image.Image> images = new ArrayList<>();

    private Task task;

    private String filesPath;

    public Text fileText;

    private ImageView imageView;

    private double time = 2000; // hver 1000 svare til 1 sek

    public SideshowTask(int count, Task task, String filesPath, Text fileText, ImageView imageView, double time) {
        this.count = count;
        this.task = task;
        this.filesPath = filesPath;
        this.fileText = fileText;
        this.imageView = imageView;
        this.time = time;
    }

    @Override
    protected Image call() throws Exception {
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




