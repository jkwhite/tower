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


import static org.excelsi.aether.Brain.*;


public class FleeDaemon extends Daemon {
    private boolean _last = false;
    private Chemical _flight;


    public void init(java.util.Map<String,Chemical> chems) {
        _flight = chems.get("flight");
    }

    public String getChemicalSpec() {
        return "flight";
    }

    public void poll(final Context c) {
        if(in.important!=null) {
            if(in.b.threat(in.important)==Threat.kos) {
                //System.err.println(BasicBot.this+" vs "+in.important);
                strength=11;
            }
            else {
                strength=-1;
                _last = false;
            }
        }
        else {
            strength=-1;
        }
    }

    @Override public void perform(final Context c) {
        if(in.important!=null) {
            ((NPC)in.b).withdraw(in.important);
            if(!_last) {
                _last = true;
                //NARRATIVE
                N.narrative().print(in.b, Grammar.start(in.b, "flee")+"!");
                c.n().print(in.b, Grammar.start(in.b, "flee")+"!");
            }
        }
    }

    public Chemical getChemical() {
        return _flight;
    }

    protected final boolean last() {
        return _last;
    }
}
