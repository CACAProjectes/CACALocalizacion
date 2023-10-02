package es.xuan.cacaloc.control;

import java.util.Locale;

import es.xuan.cacaloc.constantes.FormatoFichero;
import es.xuan.cacaloc.conv.ConversorRutaGpx;
import es.xuan.cacaloc.conv.ConversorRutaInt;
import es.xuan.cacaloc.conv.ConversorRutaKml;
import es.xuan.cacaloc.model.Posicion;
import es.xuan.cacaloc.model.Recorrido;
import es.xuan.cacaloc.model.Ruta;

public class ControladorLoc {

	ConversorRutaInt m_cr = null;

	public static ControladorLoc getInstance() {
		return new ControladorLoc();
	}

	public Ruta leerRuta(String p_aNombre) {
		if (p_aNombre != null &&
				!p_aNombre.equals("") &&
				!p_aNombre.equals("- Sin Ruta -"))
			return transformarRuta(FormatoFichero.valueOf(obtenerSufijo(p_aNombre, 3)), p_aNombre);
		return null;
	}
		
	private String obtenerSufijo(String p_aNombre, int iTamanoSufijo) {
		if (!p_aNombre.isEmpty() && p_aNombre.length() > iTamanoSufijo) {
			return p_aNombre.substring(p_aNombre.length() - iTamanoSufijo).toUpperCase(Locale.getDefault());
		}
		return "";
	}

	private Ruta transformarRuta(FormatoFichero p_aFormato, String p_aRuta) {
		switch (p_aFormato) {
			case GPX:
				m_cr = new ConversorRutaGpx();
				break;
			case KML:
				m_cr = new ConversorRutaKml();
				break;
		}
		return m_cr.getRuta(p_aRuta);
	}

	public void guardarRecorrido(FormatoFichero p_aFormato, Recorrido pRecorrido) {
		switch (p_aFormato) {
			case GPX:
				m_cr = new ConversorRutaGpx();
				break;
			case KML:
				m_cr = new ConversorRutaKml();
				break;
		}
		m_cr.saveRuta(pRecorrido);
	}

	public void guardarRecorridoPunto(Posicion pPosicion) {
		m_cr.saveRecorridoPunto(pPosicion.getPunto());
	}

	public void guardarRecorridoCabecera(FormatoFichero p_aFormato) {
		switch (p_aFormato) {
			case GPX:
				m_cr = new ConversorRutaGpx();
				break;
			case KML:
				m_cr = new ConversorRutaKml();
				break;
		}
		m_cr.saveRecorridoCabecera();
	}

	public void guardarRecorridoFin() {
		m_cr.saveRecorridoFin();
	}
}
