package es.xuan.cacaloc.constantes;

public enum FormatoFichero {
	GPX("GPX"),
	KML("KML");
	
	private String nombre;
	
	FormatoFichero(String pComp) {
		nombre = pComp;
	}
	
	public String nombre() {
		return nombre;
	}
}
