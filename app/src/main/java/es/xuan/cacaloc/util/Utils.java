package es.xuan.cacaloc.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import es.xuan.cacaloc.constantes.Constantes;
import es.xuan.cacaloc.model.Punto;
import es.xuan.cacaloc.model.Ruta;

public class Utils {
	
	private final static NumberFormat m_currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));

	public static String fecha2String(Calendar p_fecha) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constantes.CTE_FORMAT_DATA_AMP, Locale.getDefault());
		return dateFormat.format(p_fecha.getTime()); 
	}

	public static void putValorSP(SharedPreferences p_spDades, String p_strKey, String p_strValor) {
		if (p_spDades != null) {
			Editor ed = p_spDades.edit();
			ed.putString(p_strKey, p_strValor);
			ed.commit();
		}
	}
	
	public static String getValorSP(SharedPreferences p_spDades, String p_strKey) {
		//To retrieve data from shared preference
		if (p_spDades != null)
			return p_spDades.getString(p_strKey, "");
		return "";
	}

	public static String fecha2StringFile(Calendar p_fecha) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constantes.CTE_FORMAT_DATA_FILE, Locale.getDefault());
		return dateFormat.format(p_fecha.getTime()); 
	}

	public static int transformarFloat2Entero(float p_fVelocidad) {
		return Float.valueOf(p_fVelocidad).intValue();
	}

	public static int transformarFloat2Decimal(float p_fVelocidad) {
		float fAux = p_fVelocidad - Float.valueOf(p_fVelocidad).intValue();
		fAux *= 10;
		return Float.valueOf(fAux).intValue();
	}

	public static String fecha2StringLog(Calendar pCalendar) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constantes.CTE_FORMAT_DATA_LOG, Locale.getDefault());
		return dateFormat.format(pCalendar.getTime());
	}

	public static String fecha2StringHora(Calendar pCalendar) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constantes.CTE_FORMAT_HORA, Locale.getDefault());
		return dateFormat.format(pCalendar.getTime());
	}

	public static float transformarKMh(float velocidad) {
		return velocidad * Constantes.CTE_CONVERSION_KMH;
	}

	public static Calendar fechaAhora() {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		return cal;
	}

	public static String fecha2StringGPX(Calendar fecha) {
		// 2016-10-27T16:52:14Z
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constantes.CTE_FORMAT_FECHA_HORA_GPX, Locale.getDefault());
		return dateFormat.format(fecha.getTime());
	}

	public static String transformarFloat2String(float number) {
		return String.format ("%.2f", number);
	}

	public static float transformar2Km(float distancia) {
		return distancia / Constantes.CTE_CONVERSION_KM;
	}

	public static long buscarPuntoMasCercano(Ruta pRuta, Punto pPuntoActual) {
		long lPuntoCercano = 0;
		//long lContador = 0;
		float fResultado = 0;
		float fDistancia = 0;
		float fDistanciaMin = Float.MAX_VALUE;
		// x0 > xPuntoActual > x1
		// y0 > yPuntoActual > y1
		Punto puntoAnterior = null;
		Punto puntoPosterior = null;
		for (int i=0;pRuta!=null && pRuta.getPuntos()!=null && i<pRuta.getPuntos().size()-1;i++) {
			puntoAnterior = pRuta.getPuntos().get(i);
			puntoPosterior = pRuta.getPuntos().get(i+1);
			/*
			if (puntoAnterior.getLongitud() <  pPuntoActual.getLongitud() &&
					pPuntoActual.getLongitud() < puntoPosterior.getLongitud()
					)
			*/
			/*
			if (redondearCoordenadas(puntoAnterior.getLatitud()) <  pPuntoActual.getLatitud() &&
					pPuntoActual.getLatitud() < redondearCoordenadas(puntoPosterior.getLatitud()) &&
					redondearCoordenadas(puntoAnterior.getLongitud()) <  pPuntoActual.getLongitud() &&
					pPuntoActual.getLongitud() < redondearCoordenadas(puntoPosterior.getLongitud())
					)
				lPuntoCercano = i;
				*/
			float fMINLatitud = (puntoAnterior.getLatitud() < puntoPosterior.getLatitud() ? puntoAnterior.getLatitud() : puntoPosterior.getLatitud());
			float fMAXLatitud = (puntoAnterior.getLatitud() > puntoPosterior.getLatitud() ? puntoAnterior.getLatitud() : puntoPosterior.getLatitud());
			//
			float fMINLongitud = (puntoAnterior.getLongitud() < puntoPosterior.getLongitud() ? puntoAnterior.getLongitud() : puntoPosterior.getLongitud());
			float fMAXLongitud = (puntoAnterior.getLongitud() > puntoPosterior.getLongitud() ? puntoAnterior.getLongitud() : puntoPosterior.getLongitud());
			//
			if (redondearCoordenadas(fMINLatitud) <  pPuntoActual.getLatitud() &&
					pPuntoActual.getLatitud() < redondearCoordenadas(fMAXLatitud) &&
					redondearCoordenadas(fMINLongitud) <  pPuntoActual.getLongitud() &&
					pPuntoActual.getLongitud() < redondearCoordenadas(fMAXLongitud)
					) {
				lPuntoCercano = i;
			}
		}
		return lPuntoCercano;
	}

	private static float redondearCoordenadas(float p_fValor) {
		long lValor = Float.valueOf(p_fValor * 10000f).longValue();
		float fValor = lValor;
		return fValor/10000f;
	}

	public static float rotacionX(float p_fValorX, float p_fValorY, float p_fAnguloRotacion) {
		/*
			y = x' * sin(theta) + y' * cos (theta)
			x = x' * cos(theta) - y' * sin (theta)
		*/
		return p_fValorX * (float)Math.cos(p_fAnguloRotacion) - p_fValorY * (float)Math.sin(p_fAnguloRotacion);
	}

	public static float rotacionY(float p_fValorX, float p_fValorY, float p_fAnguloRotacion) {
		/*
			y = x' * sin(theta) + y' * cos (theta)
			x = x' * cos(theta) - y' * sin (theta)
		*/
		return p_fValorX * (float)Math.sin(p_fAnguloRotacion) + p_fValorY * (float)Math.cos(p_fAnguloRotacion);
	}

	public static float transformarGrados2Radianes(float p_fAngulo) {
		return p_fAngulo * Constantes.CTE_CONVERSION_GRADOS_RADIANES;
	}
	@TargetApi(23)
	public static void sollicitarPermissos(Activity pActivity) {
		if (ContextCompat.checkSelfPermission(pActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			// Should we show an explanation?
			if (pActivity.shouldShowRequestPermissionRationale(
					Manifest.permission.READ_EXTERNAL_STORAGE)) {
				// Explain to the user why we need to read the contacts
				Log.v("","Permission READ is granted0");
			}
			pActivity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
					1);
			// MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
			// app-defined int constant that should be quite unique
			Log.v("","Permission READ is granted1");
		}
		if (ContextCompat.checkSelfPermission(pActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			// Should we show an explanation?
			if (pActivity.shouldShowRequestPermissionRationale(
					Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				// Explain to the user why we need to read the contacts
				Log.v("","Permission WRITE is granted2");
			}
			pActivity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
					1);
			// MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
			// app-defined int constant that should be quite unique
			Log.v("","Permission WRITE is granted3");
		}
	}
	public static ArrayList<String> obtenerFicherosCarpeta() {
		ArrayList<String> listaRutas = new ArrayList<String>();
		String strPathFile = "";
		try {
			File sdDir = Environment.getExternalStorageDirectory();
			if (!(sdDir.canWrite() && sdDir.canRead())) {
				return null;
			}
			strPathFile = sdDir.getAbsolutePath() + Constantes.CTE_PATH_FILES;
			File folder = new File(strPathFile);
			File[] listOfFiles = folder.listFiles();
			Arrays.sort(listOfFiles);
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile() && isGPSKML(listOfFiles[i].getName())) {
					LogLoc.e("Fichero: " + listOfFiles[i].getName());
					listaRutas.add(listOfFiles[i].getName());
				}
			}
		} catch (Exception ex) {
			LogLoc.e("obtenerFicherosCarpeta. Error: " + ex.getMessage());
		}
		return listaRutas;
	}

	private static boolean isGPSKML(String p_strName) {
		return p_strName.toLowerCase().contains(Constantes.CTE_SUFIJO_GPX) ||
				p_strName.toLowerCase().contains(Constantes.CTE_SUFIJO_KML);
	}

	public static double distancia(float p_fLatitud1, float p_fLongitud1,float p_fLatitud2, float p_fLongitud2) {
		float toRad = Constantes.CTE_CONVERSION_GRADOS_RADIANES;
		float fLat1 = p_fLatitud1 * toRad;
		float fLat2 = p_fLatitud2 * toRad;
		float fLon1 = p_fLongitud1 * toRad;
		float fLon2 = p_fLongitud2 * toRad;
		double a = Math.sin(fLat1) * Math.sin(fLat2) + Math.cos(fLat1) * Math.cos(fLat2) * Math.cos(fLon2-fLon1);
		double c = Math.acos(a);
		double d = c * Constantes.CTE_RADIO_TIERRA_KM / toRad;
		return d;
	}
}
