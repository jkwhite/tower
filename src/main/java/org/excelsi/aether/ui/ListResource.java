package org.excelsi.aether.ui;


import java.util.ListResourceBundle;


public class ListResource extends ListResourceBundle {
    private final Object[][] _resources;


    public ListResource(final Object[][] resources) {
        _resources = resources;
    }

    protected Object[][] getContents() {
        return _resources;
    }
}
