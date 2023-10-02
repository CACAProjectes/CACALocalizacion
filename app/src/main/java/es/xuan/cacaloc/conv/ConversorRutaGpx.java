package es.xuan.cacaloc.conv;

import android.os.Environment;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import es.xuan.cacaloc.constantes.Constantes;
import es.xuan.cacaloc.constantes.FormatoFichero;
import es.xuan.cacaloc.model.Punto;
import es.xuan.cacaloc.model.Recorrido;
import es.xuan.cacaloc.model.Ruta;
import es.xuan.cacaloc.util.LogLoc;
import es.xuan.cacaloc.util.Utils;

public class ConversorRutaGpx implements ConversorRutaInt {
	PrintWriter m_writerPuntual = null;
	@Override
	public Ruta getRuta(String p_nomFileRuta) {		
	    try {
			return readXml(p_nomFileRuta);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getRutaStr(Ruta p_ruta) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConversorRutaInt getInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveRuta(Recorrido pRecorrido) {
		String strPathFile = "";
		File fXmlFile = null;
		File sdDir = Environment.getExternalStorageDirectory();
		if (!(sdDir.canWrite() && sdDir.canRead())) {
			return;
		}
		strPathFile = sdDir.getAbsolutePath() + Constantes.CTE_PATH_FILES + pRecorrido.getNombre() + "_" + Utils.fecha2StringFile(Utils.fechaAhora())+ Constantes.CTE_SUFIJO_GPX;
		fXmlFile = new File(strPathFile);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(fXmlFile.getAbsolutePath(), "UTF-8");
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			writer.println("<gpx version=\"1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.topografix.com/GPX/1/0\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\" creator=\"http://www.openrunner.com\">");
			writer.println("<trk>");
			writer.println("<name>"+ pRecorrido.getNombre() +"</name>");
			writer.println("<trkseg>");
			for (Punto punto : pRecorrido.getPuntos()) {
				writer.println("<trkpt lat=\"" +punto.getLatitud()+ "\" lon=\"" +punto.getLongitud()+ "\">");
				writer.println("<ele>"+punto.getAltitud()+"</ele>");
				writer.println("<time>"+ Utils.fecha2StringGPX(punto.getFecha())+"</time>");
				writer.println("</trkpt>");
			}
			writer.println("</trkseg>");
			writer.println("</trk>");
			writer.println("</gpx>");
		} catch (Exception ex) {
			LogLoc.e("Error-11: " + ex.getMessage());
		}
		writer.close();
		return;
	}

	@Override
	public void saveRecorridoCabecera() {
		String strPathFile = "";
		File fXmlFile = null;
		File sdDir = Environment.getExternalStorageDirectory();
		if (!(sdDir.canWrite() && sdDir.canRead())) {
			return;
		}
		strPathFile = sdDir.getAbsolutePath() + Constantes.CTE_PATH_FILES + Constantes.CTE_NOMBRE_RUTA + "_" + Utils.fecha2StringFile(Utils.fechaAhora())+ Constantes.CTE_SUFIJO_GPX;
		fXmlFile = new File(strPathFile);
		//
		if (m_writerPuntual != null)
			saveRecorridoFin();
		try {
			m_writerPuntual = new PrintWriter(fXmlFile.getAbsolutePath(), "UTF-8");
			m_writerPuntual.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			m_writerPuntual.println("<gpx version=\"1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.topografix.com/GPX/1/0\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\" creator=\"http://www.openrunner.com\">");
			m_writerPuntual.println("<trk>");
			m_writerPuntual.println("<name>"+ strPathFile +"</name>");
			m_writerPuntual.println("<trkseg>");
		} catch (Exception ex) {
			LogLoc.e("Error-10: " + ex.getMessage());
		}
	}

	@Override
	public void saveRecorridoPunto(Punto pPunto) {
		if (m_writerPuntual != null) {
			m_writerPuntual.println("<trkpt lat=\"" + pPunto.getLatitud() + "\" lon=\"" + pPunto.getLongitud() + "\">");
			m_writerPuntual.println("<ele>" + pPunto.getAltitud() + "</ele>");
			m_writerPuntual.println("<time>" + Utils.fecha2StringGPX(pPunto.getFecha()) + "</time>");
			m_writerPuntual.println("</trkpt>");
		}
		else {
			LogLoc.e("Error-12: El recorrido no puede guardarse porque no se ha inicializado el fichero de ruta.");
		}
	}

	@Override
	public void saveRecorridoFin() {
		if (m_writerPuntual != null) {
			m_writerPuntual.println("</trkseg>");
			m_writerPuntual.println("</trk>");
			m_writerPuntual.println("</gpx>");
			//
			m_writerPuntual.close();
		}
		else {
			LogLoc.e("Error-13: El recorrido no puede guardarse porque no se ha inicializado el fichero de ruta.");
		}
	}

	public Ruta readXml(String p_nomFile) throws ParserConfigurationException {
		Ruta ruta = new Ruta();
		String strPathFile = "";
		File fXmlFile = null;
		File sdDir = Environment.getExternalStorageDirectory(); 
        if (!(sdDir.canWrite() && sdDir.canRead())) {        	
        	return null;
        }
        strPathFile = sdDir.getAbsolutePath() + Constantes.CTE_PATH_FILES + p_nomFile;
        fXmlFile = new File(strPathFile);
		if (!fXmlFile.exists())
			return null;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = null;
		try {
		    doc = dBuilder.parse(fXmlFile);
		} catch (Exception e) {
		    e.printStackTrace();
			LogLoc.e("Error-12: " + e.getMessage());
			return null;
		}
		doc.getDocumentElement().normalize();	
		//
		ruta.setNombre(p_nomFile);
		ruta.setDescripcion(p_nomFile);
		ruta.setFecha(Calendar.getInstance());
		ruta.setFormato(FormatoFichero.GPX);
		Punto punto = null;
		//
		NodeList nList = doc.getElementsByTagName("trkpt");		
		for (int temp = 0; temp < nList.getLength(); temp++) {		
		    Node nNode = nList.item(temp);		
		    if (nNode.getNodeType() == Node.ELEMENT_NODE) {		
		        Element eElement = (Element) nNode;		
		        punto = new Punto(
		        		Float.parseFloat(eElement.getAttributeNode("lon").getTextContent()), 
		        		Float.parseFloat(eElement.getAttributeNode("lat").getTextContent()), 
		        		Float.parseFloat(eElement.getElementsByTagName("ele").item(0).getTextContent()));
		        if (eElement.getElementsByTagName("name").item(0) != null)
		        	punto.setNombre(eElement.getElementsByTagName("name").item(0).getTextContent());
		        if (temp == 0 && ruta.getPuntos().size() == 1)
					ruta.insertPunto(0, punto);	// Reemplaza el primer punto por defecto por el de la RUTA
				else
					ruta.addPunto(punto);	// Añade un nuevo punto a la ruta.
		    }
		}
		return ruta;
	}
}
