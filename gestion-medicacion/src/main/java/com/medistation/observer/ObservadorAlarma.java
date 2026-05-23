package com.medistation.observer;

import com.medistation.model.Tratamiento;

public interface ObservadorAlarma {

    void onAlarmaDisparada(Tratamiento tratamiento);
}