package es.xuan.cacaloc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import es.xuan.cacaloc.constantes.FormatoFichero;

public class Ruta implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String nombre = "";
	private String descripcion = "";
	private String tipo = "";
	private FormatoFichero formato;
	private Calendar fecha = null;
	private ArrayList<Punto> puntos = null;
	private float anchoMax = 0;
	private float largoMax = 0;
	private float desnivelMax = 0;
	private float anchoMin = 0;
	private float largoMin = 0;
	private float desnivelMin = 0;

	public Ruta() {
		this.setNombre("");
		this.setDescripcion("");
		this.setFecha(Calendar.getInstance());
		this.setFormato(FormatoFichero.GPX);
		Punto punto = new Punto(0f, 0f, 0f);
		punto.setNombre("");
		this.addPunto(punto);
	}

	public float getAnchoMin() {
		if (this.anchoMin == 0)
			calcularTamanoRuta();
		return anchoMin;
	}
	public void setAnchoMin(float anchoMin) {
		this.anchoMin = anchoMin;
	}
	public float getLargoMin() {
		if (this.largoMin == 0)
			calcularTamanoRuta();
		return largoMin;
	}
	public void setLargoMin(float largoMin) {
		this.largoMin = largoMin;
	}
	public float getDesnivelMin() {
		if (this.desnivelMin == 0)
			calcularTamanoRuta();
		return desnivelMin;
	}
	public void setDesnivelMin(float desnivelMin) {
		this.desnivelMin = desnivelMin;
	}
	public float getAnchoMax() {
		if (this.anchoMax == 0)
			calcularTamanoRuta();
		return anchoMax;
	}
	public void setAnchoMax(float anchoMax) {
		this.anchoMax = anchoMax;
	}
	public float getLargoMax() {
		if (this.largoMax == 0)
			calcularTamanoRuta();
		return largoMax;
	}
	public void setLargoMax(float largoMax) {
		this.largoMax = largoMax;
	}
	public float getDesnivelMax() {
		if (this.desnivelMax == 0)
			calcularTamanoRuta();
		return desnivelMax;
	}
	public void setDesnivelMax(float desnivelMax) {
		this.desnivelMax = desnivelMax;
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
	public FormatoFichero getFormato() {
		return formato;
	}
	public void setFormato(FormatoFichero formato) {
		this.formato = formato;
	}
	public Calendar getFecha() {
		return fecha;
	}
	public void setFecha(Calendar fecha) {
		this.fecha = fecha;
	}
	public ArrayList<Punto> getPuntos() {
		return puntos;
	}
	public void setPuntos(ArrayList<Punto> puntos) {
		this.puntos = puntos;
		calcularTamanoRuta();
	}
	private void calcularTamanoRuta() {
		//
		float fAnchoMax = Float.MIN_VALUE;
		float fLargoMax = Float.MIN_VALUE;
		float fDesnivelMax = Float.MIN_VALUE;
		float fAnchoMin = Float.MAX_VALUE;
		float fLargoMin = Float.MAX_VALUE;
		float fDesnivelMin = Float.MAX_VALUE;
		
		for (Punto punto : getPuntos()) {
			// Calcular el ancho y alto de la ruta
			if (fAnchoMax < punto.getLongitud())
				fAnchoMax = punto.getLongitud();
			if (fAnchoMin > punto.getLongitud())
				fAnchoMin = punto.getLongitud();	
			//
			if (fLargoMax < punto.getLatitud())
				fLargoMax = punto.getLatitud();
			if (fLargoMin > punto.getLatitud())
				fLargoMin = punto.getLatitud();			
			//
			if (fDesnivelMax < punto.getAltitud())
				fDesnivelMax = punto.getAltitud();
			if (fDesnivelMin > punto.getAltitud())
				fDesnivelMin = punto.getAltitud();					
		}
		setLargoMax(fLargoMax);
		setAnchoMax(fAnchoMax);
		setDesnivelMax(fDesnivelMax);
		//
		setLargoMin(fLargoMin);
		setAnchoMin(fAnchoMin);
		setDesnivelMin(fDesnivelMin);
	}
	public void addPunto(Punto p_aPunto) {
		if (this.puntos == null)
			puntos = new ArrayList<Punto>();
		this.puntos.add(p_aPunto);
	}

	public void insertPunto(int p_anOrden, Punto p_aPunto) {
		if (this.puntos == null)
			puntos = new ArrayList<Punto>();
		if (puntos.size() > 0)
			puntos.set(p_anOrden, p_aPunto);
		else
			this.puntos.add(p_aPunto);
	}
}
