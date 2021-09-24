package es.xuan.cacaloc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializar {
	
	public static String serializar(Object obj) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oout = new ObjectOutputStream(baos);
		oout.writeObject(obj);
		oout.close();
		return Base64.encodeBytes(baos.toByteArray());
	}

	public static Object desSerializar(String p_bufObject) throws IOException, ClassNotFoundException {
		byte[] buf;
		if (p_bufObject != null) {
			buf = Base64.decode(p_bufObject);
			ObjectInputStream objectIn = new ObjectInputStream(
					new ByteArrayInputStream(buf)
					);
			return objectIn.readObject();
		}
		return null;
	}
}
