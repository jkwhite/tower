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
import java.io.InputStream;


public class GroovyDataSource implements DataSource {
    public Universe populate(Universe u, Data d) {
        Universe.setUniverse(u);
        Binding b = new Binding();
        GroovyShell s = new GroovyShell(b);
        b.setVariable("u", u);
        b.setVariable("d", d);
        try {
            s.evaluate(d.getResource("universe.groovy"));
            //org.ho.yaml.Yaml.dump(u.getBots(), new java.io.File("/tmp/bots.yaml"), true);
        }
        catch(Exception e) {
            throw new Error(e);
        }
        return u;
    }
}
