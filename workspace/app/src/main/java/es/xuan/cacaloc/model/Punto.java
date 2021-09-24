package es.xuan.cacaloc.model;

import java.io.Serializable;
import java.util.Calendar;

import es.xuan.cacaloc.util.Utils;

public class Punto implements Serializable {
	private static final long serialVersionUID = 1L;
	/*
 	 * X - Longitud
	 * Y - Latitud
	 * Z - Altitud
	 */
	private float latitud = 0;
	private float longitud = 0;
	private float altitud = 0;
	private long orden = 0;
	private String nombre = "";
	private String descripcion = "";
	private String tipo = "";
	private Calendar fecha = null;
	
	public Punto(float p_longitude, float p_latitude, float p_altitude) {
		/*
		 * X - Longitud
		 * Y - Latitud
		 * Z - Altitud
		*/
		setLatitud(p_latitude);
		setLongitud(p_longitude);
		setAltitud(p_altitude);
		setFecha(Utils.fechaAhora());
	}
	public Calendar getFecha() {
		return fecha;
	}
	public void setFecha(Calendar fecha) {
		this.fecha = fecha;
	}
	public float getLatitud() {
		return latitud;
	}
	public void setLatitud(float latitud) {
		this.latitud = latitud;
	}
	public float getLongitud() {
		return longitud;
	}
	public void setLongitud(float longitud) {
		this.longitud = longitud;
	}
	public float getAltitud() {
		return altitud;
	}
	public void setAltitud(float altitud) {
		this.altitud = altitud;
	}
	public long getOrden() {
		return orden;
	}
	public void setOrden(long orden) {
		this.orden = orden;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	@Override
	public String toString() {
		return "Posición: " + getLongitud() + " - " +  getLatitud() + " - " + getAltitud();
	}
}
