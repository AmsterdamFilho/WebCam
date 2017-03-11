package br.com.luvva.webcam;

import br.com.jwheel.core.service.cdi.WeldContext;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
@Singleton
public class WebcamController implements Initializable
{
    private @Inject Logger                  logger;
    private @Inject WebcamResourcesProvider resourcesProvider;

    private @FXML ImageView imageView;
    private @FXML Parent    contentPane;

    private       WebcamStreamTask      webcamTask    = new NullWebcamStreamTask();
    private final ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();

    public void start ()
    {
        webcamTask.start();
    }

    public void pause ()
    {
        webcamTask.pause();
    }

    public void preferencesChanged (String newPreferredWebcam)
    {
        WebcamPreferences webcamPreferences = WeldContext.getInstance().getDefault(WebcamPreferences.class);
        webcamPreferences.setPreferredWebcam(newPreferredWebcam);
        WebcamPreferencesDao dao = WeldContext.getInstance().getAny(WebcamPreferencesDao.class);
        try
        {
            dao.merge(webcamPreferences);
            webcamTask.close();
            webcamTask.start();
        }
        catch (IOException e)
        {
            logger.error("Could not persist webcam preferences!", e);
        }
    }

    public BufferedImage snapShot ()
    {
        return webcamTask.snapShot();
    }

    /**
     * Gets current available webcam list. Might take a while to return. If an error,
     * occurs, it will be logged and an empty list will be returned.
     *
     * @param timeOutMs how long in milliseconds before an exception is thrown
     * @return the result List
     * @throws TimeoutException if the call exceeds times out
     */
    public List<String> getWebCams (int timeOutMs) throws TimeoutException
    {
        try
        {
            return Webcam.getWebcams(timeOutMs).stream().map(Webcam::getName).collect(Collectors.toList());
        }
        catch (TimeoutException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error("Could not retrieve available webcam list!", e);
            return new ArrayList<>();
        }
    }

    private class DefaultWebcamStreamTask extends Thread implements WebcamStreamTask
    {
        private final Webcam webcam;

        private boolean paused                 = false;
        private boolean runningEnabled         = true;
        private boolean opened                 = false;
        private boolean hasStarted             = false;
        private int     snapshotExceptionCount = 0;

        private final Thread shutdownHook = new Thread(this::closeImpl);

        private DefaultWebcamStreamTask (Webcam webcam)
        {
            this.webcam = webcam;
            setDaemon(true);
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        }

        @Override
        public void start ()
        {
            if (paused)
            {
                paused = false;
            }
            else if (!hasStarted)
            {
                super.start();
            }
        }

        @Override
        public void run ()
        {
            try
            {
                hasStarted = true;
                webcam.open();
                opened = true;
            }
            catch (WebcamException e)
            {
                logger.error("Could not open webcam", e);
                return;
            }

            final AtomicReference<WritableImage> ref = new AtomicReference<>();
            BufferedImage img;

            while (runningEnabled)
            {
                if (paused)
                {
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        logger.error("Interrupted exception!", e);
                    }
                }
                else
                {
                    try
                    {
                        if ((img = webcam.getImage()) != null)
                        {
                            ref.set(SwingFXUtils.toFXImage(img, ref.get()));
                            img.flush();
                            Platform.runLater(() -> imageProperty.set(ref.get()));
                        }
                    }
                    catch (Exception e)
                    {
                        if (snapshotExceptionCount < 10)
                        {
                            logger.error("Could not get image from Webcam!", e);
                            snapshotExceptionCount++;
                        }
                    }
                }
            }
        }

        @Override
        public void pause ()
        {
            paused = true;
        }

        @Override
        public void close ()
        {
            closeImpl();
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        }

        private void closeImpl ()
        {
            webcamTask = new NullWebcamStreamTask();
            runningEnabled = false;
            logger.info("Closing webcam...");
            webcam.close();
        }

        @Override
        public BufferedImage snapShot ()
        {
            if (opened)
            {
                return webcam.getImage();
            }
            else
            {
                return null;
            }
        }
    }

    private class NullWebcamStreamTask implements WebcamStreamTask
    {
        @Override
        public void start ()
        {
            WebcamPreferences preferences = WeldContext.getInstance().getDefault(WebcamPreferences.class);
            String preferredWebCam = preferences.getPreferredWebcam();
            if (!(preferredWebCam == null || preferredWebCam.trim().isEmpty()))
            {
                Webcam webcam = Webcam.getWebcamByName(preferredWebCam);
                if (webcam != null)
                {
                    webcamTask = new DefaultWebcamStreamTask(webcam);
                    webcamTask.start();
                }
            }
        }

        @Override
        public void pause ()
        {

        }

        @Override
        public void close ()
        {

        }

        @Override
        public BufferedImage snapShot ()
        {
            return null;
        }
    }

    private interface WebcamStreamTask
    {
        void start ();

        void pause ();

        void close ();

        BufferedImage snapShot ();
    }

    @Override
    public void initialize (URL location, ResourceBundle resources)
    {
        contentPane.getStylesheets().add(resourcesProvider.getWebcamPanelCss());
        imageView.imageProperty().bind(imageProperty);
    }
}
