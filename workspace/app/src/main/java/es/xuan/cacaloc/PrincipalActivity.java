package es.xuan.cacaloc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.IOException;
import java.util.Calendar;
import es.xuan.cacaloc.constantes.Constantes;
import es.xuan.cacaloc.constantes.Estado;
import es.xuan.cacaloc.constantes.FormatoFichero;
import es.xuan.cacaloc.constantes.ProviderLoc;
import es.xuan.cacaloc.control.ControladorLoc;
import es.xuan.cacaloc.draw.MapaView;
import es.xuan.cacaloc.draw.PerfilView;
import es.xuan.cacaloc.model.Configuracion;
import es.xuan.cacaloc.model.Posicion;
import es.xuan.cacaloc.model.Punto;
import es.xuan.cacaloc.model.Recorrido;
import es.xuan.cacaloc.model.Ruta;
import es.xuan.cacaloc.util.LogLoc;
import es.xuan.cacaloc.util.Serializar;
import es.xuan.cacaloc.util.Utils;

public class PrincipalActivity extends Activity implements LocationListener {

	private MapaView mpRuta = null;
	private PerfilView mpPerfil = null;
	private TextView tvVelocidadKm = null;
	private TextView tvVelocidadM = null;
	private TextView tvFechaEstado = null;
	private TextView tvTiempoEnCursoVal = null;
	private TextView tvTiempoPendienteVal = null;
	private TextView tvDistanciaPendienteVal = null;
	private TextView tvDistanciaEnCursoVal = null;
	private TextView tvTipoSenyal = null;
	private FloatingActionButton fabStart = null;
	private FloatingActionButton fabSettings = null;
	private FloatingActionButton fabZoomMas = null;
	private FloatingActionButton fabZoomMenos = null;
	private FloatingActionButton fabCentrar = null;
	private ImageView imgSenalGps = null;
	private ImageView imgBrujula = null;
	private Vibrator m_vibe = null;
	//
	private ControladorLoc m_controlador = null;
	private SharedPreferences m_spDades = null;
	private Configuracion m_configuracion = null;
	//
	private Recorrido m_recorrido = null;
	private boolean grabarRecorrido = false;
	private LocationManager m_locationManager = null;
	private Posicion m_pPosicionAnterior = null;
	private float m_escalaRuta = 1f;
	private float m_distanciaTotal = 0f;
	private int m_anguloAnterior = 0;
	private boolean m_brujulaParada = false;

	public ControladorLoc getControlador() {
		return m_controlador;
	}

	public boolean isGrabarRecorrido() {
		return grabarRecorrido;
	}

	public void setGrabarRecorrido(boolean grabarRecorrido) {
		this.grabarRecorrido = grabarRecorrido;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_principal);
		//
		sollicitarPermissos();
		//
		inicializarPantalla();
		//
		inicializarControlador();
		//
		inicializarTimer();
		//
		inicializarRecorrido();
		//
		inicializarRuta();
		//
		inicializarLocalizacion();
	}

	/*
	Sol·licita els permissos de READ i WRITE del dispositiu
	 */
	private void sollicitarPermissos() {
		Utils.sollicitarPermissos(this);
	}

	private void inicializarRuta() {
		//
		Ruta ruta = leerRuta(m_configuracion.getRuta());
		if (ruta == null) {
			ruta = new Ruta();
		}
		Ruta ptosInt = leerRuta(m_configuracion.getRutaPI());
		if (ptosInt == null) {
		}
		//
		dibujarRuta(ruta, ptosInt);
		// Informar la distacia total en Kms
		float fLatitud = 0f;
		float fLongitud = 0f;
		long lContador = 0l;
		m_distanciaTotal = 0f;
		for(Punto punto : ruta.getPuntos()) {
			if (lContador++ > 0)
				m_distanciaTotal += Utils.distancia(fLatitud, fLongitud, punto.getLatitud(), punto.getLongitud());
			fLatitud = punto.getLatitud();
			fLongitud = punto.getLongitud();
		}
		tvDistanciaPendienteVal.setText(Utils.transformarFloat2String(m_distanciaTotal));
	}

	private void inicializarRecorrido() {
		m_recorrido = new Recorrido();
	}

	private Ruta leerRuta(String p_aNombre) {
		return getControlador().leerRuta(p_aNombre);
	}

	private void inicializarPantalla() {
		m_vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		//
		mpRuta = (MapaView) findViewById(R.id.mpRuta);
		mpRuta.setRutaCompleta(false);	// Muestra la ruta sin centrar
		mpPerfil = (PerfilView) findViewById(R.id.mpPerfil);
		tvVelocidadKm = (TextView) findViewById(R.id.tvVelocidadKm);
		tvVelocidadM = (TextView) findViewById(R.id.tvVelocidadM);
		tvFechaEstado = (TextView) findViewById(R.id.tvFechaEstado);
		tvTiempoEnCursoVal = (TextView) findViewById(R.id.tvTiempoEnCursoVal);
		tvTiempoPendienteVal = (TextView) findViewById(R.id.tvTiempoPendienteVal);
		tvDistanciaEnCursoVal = (TextView) findViewById(R.id.tvDistanciaEnCursoVal);
		tvDistanciaPendienteVal = (TextView) findViewById(R.id.tvDistanciaPendienteVal);
		tvTipoSenyal = (TextView) findViewById(R.id.tvTipoSenyal);
		imgSenalGps = (ImageView) findViewById(R.id.ivSenyal);
		imgBrujula = (ImageView) findViewById(R.id.ivBrujula);
		imgBrujula.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_vibe.vibrate(Constantes.CTE_VIBRATION_MS);
				pararBrujula(v);
			}
		});
		//
		fabStart = (FloatingActionButton) findViewById(R.id.fab_play);
		fabStart.setRippleColor(Color.GRAY);
		fabStart.setContentDescription("STOP");
		fabStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_vibe.vibrate(Constantes.CTE_VIBRATION_MS);
				startStopRecorrido(v);
			}
		});
		//
		fabSettings = (FloatingActionButton) findViewById(R.id.fab_settings);
		fabSettings.setRippleColor(Color.GRAY);
		fabSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_vibe.vibrate(Constantes.CTE_VIBRATION_MS);
				abrirConfiguracion();
			}
		});
		fabZoomMas = (FloatingActionButton) findViewById(R.id.fab_zoom_mas);
		fabZoomMas.setRippleColor(Color.GRAY);
		fabZoomMas.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_vibe.vibrate(Constantes.CTE_VIBRATION_MS);
				realizarZoom(v);
			}
		});
		fabZoomMenos = (FloatingActionButton) findViewById(R.id.fab_zoom_menos);
		fabZoomMenos.setRippleColor(Color.GRAY);
		fabZoomMenos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_vibe.vibrate(Constantes.CTE_VIBRATION_MS);
				realizarZoom(v);
			}
		});
		fabCentrar = (FloatingActionButton) findViewById(R.id.fab_centrar);
		fabCentrar.setRippleColor(Color.GRAY);
		fabCentrar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_vibe.vibrate(Constantes.CTE_VIBRATION_MS);
				realizarZoom(v);
			}
		});
		// Cargar el layout despues de arrancar la apliación
		final LinearLayout layout = (LinearLayout) findViewById(R.id.llInferior);
		ViewTreeObserver vto = layout.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				inicializarRuta();
				inicializarLocalizacion();
			}
		});
		mantenerPantallaEncendida();
	}

	private void pararBrujula(View v) {
		m_brujulaParada = !m_brujulaParada;
        if (m_brujulaParada)
            imgBrujula.setImageDrawable(getDrawable(R.mipmap.ic_sin_brujula));
        else
            imgBrujula.setImageDrawable(getDrawable(R.mipmap.ic_brujula_naranja));
	}

	private void abrirConfiguracion() {
		Bundle b = new Bundle();
		startActivityForResult(new Intent(this, ConfiguracionActivity.class).putExtras(b),0);
	}

	private void realizarZoom(View v) {
		FloatingActionButton fab = (FloatingActionButton) v;
		String strEstado = fab.getContentDescription().toString();
		switch (Estado.valueOf(strEstado)) {
			case ZOOMMAS:
				m_escalaRuta += 1;
				mpRuta.setEscala(m_escalaRuta);
				break;
			case ZOOMMENOS:
				m_escalaRuta -= 1;
				if (m_escalaRuta < 1)
					m_escalaRuta = 1;
				mpRuta.setEscala(m_escalaRuta);
				break;
			case CENTRAR:
				m_escalaRuta = 1;
				mpRuta.setRutaCompleta(!mpRuta.isRutaCompleta());
				break;
		}
	}

	private void startStopRecorrido(View v) {
		FloatingActionButton fab = (FloatingActionButton) v;
		String strEstado = fab.getContentDescription().toString();
		switch (Estado.valueOf(strEstado)) {
			case START:
				m_recorrido.setEstado(Estado.STOP);
				m_recorrido.setTiempoFinal(Utils.fechaAhora());
				m_recorrido.setDistancia(0f);
				fab.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
				//fab.setBackgroundColor(Color.GREEN);
				fab.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(0,128,0)));
				//guardarRecorrido();
				setGrabarRecorrido(false);
				//
				getControlador().guardarRecorridoFin();
				//
				break;
			case STOP:
				m_recorrido.setEstado(Estado.START);
				m_recorrido.setTiempoInicial(Utils.fechaAhora());
				fab.setImageResource(R.drawable.ic_stop_circle_outline_white_48dp);
				//fab.setBackgroundColor(Color.RED);
				fab.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
				setGrabarRecorrido(true);
				//
				getControlador().guardarRecorridoCabecera((m_configuracion.isGPX() ? FormatoFichero.GPX : FormatoFichero.KML));
				//
				break;
			case PAUSE:
				setGrabarRecorrido(true);
				break;
		}
		fab.setContentDescription(m_recorrido.getEstado().nombre());
	}

	private void guardarRecorrido(Posicion pPosicion) {
		//getControlador().guardarRecorrido((m_configuracion.isGPX() ? FormatoFichero.GPX : FormatoFichero.KML) ,m_recorrido);
		getControlador().guardarRecorridoPunto(pPosicion);
		//
		//Toast.makeText(getApplicationContext(), getString(R.string.recorridoGuardado), Toast.LENGTH_LONG).show();
	}

	@SuppressLint("RestrictedApi")
	private void inicializarControlador() {
		m_controlador = ControladorLoc.getInstance();
		m_spDades = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
		recuperarConfiguracion();
		//
		if (m_configuracion!=null && m_configuracion.getRuta().equals(getString(R.string.sinRuta)))
			fabCentrar.setVisibility(View.GONE);
		else
			fabCentrar.setVisibility(View.VISIBLE);
	}

	public void actualizarPosicion(Location pLocation) {
		Posicion posicion = new Posicion(pLocation);
		//
		if (pLocation!=null) {
			//lat="41.474700" lon="2.035670"
			/*
			LogLoc.w("Proveedor: " + posicion.getProveedor() +
					" - Latitud: " + posicion.getPunto().getLatitud() +
					" - Longitud: " + posicion.getPunto().getLongitud() +
					" - Altitud: " + posicion.getPunto().getAltitud() +
					" - Velocidad: " + posicion.getVelocidad() +
					" - Dirección: " + posicion.getOrientacion() +
					" - Calidad: " + posicion.getPrecision());
			//
			*/
			posicion.setOrientacion(m_brujulaParada ? 0f : posicion.getOrientacion());
			actualizarPrecisionGps(posicion);
			actualizarProveedorGps(posicion);
			//verificarPosicion(posicion);
			//
			if (esPosicionPrecisa(posicion)) {
				if (m_pPosicionAnterior == null)
					m_pPosicionAnterior = posicion;
				//
				actualizarPantallaRuta(posicion);
				actualizarBrujula(posicion);
				actualizarPantallaPerfil(posicion);
				actualizarPantallaVelocidad(posicion);
				actualizarRecorrido(posicion);
				//
				if (isGrabarRecorrido())
					guardarRecorrido(posicion);
				// Guardar la posición actual para la siguiente -> Posición Anterior
				m_pPosicionAnterior = posicion;
			}
		}
	}
	/*
	private void verificarPosicion(Posicion posicion) {
		if (posicion.getPunto().getLatitud() == 0 &&
				posicion.getPunto().getLongitud() == 0 &&
				mpRuta.getRuta().getPuntos().size() > 1) {
			posicion.setPunto(mpRuta.getRuta().getPuntos().get(0));
		}
	}
	*/
	private boolean esPosicionPrecisa(Posicion pPosicion) {
		return !pPosicion.getProveedor().equals(ProviderLoc.NETWORK);
	}

	private void actualizarBrujula(Posicion p_aPosicion) {
		if (!mpRuta.isRutaCompleta())
			girarBrujula(p_aPosicion.getOrientacion());
	}

	private void actualizarPrecisionGps(Posicion p_aPosicion) {
		//
		int anIntervalo = Float.valueOf(p_aPosicion.getPrecision()/5).intValue();
		switch (anIntervalo) {
			case 0:
				imgSenalGps.setImageDrawable(getDrawable(R.mipmap.ic_senyal_5));
				break;
			case 1:
				imgSenalGps.setImageDrawable(getDrawable(R.mipmap.ic_senyal_4));
				break;
			case 2:
				imgSenalGps.setImageDrawable(getDrawable(R.mipmap.ic_senyal_3));
				break;
			case 3:
				imgSenalGps.setImageDrawable(getDrawable(R.mipmap.ic_senyal_2));
				break;
			case 4:
				imgSenalGps.setImageDrawable(getDrawable(R.mipmap.ic_senyal_1));
				break;
			default:
				imgSenalGps.setImageDrawable(getDrawable(R.drawable.ic_signal_off_black_48dp));
				break;
		}
	}

	private void actualizarProveedorGps(Posicion p_aPosicion) {
		tvTipoSenyal.setText(getString(R.string.pantalla_TipoSenyalDesconocida));
		//
		if (tvTipoSenyal != null && p_aPosicion != null && p_aPosicion.getProveedor() != null) {
			switch (p_aPosicion.getProveedor()) {
				case GPS:
					tvTipoSenyal.setText(getString(R.string.pantalla_TipoSenyalGps));
					break;
				case NETWORK:
					tvTipoSenyal.setText(getString(R.string.pantalla_TipoSenyalNetwork));
					break;
				case SIN:
					tvTipoSenyal.setText(getString(R.string.pantalla_TipoSenyalSin));
					break;
				default:
					tvTipoSenyal.setText(getString(R.string.pantalla_TipoSenyalDesconocida));
					break;
			}
		}
	}

	private void actualizarRecorrido(Posicion p_aPosicion) {

		if (isGrabarRecorrido()) {
			float distancia = m_recorrido.getDistancia();
			distancia += Math.abs(p_aPosicion.getLocation().distanceTo(m_pPosicionAnterior.getLocation()));
			m_recorrido.addPunto(p_aPosicion.getPunto());
			m_recorrido.setTiempoFinal(Utils.fechaAhora());
			m_recorrido.setDistancia(distancia);
		}
	}

	public void actualizarTiempos() {
		Calendar cal = Utils.fechaAhora();
		actualizarPantallaFechaHora(cal);
		actualizarPantallaRecorridoTiempos(cal);
	}

	private void actualizarPantallaRecorridoTiempos(Calendar p_cal) {
		if (isGrabarRecorrido()) {
			///////////////////////////////////////////////////
			// Calcular tiempo transcurrido
			long t = p_cal.getTimeInMillis() - m_recorrido.getTiempoInicial().getTimeInMillis();
			Calendar cal = Calendar.getInstance();
			// Restar 1 hora del GMT ¿?
			cal.setTimeInMillis(t - Constantes.CTE_TIEMPO_MILISEGUNDOS_HORA);
			if (tvTiempoEnCursoVal != null)
				tvTiempoEnCursoVal.setText(Utils.fecha2StringHora(cal));
			///////////////////////////////////////////////////
			// Calcular tiempo restante
			if (tvTiempoPendienteVal != null)
				tvTiempoPendienteVal.setText(Utils.fecha2StringHora(calcularTiempoRestante()));
			///////////////////////////////////////////////////
			// Calcular recorrido en curso
			float fDistancia = Utils.transformar2Km(m_recorrido.getDistancia()); // Convertir metros en Km
			if (tvDistanciaEnCursoVal != null) {
				tvDistanciaEnCursoVal.setText(Utils.transformarFloat2String(fDistancia));
			}
			// Calcular recorrido restante
			if (tvDistanciaPendienteVal != null) {
				float fDist = m_distanciaTotal - fDistancia;
				tvDistanciaPendienteVal.setText(Utils.transformarFloat2String(fDist));
			}
		}
	}

	private Calendar calcularTiempoRestante() {
		Calendar cal = Calendar.getInstance();
		cal.set(0, 0, 0, 0, 0, 0);
		return cal;
	}

	private void actualizarPantallaFechaHora(Calendar pCal) {
		if (tvFechaEstado != null)
			tvFechaEstado.setText(Utils.fecha2String(pCal));
	}

	public void actualizarPantallaVelocidad(Posicion p_aPosicion) {
		try {
			float fVelocidad = Utils.transformarKMh(p_aPosicion.getVelocidad());
			tvVelocidadKm.setText("" + Utils.transformarFloat2Entero(fVelocidad));
			tvVelocidadM.setText("" + Utils.transformarFloat2Decimal(fVelocidad));
		} catch (Exception ex) {
			System.err.println(ex);
			LogLoc.e("Error-1: " + ex.getMessage());
		}
	}

	private void inicializarTimer() {
		final Handler handler = new Handler();
		Runnable varRun;
		runOnUiThread(varRun = new Runnable() {
			@Override
			public void run() {
				actualizarTiempos();
				//
				handler.postDelayed(this, Constantes.CTE_TIMER_PERIODO);
			}
		});
		handler.postDelayed(varRun, Constantes.CTE_TIMER_PERIODO);
	}
	/*
	private void girarBrujula(float fAngulo) {
		Matrix matrix = new Matrix();
		matrix.postRotate(fAngulo);  // La rotación debe ser decimal (float o double)
		Bitmap original = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_brujula_naranja);
		//Bitmap rotatedBitmap = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
		Bitmap rotatedBitmap = Bitmap.createBitmap(original, 0, 0, 70, 70, matrix, true);
		imgBrujula.setImageBitmap(rotatedBitmap);
	}
	*/
	private void girarBrujula(final float fAngulo) {
		RotateAnimation rotateAnimation1 = new RotateAnimation(m_anguloAnterior, (int)fAngulo,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation1.setInterpolator(new LinearInterpolator());
		rotateAnimation1.setDuration(1000);
		rotateAnimation1.setRepeatCount(0);
		rotateAnimation1.setFillAfter(true);
		imgBrujula.startAnimation(rotateAnimation1);
		//
		rotateAnimation1.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation anim) {
			};
			public void onAnimationRepeat(Animation anim) {
			};
			public void onAnimationEnd(Animation anim) {
				m_anguloAnterior = (int)fAngulo;
			};
		});
	}
/*
	private Bitmap rotateBitmap(Bitmap bitmap, int rotationAngleDegree){
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int newW=w, newH=h;
		if (rotationAngleDegree==90 || rotationAngleDegree==270){
			newW = h;
			newH = w;
		}
		Bitmap rotatedBitmap = Bitmap.createBitmap(newW,newH, bitmap.getConfig());
		Canvas canvas = new Canvas(rotatedBitmap);

		Rect rect = new Rect(0,0,newW, newH);
		Matrix matrix = new Matrix();
		float px = rect.exactCenterX();
		float py = rect.exactCenterY();
		matrix.postTranslate(-bitmap.getWidth()/2, -bitmap.getHeight()/2);
		matrix.postRotate(rotationAngleDegree);
		matrix.postTranslate(px, py);
		canvas.drawBitmap(bitmap, matrix, new Paint( Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG ));
		matrix.reset();
		return rotatedBitmap;
	}
*/
	public void actualizarPantallaRuta(Posicion pPosicion) {
		mpRuta.setPuntoActual(pPosicion.getPunto());
		mpRuta.addPuntoActual(pPosicion.getPunto());
		mpRuta.setAnguloRotacion(pPosicion.getOrientacion());
	}

	public void actualizarPantallaPerfil(Posicion pPosicion) {
		mpPerfil.setPuntoActual(pPosicion.getPunto());
	}

	public void dibujarRuta(Ruta p_aRuta, Ruta p_aPtosInt) {
		/////////////////////////////
		// Ruta - Mapa principal
		/////////////////////////////
		if (mpRuta == null)
			mpRuta = (MapaView)findViewById(R.id.mpRuta);
		mpRuta.setRuta(p_aRuta);
		mpRuta.setPuntosInteres(p_aPtosInt);
		mpRuta.setEscala(m_escalaRuta);
		mpRuta.inicializar();
		/////////////////////////////
		// Ruta - Mapa Perfil
		/////////////////////////////
		if (mpPerfil == null)
			mpPerfil = (PerfilView)findViewById(R.id.mpPerfil);
		mpPerfil.setRuta(p_aRuta);
		mpPerfil.inicializar();
	}

	@Override
	public void onLocationChanged(Location location) {
		//LogLoc.w("onLocationChanged");
		actualizarPosicion(location);
	}

	@Override
	public void onStatusChanged(String s, int i, Bundle bundle) {
		//LogLoc.w("onStatusChanged");
		//inicializarLocalizacion();
	}

	@Override
	public void onProviderEnabled(String s) {
		//LogLoc.w("onProviderEnabled");
		//inicializarLocalizacion();
	}

	@Override
	public void onProviderDisabled(String s) {
		//LogLoc.w("onProviderDisabled");
		//inicializarLocalizacion();
	}

	private void inicializarLocalizacion() {
		Location location = null;
		m_locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		/*
			Inicialitzar permissos
		 */
		//
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_COARSE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			// Permission is not granted
			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.ACCESS_COARSE_LOCATION)) {
				// Show an explanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.
			} else {
				// No explanation needed, we can request the permission.
				String[] arrStr = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
				ActivityCompat.requestPermissions(this, arrStr, 1);

				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		} else {
			// Permission has already been granted
		}
		/*
			Fi - Inicialitzar permissos
		 */
		// getting GPS status
		boolean isGPSEnabled = m_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (isGPSEnabled) {
			m_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
			if (m_locationManager != null) {
				location = m_locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				//LogLoc.e("isGPSEnabled");
			}
		}
		// Si está marcado SÓLO GPS, no comprueba la RED de DATOS
		if (!m_configuracion.isGPS()) {
			// getting network status
			boolean isNetworkEnabled = m_locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (isNetworkEnabled) {
				m_locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
				if (m_locationManager != null) {
					location = m_locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					//LogLoc.e("isNetworkEnabled");
				}
			}
		}
		if (location != null) {
			actualizarPosicion(location);
		}
	}
	@SuppressLint("RestrictedApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 0) {
			if(resultCode == Activity.RESULT_OK){
				recuperarConfiguracion();
				if (m_configuracion!=null && m_configuracion.getRuta().equals(getString(R.string.sinRuta)))
					fabCentrar.setVisibility(View.GONE);
				else
					fabCentrar.setVisibility(View.VISIBLE);
			}
			if (resultCode == Activity.RESULT_CANCELED) {
				//Write your code if there's no result
			}
		}

	}

	private void recuperarConfiguracion() {
		String str = Utils.getValorSP(m_spDades, Constantes.CTE_CLAVE_CONFIGURACION);
		try {
			if (m_spDades==null || "".equals(str)) {
				m_configuracion = new Configuracion();
				m_configuracion.setGPS(false);
				m_configuracion.setRuta("");
				m_configuracion.setGPX(true);
				//
				Utils.putValorSP(m_spDades, Constantes.CTE_CLAVE_CONFIGURACION, Serializar.serializar(m_configuracion));
			}
			else {
				m_configuracion = (Configuracion) Serializar.desSerializar(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
			LogLoc.e("Error-2: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			LogLoc.e("Error-3: " + e.getMessage());
		}
		inicializarRuta();
		inicializarLocalizacion();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	private void mantenerPantallaEncendida() {
		/* This code together with the one in onDestroy()
         * will make the screen be always on until this Activity gets destroyed.
        */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private void devolverPantallaEstado() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	@Override
	public void onDestroy() {
		devolverPantallaEstado();
		super.onDestroy();
	}
}
