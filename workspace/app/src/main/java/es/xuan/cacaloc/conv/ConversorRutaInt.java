package es.xuan.cacaloc.conv;

import es.xuan.cacaloc.model.Punto;
import es.xuan.cacaloc.model.Recorrido;
import es.xuan.cacaloc.model.Ruta;

public interface ConversorRutaInt  {

	public Ruta getRuta(String p_strRuta);
	public String getRutaStr(Ruta p_ruta);
	public ConversorRutaInt getInstance();
	public void saveRuta(Recorrido pRecorrido);
	public void saveRecorridoCabecera();
	public void saveRecorridoPunto(Punto pPunto);
	public void saveRecorridoFin();
}
