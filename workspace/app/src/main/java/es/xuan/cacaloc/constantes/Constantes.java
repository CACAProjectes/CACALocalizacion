package es.xuan.cacaloc.constantes;

import java.io.Serializable;

public class Constantes implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String CTE_SUFIJO_GPX = ".gpx";
	public static final String CTE_SUFIJO_KML = ".kml";
	public static final String CTE_PATH_FILES = "/apps/rutas/";
	public static final String CTE_PATH_LOG = "/apps/rutas/log/cacalocation.log";
	public static final String CTE_CAMBIO_LINIA_FICHERO = "\r\n";
	//
	public static final String CTE_FORMAT_DATA_RED = "dd/MM/yyyy";
	public static final String CTE_FORMAT_DATA_AMP = "dd/MM/yyyy HH:mm";
	public static final String CTE_FORMAT_DATA_LOG = "dd/MM/yyyy HH:mm:ss:ccc";
	public static final String CTE_FORMAT_DATA_FILE = "yyyyMMddHHmmss";
	public static final String CTE_FORMAT_HORA = "HH:mm:ss";
	public static final long CTE_TIMER_PERIODO = 500;
	public static final float CTE_CONVERSION_KMH = 3.600f;
	//public static final float CTE_RADIO_TIERRA_KM = 6371;
	public static final float CTE_RADIO_TIERRA_KM = 111.32f;
	//
	public static final int CTE_VIBRATION_MS = 50;
	public static final String CTE_FORMAT_FECHA_HORA_GPX = "yyyy-MM-dd'T'hh:mm:ss'Z'";
	public static final float CTE_CONVERSION_GRADOS_RADIANES = (float)Math.PI / 180;
	public static final String CTE_CLAVE_CONFIGURACION = "LOC_CONFIGURACION";
	public static final float CTE_ANCHO_PANTALLA = 5.8f;
	public static final float CTE_CONVERSION_KM_CM = 100000f;
    public static final String CTE_NOMBRE_RUTA = "Ruta_recorrida";
    public static final float CTE_ESCALA_DEF = 15000f;
    public static long CTE_TIEMPO_MILISEGUNDOS_HORA = 3600000;
	public static float CTE_CONVERSION_KM = 1000;
}
