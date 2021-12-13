import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {
    @FXML
    public Label status;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField width;
    @FXML
    private TextField height;
    @FXML
    private Button convert;
    @FXML
    private CheckBox checkBox1;
    private List<File> selectedFiles;
    private File directory;
    private SimpleStringProperty statusProperty = new SimpleStringProperty();

    public void initialize() {
        BooleanBinding booleanBinding = Bindings.not(width.textProperty().isNotEmpty());
        convert.disableProperty().bind(booleanBinding);

        height.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")){
                    height.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        width.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!newValue.matches("\\d")){
                    width.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        status.textProperty().bind(statusProperty);
        statusProperty.set("Nie wybrano zdjęcia");

        selectedFiles = null;
        directory = null;
    }

    @FXML
    public void handleFiles(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Obrazy", "*.jpg", "*.png")
        );
        selectedFiles = fileChooser.showOpenMultipleDialog(anchorPane.getScene().getWindow());
        if (selectedFiles != null) {
            statusProperty.set("Wybrano: " + selectedFiles.size() + " element(y) do skonwertowania.");
        }
    }

    @FXML
    public void handleDirectory(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directory = directoryChooser.showDialog(anchorPane.getScene().getWindow());
        if (directory != null) {
            System.out.println("Selected directory: " + directory);
        } else {
            System.out.println("Directory selection canceled");
        }
    }

    @FXML
    public void handleProportions(ActionEvent actionEvent) {
        if (checkBox1.isSelected()) {
            height.clear();
            height.setDisable(true);
        } else {
            height.setDisable(false);
        }
    }

    @FXML
    public void setConvert(ActionEvent actionEvent) throws IOException {
        double scaledWidth = 0;
        double scaledHeight = 0;

        for (int i = 0; i < selectedFiles.size(); i++) {
            BufferedImage originalImage = ImageIO.read(selectedFiles.get(i));

            if (checkBox1.isSelected()){
                double ratio = Double.parseDouble(width.getText()) / (double)originalImage.getWidth();
                scaledWidth = originalImage.getWidth() * ratio;
                scaledHeight = originalImage.getHeight() * ratio;

            } else {
                scaledWidth = Integer.parseInt(width.getText());
                scaledHeight = Integer.parseInt(height.getText());
            }
        }
        try {
            String outputPath = directory.getPath();
            resize(selectedFiles, outputPath, (int) scaledWidth, (int) scaledHeight);
            System.out.println("Skonwertowano");
        } catch (Exception e) {
            System.out.println("Directory exception triggered");
        }
    }
    public void resize(List<File> files, String output, int width, int height) throws IOException {
        try {
                for (int i = 0; i < files.size(); i++) {
                    List<String> fileNames = selectedFiles.stream().map(File::getName).collect(Collectors.toList());
                    String outputPath = output + "\\Nowy_" + fileNames.get(i);
                    System.out.println(outputPath);

                    BufferedImage inputImage = ImageIO.read(files.get(i));
                    BufferedImage outputImage = new BufferedImage(width, height, inputImage.getType());

                    Graphics2D graphics2D = outputImage.createGraphics();
                    graphics2D.drawImage(inputImage, 0, 0, width, height, null);
                    graphics2D.dispose();

                    ImageIO.write(outputImage, "jpg", new File(outputPath));
                }
        } catch (IllegalArgumentException arg) {
            statusProperty.set("Wysokość nie została ustawiona");
        }
    }
}
