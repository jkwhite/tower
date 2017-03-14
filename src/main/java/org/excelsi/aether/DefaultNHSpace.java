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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.excelsi.matrix.*;
import java.util.TreeSet;
import java.util.Collections;
import java.util.Vector;


public abstract class DefaultNHSpace extends MatrixMSpace implements NHSpace, ContainerListener, Createable {
    private static final long serialVersionUID = 1L;
    private static final boolean OLD_NARRATIVE = Boolean.getBoolean("narrative");
    private Inventory _loot;
    private List<Parasite> _parasites;
    private List<ContainerListener> _listeners;
    private Overlay _overlay;
    private String _color;
    private int _alt;
    private Status _s = Status.uncursed;
    //private int _depth;


    public DefaultNHSpace(String color) {
        _color = color;
    }

    public String getName() {
        return getClass().getName().substring(getClass().getName().lastIndexOf('.')).toLowerCase();
    }

    public float getShininess() {
        return 10f;
    }

    public Item[] getIngredients() {
        return null;
    }

    public boolean accept(NHSpace s) {
        return true;
    }

    public String getCreationSkill() {
        return "construction";
    }

    public Maneuver getDifficulty() {
        return Maneuver.medium;
    }

    public MatrixMSpace union(MatrixMSpace m) {
        if(m instanceof Blank) {
            return this;
        }
        else {
            return super.union(m);
        }
    }

    public boolean isSpecial() {
        return false;
    }

    public void setOverlay(Overlay overlay) {
        if(_overlay!=null) {
            for(MSpaceListener m:getMSpaceListeners()) {
                if(m instanceof NHSpaceListener) {
                    ((NHSpaceListener)m).overlayRemoved(this, _overlay);
                }
            }
        }
        _overlay = overlay;
        if(overlay!=null) {
            for(MSpaceListener m:getMSpaceListeners()) {
                if(m instanceof NHSpaceListener) {
                    ((NHSpaceListener)m).overlayAdded(this, overlay);
                }
            }
        }
    }

    public Overlay getOverlay() {
        return _overlay;
    }

    public void moveOverlay(NHSpace other) {
        if(_overlay==null) {
            throw new IllegalStateException("no overlay on "+this);
        }
        Overlay o = _overlay;
        setOverlay(null);
        other.setOverlay(o);
    }

    public void removeParasite(Parasite p) {
        if(_parasites==null||!_parasites.remove(p)) {
            throw new IllegalStateException("parasite '"+p+"' not attached to '"+this+"'");
        }
        for(MSpaceListener l:getMSpaceListeners()) {
            if(l instanceof NHSpaceListener) {
                ((NHSpaceListener)l).parasiteRemoved(this, p);
            }
        }
    }

    public void addParasite(Parasite p) {
        if(_parasites==null) {
            _parasites = new Vector<Parasite>();
        }
        if(_parasites.contains(p)) {
            throw new IllegalStateException("parasite '"+p+"' already attached to '"+this+"'");
        }
        _parasites.add(p);
        p.setSpace(this);
        for(MSpaceListener l:getMSpaceListeners()) {
            if(l instanceof NHSpaceListener) {
                ((NHSpaceListener)l).parasiteAdded(this, p);
            }
        }
        trigger();
    }

    public List<Parasite> getParasites() {
        if(_parasites==null) {
            return new ArrayList<Parasite>(0);
        }
        else {
            synchronized(_parasites) {
                return new ArrayList<Parasite>(_parasites);
            }
        }
    }

    public boolean hasParasite(Class parasite) {
        if(_parasites==null) {
            return false;
        }
        for(Parasite p:_parasites) {
            if(parasite.isAssignableFrom(p.getClass())) {
                return true;
            }
        }
        return false;
    }

    public void moveParasite(Parasite p, NHSpace s) {
        if(!_parasites.remove(p)) {
            throw new IllegalArgumentException("parasite '"+p+"' is not on "+this);
        }
        DefaultNHSpace o = (DefaultNHSpace) s;
        if(o._parasites==null) {
            o._parasites = new ArrayList<Parasite>(1);
        }
        o._parasites.add(p);
        for(MSpaceListener l:getMSpaceListeners()) {
            if(l instanceof NHSpaceListener) {
                ((NHSpaceListener)l).parasiteMoved(this, s, p);
            }
        }
    }

    public void addContainerListener(ContainerListener listener) {
        if(_listeners==null) {
            _listeners = new ArrayList<ContainerListener>();
        }
        if(!_listeners.contains(listener)) {
            _listeners.add(listener);
        }
    }

    public void removeContainerListener(ContainerListener listener) {
        if(_listeners==null||!_listeners.remove(listener)) {
            throw new IllegalStateException("listeners '"+listener+"' not listening to '"+this+"'");
        }
    }

    public void setAltitude(int alt) {
        if(alt!=_alt) {
            int oa = _alt;
            _alt = alt;
            notifyAttr("altitude", oa, _alt);
        }
    }

    public int getAltitude() {
        return _alt;
    }

    public void setStatus(Status s) {
        if(s!=_s) {
            Status os = _s;
            _s = s;
            notifyAttr("status", os, _s);
        }
    }

    public Status getStatus() {
        return _s;
    }

    public int getDepth() {
        return 0;
    }

    public int getHeight() {
        return 0;
    }

    public int getModifiedDepth() {
        int d = getDepth();
        if(_parasites!=null) {
            for(Parasite p:_parasites) {
                d -= p.getHeight();
            }
        }
        //return d-getAltitude();
        return d;
    }

    public int getOccupantDepth() {
        NHBot b = getOccupant();
        if(b==null) {
            throw new IllegalStateException("no occupant");
        }
        int d = getModifiedDepth()-numItems();
        if(b.isLevitating()||b.isAirborn()) {
            int o = -6;
            d = Math.min(o, d-1);
        }
        return d;
    }

    public NHSpace replace(MSpace replacement) {
        if(!isReplaceable()) {
            throw new IllegalStateException("space '"+this+"' is not replaceable");
        }
        NHSpace rep = (NHSpace) super.replace(replacement);
        if(rep!=null) {
            rep.setAltitude(getAltitude());
        }
        if(_loot!=null&&rep!=null) {
            for(Item i:_loot.getItem()) {
                rep.add(i);
            }
        }
        return rep;
    }

    public void setOccupant(Bot occupant) {
        super.setOccupant(occupant);
        trigger();
    }

    public NHBot getOccupant() {
        return (NHBot) super.getOccupant();
    }

    public boolean isDestroyable() {
        return false;
    }

    public void destroy() {
    }

    //private boolean look(NHBot b) {
        //return look(b, true);
    //}

    /**
     * Looks at this space.
     *
     * @param b bot who is looking
     * @param nothing whether or not to print a message even if nothing is here
     */
    //private boolean look(NHBot b, boolean nothing) {
        //return look(b, nothing, false);
    //}

    /**
     * Looks at this space.
     *
     * @param b bot who is looking
     * @param nothing whether or not to print a message even if nothing is here
     * @param lootOnly whether or not to ignore structures, parasites, etc
     */
    @Override public boolean look(final Context c, boolean nothing, boolean lootOnly) {
        boolean saw = false;
        final NHBot b = c.getActor();
        if(!lootOnly) {
            if(_parasites!=null) {
                for(Parasite p:_parasites) {
                    saw = p.notice(b) || saw;
                }
            }
            if(getOccupant()!=null&&!getOccupant().isPlayer()) {
                saw = true;
                if(getOccupant().isUnique()||getOccupant().getName()!=null) {
                    //N.narrative().print(this, "You see "+Grammar.nonspecific(getOccupant())+" here.");
                    c.n().print(this, "You see "+Grammar.nonspecific(getOccupant())+" here.");
                }
                else {
                    //N.narrative().print(this, "There is "+Grammar.nonspecific(getOccupant())+" here.");
                    c.n().print(this, "There is "+Grammar.nonspecific(getOccupant())+" here.");
                }
                //if(numItems()>0) {
                    //N.narrative().more();
                //}
            }
        }
        Inventory in = getLoot();
        int size = in==null?0:in.size();
        if(size==0) {
            if(!saw&&nothing) {
                if(OLD_NARRATIVE) {
                    //N.narrative().print(this, "You see nothing here.");
                    c.n().print(this, "You see nothing here.");
                }
            }
            c.n().show(b, this);
        }
        else {
            saw = true;
            NHSpace s = this;
            //boolean isHere = b==s.getOccupant();
            // can see current space and all surrounding spaces
            boolean isHere = b.getEnvironment().getMSpace().distance(s)<3f;
            StringBuilder sb = new StringBuilder();
            if(OLD_NARRATIVE) {
                if(size==1) {
                    if(firstItem().getCount()==1||firstItem().isAlwaysSingular()) {
                        sb.append("There is ");
                    }
                    else {
                        sb.append("There are ");
                    }
                }
                else {
                    sb.append("You see ");
                }
                Item[] all = getItem();
                if(b.isPlayer()) {
                    for(Item it:all) {
                        ((Patsy)b).analyze(it);
                    }
                }

                for(int i=0;i<all.length;i++) {
                    if(all.length>1 && i==all.length-1) {
                        if(all.length<=2) {
                            sb.append(" ");
                        }
                        sb.append("and ");
                    }
                    if(isHere&&!b.isBlind()) {
                        sb.append(Grammar.nonspecific(all[i]));
                    }
                    else {
                        sb.append(Grammar.nonspecificObscure(all[i]));
                    }
                    if(all.length>2 && i<all.length-1) {
                        sb.append(", ");
                    }
                }
                sb.append(" here.");
            }
            if(OLD_NARRATIVE&&sb.length()<80) {
                //N.narrative().print(this, sb.toString());
                c.n().print(this, sb.toString());
            }
            else {
                //N.narrative().showLoot(s);
                //NEXT
                c.n().show(b, s);
            }
        }
        return saw;
    }

    protected MatrixEnvironment createEnvironment(Bot b) {
        return new NHEnvironment((NHBot)b, this);
    }

    protected void trigger() {
        if(getOccupant()!=null&&_parasites!=null) {
            for(Parasite p:new ArrayList<Parasite>(_parasites)) {
                p.trigger(getOccupant());
            }
        }
    }

    protected boolean canLeave(MSpace to) {
        if(_parasites==null) {
            return super.canLeave(to);
        }
        for(int i=0;i<_parasites.size();i++) {
            if(!_parasites.get(i).canLeave(to)) {
                return false;
            }
        }
        return true;
    }

    public Inventory getLoot() {
        return _loot;
    }

    public void setLoot(Inventory loot) {
        if(loot!=null) {
            if(_loot!=null) {
                _loot.removeContainerListener(this);
            }
            _loot = loot;
            _loot.addContainerListener(this);
        }
    }

    public boolean isAutopickup() {
        return false;
    }

    public boolean pickup(NHBot b) {
        return false;
    }

    static class Bloom implements Comparable {
        double d;
        int i;
        int j;

        public Bloom(double d, int i, int j) {
            this.d = d; this.i = i; this.j = j;
        }

        public int compareTo(Object o) {
            return (int) (d - ((Bloom)o).d);
        }

        public String toString() {
            return "["+d+","+i+","+j+"]";
        }
    }

    public void bloom(Transform c, int radius) {
        ArrayList<Bloom> sorted = new ArrayList<Bloom>();
        for(int i=getI()-radius;i<=getI()+radius;i++) {
            for(int j=getJ()-radius;j<=getJ()+radius;j++) {
                double d = Math.hypot(getI()-i, getJ()-j);
                if(d<=radius) {
                    sorted.add(new Bloom(d, i, j));
                }
            }
        }
        Collections.sort(sorted);
        for(Bloom b:sorted) {
            try {
                MSpace old = getMatrix().getSpace(b.i, b.j);
                if(old==null) {
                    c.transform(getMatrix(), b.i, b.j);
                    //getMatrix().setSpace((MatrixMSpace)c.transform(null), b.i, b.j);
                }
                else {
                    c.transform(getMatrix(), b.i, b.j);
                    //old.replace(c.transform(old));
                }
            }
            catch(Throwable t) {
                t.printStackTrace();
                Logger.global.severe(t.toString());
            }
        }
    }

    public Item[] getItem() {
        if(_loot == null) {
            return new Item[0];
        }
        return _loot.getItem();
    }

    public Item firstItem() {
        if(_loot == null) {
            throw new IllegalStateException("no loot");
        }
        return _loot.firstItem();
    }

    public int numItems() {
        return _loot==null?0:_loot.numItems();
    }

    public int add(Item item) {
        if(_loot==null) {
            _loot = new Inventory();
            //_loot.setKeyed(true);
            _loot.addContainerListener(this);
        }
        return _loot.add(item);
    }

    public int add(Item item, NHBot adder, NHSpace origin) {
        if(_loot==null) {
            _loot = new Inventory();
            //_loot.setKeyed(true);
            _loot.addContainerListener(this);
        }
        return _loot.add(item, adder, origin);
    }

    public int add(Item item, NHBot adder) {
        if(_loot==null) {
            _loot = new Inventory();
            //_loot.setKeyed(true);
            _loot.addContainerListener(this);
        }
        return _loot.add(item, adder);
    }

    public boolean contains(Item item) {
        return _loot!=null?_loot.contains(item):false;
    }

    public int remove(Item item) {
        if(_loot != null) {
            int idx = _loot.remove(item);
            if(_loot.numItems()==0) {
                _loot.removeContainerListener(this);
                _loot = null;
            }
            return idx;
        }
        else {
            throw new IllegalStateException("item '"+item+"' is not in inventory "+this);
        }
    }

    public int consume(Item item) {
        if(_loot!=null) {
            int idx = _loot.consume(item);
            if(_loot.numItems()==0) {
                _loot.removeContainerListener(this);
                _loot = null;
            }
            return idx;
        }
        else {
            throw new IllegalStateException("item '"+item+"' is not in inventory "+this);
        }
    }

    public boolean destroy(Item item) {
        if(_loot != null) {
            boolean ret = _loot.destroy(item);
            if(_loot.size()==0) {
                _loot.removeContainerListener(this);
                _loot = null;
            }
            return ret;
        }
        else {
            throw new IllegalArgumentException("item '"+item+"' is not in inventory"+this);
        }
    }

    public void destroyAll() {
        if(_loot!=null) {
            _loot.destroyAll();
            _loot = null;
        }
    }

    public void transfer(Item item, Container destination) {
        if(_loot!=null) {
            _loot.transfer(item, destination);
        }
        else {
            throw new IllegalArgumentException("item '"+item+"' is not in inventory"+this);
        }
    }

    public Item split(Item item) {
        if(_loot!=null) {
            return _loot.split(item);
        }
        else {
            throw new IllegalArgumentException("item '"+item+"' is not in inventory"+this);
        }
    }

    public void destroyLoot() {
        destroyAll();
    }

    public void addLoot(Container loot) {
        if(loot!=null&&loot.numItems()>0) {
            if(_loot == null) {
                _loot = new Inventory();
                //_loot.setKeyed(true);
                _loot.addContainerListener(this);
            }
            _loot.add(loot);
        }
    }

    public List<MSpace> visible(Set bots, Set<NHSpace> spaces, Set<NHSpace> knowns, float max) {
        HashSet temp = new HashSet();
        NHSpace s = this;
        List<MSpace> discovered = new ArrayList<MSpace>();
        spaces.clear();
        bots.clear();
        if(isOccupied()) {
            bots.add(getOccupant());
        }
        buildVisibles(s, knowns, discovered, bots, temp, spaces, s, max);
        //unhid.add(this);
        spaces.add(this);
        if(!knowns.contains(this)) {
            knowns.add(this);
            discovered.add(this);
        }
        //_temp.clear();
        return discovered;
        //MSpace[] uh = (MSpace[]) unhid.toArray(new MSpace[unhid.size()]);
        //return uh;
    }

    //private void buildVisibles(NHSpace s, List unhid, Set visibleBots, Set visited, Set<NHSpace> visible, NHSpace orig) {
    private void buildVisibles(NHSpace s, Set<NHSpace> knowns, List<MSpace> discovered, Set visibleBots, Set visited, Set<NHSpace> visible, NHSpace orig, float max) {
        MSpace[] sur = s.surrounding();
        for(int i=0;i<sur.length;i++) {
            if(sur[i] != null && !sur[i].isNull()) {
                NHSpace ns = (NHSpace) sur[i];
                if(!visited.contains(ns)) {
                    visited.add(ns);
                    if(ns.visibleFrom(orig, max)) {
                        visible.add(ns);
                        //unhid.add(ns);
                        if(!knowns.contains(ns)) {
                            discovered.add(ns);
                            knowns.add(ns);
                        }
                        if(ns.isOccupied()&&!((NHBot)ns.getOccupant()).isInvisible()) {
                            visibleBots.add(ns.getOccupant());
                        }
                        if(s.isWalkable()||s.isTransparent()) {
                            if(ns.isWalkable()) {
                                for(MSpace check:ns.surrounding()) {
                                    if(check != null && check.isOccupied() && !((NHBot)check.getOccupant()).isInvisible()) {
                                        visibleBots.add(check.getOccupant());
                                    }
                                }
                            }
                            if(ns.isTransparent()) {
                                buildVisibles(ns, knowns, discovered, visibleBots, visited, visible, orig, max);
                            }
                        }
                        /*
                        if((s.isWalkable()||s.isTransparent())&&ns.isTransparent()) {
                            buildVisibles(ns, unhid, visibleBots, visited, visible, orig);
                        }
                        */
                    }
                }
            }
        }
    }

    public void itemDropped(Container container, Item item, int idx, boolean incremented) {
        for(ContainerListener l:getListeners()) {
            l.itemDropped(this, item, idx, incremented);
        }
    }

    public void itemAdded(Container container, Item item, int idx, boolean incremented) {
        for(ContainerListener l:getListeners()) {
            l.itemAdded(this, item, idx, incremented);
        }
    }

    public void itemAdded(Container container, Item item, int idx, boolean incremented, NHBot adder, NHSpace origin) {
        for(ContainerListener l:getListeners()) {
            l.itemAdded(this, item, idx, incremented, adder, origin);
        }
    }

    public void itemTaken(Container container, Item item, int idx) {
        for(ContainerListener l:getListeners()) {
            l.itemTaken(this, item, idx);
        }
    }

    public void itemDestroyed(Container container, Item item, int idx) {
        for(ContainerListener l:getListeners()) {
            l.itemDestroyed(this, item, idx);
        }
    }

    public void itemsDestroyed(Container container, Item[] items) {
        for(ContainerListener l:getListeners()) {
            l.itemsDestroyed(this, items);
        }
    }

    public void setColor(String color) {
        String oc = _color;
        _color = color;
        notifyAttr("color", oc, _color);
    }

    public String getColor() {
        return _color;
    }

    @Override public Architecture getArchitecture() {
        return Architecture.structural;
    }

    @Override public Orientation getOrientation() {
        return Orientation.natural;
    }

    @Override public Origin getOrigin() {
        return Origin.artificial;
    }

    public void update() {
        if(_parasites!=null) {
            List<Parasite> pars = getParasites();
            for(int i=0;i<pars.size();i++) {
                pars.get(i).update();
            }
        }
        if(_loot!=null&&_loot.numItems()>0) {
            Item[] its = _loot.getItem();
            for(int i=0;i<its.length;i++) {
                its[i].update(this);
            }
        }
    }

    protected MatrixMSpace createNullSpace(Matrix m, int i, int j) {
        return new NullNHSpace(m, i, j);
    }

    protected void notifyAttr(String attr, Object oldValue, Object newValue) {
        for(MSpaceListener l:getMSpaceListeners()) {
            if(l instanceof NHSpaceListener) {
                ((NHSpaceListener)l).attributeChanged(this, attr, oldValue, newValue);
            }
        }
    }

    private List<ContainerListener> getListeners() {
        return _listeners!=null?new ArrayList<ContainerListener>(_listeners):new ArrayList<ContainerListener>();
    }
}
