package org.excelsi.aether;


import org.excelsi.matrix.Environs;
import org.excelsi.matrix.Matrix;
import org.excelsi.matrix.Typed;


import org.excelsi.aether.EventSource;


public interface Stage extends Typed, Temporal, Environs {
    String getName();
    String getRealm();
    String getDisplayedFloor();
    int getOrdinal();
    Matrix getMatrix();
    EventSource getEventSource();
}
