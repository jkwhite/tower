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


import static org.excelsi.aether.Brain.*;
import org.excelsi.aether.*;


public class HowlDaemon extends Daemon {
    private Chemical _flight;
    private long _last;


    public void init(java.util.Map<String,Chemical> chems) {
        _flight = chems.get("flight");
    }

    public String getChemicalSpec() {
        return "flight";
    }

    @Override public void poll(final Context c) {
        if(in.important!=null&&in.b.threat(in.important)==Threat.kos&&Time.now()>_last+8) {
            //System.err.println(BasicBot.this+" vs "+in.important);
            strength=11;
        }
        else {
            strength=-1;
        }
    }

    @Override public void perform(final Context c) {
        if(in.important!=null) {
            if(Canid.Howl.canActivate(in.b)) {
                _last = Time.now();
                Canid.Howl h = new Canid.Howl();
                h.setBot(in.b);
                h.perform();
            }
        }
    }

    public Chemical getChemical() {
        return _flight;
    }
}
