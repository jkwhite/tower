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
    //private Filter _pathFilter = _defaultFilter;
    private Filter _pathFilter = new Filter() {
        public boolean accept(MSpace s) {
            if(s!=null) {
                if(s.isOccupied()) {
                    Threat t = threat((NHBot)s.getOccupant());
                    if((getTemperament()==Temperament.hostile&&(t==Threat.kos||t==Threat.none))
                        || (getTemperament()!=Temperament.hostile&&(t==Threat.kos))) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                //if(s instanceof Doorway) {
                    //System.err.println(NPC.this+" would but: "+getForm().hasOpposableThumb());
                //}
                return s.isWalkable() || (s instanceof Doorway&&!((Doorway)s).isLocked()&&getForm().hasOpposableThumb());
                //return s.isWalkable();
            }
            return false;
        }
    };



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

    public void setPathFilter(Filter pathFilter) {
        _pathFilter = pathFilter;
    }

    public Filter getPathFilter() {
        return _pathFilter;
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

    public boolean intercept(Attack a) {
        Runnable r = getAi().react(this, null, a);
        //System.err.println("*** "+this+" INTERCEPTING WITH "+r);
        if(r!=null) {
            r.run();
        }
        return r!=null;
    }

    @Override public void act(final Context c) {
        act();
    }

    private boolean _last = Rand.om.nextBoolean();
    @Override public void act() {
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

        Runnable r = _ai.react(this, important, null);
        if(r!=null) {
            r.run();
        }
    }

    public void approach(Bot b, int max) {
        approach(b, max, true, null);
    }

    public boolean approach(NHSpace space, int max) {
        return approach(space, max, true);
    }

    private boolean occupy(MSpace s, boolean overrun) {
        //if(s instanceof Doorway) {
            //System.err.println(this+" DOOR: thumb="+getForm().hasOpposableThumb());
        //}
        getEnvironment().face(s);
        if(s instanceof Doorway && getForm().hasOpposableThumb()) {
            if(((Doorway)s).isLocked()) {
                return false;
            }
            if(!((Doorway)s).isOpen()) {
                Open o = new Open((Doorway)s);
                o.setBot(this);
                o.perform();
                return true;
            }
        }
        if(overrun||!s.isOccupied()) {
            _move.setBot(this);
            _move.perform();
            return true;
        }
        else {
            return false;
        }
    }

    private Map<MSpace,MSpace[]> _paths = new LinkedHashMap<MSpace,MSpace[]>(10, 0.7f, true) {
        protected boolean removeEldestEntry(Map.Entry<MSpace,MSpace[]> e) {
            return Time.now()%10==0;
        }
    };
    private static MoveAction _move = new Forward();
    public boolean approach(final NHSpace s, int max, boolean overrun) {
        MSpace[] path = _paths.get(s);
        if(path==null) {
            path = getEnvironment().getMSpace().path(s, false, _pathFilter, Math.max(1f, sanity()), getAffinityEvaluator());
            _paths.put(s, path);
            //System.err.println(toString()+": found new path");
        }
        else {
            //System.err.println(toString()+": found cached path");
        }
        //System.err.println("Path to "+s+": "+Arrays.asList(path));
        if(path.length>1&&path.length<=max) {
            MSpace f = null;
            for(int i=path.length-1;i>=0;i--) {
                if(path[i].equals(getEnvironment().getMSpace())) {
                    if(i!=path.length-1) {
                        f = path[i+1];
                    }
                    break;
                }
            }
            //System.err.println("trying to move to: "+f);
            if(f!=null) {
                getEnvironment().face(f);
                return occupy(f, overrun);
            }
            else {
                _paths.remove(s);
            }
        }
        return false;
    }

    protected Affinity getAffinityEvaluator() {
        return null;
    }

    public void withdraw(final NHBot b) {
        getEnvironment().faceAway(b);
        MSpace to = getEnvironment().getMSpace().move(getEnvironment().getFacing());
        if(to!=null&&!to.isOccupied()&&to.isWalkable()) {
            _move.setBot(this);
            if(getEnvironment().getMSpace().isOccupied()) {
                _move.perform();
            }
        }
        else {
            MSpace chose = null;
            float dist = getEnvironment().getMSpace().distance(b.getEnvironment().getMSpace());
            for(MSpace m:getEnvironment().getMSpace().surrounding()) {
                if(m!=null&&m.isWalkable()&&dist<m.distance(b.getEnvironment().getMSpace())) {
                    if(m.isOccupied()&&threat((NHBot)m.getOccupant())==Threat.familiar) {
                        continue;
                    }
                    if(chose==null) {
                        chose = m;
                    }
                    else {
                        if(chose.isOccupied()&&!m.isOccupied()) {
                            chose = m;
                        }
                    }
                }
            }
            if(chose!=null) {
                getEnvironment().face(chose);
                _move.setBot(this);
                _move.perform();
            }
            else {
                N.narrative().print(this, Grammar.start(this, "cower")+".");
            }
        }
    }

    private MSpace[] findPath(final Bot b, final Filter f) {
        MSpace[] path = getEnvironment().getMSpace().path(((MatrixEnvironment)b.getEnvironment()).getMSpace(), false, new Filter() {
                public boolean accept(MSpace s) {
                    return s!=null&&(s.getOccupant()==b||(_pathFilter.accept(s)&&(f==null||f.accept(s))));
                } }, Math.max(1f, sanity()), getAffinityEvaluator());
        return path;
    }

    private MSpace findSpaceInPath(MSpace[] path, MSpace m) {
        MSpace d = null;
        for(int i=path.length-1;i>=0;i--) {
            if(path[i].equals(m)) {
                if(i!=path.length-1) {
                    d = path[i+1];
                }
                break;
            }
        }
        return d;
    }

    public void approach(final Bot b, int max, boolean overrun, final Filter f) {
        MSpace s = ((MatrixEnvironment)b.getEnvironment()).getMSpace();
        MSpace[] path = _paths.get(s);
        if(path==null) {
            path = findPath(b, f);
            /*
            path = getEnvironment().getMSpace().path(((MatrixEnvironment)b.getEnvironment()).getMSpace(), false, new Filter() {
                    public boolean accept(MSpace s) {
                        return s!=null&&(s.getOccupant()==b||(_pathFilter.accept(s)&&(f==null||f.accept(s))));
                    } }, Math.max(1f, sanity()), getAffinityEvaluator());
                    */
            _paths.put(s, path);
            //System.err.println(toString()+" found new path "+path);
        }
        else {
            //System.err.println(toString()+" using cached path "+path);
        }
        if(path.length>1) {
            if(path.length<=max) {
                MSpace d = findSpaceInPath(path, getEnvironment().getMSpace());
                /*for(int i=path.length-1;i>=0;i--) {
                    if(path[i].equals(getEnvironment().getMSpace())) {
                        if(i!=path.length-1) {
                            d = path[i+1];
                        }
                        break;
                    }
                }*/
                if(d==null) {
                    path = findPath(b, f);
                    d = findSpaceInPath(path, getEnvironment().getMSpace());
                }
                //System.err.println("next space: "+d);
                if(d!=null) {
                    getEnvironment().face(d);
                    if(overrun||d.getOccupant()!=b) {
                        if(!getEnvironment().getMSpace().isOccupied()) {
                            Logger.global.severe("bot "+this+" has come unstuck; dead: "+isDead());
                        }
                        else {
                            occupy(d, true);
                            /*
                            _move.setBot(this);
                            _move.perform();
                            */
                        }
                    }
                }
            }
        }
        else {
            //System.err.println("NO LUCK!");
            getEnvironment().face(b);
            MSpace m = getEnvironment().getMSpace().move(getEnvironment().getFacing());
            if(_pathFilter.accept(m)) {
                _move.setBot(this);
                _move.perform();
            }
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
