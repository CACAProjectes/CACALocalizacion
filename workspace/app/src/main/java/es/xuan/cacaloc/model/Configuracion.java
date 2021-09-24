package es.xuan.cacaloc.model;

import java.io.Serializable;

/**
 * Created by Jcamposp on 01/12/2016.
 */
public class Configuracion implements Serializable {

    private static final long serialVersionUID = 1L;
    //
    private boolean GPS;
    private boolean GPX;
    private String ruta;

    public boolean isGPS() {
        return GPS;
    }

    public void setGPS(boolean GPS) {
        this.GPS = GPS;
    }

    public boolean isGPX() {
        return GPX;
    }

    public void setGPX(boolean GPX) {
        this.GPX = GPX;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getRutaPI() {
        if (isGPX())
            return getRuta().replace(".gpx","_pi.gpx");
        return getRuta().replace(".kml","_pi.kml");
    }
}
