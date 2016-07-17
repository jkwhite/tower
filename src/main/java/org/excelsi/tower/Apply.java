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
package org.excelsi.tower;


import org.excelsi.aether.*;


public class Apply extends ItemAction implements SpaceAction {
    public Apply() {
        super("use", new InstanceofFilter(Useable.class));
    }

    public String getDescription() {
        return "Use a tool.";
    }

    public boolean isPerformable(NHBot b) {
        NHSpace s = b.getEnvironment().getMSpace();
        if(s instanceof Device) {
            return true;
        }
        for(Parasite p:s.getParasites()) {
            if(p instanceof Device) {
                return true;
            }
        }
        return false;
    }

    protected void act() {
        ((Useable)getItem()).use(getBot());
    }

    protected boolean useSpace() {
        if(getBot().getEnvironment()!=null) {
            NHSpace s = getBot().getEnvironment().getMSpace();
            if(s instanceof Device) {
                Device d = (Device) s;
                if(N.narrative().confirm(getBot(), "There is "+Grammar.nonspecific(d.getName())+" here. Use it?")) {
                    d.use(getBot());
                    return true;
                }
            }
            for(Parasite p:s.getParasites()) {
                if(p instanceof Device) {
                    Device d = (Device) p;
                    if(N.narrative().confirm(getBot(), "There is "+Grammar.nonspecific(d.getName())+" here. Use it?")) {
                        d.use(getBot());
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
