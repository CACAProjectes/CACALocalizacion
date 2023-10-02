package es.xuan.cacaloc.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import es.xuan.cacaloc.constantes.Constantes;
import es.xuan.cacaloc.model.Punto;
import es.xuan.cacaloc.model.Ruta;
import es.xuan.cacaloc.util.Utils;

//import es.xuan.cacaloc.util.LogLoc;

public class MapaView extends View {

	private Paint drawPaint;
	private Paint drawPaintPtosInt;
	private Paint drawPaintPtosIniFin;
	private Paint drawPaintPosAct;
	private Paint drawPaintRutaRec;	// Ruta recorrida
	private Ruta ruta;
	private Ruta puntosInteres;
	private Punto puntoActual;
	private float escala = 1;
	private float anguloRotacion = 0;
	private float escalaX = 0;
	private float escalaY = 0;
	private float puntoMedioX;
	private float puntoMedioY;
	private boolean rutaCompleta;
	private ArrayList<Punto> m_rutaRecorrida;
	private int posAnteriorX = 0;
	private int posAnteriorY = 0;
	private int posTranlacionX = 0;
	private int posTranlacionY = 0;

	public float getAnguloRotacion() {
		return anguloRotacion;
	}

	public void setAnguloRotacion(float anguloRotacion) {

		this.anguloRotacion = Utils.transformarGrados2Radianes(anguloRotacion);
	}

	public Punto getPuntoActual() {
		if (puntoActual !=null &&
				puntoActual.getLatitud() == 0 &&
				puntoActual.getLongitud() == 0 &&
				getRuta() != null &&
				getRuta().getPuntos().size() > 1)
			puntoActual = getRuta().getPuntos().get(0);
		return puntoActual;
	}

	public void setPuntoActual(Punto puntoActual) {
		this.puntoActual = puntoActual;
	}

	public float getEscala() {
		return escala;
	}

	public void setEscala(float escala) {
		this.escala = escala;
	}

	public Ruta getPuntosInteres() {
		return puntosInteres;
	}

	public void setPuntosInteres(Ruta puntosInteres) {
		this.puntosInteres = puntosInteres;
	}

	public Ruta getRuta() {
		return ruta;
	}

	public void setRuta(Ruta ruta) {
		this.ruta = ruta;
	}
	public MapaView(Context context,AttributeSet attrs) {
	    super(context, attrs);
	    inicializar();
		inicializarEvents();
		setupPaintRuta();
		setupPaintRutaRec();
		setupPaintPtosInt();
	    setupPaintPosicionActual();
		setupPaintPtosInicioFin();
	}

	private double calcularEscalaDistancia() {
		double fDistancia = 0;
		if (getRuta()!=null) {
			fDistancia = Utils.distancia(
					getRuta().getLargoMax(),
					getRuta().getAnchoMax(),
					getRuta().getLargoMin(),
					getRuta().getAnchoMin());
		}
		return fDistancia * Constantes.CTE_CONVERSION_KM_CM / Constantes.CTE_ANCHO_PANTALLA / getEscala();	// Divide la distancia por la Escala seleccionada por el usuario
	}

	private void inicializarEvents() {
		// Inicializa los eventos para mover las rutas en pantalla
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						posAnteriorX = (int)event.getX();
						posAnteriorY = (int)event.getY();
						break;
					case MotionEvent.ACTION_MOVE:
						//LogLoc.w("X: " + event.getX() + " - Y: " + event.getY());
						posTranlacionX = (int)event.getX() - posAnteriorX;
						posTranlacionY = (int)event.getY() - posAnteriorY;
						//System.out.println("X: " + posTranlacionX + " - Y: " + posTranlacionY);
						break;
					case MotionEvent.ACTION_UP:
						posTranlacionX = 0;
						posTranlacionY = 0;
						break;
					default:
						return false;
				}
				return true;
			}
		});
	}

	public void inicializar() {
		puntoActual = new Punto(0, 0, 0);
		//
		calcularEscala();
		calcularPuntoMedio();
		//
		m_rutaRecorrida = new ArrayList<Punto>();
	}

	// Setup paint with color and stroke styles
	private void setupPaintRutaRec() {
		drawPaintRutaRec = new Paint();
		drawPaintRutaRec.setColor(Color.GREEN);
		drawPaintRutaRec.setAntiAlias(true);
		drawPaintRutaRec.setStrokeWidth(2);
		drawPaintRutaRec.setStyle(Paint.Style.STROKE);
		drawPaintRutaRec.setStrokeJoin(Paint.Join.ROUND);
		drawPaintRutaRec.setStrokeCap(Paint.Cap.ROUND);
	}
	// Setup paint with color and stroke styles
	private void setupPaintRuta() {
	    drawPaint = new Paint();
	    drawPaint.setColor(Color.WHITE);
	    drawPaint.setAntiAlias(true);
	    drawPaint.setStrokeWidth(2);
	    drawPaint.setStyle(Paint.Style.STROKE);
	    drawPaint.setStrokeJoin(Paint.Join.ROUND);
	    drawPaint.setStrokeCap(Paint.Cap.ROUND);
	  }
	// Setup paint with color and stroke styles
	private void setupPaintPtosInicioFin() {
		drawPaintPtosIniFin = new Paint();
		drawPaintPtosIniFin.setColor(Color.WHITE);
		drawPaintPtosIniFin.setAntiAlias(true);
		drawPaintPtosIniFin.setStrokeWidth(1);
		drawPaintPtosIniFin.setStyle(Paint.Style.FILL);
		drawPaintPtosIniFin.setStrokeJoin(Paint.Join.ROUND);
		drawPaintPtosIniFin.setStrokeCap(Paint.Cap.ROUND);
		drawPaintPtosIniFin.setTextSize(15);
	}
	// Setup paint with color and stroke styles
	private void setupPaintPtosInt() {
	    drawPaintPtosInt = new Paint();
	    drawPaintPtosInt.setColor(Color.YELLOW);
	    drawPaintPtosInt.setAntiAlias(true);
	    drawPaintPtosInt.setStrokeWidth(1);
	    drawPaintPtosInt.setStyle(Paint.Style.FILL);
	    drawPaintPtosInt.setStrokeJoin(Paint.Join.ROUND);
	    drawPaintPtosInt.setStrokeCap(Paint.Cap.ROUND);
	    drawPaintPtosInt.setTextSize(20);
	}
	// Setup paint with color and stroke styles
	private void setupPaintPosicionActual() {
	    drawPaintPosAct = new Paint();
	    drawPaintPosAct.setColor(Color.CYAN);
	    drawPaintPosAct.setAntiAlias(true);
	    drawPaintPosAct.setStrokeWidth(4);
	    drawPaintPosAct.setStyle(Paint.Style.STROKE);
	    drawPaintPosAct.setStrokeJoin(Paint.Join.ROUND);
	    drawPaintPosAct.setStrokeCap(Paint.Cap.ROUND);
	  }
	// Draws the path created during the touch events
	@Override
	protected void onDraw(Canvas canvas) {
		if (this.getWidth() != 0 && this.getHeight() != 0) {
			calcularEscala();
			pintarPuntosRuta(canvas);
			pintarPuntosRutaRec(canvas);
			pintarTextoPuntosInteres(canvas);
			pintarPosicionActual(canvas);
			pintarTextoInicioFin(canvas);
			pintarEscala(canvas);
		}
		//
		refrescarDraw();
	}

	private void pintarEscala(Canvas p_aCanvas) {
		//
		float dEscala = (float)calcularEscalaDistancia();
		String strEscala = "1:1";
		if (dEscala != 0)
			strEscala = "1:" + Utils.transformarFloat2Entero(dEscala);
		else
			strEscala = "1:" + Utils.transformarFloat2Entero(Constantes.CTE_ESCALA_DEF);
		//
		p_aCanvas.drawText(strEscala, puntoMedioX - 5 * strEscala.length(), puntoMedioY + 160, drawPaintPtosInt);
	}

	private void calcularPuntoMedio() {
		if (this.getWidth() != 0 && this.getHeight() != 0) {
			puntoMedioX = this.getWidth() / 2;
			puntoMedioY = this.getHeight() * 3 / 4;			
		}		
	}

	private void refrescarDraw() {
		postInvalidate(); // Indicate view should be redrawn		
	}

	private void calcularEscala() {
		try {
			/*
			if ((getRuta().getLargoMax() - getRuta().getLargoMin() < 1)  ||
				(getRuta().getAnchoMax() - getRuta().getAnchoMin()) < 1) {
				escalaY = 1;
				escalaX = 1;
				return;
			}
			*/
			escalaY = getEscala() * this.getHeight() / (getRuta().getLargoMax() - getRuta().getLargoMin());		
			escalaX = getEscala() * this.getWidth() / (getRuta().getAnchoMax() - getRuta().getAnchoMin());
			// Rectificar la Escala si es demasiado grande
			if (escalaX > Float.MAX_VALUE - 1)
				escalaX = Constantes.CTE_ESCALA_DEF;
			if (escalaY > Float.MAX_VALUE - 1)
				escalaY = Constantes.CTE_ESCALA_DEF;
		} catch (Exception e) {
			escalaY = Constantes.CTE_ESCALA_DEF;
			escalaX = Constantes.CTE_ESCALA_DEF;
		}
	}
		
	private void pintarPosicionActual(Canvas p_aCanvas) {
		if (isRutaCompleta()) {
			//
			float escalaXY = (escalaX > escalaY ? escalaY : escalaX);
			float fX = (getPuntoActual().getLongitud() - getRuta().getAnchoMin()) * escalaXY;
			float fY = (getRuta().getLargoMax() - getPuntoActual().getLatitud()) * escalaXY;
			p_aCanvas.drawCircle(fX, fY, (float)5, drawPaintPosAct);
		}
		else {
			p_aCanvas.drawCircle(puntoMedioX, puntoMedioY, (float)5, drawPaintPosAct);
		}
	}

	private void pintarTextoInicioFin(Canvas p_aCanvas) {
		float fX = 0;
		float fXF = 0;
		float fY = 0;
		float fYF = 0;
		//
		float escalaXY = (escalaX > escalaY ? escalaY : escalaX);
		// Al menos debe haber dos puntos para poner INICIO y FIN
		for (int i=0;i<2 && getRuta()!=null && getRuta().getPuntos()!=null && getRuta().getPuntos().size()>1;i++) {
			Punto punto = null;
			if (i==0) {
				punto = getRuta().getPuntos().get(0);
			}
			else {
				punto = getRuta().getPuntos().get(getRuta().getPuntos().size()-1);
			}
			//
			float anguloDef = (isRutaCompleta() ? 0f : getAnguloRotacion());
			float ptoMedX = (isRutaCompleta() ? 0f : puntoMedioX);
			float ptoMedY = (isRutaCompleta() ? 0f : puntoMedioY);
			//
			if (isRutaCompleta()) {
				fX = (punto.getLongitud() - getRuta().getAnchoMin()) * escalaXY + posTranlacionX;
				fY = (getRuta().getLargoMax() - punto.getLatitud()) * escalaXY + posTranlacionY;
			}
			else {
				fX = (punto.getLongitud() - getPuntoActual().getLongitud()) * escalaXY + posTranlacionX;
				fY = (getPuntoActual().getLatitud() - punto.getLatitud()) * escalaXY + posTranlacionY;
			}
			//
			fXF = fX * (float) Math.cos(anguloDef) - fY * (float) Math.sin(anguloDef);
			fYF = fX * (float) Math.sin(anguloDef) + fY * (float) Math.cos(anguloDef);
			//
			p_aCanvas.drawCircle(fXF + ptoMedX, fYF + ptoMedY, (float)5, drawPaintPtosIniFin);
			if (i==0) {
				p_aCanvas.drawText("INICIO", fXF + ptoMedX - 5, fYF + ptoMedY - 5, drawPaintPtosIniFin);
			}
			else {
				p_aCanvas.drawText("FINAL", fXF + ptoMedX + 5, fYF + ptoMedY + 5, drawPaintPtosIniFin);
			}
		}
	}

	private void pintarTextoPuntosInteres(Canvas p_aCanvas) {
		if (getPuntosInteres() == null)
			  return;
		  //
		float fX = 0;
		float fXF = 0;
		float fY = 0;
		float fYF = 0;
		//
		float escalaXY = (escalaX > escalaY ? escalaY : escalaX);
		//
		//setAnguloRotacion(45f);
		//
		for (Punto punto : getPuntosInteres().getPuntos()) {
			//
			/*
			y = x' * sin(theta) + y' * cos (theta)
			x = x' * cos(theta) - y' * sin (theta)
			*/
			//
			/*
			fX = (punto.getLongitud() - getPuntoActual().getLongitud()) * escalaXY;
			fY = (getPuntoActual().getLatitud() - punto.getLatitud()) * escalaXY;
			//
			fXF = fX * (float)Math.cos(getAnguloRotacion()) - fY * (float)Math.sin(getAnguloRotacion());
			fYF = fX * (float)Math.sin(getAnguloRotacion()) + fY * (float)Math.cos(getAnguloRotacion());
			*/
			float anguloDef = (isRutaCompleta() ? 0f : getAnguloRotacion());
			float ptoMedX = (isRutaCompleta() ? 0f : puntoMedioX);
			float ptoMedY = (isRutaCompleta() ? 0f : puntoMedioY);
			// Muestra la ruta completa centrada en la pantalla
			/*
			y = x' * sin(theta) + y' * cos (theta)
			x = x' * cos(theta) - y' * sin (theta)
			*/
			//
			if (isRutaCompleta()) {
				fX = (punto.getLongitud() - getRuta().getAnchoMin()) * escalaXY + posTranlacionX;
				fY = (getRuta().getLargoMax() - punto.getLatitud()) * escalaXY + posTranlacionY;
			}
			else {
				fX = (punto.getLongitud() - getPuntoActual().getLongitud()) * escalaXY + posTranlacionX;
				fY = (getPuntoActual().getLatitud() - punto.getLatitud()) * escalaXY + posTranlacionY;
			}
			//
			fXF = fX * (float) Math.cos(anguloDef) - fY * (float) Math.sin(anguloDef);
			fYF = fX * (float) Math.sin(anguloDef) + fY * (float) Math.cos(anguloDef);
			//
			p_aCanvas.drawCircle(fXF + ptoMedX, fYF + ptoMedY, (float)5, drawPaintPtosInt);
			p_aCanvas.drawText(punto.getNombre(), fXF + ptoMedX + 4, fYF + ptoMedY - 4, drawPaintPtosInt);
		}
	  }

	private void pintarPuntosRutaRec(Canvas p_aCanvas) {
        Path path = new Path();
        if (m_rutaRecorrida == null)
            return;
        long lContador = 0;
        float fX = 0;
        float fXF = 0;
        float fY = 0;
        float fYF = 0;
        float escalaXY = (escalaX > escalaY ? escalaY : escalaX);
        //
        float anguloDef = (isRutaCompleta() ? 0f : getAnguloRotacion());
        float ptoMedX = (isRutaCompleta() ? 0f : puntoMedioX);
        float ptoMedY = (isRutaCompleta() ? 0f : puntoMedioY);
		//
		for (Punto punto : m_rutaRecorrida) {
            //if (isRutaCompleta()) {
            //    fX = (punto.getLongitud() - getRuta().getAnchoMin()) * escalaXY + posTranlacionX;
            //    fY = (getRuta().getLargoMax() - punto.getLatitud()) * escalaXY + posTranlacionY;
            //}
            //else {
                fX = (punto.getLongitud() - getPuntoActual().getLongitud()) * escalaXY + posTranlacionX;
                fY = (getPuntoActual().getLatitud() - punto.getLatitud()) * escalaXY + posTranlacionY;
            //}
            //
            fXF = fX * (float) Math.cos(anguloDef) - fY * (float) Math.sin(anguloDef);
            fYF = fX * (float) Math.sin(anguloDef) + fY * (float) Math.cos(anguloDef);
            //
			p_aCanvas.drawCircle(fXF + ptoMedX, fYF + ptoMedY, (float)2, drawPaintRutaRec);
        }
	}

	private void pintarPuntosRuta(Canvas p_aCanvas) {
		Path path = new Path();
		if (getRuta() == null)
			return;
		long lContador = 0;
		float fX = 0;
		float fXF = 0;
		float fY = 0;
		float fYF = 0;
		float escalaXY = (escalaX > escalaY ? escalaY : escalaX);
		//
		float anguloDef = (isRutaCompleta() ? 0f : getAnguloRotacion());
		float ptoMedX = (isRutaCompleta() ? 0f : puntoMedioX);
		float ptoMedY = (isRutaCompleta() ? 0f : puntoMedioY);
		for (Punto punto : getRuta().getPuntos()) {
			// Muestra la ruta completa centrada en la pantalla
			/*
			y = x' * sin(theta) + y' * cos (theta)
			x = x' * cos(theta) - y' * sin (theta)
			*/
			//
			if (isRutaCompleta()) {
				fX = (punto.getLongitud() - getRuta().getAnchoMin()) * escalaXY + posTranlacionX;
				fY = (getRuta().getLargoMax() - punto.getLatitud()) * escalaXY + posTranlacionY;
			}
			else {
				fX = (punto.getLongitud() - getPuntoActual().getLongitud()) * escalaXY + posTranlacionX;
				fY = (getPuntoActual().getLatitud() - punto.getLatitud()) * escalaXY + posTranlacionY;
			}
			//
			fXF = fX * (float) Math.cos(anguloDef) - fY * (float) Math.sin(anguloDef);
			fYF = fX * (float) Math.sin(anguloDef) + fY * (float) Math.cos(anguloDef);
			//
			if (lContador++ == 0) {
				path.moveTo(fXF + ptoMedX,
						fYF + ptoMedY
				);
			} else {
				path.lineTo(fXF + ptoMedX,
						fYF + ptoMedY);
			}
		}
		//
		if(!path.isEmpty())
			p_aCanvas.drawPath(path, drawPaint);
	}

	public void setRutaCompleta(boolean rutaCompleta) {
		this.rutaCompleta = rutaCompleta;
	}

	public boolean isRutaCompleta() {
		return rutaCompleta;
	}

	public void addPuntoActual(Punto pPunto) {
		// Guarda en una lista los puntos por los que va pasando
		// Comprobar que los puntos guardados son diferentes
		//System.out.println("Tam.: " + m_rutaRecorrida.size() + " - Lat.: " + getPuntoActual().getLatitud() + " - Lon.: " + getPuntoActual().getLongitud());
		if (m_rutaRecorrida == null)
			m_rutaRecorrida = new ArrayList<Punto>();
		// El primer punto lo añade siempre
		if (m_rutaRecorrida.size() == 0)
			m_rutaRecorrida.add(pPunto);
		// Sólo añade los puntos diferentes
		else if ((m_rutaRecorrida.size() > 0) &&
				m_rutaRecorrida.get(m_rutaRecorrida.size()-1).getLatitud() != pPunto.getLatitud() &&
				m_rutaRecorrida.get(m_rutaRecorrida.size()-1).getLongitud() != pPunto.getLongitud())
			m_rutaRecorrida.add(pPunto);
	}
}
