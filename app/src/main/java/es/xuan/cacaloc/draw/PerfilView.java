package es.xuan.cacaloc.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import es.xuan.cacaloc.model.Punto;
import es.xuan.cacaloc.model.Ruta;
import es.xuan.cacaloc.util.Utils;

/**
 * Created by jcamposp on 18/11/2016.
 */
public class PerfilView extends View {
    private Paint drawPaint;
    private Paint drawPaintPosicion;
    private float escalaX = 0;
    private float escalaY = 0;
    private Punto puntoActual;
    private Ruta ruta;

    public Punto getPuntoActual() {
        return puntoActual;
    }
    public void setPuntoActual(Punto puntoActual) {
        this.puntoActual = puntoActual;
    }
    public Ruta getRuta() { return ruta; }
    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }
    //
    public PerfilView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inicializar();
        setupPaintPerfil();
        setupPaintPosicion();
    }
    public void inicializar() {
        puntoActual = new Punto(0, 0, 0);
        //
        calcularEscala();
    }
    // Setup paint with color and stroke styles
    private void setupPaintPerfil() {
        drawPaint = new Paint();
        drawPaint.setColor(Color.YELLOW);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(2);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }
    // Setup paint with color and stroke styles
    private void setupPaintPosicion() {
        drawPaintPosicion = new Paint();
        drawPaintPosicion.setColor(Color.WHITE);
        drawPaintPosicion.setAntiAlias(true);
        drawPaintPosicion.setStrokeWidth(2);
        drawPaintPosicion.setStyle(Paint.Style.STROKE);
        drawPaintPosicion.setStrokeJoin(Paint.Join.ROUND);
        drawPaintPosicion.setStrokeCap(Paint.Cap.ROUND);
        drawPaintPosicion.setTextSize(20);
    }
    // Draws the path created during the touch events
    @Override
    protected void onDraw(Canvas canvas) {
        if (this.getWidth() != 0 && this.getHeight() != 0) {
            pintarPuntosRuta(canvas);
            pintarPosicionActual(canvas);
        }
        //
        refrescarDraw();
    }

    private void pintarPuntosRuta(Canvas p_aCanvas) {
        Path path = new Path();
        if (getRuta() == null)
            return;
        long lContador = 0;
        //
        for (Punto punto : getRuta().getPuntos()) {
            //
            if (lContador++ == 0) {
                path.moveTo(0,
                        (getRuta().getDesnivelMax() - punto.getAltitud()) * escalaY
                );
            }
            else {
                /*
                    Si la altitud es cero, es que no ha cogido buena señal
                    En ese caso no se pinta el punto en pantalla.
                 */
                if (punto.getAltitud() != 0)
                    path.lineTo(escalaX * lContador,
                        (getRuta().getDesnivelMax() - punto.getAltitud()) * escalaY
                );
            }
        }
        //
        if(!path.isEmpty())
            p_aCanvas.drawPath(path, drawPaint);
    }

    private void pintarPosicionActual(Canvas p_aCanvas) {
        long orden = Utils.buscarPuntoMasCercano(getRuta(), getPuntoActual());
        // Línea vertical
        p_aCanvas.drawLine(escalaX * orden,0,escalaX * orden,this.getHeight(),drawPaintPosicion);
        // Texto con la altura centrado
        float lX = escalaX * orden;
        String strAltura = "" + Utils.transformarFloat2Entero(getPuntoActual().getAltitud());
        if (lX > (this.getWidth()/2))   // Si la posición supera la mitad de la gráfica (X) pone el texto en la izquierda. Si no en la derecha de la línea de posición
            lX = lX - strAltura.length() * 15;
        else
            lX += 5;
        //
        p_aCanvas.drawText(strAltura, lX, this.getHeight()/2, drawPaintPosicion);
    }

    private void refrescarDraw() {
        postInvalidate(); // Indicate view should be redrawn
    }

    private void calcularEscala() {
        try {
            escalaY = (float)this.getHeight() / (getRuta().getDesnivelMax() - getRuta().getDesnivelMin());
            escalaX = (float)this.getWidth() / (getRuta().getPuntos().size());
        } catch (Exception e) {
            escalaY = 1;
            escalaX = 1;
        }
    }
}
