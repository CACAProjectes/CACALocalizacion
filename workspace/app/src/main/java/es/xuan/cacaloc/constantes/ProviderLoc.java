package es.xuan.cacaloc.constantes;

public enum ProviderLoc {
	GPS("GPS"),
	NETWORK("NETWORK"),
	GPS_NETWORK("GPS-NETWORK"),
	SIN("SIN-SEÑAL");
	
	private String nombre;
	
	ProviderLoc(String pComp) {
		nombre = pComp;
	}
	
	public String nombre() {
		return nombre;
	}
}
