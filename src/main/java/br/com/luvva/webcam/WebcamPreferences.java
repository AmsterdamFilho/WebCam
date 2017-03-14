package br.com.luvva.webcam;

import br.com.jwheel.cdi.Custom;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
@Custom
public class WebcamPreferences
{
    private String preferredWebcam;

    public String getPreferredWebcam ()
    {
        return preferredWebcam;
    }

    public void setPreferredWebcam (String preferredWebcam)
    {
        this.preferredWebcam = preferredWebcam;
    }
}
