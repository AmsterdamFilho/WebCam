package br.com.luvva.webcam;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class WebcamPreferences
{
    private String preferredWebcam;

    public WebcamPreferences ()
    {
    }

    public WebcamPreferences (String preferredWebcam)
    {
        this.preferredWebcam = preferredWebcam;
    }

    public String getPreferredWebcam ()
    {
        return preferredWebcam;
    }

    public void setPreferredWebcam (String preferredWebcam)
    {
        this.preferredWebcam = preferredWebcam;
    }
}
