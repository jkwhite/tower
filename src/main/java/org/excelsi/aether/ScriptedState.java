package org.excelsi.aether;


public class ScriptedState implements State {
    private final String _name;
    private final Script _script;


    public ScriptedState(final String name, final Script s) {
        _name = name;
        _script = s;
    }

    @Override public String getName() {
        return _name;
    }

    @Override public void run(final Context c) {
        _script.run(c);
    }
}
