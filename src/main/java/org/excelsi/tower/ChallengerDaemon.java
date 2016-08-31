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
import org.excelsi.matrix.*;
import static org.excelsi.aether.Brain.*;


/**
 * A daemon that confronts lesser pack members over food.
 */
public class ChallengerDaemon extends Daemon {
    private EventSource _e;
    private NHBot _c;
    private Chemical _hunger;


    public ChallengerDaemon() {
    }

    public void init(java.util.Map<String,Chemical> chems) {
        _hunger = chems.get("hunger");
    }

    public String getChemicalSpec() {
        return "hunger";
    }

    public void setEventSource(EventSource e) {
        _e = e;
        _e.addNHEnvironmentListener(new NHEnvironmentAdapter() {
            public void died(Bot b) {
                _e.removeNHEnvironmentListener(this);
            }

            public void actionStarted(NHBot b, ProgressiveAction action) {
                if(in.b!=null&&in.b!=b&&action.getClass()==Consume.Consuming.class
                    &&b.getStrength()<=in.b.getStrength()) {
                    if(in.b.threat(b)!=Threat.kos) {
                        N.narrative().print(in.b, Grammar.start(in.b, "snarl")+" at "+Grammar.noun(b)+".");
                        in.b.setThreat(b, Threat.kos);
                        _c = b;
                        strength = 10;
                    }
                }
            }
        });
    }

    public void poll() {
        if(strength>-1) {
            strength--;
        }
    }

    @Override public void run(final Context c) {
        ((NPC)in.b).approach(_c, 10);
    }

    public Chemical getChemical() {
        return _hunger;
    }
}
