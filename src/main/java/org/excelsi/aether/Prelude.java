package org.excelsi.aether;


public class Prelude implements State {
    private final String _text;


    public Prelude(final String text) {
        _text = text;
    }

    @Override public String getName() {
        return "prelude";
    }

    @Override public void run(final Context c) {
        c.n().poster(_text);
        c.n().pause();
        c.setState(new World());
    }
}
