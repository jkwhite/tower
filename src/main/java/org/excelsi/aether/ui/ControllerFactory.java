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
package org.excelsi.aether.ui;


import org.excelsi.aether.*;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import java.lang.reflect.Constructor;


public final class ControllerFactory {
    public static StoppableController createController(NHBot b, NHBotAction a, Node n) throws ClassNotFoundException {
        return create(b, a, n);
    }

    public static StoppableController createController(NHBot b, ProgressiveAction a, Node n) throws ClassNotFoundException {
        return create(b, a, n);
    }

    public static StoppableController createController(NHBot b, InstantaneousAction a, Node n) throws ClassNotFoundException {
        return create(b, a, n);
    }

    public static StoppableController createController(NHBot b, Affliction a, Node n) throws ClassNotFoundException {
        return create(b, a, n);
    }

    private static StoppableController create(NHBot b, Object a, Node n) throws ClassNotFoundException {
        try {
            String cname = a.getClass().getName();
            String unscoped = cname.substring(cname.lastIndexOf('.'));
            if(unscoped.indexOf('$')>=0) {
                unscoped = "."+unscoped.substring(1+unscoped.indexOf('$'));
            }
            String cfact = cname.substring(0, cname.lastIndexOf('.'))+".ui"+unscoped+"ControllerFactory";
            try {
                Class c = Class.forName(cfact);
                UIFactory f = (UIFactory) c.newInstance();
                return f.createController(b, n, a);
            }
            catch(Exception e) {
                // fall-through, use old style
            }
            cname = cname.substring(0, cname.lastIndexOf('.'))+".ui"+unscoped+"Controller";
            StoppableController c = (StoppableController) Thread.currentThread().getContextClassLoader().loadClass(cname).getConstructor(NHBot.class, Object.class, Node.class).newInstance(b, a, n);
            return c;
        }
        catch(ClassNotFoundException e) {
            throw e;
        }
        catch(Exception e) {
            throw new Error(e);
        }
    }
}
