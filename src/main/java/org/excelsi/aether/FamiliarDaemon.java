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


import org.excelsi.matrix.*;
import static org.excelsi.aether.Brain.*;
import java.util.List;
import java.util.Arrays;


public class FamiliarDaemon extends Daemon {
    private Chemical _basic;
    private NHBot _f;
    private NHEnvironmentAdapter _l;
    private MSpace[] _path;
    private static DefaultNHBot.MoveAction _move = new DefaultNHBot.Forward();


    public FamiliarDaemon() {
    }

    public void setFamiliar(NHBot f) {
        if(_f!=null) {
            _f.removeListener(_l);
        }
        _f = f;
        _l = new NHEnvironmentAdapter() {
            public void attackedBy(NHBot b, NHBot attacker) {
                if(in.b!=null) {
                    in.b.setThreat(attacker, Threat.kos);
                }
            }

            public void attacked(NHBot b, NHBot attacked) {
                if(in.b!=null&&!in.b.isDead()) {
                    in.b.setThreat(attacked, Threat.kos);
                }
            }
        };
        _f.addListener(_l);
    }

    public NHBot getFamiliar() {
        return _f;
    }

    public void init(java.util.Map<String,Chemical> chems) {
        _basic = chems.get("basic");
    }

    private EventSource _e;
    public void setEventSource(EventSource e) {
        _e = e;
        _e.addNHEnvironmentListener(new NHEnvironmentAdapter() {
            public void died(Bot b) {
                _e.removeNHEnvironmentListener(this);
            }

            public void attacked(NHBot b, NHBot attacked) {
                //battle();
            }

            public void attackedBy(NHBot b, NHBot attacker) {
                battle();
            }

            private void battle() {
                if(in.b!=null&&_f!=null&&!in.b.isDead()) {
                    if(!_f.getEnvironment().getVisibleBots().contains(in.b)) {
                        if(Rand.d100(33)) { // simple way to not overwhelm narrative
                            N.narrative().print(_f, Grammar.start(in.b, in.b.getForm().getShout())+"!");
                        }
                    }
                }
            }
        });
    }

    public String getChemicalSpec() {
        return "basic";
    }

    public void poll() {
        _path = null;
        if(in.attack==null) {
            float dist = in.b.getEnvironment().getMSpace().distance(_f.getEnvironment().getMSpace());
            if(dist>=5f) {
                strength = 6;
            }
            else {
                strength = Rand.om.nextInt(3);
            }
        }
        else {
            strength = -1;
        }
    }

    public void run() {
        ((NPC)in.b).approach(_f, Integer.MAX_VALUE, false, null);
    }

    public Chemical getChemical() {
        return _basic;
    }
}
