package org.excelsi.aether;


public interface State {
    void run(Context c);
    String getName();
}
