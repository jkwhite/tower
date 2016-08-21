package org.excelsi.aether;


import org.excelsi.matrix.Matrix;
import org.excelsi.matrix.Typed;


import org.excelsi.aether.EventSource;


public interface Stage extends Typed, Temporal {
    String getName();
    int getOrdinal();
    Matrix getMatrix();
    EventSource getEventSource();
}
