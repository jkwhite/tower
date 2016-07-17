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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.excelsi.matrix.*;
import java.util.logging.Logger;


public class NHEnvironment extends MatrixEnvironment implements MatrixListener {
    private Set<NHSpace> _visible = new HashSet<NHSpace>();
    private Set<Bot> _visibleBots = new HashSet<Bot>();
    private Set<NHSpace> _known = new HashSet<NHSpace>();
    private static Mechanics _mechanics;


    public static void setMechanics(Mechanics mechanics) {
        _mechanics = mechanics;
    }

    public static Mechanics getMechanics() {
        return _mechanics;
    }

    public NHEnvironment(NHBot b, MatrixMSpace m) {
        super(b, m);
        unhide();
    }

    public Set<NHSpace> getVisible() {
        return _visible;
    }

    public Set<NHSpace> getKnown() {
        return _known;
    }

    public Set<Bot> getVisibleBots() {
        return _visibleBots;
    }

    public void forward() {
        super.forward();
        //unhide();
    }

    public void backward() {
        super.backward();
        //unhide();
    }

    public NHSpace getMSpace() {
        return (NHSpace) super.getMSpace();
    }

    public NHSpace getLast() {
        return (NHSpace) super.getLast();
    }

    public void setMSpace(MatrixMSpace m) {
        super.setMSpace(m);
        m.getMatrix().addListener(this);
        unhide();
    }

    public void spacesRemoved(Matrix m, MSpace[] spaces, Bot b) {
        for(MSpace s:spaces) {
            _known.remove(s);
            if(s==getMSpace()) {
                m.removeListener(this);
            }
        }
    }

    public void spacesAdded(Matrix m, MSpace[] spaces, Bot b) {
        for(MSpace s:spaces) {
            if(_visible.contains(s)) {
                unhide();
            }
        }
    }

    public void attributeChanged(Matrix m, String attr, Object oldValue, Object newValue) {
    }

    protected void moveMSpace(MatrixMSpace m) {
        super.moveMSpace(m);
        unhide();
    }

    public NHBot getBot() {
        return (NHBot) super.getBot();
    }

    public void move(Direction d) {
        NHSpace s = getMSpace();
        super.move(d);
        if(getBot().isBlind()&&getMSpace()==s) {
            unhide((NHSpace)getMSpace().move(d));
        }
    }

    public void die(Source s) {
        try {
            ((MatrixMSpace)getMSpace()).getMatrix().removeListener(this);
        }
        catch(IllegalArgumentException e) {
            Logger.global.severe(e.getMessage());
        }
        super.die(s);
        for(Item i:getBot().getInventory().getItem()) {
            if((i.getOccurrence()==0||i.isUnique())||(Rand.d100(10)||Rand.d100(i.getOccurrence()))) {
                getBot().getInventory().remove(i);
                if(i.getCount()>1) {
                    i.setCount(1+Rand.om.nextInt(i.getCount()));
                }
                getMSpace().add(i, getBot());
            }
        }
    }

    public void revealAll() {
        MSpace[] spaces = ((MatrixMSpace)getMSpace()).spaces();
        List<MSpace> re = Arrays.asList(spaces);
        for(MSpace r:re) {
            if(r!=null) {
                _known.add((NHSpace)r);
            }
        }
        for(EnvironmentListener e:getListeners()) {
            e.discovered(getBot(), re);
        }
    }

    public void hideAll() {
        _known.clear();
        MSpace[] spaces = ((MatrixMSpace)getMSpace()).spaces();
        List<MSpace> hi = Arrays.asList(spaces);
        for(EnvironmentListener e:getListeners()) {
            e.forgot(getBot(), hi);
        }
        unhide();
    }

    public void ascend(MSpace to) {
        //getGame().ascend();
        setLevel(getLevel()+1, to);
    }

    public void descend(MSpace to) {
        //getGame().descend();
        setLevel(getLevel()-1, to);
    }

    public void setLevel(int level) {
        getGame().setLevel(level);
    }

    public void setLevel(int level, MSpace m) {
        getGame().setLevel(level, m);
    }

    public int getLevel() {
        return getGame().getLevel();
    }

    public Level getFloor(int floor) {
        return getGame().getFloor(floor);
    }

    public Outcome[] project(NHBot defender, Attack a) {
        return project(getMSpace().directionTo(defender.getEnvironment().getMSpace()), a);
    }

    public Outcome[] project(Direction d, Attack a) {
        return project(d, a, null);
    }

    public Outcome[] project(Direction d, Attack a, Filter f) {
        Outcome[] os = _mechanics.resolve(getBot(), d, a, f);
        for(Outcome o:os) {
            if(o.getResult()!=Outcome.Result.intercept) {
                for(EnvironmentListener listener:getListeners()) {
                    if(listener instanceof NHEnvironmentListener) {
                        ((NHEnvironmentListener)listener).attacked(getBot(), o);
                    }
                }
                if(o.getDefender()!=null) {
                    for(EnvironmentListener listener:o.getDefender().getListeners()) {
                        if(listener instanceof NHEnvironmentListener) {
                            ((NHEnvironmentListener)listener).attackedBy(o.getDefender(), getBot());
                        }
                    }
                }
            }
        }
        return os;
    }

    protected void collide(final Bot b) {
        Armament a = (Armament) getBot().getWielded();
        if(a==null) {
            a = getBot().getForm().getNaturalWeapon();
        }
        if(a==null) {
            return;
        }
        final Armament arm = a;
        Outcome[] os = _mechanics.resolve(getBot(), getFacing(), new Attack() {
            public Source getSource() {
                return new Source(getBot());
            }

            public NHBot getAttacker() {
                return getBot();
            }

            public boolean isPhysical() {
                return true;
            }

            public Armament getWeapon() {
                return arm;
            }

            public int getRadius() {
                return 1;
            }

            public boolean affectsAttacker() {
                return false;
            }

            public Type getType() {
                return Type.melee;
            }
        }, null);
        notifyAttack((NHBot) b, os);
    }

    private void notifyAttack(NHBot other, Outcome[] outcomes) {
        for(Outcome outcome:outcomes) {
            // intercept indicates something out of the ordinary
            // happened, so don't notify about attacks
            if(outcome.getResult()!=Outcome.Result.intercept) {
                for(EnvironmentListener listener:getListeners()) {
                    listener.collided(getBot(), other);
                    if(listener instanceof NHEnvironmentListener) {
                        ((NHEnvironmentListener)listener).attacked(getBot(), outcome);
                    }
                }
                for(EnvironmentListener listener:other.getListeners()) {
                    assert listener!=null;
                    listener.collided(other, getBot());
                    if(listener instanceof NHEnvironmentListener) {
                        ((NHEnvironmentListener)listener).attackedBy(other, getBot());
                    }
                }
            }
        }
        for(Outcome o:outcomes) {
            if(o.getResult()!=Outcome.Result.intercept) {
                for(EnvironmentListener listener:getListeners()) {
                    if(listener instanceof NHEnvironmentListener) {
                        ((NHEnvironmentListener)listener).attacked(getBot(), other);
                    }
                }
            }
        }
    }

    private List<MSpace> _tempVis = new ArrayList<MSpace>();
    public void unhide() {
        unhide(null);
    }

    public void unhide(NHSpace touch) {
        NHSpace s = (NHSpace) getSpace();
        Set<Bot> visibleBots = new HashSet<Bot>();
        List<MSpace> discovered;
        if(getBot().isBlind()) {
            visibleBots.add(getBot());
            _known.add((NHSpace)getSpace());
            if(touch!=null) {
                _known.add(touch);
                discovered = Arrays.asList(new MSpace[]{getSpace(), touch});
            }
            else {
                discovered = Arrays.asList(new MSpace[]{getSpace()});
            }
        }
        else {
            float max = getBot().getModifiedVision()*(((Level)((MatrixMSpace)getMSpace()).getMatrix()).getLight());
            max += getBot().getCandela()/1f;
            max += getBot().getModifiedNightvision();
            discovered = s.visible(visibleBots, _visible, _known, Math.max(1, (int) Math.ceil(max)));
        }
        switch(getBot().getModifiedConnected()) {
            case full:
                MatrixMSpace m = (MatrixMSpace) getMSpace();
                List<NHBot> pos = ((Level)m.getMatrix()).bots();
                for(int i=0;i<pos.size();i++) {
                    NHBot p = pos.get(i);
                    if(true||!p.isInvisible()) {
                        visibleBots.add(p);
                    }
                }
                //visibleBots.addAll(((Level)m.getMatrix()).bots());
                break;
            case strong:
                m = (MatrixMSpace) getMSpace();
                if(getBot().isBlind()) {
                    //visibleBots.addAll(((Level)m.getMatrix()).bots());
                    List<NHBot> ps = ((Level)m.getMatrix()).bots();
                    for(int i=0;i<ps.size();i++) {
                        NHBot p = ps.get(i);
                        if(true||!p.isInvisible()) {
                            visibleBots.add(p);
                        }
                    }
                }
                else {
                    List<NHBot> bots = ((Level)m.getMatrix()).bots();
                    do {
                        NHBot p = bots.get(Rand.om.nextInt(bots.size()));
                        if(true||!p.isInvisible()) {
                            visibleBots.add(p);
                        }
                    } while(Rand.d100(15));
                }
                break;
            case weak:
                m = (MatrixMSpace) getMSpace();
                if(getBot().isBlind()) {
                    List<NHBot> bots = ((Level)m.getMatrix()).bots();
                    do {
                        NHBot p = bots.get(Rand.om.nextInt(bots.size()));
                        if(true||!p.isInvisible()) {
                            visibleBots.add(p);
                        }
                    } while(Rand.d100(15));
                }
                break;
            case none:
                break;
            case negative:
                m = (MatrixMSpace) getMSpace();
                //NHBot[] all = visibleBots.toArray(new NHBot[visibleBots.size()]);
                List<NHBot> all = ((Level)m.getMatrix()).bots();
                do {
                    NHBot rem = all.get((Rand.om.nextInt(all.size())));
                    if(rem!=getBot()) {
                        visibleBots.remove(rem);
                    }
                } while(Rand.d100(85));
                break;
        }
        for(Bot b:((Level)((MatrixMSpace)getBot().getEnvironment().getMSpace()).getMatrix()).bots()) {
            if(((NHBot)b).isAudible()) {
                visibleBots.add(b);
            }
        }
        List noticedBots = new ArrayList();
        List missedBots = new ArrayList();
        for(Iterator i=visibleBots.iterator();i.hasNext();) {
            NHBot b = (NHBot) i.next();
            if(!_visibleBots.contains(b)) {
                noticedBots.add(b);
            }
        }
        for(Iterator i=_visibleBots.iterator();i.hasNext();) {
            NHBot b = (NHBot) i.next();
            if(!visibleBots.contains(b)) {
                missedBots.add(b);
            }
        }
        _visibleBots = visibleBots;
        if(getBot().isPlayer()) {
            // optimize - only do this for player
            //_known.addAll(uh);
            // optimization hack - technically we should send these events but
            // they're mostly irrelevant for non-human players since the ui
            // is the listener here and the ui is from the player's POV.
            _tempVis.clear();
            _tempVis.addAll(_visible);
            for(Iterator i=getListeners().iterator();i.hasNext();) {
                EnvironmentListener e = (EnvironmentListener) i.next();
                //e.unhid(getBot(), uh);
                e.discovered(getBot(), discovered);
                e.seen(getBot(), _tempVis);
                e.noticed(getBot(), noticedBots);
                e.missed(getBot(), missedBots);
            }
        }
    }

    public NHBot getPlayer() {
        return getGame().getPlayer();
    }

    private Game getGame() {
        return Universe.getUniverse().getGame();
    }
}
