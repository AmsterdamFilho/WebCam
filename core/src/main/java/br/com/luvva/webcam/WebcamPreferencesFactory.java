package br.com.luvva.webcam;

import br.com.jwheel.xml.model.FromXmlPreferences;
import br.com.jwheel.xml.service.PreferencesFactoryFromXml;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class WebcamPreferencesFactory implements PreferencesFactoryFromXml<WebcamPreferences>
{
    private @Inject WebcamPreferencesDao dao;

    @Produces
    @FromXmlPreferences
    private WebcamPreferences produce ()
    {
        return produce(dao);
    }

    @Override
    public WebcamPreferences produceDefault ()
    {
        return new WebcamPreferences("");
    }
}
