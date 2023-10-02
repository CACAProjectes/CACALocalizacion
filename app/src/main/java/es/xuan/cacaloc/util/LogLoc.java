package es.xuan.cacaloc.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import es.xuan.cacaloc.constantes.Constantes;

public class LogLoc {

    public static void d(String p_string) {
        String strPathFile;
        FileWriter fXmlFile;
        File sdDir = Environment.getExternalStorageDirectory();
        if (!(sdDir.canWrite() && sdDir.canRead())) {
            return;
        }
        strPathFile = sdDir.getAbsolutePath() + Constantes.CTE_PATH_LOG;
        PrintWriter writer = null;
        BufferedWriter bw = null;
        try {
            fXmlFile = new FileWriter(strPathFile, true);
            bw = new BufferedWriter(fXmlFile);
            writer = new PrintWriter(bw);
            writer.println(Utils.fecha2StringLog(Utils.fechaAhora()) + " - " + p_string);
            Log.d("LogLoc", p_string);
            System.out.println(p_string);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (writer!=null)
            writer.close();
        if (bw!=null)
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println(e.getMessage());
            }
    }
    public static void e(String p_string) {
        String strPathFile;
        FileWriter fXmlFile;
        File sdDir = Environment.getExternalStorageDirectory();
        if (!(sdDir.canWrite() && sdDir.canRead())) {
            return;
        }
        strPathFile = sdDir.getAbsolutePath() + Constantes.CTE_PATH_LOG;
        PrintWriter writer = null;
        BufferedWriter bw = null;
        try {
            fXmlFile = new FileWriter(strPathFile, true);
            bw = new BufferedWriter(fXmlFile);
            writer = new PrintWriter(bw);
            writer.println(Utils.fecha2StringLog(Utils.fechaAhora()) + " - " + p_string);
            Log.e("LogLoc", p_string);
            System.err.println(p_string);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (writer!=null)
            writer.close();
        if (bw!=null)
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
