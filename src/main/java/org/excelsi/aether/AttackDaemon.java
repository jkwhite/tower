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


import java.util.Map;
import static org.excelsi.aether.Brain.*;


public class AttackDaemon extends Daemon {
    private Chemical _fight;
    private Chemical _basic;

    protected NHBot _last = null;


    public void init(Map<String,Chemical> chems) {
        _fight = chems.get("fight");
        _basic = chems.get("basic");
    }

    public String getChemicalSpec() {
        return "fight=flight,basic";
    }

    public void poll(final Context c) {
        if(in.attack==null) {
            if(in.important!=null) {
                _last = in.important;
            }
            if(in.important!=null&&in.b.threat(in.important)==Threat.kos) {
                strength = 3;
                if(in.b.getTemperament()==Temperament.hostile) {
                    strength*=2;
                    _basic.deactivate();
                }
                else if(in.b.getTemperament()==Temperament.friendly||in.b.getTemperament()==Temperament.shy) {
                    strength/=2;
                }
                else if(in.b.getTemperament()==Temperament.hungry&&Hunger.Degree.degreeFor(in.b.getHunger())==Hunger.Degree.satiated) {
                    strength/=5;
                }
            }
            else {
                if(strength>0) {
                    strength--;
                }
            }
            if(strength<1) {
                _basic.activate();
            }
        }
        else {
            strength = -1;
        }
        if(_last==null) {
            strength = -1;
        }
    }

    @Override public void perform(final Context c) {
        if(in.b.threat(_last)==Threat.kos) {
            ((NPC)in.b).approach(_last, 10);
        }
    }

    public Chemical getChemical() {
        return _fight;
    }

    public void setEventSource(EventSource e) {
        e.addNHEnvironmentListener(new NHEnvironmentAdapter() {
            public void attacked(NHBot b, NHBot attacked) {
                if(in.b==null) {
                    return;
                }
                //System.err.println(in.b+" got event: "+b+" attacked "+attacked);
                //System.err.println(in.b+"'s threat vs "+attacked+"= "+in.b.threat(attacked));
                if(in.b.threat(attacked)==Threat.friendly&&in.b.threat(b)!=Threat.kos) {
                    //System.err.println("set kos: "+in.b+" to "+b);
                    in.b.setThreat(b, Threat.kos);
                }
            }
        });
    }
}
