package es.xuan.cacaloc.model;

import android.location.Location;

import java.io.Serializable;
import java.util.Locale;

import es.xuan.cacaloc.constantes.ProviderLoc;

/**
 * Created by jcamposp on 18/11/2016.
 */
public class Posicion implements Serializable {

    private static final long serialVersionUID = 1L;
    //
    private Punto punto;
    private float velocidad;
    private float orientacion;
    private ProviderLoc proveedor;
    private float precision;
    private Location location;

    public Posicion(Location p_location) {
        if (p_location != null) {
            setPunto(new Punto((float) p_location.getLongitude(),
                    (float) p_location.getLatitude(),
                    (float) p_location.getAltitude()));
            setVelocidad(p_location.getSpeed());
            setOrientacion(p_location.getBearing());
            setPrecision(p_location.getAccuracy());
            setLocation(p_location);
            setProveedor(ProviderLoc.valueOf(p_location.getProvider().toUpperCase(Locale.getDefault())));
        }
        else {
            setPunto(new Punto(0f,0f,0f));
            //setVelocidad(0f);
            setOrientacion(0f);
            //setPrecision(p_location.getAccuracy());
            setLocation(null);
            //setProveedor(ProviderLoc.valueOf(p_location.getProvider().toUpperCase(Locale.getDefault())));
        }

    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setPrecision(float exactitud) {
        this.precision = exactitud;
    }

    public float getOrientacion() {
        return orientacion;
    }

    public void setOrientacion(float orientacion) {
        this.orientacion = orientacion;
    }

    public float getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(float velocidad) {
        this.velocidad = velocidad;
    }

    public Punto getPunto() {
        return punto;
    }

    public void setPunto(Punto punto) {
        this.punto = punto;
    }

    public ProviderLoc getProveedor() {
        return proveedor;
    }

    public float getPrecision() {
        return precision;
    }

    public void setProveedor(ProviderLoc proveedor) {
        this.proveedor = proveedor;
    }
}
