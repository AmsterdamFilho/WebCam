package br.com.luvva.webcam;

import br.com.jwheel.xml.service.PreferencesFactoryFromXml;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class WebcamPreferencesFactory extends PreferencesFactoryFromXml<WebcamPreferences>
{
    private @Inject WebcamPreferencesDao dao;

    @Produces
    private WebcamPreferences produce ()
    {
        return produce(dao);
    }

    @Override
    protected void setDefaultPreferences (WebcamPreferences preferencesBean)
    {
        preferencesBean.setPreferredWebcam(null);
    }
}
