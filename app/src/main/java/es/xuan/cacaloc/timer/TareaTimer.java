package es.xuan.cacaloc.timer;

import java.util.TimerTask;

import es.xuan.cacaloc.PrincipalActivity;

/**
 * Created by jcamposp on 21/11/2016.
 */

public class TareaTimer extends TimerTask {

    private PrincipalActivity princAct = null;

    public TareaTimer(PrincipalActivity pActivity) {
        princAct = pActivity;
    }

    @Override
    public void run() {
        //
        princAct.actualizarTiempos();
    }
}
