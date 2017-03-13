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


import java.util.Iterator;
import java.util.Random;

import org.excelsi.matrix.*;
import java.util.EnumSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Arrays;

import static org.excelsi.aether.Brain.Chemical;
import static org.excelsi.aether.Brain.Daemon;
import java.util.logging.Logger;


/**
 * Represents non-player characters.
 */
public class NPC extends DefaultNHBot {
    private static final long serialVersionUID = 1L;
    private static Filter _defaultFilter;
    static {
        _defaultFilter = new Filter() {
            public boolean accept(MSpace s) {
                return s!=null&&s.isWalkable()&&(!s.isOccupied());
            }
        };
    }

    private Brain _ai = new Brain();
    private NHBot _familiar;


    public NPC() {
    }

    public float sanity() {
        return _familiar!=null?1f:super.sanity();
    }

    public final void setEventSource(EventSource events) {
        _ai.setEventSource(events);
    }

    public final void clearEventSource() {
        _ai = null;
    }

    public void setCommon(String common) {
        // TODO: figure out diff between common/profession
        super.setCommon(common);
        setProfession(common);
    }

    public void setFamiliar(NHBot b) {
        _familiar = b;
        FamiliarDaemon f = (FamiliarDaemon) _ai.daemon(FamiliarDaemon.class);
        if(f==null) {
            f = new FamiliarDaemon();
            _ai.addDaemon(f);
        }
        setThreat(b, Threat.familiar);
        b.setThreat(this, Threat.familiar);
        f.setFamiliar(b);
        if(b instanceof Patsy) {
            Patsy p = (Patsy) b;
            for(NHBot o:p.getFamiliars()) {
                setThreat(o, Threat.familiar);
                o.setThreat(this, Threat.familiar);
            }
            p.addFamiliar(this);
        }
    }

    public NHBot getFamiliar() {
        return _familiar;
    }

    public void polymorph(NHBot to) {
        // polymorphing resets the brain, which contains
        // familiar info. remind bot about familiar
        // after polymorphing.
        super.polymorph(to);
        if(_familiar!=null) {
            setFamiliar(_familiar);
        }
    }

    public boolean intercept(final Context c, final Attack a) {
        Performable r = getAi().react(c, this, null, a);
        //System.err.println("*** "+this+" INTERCEPTING WITH "+r);
        if(r!=null) {
            r.perform(c);
        }
        return r!=null;
    }

    private boolean _last = Rand.om.nextBoolean();
    @Override public void act() {
        throw new IllegalStateException("deprecated act");
    }

    @Override public void act(final Context c) {
        if(isOccupied()) {
            return;
        }
        if(isDead()) {
            Logger.global.severe(this+" is dead; cannot act");
            return;
        }
        //OPT
        if(!_last) {
            ((NHEnvironment)getEnvironment()).unhide();
        }
        _last = !_last;
        NHBot[] bots = (NHBot[]) getEnvironment().getVisibleBots().toArray(new NHBot[0]);
        NHBot important = null;
        if(bots.length>0) {
            important = bots[0];
            int max = threat(important).indicator();
            for(NHBot t:bots) {
                int threat = threat(t).indicator();
                if(important==this||(threat>=max||(threat==max&&t.getStrength()>important.getStrength()))) {
                    if(t!=this) {
                        important = t;
                        max = threat;
                    }
                }
            }
        }
        if(important==this) {
            important = null;
        }

        Performable r = _ai.react(c, this, important, null);
        if(r!=null) {
            r.perform(c);
        }
    }

    public void act(Class action) {
        try {
            NHBotAction a = (NHBotAction) action.newInstance();
            a.setBot(this);
            a.perform();
        }
        catch(IllegalAccessException e) {
            throw new Error(e);
        }
        catch(InstantiationException e) {
            throw new Error(e);
        }
    }

    public void setAi(Brain b) {
        //_ai = b.clone();
        _ai = b;
    }

    public Brain getAi() {
        return _ai;
    }
}
