package org.excelsi.aether;


import groovy.lang.GroovyShell;
import groovy.lang.Binding;
import java.io.InputStreamReader;
import java.net.URL;


public class Script {
    private final URL _script;
    private final GroovyShell _shell;
    private final Binding _binding;


    public Script(final String url) {
        _script = getClass().getClassLoader().getResource(url);
        if(_script==null) {
            throw new IllegalArgumentException("no such resource '"+url+"'");
        }
        _binding = new Binding();
        _shell = new GroovyShell(_binding);
    }

    public Script(final URL script) {
        if(script==null) {
            throw new IllegalArgumentException("null script");
        }
        _script = script;
        _binding = new Binding();
        _shell = new GroovyShell(_binding);
    }

    public void run(final Context ctx) {
        _binding.setVariable("$c", ctx);
        try {
            _shell.evaluate(new InputStreamReader(_script.openStream()));
        }
        catch(Exception e) {
            throw new RuntimeException("failed evaluating '"+_script+"': "+e, e);
        }
    }
}
