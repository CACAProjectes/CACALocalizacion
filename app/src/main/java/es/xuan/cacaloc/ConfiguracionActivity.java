package es.xuan.cacaloc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;

import es.xuan.cacaloc.constantes.Constantes;
import es.xuan.cacaloc.model.Configuracion;
import es.xuan.cacaloc.util.Serializar;
import es.xuan.cacaloc.util.Utils;

public class ConfiguracionActivity extends AppCompatActivity {

    private Vibrator m_vibe = null;
    private SharedPreferences m_spDades = null;
    private Configuracion m_configuracion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        //
        inicializar();
        //
        inicializarValores();
    }

    private void inicializarValores() {
        // Rutas
        ArrayList<String> listaRutas = cargarRutas();
        rellenarRutas(listaRutas);
        // GPS/NETWORK
        RadioButton rbGPSSi = (RadioButton)findViewById(R.id.rbSi);
        rbGPSSi.setChecked(m_configuracion.isGPS());
        RadioButton rbGPSNo = (RadioButton)findViewById(R.id.rbNo);
        rbGPSNo.setChecked(!m_configuracion.isGPS());
        // GPX/KML
        RadioButton rbGPX = (RadioButton)findViewById(R.id.rbGPX);
        rbGPX.setChecked(m_configuracion.isGPX());
        RadioButton rbKML = (RadioButton)findViewById(R.id.rbKML);
        rbKML.setChecked(!m_configuracion.isGPX());
    }

    private ArrayList<String> cargarRutas() {
        ArrayList<String> rutas = new ArrayList<String>();
        rutas.add(getString(R.string.sinRuta));
        rutas.addAll(Utils.obtenerFicherosCarpeta());
        return rutas;
    }

    private void inicializar() {
        //
        m_spDades = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        try {
            m_configuracion = (Configuracion) Serializar.desSerializar(Utils.getValorSP(m_spDades, Constantes.CTE_CLAVE_CONFIGURACION));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //
        m_vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //
        Button bGuardar = (Button)findViewById(R.id.btGuardar);
        bGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_vibe.vibrate(Constantes.CTE_VIBRATION_MS);
                volverPrincipal(true);
            }
        });        //
        Button bCancelar = (Button)findViewById(R.id.btCancelar);
        bCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_vibe.vibrate(Constantes.CTE_VIBRATION_MS);
                volverPrincipal(false);
            }
        });
    }
    private void rellenarRutas(ArrayList<String> listaRutas) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked, listaRutas);
        adapter.notifyDataSetChanged();
        //
        Spinner spRutas = (Spinner) findViewById(R.id.spRutas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRutas.setAdapter(adapter);
        //
        spRutas.setSelection(adapter.getPosition(m_configuracion.getRuta()));
    }

    private void volverPrincipal(boolean p_isGuardar) {
        Bundle b = new Bundle();
        if (p_isGuardar) {
            Configuracion config = new Configuracion();
            //
            RadioButton rbGPS = (RadioButton)findViewById(R.id.rbSi);
            config.setGPS(rbGPS.isChecked());
            //
            RadioButton rbGPX = (RadioButton)findViewById(R.id.rbGPX);
            config.setGPX(rbGPX.isChecked());
            //
            Spinner spRutas = (Spinner)findViewById(R.id.spRutas);
            config.setRuta(spRutas.getSelectedItem().toString());
            //
            try {
                Utils.putValorSP(m_spDades, Constantes.CTE_CLAVE_CONFIGURACION, Serializar.serializar(config));
            } catch (IOException e) {
                e.printStackTrace();
                //startActivityForResult(new Intent(this, PrincipalActivity.class).putExtras(b), Activity.RESULT_CANCELED);
                Intent i = getIntent();
                setResult(RESULT_CANCELED, i);
                finish();
            }
            //startActivityForResult(new Intent(this, PrincipalActivity.class).putExtras(b), Activity.RESULT_OK);
            Intent i = getIntent();
            setResult(RESULT_OK, i);
            finish();
        }
        //startActivityForResult(new Intent(this, PrincipalActivity.class).putExtras(b), Activity.RESULT_CANCELED);
        Intent i = getIntent();
        setResult(RESULT_CANCELED, i);
        finish();
    }
}
