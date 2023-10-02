package es.xuan.cacaloc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import es.xuan.cacaloc.constantes.Constantes;
import es.xuan.cacaloc.constantes.Estado;

/**
 * Created by jcamposp on 21/11/2016.
 */

public class Recorrido implements Serializable {
    private static final long serialVersionUID = 1L;
    //
    private String nombre;
    private ArrayList<Punto> puntos;
    private Estado estado;
    private float distancia;
    private Calendar tiempoInicial;
    private Calendar tiempoFinal;

    public Recorrido() {
        this.estado = Estado.STOP;
        this.nombre = Constantes.CTE_NOMBRE_RUTA;
        this.puntos = new ArrayList<Punto>();
    }

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }

    public Calendar getTiempoFinal() {
        return tiempoFinal;
    }

    public void setTiempoFinal(Calendar tiempoFinal) {
        this.tiempoFinal = tiempoFinal;
    }

    public Calendar getTiempoInicial() {
        return tiempoInicial;
    }

    public void setTiempoInicial(Calendar tiempoInicial) {
        this.tiempoInicial = tiempoInicial;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<Punto> getPuntos() {
        return puntos;
    }

    public void setPuntos(ArrayList<Punto> puntos) {
        this.puntos = puntos;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void addPunto(Punto pPunto) {
        if (puntos==null)
            puntos = new ArrayList<Punto>();
        puntos.add(pPunto);
    }
}
