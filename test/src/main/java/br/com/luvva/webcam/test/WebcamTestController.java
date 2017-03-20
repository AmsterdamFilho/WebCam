package br.com.luvva.webcam.test;

import br.com.jwheel.xml.model.FromXmlPreferences;
import br.com.jwheel.xml.model.PathPreferences;
import br.com.luvva.webcam.WebcamController;
import br.com.luvva.webcam.WebcamPreferences;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class WebcamTestController implements Initializable
{
    private @FXML ProgressIndicator progress;
    private @FXML ComboBox<String>  cmbWebcam;
    private @FXML WebcamController  webcamController;

    private @Inject PathPreferences pathPreferences;
    private @Inject Logger          logger;

    private @Inject @FromXmlPreferences WebcamPreferences webcamPreferences;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");

    private void selectedWebcamChanged ()
    {
        webcamController.preferencesChanged(cmbWebcam.getValue());
    }

    public void pause ()
    {
        webcamController.pause();
    }

    public void resume ()
    {
        webcamController.start();
    }

    public void takeSnapshot ()
    {
        BufferedImage bufferedImage = webcamController.snapShot();
        if (bufferedImage != null)
        {
            Path appDataDirectory = pathPreferences.getAppDataDirectory();
            try
            {
                ImageIO.write(bufferedImage, "jpg",
                        appDataDirectory.resolve(sdf.format(new Date()) + ".jpg").toFile());
            }
            catch (IOException e)
            {
                logger.error("Could not save image!", e);
            }
        }
    }

    private void loadWebcams ()
    {
        try
        {
            final List<String> webcamsNames = webcamController.getWebCams(10000);
            webcamController.start();
            Platform.runLater(() ->
            {
                cmbWebcam.setItems(FXCollections.observableArrayList(webcamsNames));
                progress.setVisible(false);
                String preferredWebcam = webcamPreferences.getPreferredWebcam();
                if (webcamsNames.contains(preferredWebcam))
                {
                    cmbWebcam.setValue(preferredWebcam);
                }
                cmbWebcam.setOnAction(event -> selectedWebcamChanged());
            });
        }
        catch (TimeoutException e)
        {
            logger.error("TimeoutException when loading webcams!", e);
        }
        progress.setVisible(false);
    }

    @Override
    public void initialize (URL location, ResourceBundle resources)
    {
        new Thread(this::loadWebcams, "Webcams loading thread").start();
    }
}
