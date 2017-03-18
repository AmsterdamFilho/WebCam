package br.com.luvva.webcam.test;

import br.com.jwheel.javafx.JWheelFxmlLoader;
import br.com.jwheel.javafx.JavaFxApplication;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;

import javax.inject.Inject;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class WebcamTestApplication implements JavaFxApplication
{
    private @Inject WebcamTestResourcesProvider resourceProvider;
    private @Inject Logger                      logger;

    @Override
    public void init (Stage primaryStage)
    {
        try
        {
            Stage newPrimaryStage = new Stage();
            newPrimaryStage.setTitle("Webcam framework test");
            newPrimaryStage.setScene(new Scene(JWheelFxmlLoader.loadWithCdi(resourceProvider.getMainSceneFxml())));
            newPrimaryStage.centerOnScreen();

            primaryStage.close();
            newPrimaryStage.show();
        }
        catch (Exception ex)
        {
            logger.error("Can't load WebcamTestApplication!", ex);
            primaryStage.close();
        }
    }
}
