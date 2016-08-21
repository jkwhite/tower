package org.excelsi.matrix;


public interface Environs {
    String findString(String name, String dvalue);
    float findFloat(String name, float dvalue);
    Environs putProperty(String name, Object value);
    //Bot[] inhabitants();
}
