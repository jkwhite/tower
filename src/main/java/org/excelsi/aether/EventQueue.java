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
import java.util.ArrayList;
import java.util.List;
import java.util.IdentityHashMap;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * Big hack to allow logic and UI to run on separate threads. EventQueue
 * implements all listener-type interfaces provided by the game logic classes.
 * Rather than adding a listener directly to a logic class, a UI element
 * may opt to instead attach the listener to the EventQueue and thereby
 * receive events asynchronously. Events are polled via {@link #play()}.
 * This is good for single-threaded UIs, like JME.
 */
public class EventQueue implements NHEnvironmentListener, ContainerListener, GameListener, MatrixListener, NHSpaceListener, OverlayListener, MechanicsListener, java.io.Serializable {
    private transient List<Event> _q = new ArrayList<Event>();
    private static EventQueue _eventQueue;
    private static Throwable _lastError;


    private transient IdentityHashMap<Object, List> _emap;
    private transient IdentityHashMap<Object, List> _cmap;
    private transient IdentityHashMap<Object, List> _gmap;
    private transient IdentityHashMap<Object, List> _mmap;
    private transient IdentityHashMap<Object, List> _msmap;
    private transient IdentityHashMap<Object, List> _omap;
    private transient IdentityHashMap<Object, List> _mechmap;


    public static Throwable getLastError() {
        return _lastError;
    }

    public static /*synchronized*/ EventQueue getEventQueue() {
        if(_eventQueue==null) {
            initMethodCache();
            _eventQueue = new EventQueue();
        }
        return _eventQueue;
    }

    public void addMechanicsListener(Mechanics source, MechanicsListener listener) {
        List l = _mechmap.get(source);
        if(l==null) {
            l = new ArrayList<MechanicsListener>();
            _mechmap.put(source, l);
            source.addMechanicsListener(this);
        }
        l.add(listener);
    }

    public void addOverlayListener(Overlay source, OverlayListener listener) {
        List l = _omap.get(source);
        if(l==null) {
            l = new ArrayList<OverlayListener>();
            _omap.put(source, l);
            source.addOverlayListener(this);
        }
        l.add(listener);
    }

    public void addNHEnvironmentListener(NHBot source, NHEnvironmentListener listener) {
        List l = _emap.get(source);
        if(l==null) {
            l = new ArrayList<NHEnvironmentListener>();
            _emap.put(source, l);
            source.addListener(this);
        }
        l.add(listener);
    }

    public void addContainerListener(Container source, ContainerListener listener) {
        List l = _cmap.get(source);
        if(l==null) {
            l = new ArrayList<ContainerListener>();
            _cmap.put(source, l);
            source.addContainerListener(this);
        }
        l.add(listener);
    }

    public void addGameListener(Game source, GameListener listener) {
        List l = _gmap.get(source);
        if(l==null) {
            l = new ArrayList<GameListener>();
            _gmap.put(source, l);
            source.addListener(this);
        }
        l.add(listener);
    }

    public void addMatrixListener(Matrix source, MatrixListener listener) {
        List l = _mmap.get(source);
        if(l==null) {
            l = new ArrayList<MatrixListener>();
            _mmap.put(source, l);
            source.addListener(this);
        }
        l.add(listener);
    }

    public void addMSpaceListener(MSpace source, NHSpaceListener listener) {
        List l = _msmap.get(source);
        if(l==null) {
            l = new ArrayList<NHSpaceListener>();
            _msmap.put(source, l);
            source.addMSpaceListener(this);
        }
        l.add(listener);
    }

    public void removeMechanicsListener(Mechanics source, MechanicsListener listener) {
        List list = _mechmap.get(source);
        if(list==null||!list.remove(listener)) {
            throw new IllegalArgumentException(listener+" not listening to "+source);
        }
        if(list!=null&&list.isEmpty()) {
            source.removeMechanicsListener(this);
            _mechmap.remove(source);
        }
    }

    public void removeOverlayListener(Overlay source, OverlayListener listener) {
        List list = _omap.get(source);
        if(list==null||!list.remove(listener)) {
            throw new IllegalArgumentException(listener+" not listening to "+source);
        }
        if(list!=null&&list.isEmpty()) {
            source.removeOverlayListener(this);
            _omap.remove(source);
        }
    }

    public void removeContainerListener(Container source, ContainerListener listener) {
        List list = _cmap.get(source);
        if(list==null||!list.remove(listener)) {
            throw new IllegalArgumentException(listener+" not listening to "+source);
        }
        if(list!=null&&list.isEmpty()) {
            source.removeContainerListener(this);
            _cmap.remove(source);
        }
    }

    public void removeNHEnvironmentListener(NHBot source, NHEnvironmentListener listener) {
        List list = _emap.get(source);
        if(list==null||!list.remove(listener)) {
            throw new IllegalArgumentException(listener+" not listening to "+source);
        }
        if(list!=null&&list.isEmpty()) {
            source.removeListener(this);
            _emap.remove(source);
        }
    }

    public void removeGameListener(Game source, GameListener listener) {
        List list = _gmap.get(source);
        if(list==null||!list.remove(listener)) {
            throw new IllegalArgumentException(listener+" not listening to "+source);
        }
        if(list!=null&&list.isEmpty()) {
            source.removeListener(this);
            _gmap.remove(source);
        }
    }

    public void removeMatrixListener(Matrix source, MatrixListener listener) {
        List list = _mmap.get(source);
        if(list==null||!list.remove(listener)) {
            throw new IllegalArgumentException(listener+" not listening to "+source);
        }
        if(list!=null&&list.isEmpty()) {
            source.removeListener(this);
            _mmap.remove(source);
        }
    }

    public void removeMSpaceListener(MSpace source, NHSpaceListener listener) {
        List list = _msmap.get(source);
        if(list==null||!list.remove(listener)) {
            throw new IllegalArgumentException(listener+" not listening to "+source);
        }
        if(list!=null&&list.isEmpty()) {
            source.removeMSpaceListener(this);
            _msmap.remove(source);
        }
    }

    /**
     * Removes all listeners except GameListeners.
     * This convenience can be used to ensure garbage collection
     * of inactive level UIs. Note that any persistent UI
     * elements listening to this queue will also be removed.
     */
    public void removeLevelListeners() {
        for(Map.Entry<Object, List> e:new IdentityHashMap<Object,List>(_mechmap).entrySet()) {
            for(Object listener:new ArrayList(e.getValue())) {
                removeMechanicsListener((Mechanics)e.getKey(), (MechanicsListener)listener);
            }
        }
        for(Map.Entry<Object, List> e:new IdentityHashMap<Object,List>(_mmap).entrySet()) {
            for(Object listener:new ArrayList(e.getValue())) {
                removeMatrixListener((Matrix)e.getKey(), (MatrixListener)listener);
            }
        }
        for(Map.Entry<Object, List> e:new IdentityHashMap<Object,List>(_msmap).entrySet()) {
            for(Object listener:new ArrayList(e.getValue())) {
                removeMSpaceListener((MSpace)e.getKey(), (NHSpaceListener)listener);
            }
        }
        for(Map.Entry<Object, List> e:new IdentityHashMap<Object,List>(_cmap).entrySet()) {
            for(Object listener:new ArrayList(e.getValue())) {
                removeContainerListener((Container)e.getKey(), (ContainerListener)listener);
            }
        }
        for(Map.Entry<Object, List> e:new IdentityHashMap<Object,List>(_emap).entrySet()) {
            for(Object listener:new ArrayList(e.getValue())) {
                removeNHEnvironmentListener((NHBot)e.getKey(), (NHEnvironmentListener)listener);
            }
        }
        for(Map.Entry<Object, List> e:new IdentityHashMap<Object,List>(_omap).entrySet()) {
            for(Object listener:new ArrayList(e.getValue())) {
                removeOverlayListener((Overlay)e.getKey(), (OverlayListener)listener);
            }
        }
    }

    public void overlayMoved(Overlay o, NHSpace from, NHSpace to) {
        addEvent(_omap, o, _overlayMoved, o, from, to);
    }

    public void overlayRemoved(Overlay o) {
        addEvent(_omap, o, _ooverlayRemoved, o);
    }

    public void occupied(MSpace s, Bot b) {
        addEvent(_msmap, s, _occupied, s, b);
    }

    public void unoccupied(MSpace s, Bot b) {
        addEvent(_msmap, s, _unoccupied, s, b);
    }

    public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
        addEvent(_msmap, source, _msmoved, source, from, to, b);
    }

    public void overlayAdded(NHSpace n, Overlay o) {
        addEvent(_msmap, n, _overlayAdded, n, o);
    }

    public void attributeChanged(NHSpace s, String attr, Object oldValue, Object newValue) {
        addEvent(_msmap, s, _nsattributeChanged, s, attr, oldValue, newValue);
    }

    public void overlayRemoved(NHSpace n, Overlay o) {
        addEvent(_msmap, n, _nsoverlayRemoved, n, o);
    }

    public void parasiteAdded(NHSpace s, Parasite p) {
        addEvent(_msmap, s, _parasiteAdded, s, p);
    }

    public void parasiteAttributeChanged(NHSpace s, Parasite p, String attr, Object oldValue, Object newValue) {
        addEvent(_msmap, s, _parasiteAttributeChanged, s, p, attr, oldValue, newValue);
    }

    public void parasiteRemoved(NHSpace s, Parasite p) {
        addEvent(_msmap, s, _parasiteRemoved, s, p);
    }

    public void parasiteMoved(NHSpace s, NHSpace to, Parasite p) {
        addEvent(_msmap, s, _parasiteMoved, s, to, p);
    }

    public void spacesRemoved(Matrix m, MSpace[] spaces, Bot b) {
        addEvent(_mmap, m, _spacesRemoved, m, spaces, b);
    }

    public void spacesAdded(Matrix m, MSpace[] spaces, Bot b) {
        addEvent(_mmap, m, _spacesAdded, m, spaces, b);
    }

    public void attributeChanged(Matrix m, String attr, Object oldValue, Object newValue) {
        addEvent(_mmap, m, _attributeChanged, m, attr, oldValue, newValue);
    }

    public void ascended(Game g) {
        addEvent(_gmap, g, _ascended, g);
    }

    public void descended(Game g) {
        addEvent(_gmap, g, _descended, g);
    }

    public void afflicted(NHBot b, Affliction a) {
        addEvent(_emap, b, _afflicted, b, a);
    }

    public void cured(NHBot b, Affliction a) {
        addEvent(_emap, b, _cured, b, a);
    }

    public void modifierAdded(NHBot b, Modifier m) {
        addEvent(_emap, b, _modifierAdded, b, m);
    }

    public void modifierRemoved(NHBot b, Modifier m) {
        addEvent(_emap, b, _modifierRemoved, b, m);
    }

    public void equipped(NHBot b, Item i) {
        addEvent(_emap, b, _equipped, b, i);
    }

    public void unequipped(NHBot b, Item i) {
        addEvent(_emap, b, _unequipped, b, i);
    }

    public void itemModified(NHBot b, Item i) {
        addEvent(_emap, b, _itemModified, b, i);
    }

    public void faced(Bot b, Direction old, Direction d) {
        addEvent(_emap, b, _faced, b, old, d);
    }

    public void moved(Bot b, MSpace from, MSpace to) {
        addEvent(_emap, b, _emoved, b, from, to);
    }

    public void forgot(Bot b, List<MSpace> s) {
        addEvent(_emap, b, _forgot, b, s);
    }

    public void discovered(Bot b, List<MSpace> s) {
        addEvent(_emap, b, _discovered, b, s);
    }

    public void seen(Bot b, List<MSpace> s) {
        addEvent(_emap, b, _seen, b, s);
    }

    public void obscured(Bot b, List<MSpace> s) {
        addEvent(_emap, b, _obscured, b, s);
    }

    public void noticed(Bot b, List<Bot> bots) {
        addEvent(_emap, b, _noticed, b, bots);
    }

    public void missed(Bot b, List<Bot> bots) {
        addEvent(_emap, b, _missed, b, bots);
    }

    public void died(Bot b, MSource s) {
        addEvent(_emap, b, _died, b, s);
    }

    public void collided(Bot active, Bot passive) {
        addEvent(_emap, active, _collided, active, passive);
    }

    public void attacked(NHBot b, Outcome outcome) {
        addEvent(_emap, b, _attackedO, b, outcome);
    }

    public void attacked(NHBot b, NHBot attacked) {
        addEvent(_emap, b, _attackedB, b, attacked);
    }

    public void attackedBy(NHBot b, NHBot attacker) {
        addEvent(_emap, b, _attackedBy, b, attacker);
    }

    public void actionStarted(NHBot b, ProgressiveAction action) {
        addEvent(_emap, b, _actionStarted, b, action);
    }

    public void actionStopped(NHBot b, ProgressiveAction action) {
        addEvent(_emap, b, _actionStopped, b, action);
    }

    public void actionPerformed(NHBot b, InstantaneousAction action) {
        addEvent(_emap, b, _actionPerformed, b, action);
    }

    public void attributeChanged(Bot b, String attribute, Object newValue) {
        addEvent(_emap, b, _neattributeChanged, b, attribute, newValue);
    }

    public void attackStarted(Mechanics m, Attack attack, NHBot attacker, NHBot defender, NHSpace[] path) {
        addEvent(_mechmap, m, _attackStarted, m, attack, attacker, defender, path);
    }

    public void attackEnded(Mechanics m, Attack attack, NHBot attacker, NHBot defender, Outcome outcome) {
        addEvent(_mechmap, m, _attackEnded, m, attack, attacker, defender, outcome);
    }

    public void itemDropped(Container space, Item item, int idx, boolean incremented) {
        addEvent(_cmap, space, _itemDropped, space, item, idx, incremented);
    }

    public void itemAdded(Container space, Item item, int idx, boolean incremented) {
        addEvent(_cmap, space, _itemAdded1, space, item, idx, incremented);
    }

    public void itemAdded(Container space, Item item, int idx, boolean incremented, NHBot adder, NHSpace origin) {
        addEvent(_cmap, space, _itemAdded2, space, item, idx, incremented, adder, origin);
    }

    public void itemTaken(Container space, Item item, int idx) {
        addEvent(_cmap, space, _itemTaken, space, item, idx);
    }

    public void itemDestroyed(Container space, Item item, int idx) {
        addEvent(_cmap, space, _itemDestroyed, space, item, idx);
    }

    public void itemsDestroyed(Container space, Item[] items) {
        addEvent(_cmap, space, _itemsDestroyed, space, items);
    }

    private void addEvent(Map<Object, List> map, Object source, Method method, Object... args) {
        List l = map.get(source);
        if(l!=null) {
            synchronized(_q) {
                try {
                    _q.add(new NHEvent(l, method, args));
                }
                catch(Exception e) {
                    throw new Error(e);
                }
            }
        }
    }

    private void addEvent(Map<Object, List> map, Object source, String mname, Class mclass, Class[] margs, Object... args) {
        List l = map.get(source);
        if(l!=null) {
            synchronized(_q) {
                try {
                    _q.add(new NHEvent(l, mclass.getMethod(mname, margs), args));
                }
                catch(Exception e) {
                    throw new Error(e);
                }
            }
        }
    }

    public /*synchronized*/ void play() {
        List<Event> play = null;
        //long qs = System.currentTimeMillis();
        synchronized(_q) {
            if(_q.size()>0) {
                play = new ArrayList<Event>(_q);
                _q.clear();
            }
        }
        if(play!=null) {
            //long be = System.currentTimeMillis();
            //System.err.println("queue copy took "+(be-qs));
            for(int i=0;i<play.size();i++) {
            //for(Event e:play) {
                Event e = play.get(i);
                //long st = System.currentTimeMillis();
                e.invoke();
                //long now = System.currentTimeMillis();
                //System.err.println((now-st)+" processed "+e);
            }
            //System.err.println("queue took "+(System.currentTimeMillis()-be));
        }
    }

    public void postback(GameAction a) {
        synchronized(_q) {
            _q.add(new UIEvent(a));
            _q.notify();
        }
    }

    public String toString() {
        synchronized(_q) {
            StringBuilder b = new StringBuilder();
            b.append("_emap: ");
            b.append(_emap);
            b.append("_cmap: ");
            b.append(_cmap);
            b.append("_gmap: ");
            b.append(_gmap);
            b.append("_mmap: ");
            b.append(_mmap);
            b.append("_msmap: ");
            b.append(_msmap);
            b.append("_omap: ");
            b.append(_omap);
            b.append("_mechmap: ");
            b.append(_mechmap);
            return b.toString();
        }
    }

    interface Event {
        void invoke();
    }

    static final class UIEvent implements Event {
        private GameAction _a;


        public UIEvent(GameAction uiAction) {
            _a = uiAction;
        }

        public void invoke() {
            _a.perform();
        }
    }

    static final class NHEvent implements Event {
        private List _listeners;
        private Method _method;
        private Object[] _args;

        public NHEvent(List listeners, Method method, Object... args) {
            _listeners = listeners;
            _method = method;
            _args = args;
        }

        public void invoke() {
            for(Object o:new ArrayList(_listeners)) {
                try {
                    _method.invoke(o, _args);
                }
                catch(Throwable e) {
                    _lastError = e;
                    throw new Error("failed invoking "+_method.getName()+" with args "+Arrays.toString(_args), e);
                }
            }
        }

        public String toString() {
            //return _method.getDeclaringClass()+"."+_method.getName()+" with "+Arrays.asList(_args)+" for "+_listeners;
            return _method.getDeclaringClass()+"."+_method.getName();
        }
    }

    private EventQueue() {
        initMaps();
    }

    private void initMaps() {
        _emap = new IdentityHashMap<Object, List>();
        _cmap = new IdentityHashMap<Object, List>();
        _gmap = new IdentityHashMap<Object, List>();
        _mmap = new IdentityHashMap<Object, List>();
        _msmap = new IdentityHashMap<Object, List>();
        _omap = new IdentityHashMap<Object, List>();
        _mechmap = new IdentityHashMap<Object, List>();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        _q = new ArrayList<Event>();
        initMethodCache();
        initMaps();
        setEventQueue(this); // should only be called once if everything was serialized correctly
    }

    private static Method _overlayMoved, _ooverlayRemoved, _occupied, _unoccupied, _msmoved,
        _overlayAdded, _nsattributeChanged, _nsoverlayRemoved, _parasiteAdded, _parasiteRemoved, _parasiteMoved,
        _spacesRemoved, _spacesAdded, _attributeChanged, _ascended, _descended, _afflicted, _cured,
        _modifierAdded, _modifierRemoved, _equipped,
        _unequipped, _itemModified, _faced, _emoved, _forgot, _discovered, _noticed, _missed, _died,
        _collided, _attackedO, _attackedB, _attackedBy, _actionStarted, _actionStopped, _actionPerformed,
        _neattributeChanged, _attackStarted, _attackEnded, _itemDropped, _itemAdded1,
        _itemAdded2, _itemTaken, _itemDestroyed, _itemsDestroyed, _seen, _obscured, _parasiteAttributeChanged;

    private static void initMethodCache() {
        try {
            _overlayMoved = OverlayListener.class.getMethod("overlayMoved", new Class[]{Overlay.class, NHSpace.class, NHSpace.class});
            _ooverlayRemoved = OverlayListener.class.getMethod("overlayRemoved", new Class[]{Overlay.class});
            _occupied = MSpaceListener.class.getMethod("occupied", new Class[]{MSpace.class, Bot.class});
            _unoccupied = MSpaceListener.class.getMethod("unoccupied", new Class[]{MSpace.class, Bot.class});
            _msmoved = MSpaceListener.class.getMethod("moved", new Class[]{MSpace.class, MSpace.class, MSpace.class, Bot.class});
            _overlayAdded = NHSpaceListener.class.getMethod("overlayAdded", new Class[]{NHSpace.class, Overlay.class});
            _nsattributeChanged = NHSpaceListener.class.getMethod("attributeChanged", new Class[]{NHSpace.class, String.class, Object.class, Object.class});
            _nsoverlayRemoved = NHSpaceListener.class.getMethod("overlayRemoved", new Class[]{NHSpace.class, Overlay.class});
            _parasiteAdded = NHSpaceListener.class.getMethod("parasiteAdded", new Class[]{NHSpace.class, Parasite.class});
            _parasiteRemoved = NHSpaceListener.class.getMethod("parasiteRemoved", new Class[]{NHSpace.class, Parasite.class});
            _parasiteMoved = NHSpaceListener.class.getMethod("parasiteMoved", new Class[]{NHSpace.class, NHSpace.class, Parasite.class});
            _parasiteAttributeChanged = NHSpaceListener.class.getMethod("parasiteAttributeChanged", new Class[]{NHSpace.class, Parasite.class, String.class, Object.class, Object.class});
            _spacesRemoved = MatrixListener.class.getMethod("spacesRemoved", new Class[]{Matrix.class, new MSpace[0].getClass(), Bot.class});
            _spacesAdded = MatrixListener.class.getMethod("spacesAdded", new Class[]{Matrix.class, new MSpace[0].getClass(), Bot.class});
            _attributeChanged = MatrixListener.class.getMethod("attributeChanged", new Class[]{Matrix.class, String.class, Object.class, Object.class});
            _ascended = GameListener.class.getMethod("ascended", Game.class);
            _descended = GameListener.class.getMethod("descended", Game.class);
            _afflicted = NHEnvironmentListener.class.getMethod("afflicted", NHBot.class, Affliction.class);
            _cured = NHEnvironmentListener.class.getMethod("cured", NHBot.class, Affliction.class);
            _modifierAdded = NHEnvironmentListener.class.getMethod("modifierAdded", NHBot.class, Modifier.class);
            _modifierRemoved = NHEnvironmentListener.class.getMethod("modifierRemoved", NHBot.class, Modifier.class);
            _equipped = NHEnvironmentListener.class.getMethod("equipped", NHBot.class, Item.class);
            _unequipped = NHEnvironmentListener.class.getMethod("unequipped", NHBot.class, Item.class);
            _itemModified = NHEnvironmentListener.class.getMethod("itemModified", NHBot.class, Item.class);
            _faced = EnvironmentListener.class.getMethod("faced", Bot.class, Direction.class, Direction.class);
            _emoved = EnvironmentListener.class.getMethod("moved", Bot.class, MSpace.class, MSpace.class);
            _forgot = EnvironmentListener.class.getMethod("forgot", Bot.class, List.class);
            _discovered = EnvironmentListener.class.getMethod("discovered", Bot.class, List.class);
            _seen = EnvironmentListener.class.getMethod("seen", Bot.class, List.class);
            _obscured = EnvironmentListener.class.getMethod("obscured", Bot.class, List.class);
            _noticed = EnvironmentListener.class.getMethod("noticed", Bot.class, List.class);
            _missed = EnvironmentListener.class.getMethod("missed", Bot.class, List.class);
            _died = EnvironmentListener.class.getMethod("died", Bot.class, MSource.class);
            _collided = EnvironmentListener.class.getMethod("collided", Bot.class, Bot.class);
            _attackedO = NHEnvironmentListener.class.getMethod("attacked", NHBot.class, Outcome.class);
            _attackedB = NHEnvironmentListener.class.getMethod("attacked", NHBot.class, NHBot.class);
            _attackedBy = NHEnvironmentListener.class.getMethod("attackedBy", NHBot.class, NHBot.class);
            _actionStarted = NHEnvironmentListener.class.getMethod("actionStarted", NHBot.class, ProgressiveAction.class);
            _actionStopped = NHEnvironmentListener.class.getMethod("actionStopped", NHBot.class, ProgressiveAction.class);
            _actionPerformed = NHEnvironmentListener.class.getMethod("actionPerformed", NHBot.class, InstantaneousAction.class);
            _neattributeChanged = EnvironmentListener.class.getMethod("attributeChanged", Bot.class, String.class, Object.class);
            _attackStarted = MechanicsListener.class.getMethod("attackStarted", Mechanics.class, Attack.class, NHBot.class, NHBot.class, new NHSpace[0].getClass());
            _attackEnded = MechanicsListener.class.getMethod("attackEnded", Mechanics.class, Attack.class, NHBot.class, NHBot.class, Outcome.class);
            _itemDropped = ContainerListener.class.getMethod("itemDropped", Container.class, Item.class, Integer.TYPE, Boolean.TYPE);
            _itemAdded1 = ContainerListener.class.getMethod("itemAdded", Container.class, Item.class, Integer.TYPE, Boolean.TYPE);
            _itemAdded2 = ContainerListener.class.getMethod("itemAdded", Container.class, Item.class, Integer.TYPE, Boolean.TYPE, NHBot.class, NHSpace.class);
            _itemTaken = ContainerListener.class.getMethod("itemTaken", Container.class, Item.class, Integer.TYPE);
            _itemDestroyed = ContainerListener.class.getMethod("itemDestroyed", Container.class, Item.class, Integer.TYPE);
            _itemsDestroyed = ContainerListener.class.getMethod("itemsDestroyed", Container.class, new Item[0].getClass());
        }
        catch(Exception e) {
            throw new Error(e);
        }
    }

    private static /*synchronized*/ void setEventQueue(EventQueue queue) {
        if(_eventQueue!=null) {
            throw new IllegalStateException("cannot set queue: event queue is already set");
        }
        _eventQueue = queue;
    }
}
