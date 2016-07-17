/*
    Tower
    Copyright (C) 2007, John K White, All Rights Reserved
*/
/*
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/
package org.excelsi.aether;


import groovy.lang.*;
import java.util.logging.Logger;


public class Evaluate extends DefaultNHBotAction {
    private String _code;


    public Evaluate(String code) {
        _code = code;
    }

    public Evaluate() {
    }

    public String getDescription() {
        return "Evaluates a Groovy expression.";
    }

    public void perform() {
        String text = _code;
        if(text==null) {
            text = N.narrative().reply(getBot(), "What do you want to ask?");
        }
        try {
            Binding b = new Binding();
            GroovyShell s = new GroovyShell(b);
            b.setVariable("p", getBot());
            text = "import org.excelsi.aether.*;\nimport org.excelsi.matrix.*;\nimport org.excelsi.tower.*;\n"+text;
            Object r = s.evaluate(text);
            if(r==null) {
                N.narrative().print(getBot(), "The outlook dissipates into a hidden dimension.");
            }
            else {
                N.narrative().print(getBot(), "Outlook: "+r);
                Logger.global.info(r.toString());
            }
        }
        catch(Throwable t) {
            N.narrative().print(getBot(), "Outlook not so good: "+t.getClass().getName()+": "+t.getMessage());
            t.printStackTrace();
        }
    }
}
