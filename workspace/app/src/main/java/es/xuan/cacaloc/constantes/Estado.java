package es.xuan.cacaloc.constantes;

/**
 * Created by jcamposp on 21/11/2016.
 */

public enum Estado {
    START("START"),
    PAUSE("PAUSE"),
    STOP("STOP"),
    ZOOMMAS("ZOOMMAS"),
    ZOOMMENOS("XOOMENOS"),
    CENTRAR("CENTRAR");

    private String nombre;

    Estado(String pComp) {
        nombre = pComp;
    }

    public String nombre() {
        return nombre;
    }
}
