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
import static org.excelsi.aether.Brain.*;
import java.util.Map;


/**
 * A daemon that cowers when enemies are too powerful.
 */
public class DeferenceDaemon extends Daemon {
    private boolean _act = false;
    private boolean _last = false;
    private Chemical _flight;
    private Chemical _nop = new Chemical("nop");


    public void init(Map<String,Chemical> chemicals) {
        _flight = chemicals.get("flight");
    }

    public String getChemicalSpec() {
        return "flight";
    }

    public void poll(final Context c) {
        if(in.important!=null) {
            if(in.b.threat(in.important)==Threat.kos&&calc(in.b)<calc(in.important)/3) {
                if(!_flight.isActive()&&Rand.om.nextBoolean()) {
                    _flight.activate();
                    _act = true;
                }
                else {
                    strength = 12;
                    if(_act&&Rand.om.nextBoolean()) {
                        _flight.deactivate();
                        _act = false;
                    }
                }
            }
            else {
                if(_act) {
                    _flight.deactivate();
                }
                strength = 0;
                _last = false;
            }
        }
    }

    private static int calc(NHBot b) {
        return (b.getModifiedStrength()+b.getModifiedPresence())/2;
    }

    public void perform(final Context c) {
        strength=0;
        if(!_last) {
            _last = true;
            //NARRATIVE
            //N.narrative().print(in.b, Grammar.start(in.b, "cower")+".");
            c.n().print(in.b, Grammar.start(in.b, "cower")+".");
        }
    }

    public Chemical getChemical() {
        return _nop;
    }
}
