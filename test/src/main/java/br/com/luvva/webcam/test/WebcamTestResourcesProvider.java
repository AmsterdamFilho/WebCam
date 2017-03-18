package br.com.luvva.webcam.test;

import br.com.jwheel.utils.ResourceProvider;

import java.net.URL;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class WebcamTestResourcesProvider extends ResourceProvider
{
    URL getMainSceneFxml ()
    {
        return getFxml("webcam-test");
    }

    @Override
    protected String root ()
    {
        return "webcam-test";
    }
}
