package com.medistation.strategy;

import com.medistation.model.Tratamiento;
import java.util.List;

public interface ValidadorTratamiento {
    
    // retorna true si no hay conflictos medicos
    boolean validar(Tratamiento nuevo, List<Tratamiento> activos);
}