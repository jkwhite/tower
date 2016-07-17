package org.excelsi.aether;


import org.excelsi.matrix.*;


public interface Transform {
    void transform(Matrix m, int i, int j);
}
