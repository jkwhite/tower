package org.excelsi.aether;


public final class DisplayHints {
    public static final DisplayHints NONE = new DisplayHints();
    public static final DisplayHints MODAL = new DisplayHints().modal();
    public static final DisplayHints KEYED = new DisplayHints().keyed();

    private boolean _keyed;
    private boolean _modal;


    public static DisplayHints n() {
        return new DisplayHints();
    }

    public DisplayHints keyed() {
        _keyed = true;
        return this;
    }

    public DisplayHints modal() {
        _modal = true;
        return this;
    }

    public boolean isKeyed() {
        return _keyed;
    }

    public boolean isModal() {
        return _modal;
    }

    public DisplayHints() {}
}
