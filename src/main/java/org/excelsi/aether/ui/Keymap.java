package org.excelsi.aether.ui;


public class Keymap {
    private final String _k;


    public Keymap(final String k) {
        switch(k) {
            case "TAB":
                _k = "\t";
                break;
            default:
                _k = k;
        }
    }

    public static String encode(final String k) {
        switch(k) {
            case "\t":
                return "TAB";
            default:
                return k;
        }
    }

    @Override public boolean equals(Object o) {
        return o.getClass()==Keymap.class
            && ((Keymap)o)._k.equals(_k);
    }

    @Override public int hashCode() {
        return _k.hashCode();
    }
}
