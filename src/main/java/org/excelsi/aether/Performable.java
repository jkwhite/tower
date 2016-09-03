package org.excelsi.aether;


@FunctionalInterface
public interface Performable extends java.io.Serializable {
    void perform(Context c);
}
