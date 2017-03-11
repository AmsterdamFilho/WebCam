package br.com.luvva.webcam;

import br.com.jwheel.core.service.java.ResourceProvider;

import javax.inject.Singleton;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
@Singleton
public class WebcamResourcesProvider extends ResourceProvider
{
    public String getWebcamPanelCss ()
    {
        return getCss("webcam");
    }

    @Override
    protected String root ()
    {
        return "webcam";
    }
}
