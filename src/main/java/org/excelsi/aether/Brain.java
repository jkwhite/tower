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
import java.util.HashMap;
import java.util.Arrays;


/**
 * A bot intelligence system based on pandemonium.
 * <p/>
 * The system is designed to reach decisions very quickly
 * and with a minimum of method invocation and object
 * allocation.
 * <p/>
 * The system is composed of warring daemons. Each daemon
 * represents one possible course of action. Each daemon
 * can exert influence over the bot with a variable-strength
 * signal, and can also affect the strength of other daemons
 * by activating or deactivating chemicals on which other
 * daemons are dependent. It is a crude doppelganger of
 * the electro-chemical brain.
 * <p/>
 * The order in which daemons are added is irrelevant. All
 * daemons will be polled and chemicals adjusted before
 * their strengths are compared.
 */
public class Brain implements java.io.Serializable, Cloneable {
    /**
     * Chemical represents the saturation level of a chemical
     * in the brain. Chemicals provide an easy way to quickly
     * change global behavior while allowing daemons to remain
     * loosely coupled.
     * <p/>
     * Chemicals may operate individually or they may be linked
     * to an "inverse" chemical. If linked in this manner, the
     * level of the inverse chemical will always be the inverse
     * of this chemical, and vice versa. In other words,
     * activating one chemical automatically deactivates the other.
     */
    public static class Chemical implements java.io.Serializable {
        /** level, initially 1 */
        private int _level = 1;
        /** chemical hooked to this one */
        private Chemical _inverse;
        /** name of this chemical */
        private String _name;


        /**
         * Constructs a new, activated chemical.
         */
        public Chemical(String name) {
            _name = name;
        }

        /**
         * Gets the level of this chemical. Activated chemicals
         * have higher levels than deactivated chemicals.
         *
         * @return level of this chemical
         */
        public int getLevel() {
            return _level;
        }

        /**
         * Activates this chemical. If this chemical has an
         * inverse, the inverse will be deactivated.
         */
        public void activate() {
            _level = 1;
            if(_inverse!=null) {
                _inverse._level = 0;
            }
        }

        /**
         * Deactivates this chemical. If this chemical has an
         * inverse, the inverse will be activated.
         */
        public void deactivate() {
            _level = 0;
            if(_inverse!=null) {
                _inverse._level = 1;
            }
        }

        /**
         * Tests if this chemical is active.
         *
         * @return <code>true</code> if this chemical is active
         */
        public boolean isActive() {
            return _level>0;
        }

        /**
         * Sets this chemical's inverse and activates
         * this chemical.
         *
         * @param inverse inverse of this chemical.
         */
        private void setInverse(Chemical inverse) {
            _inverse = inverse;
            _inverse._inverse = this;
            activate();
        }

        /**
         * Gets the name of this chemical.
         *
         * @return name
         */
        public String getName() {
            return _name;
        }
    }

    /** daemons */
    private Daemon[] _daemons;
    /** last input */
    private Input _in;
    /** event source */
    private EventSource _e;
    /** chemicals */
    private Map<String,Chemical> _chemicals = new HashMap<String,Chemical>();


    /**
     * Constructs a new, empty brain.
     */
    public Brain() {
    }

    /**
     * Constructs a new brain.
     *
     * @param daemons constituents of this brain
     */
    public Brain(Daemon[] daemons) {
        setDaemons(daemons);
    }

    /**
     * Constructs a new brain.
     *
     * @param daemons constituents of this brain
     */
    public Brain(java.util.List<Daemon> daemons) {
        this((Daemon[])daemons.toArray(new Daemon[daemons.size()]));
    }

    /**
     * Sets all daemons in this brain.
     *
     * @param daemons daemons
     */
    public void setDaemons(Daemon[] daemons) {
        _daemons = daemons;
        _in = new Input();
        _chemicals.clear();
        for(int i=0;i<_daemons.length;i++) {
            _daemons[i].in = _in;
            if(_e!=null) {
                _daemons[i].setEventSource(_e);
            }
            String[] chems = _daemons[i].getChemicalSpec().split(",");
            for(String chem:chems) {
                chem = chem.trim();
                String[] linked = chem.split("=");
                if(linked.length==1) {
                    if(!_chemicals.containsKey(linked[0])) {
                        _chemicals.put(linked[0], new Chemical(linked[0]));
                    }
                }
                else if(linked.length==2) {
                    Chemical a = new Chemical(linked[0]);
                    Chemical b = new Chemical(linked[1]);
                    a.setInverse(b);
                    _chemicals.put(linked[0], a);
                    _chemicals.put(linked[1], b);
                }
                else {
                    throw new IllegalArgumentException("illegal chem spec '"+_daemons[i].getChemicalSpec()+"'");
                }
            }
        }
        for(Daemon d:_daemons) {
            d.init(_chemicals);
        }
    }

    /**
     * Gets all daemons in this brain.
     *
     * @return daemons
     */
    public Daemon[] getDaemons() {
        return _daemons;
    }

    /**
     * Tests if this brain contains a daemon.
     *
     * @param daemon daemon to test
     * @return <code>true</code> if this brain contains <code>daemon</code>
     */
    public boolean containsDaemon(Daemon daemon) {
        for(int i=0;i<_daemons.length;i++) {
            if(_daemons[i].equals(daemon)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a daemon to this brain.
     *
     * @param daemon daemon to add
     */
    public void addDaemon(Daemon daemon) {
        addDaemons(new Daemon[]{daemon});
    }

    /**
     * Adds some daemons to this brain.
     *
     * @param daemons daemons to add
     */
    public void addDaemons(Daemon[] daemons) {
        if(_daemons==null) {
            setDaemons(daemons);
        }
        else {
            Daemon[] nd = new Daemon[_daemons.length+daemons.length];
            System.arraycopy(_daemons, 0, nd, 0, _daemons.length);
            System.arraycopy(daemons, 0, nd, _daemons.length, daemons.length);
            setDaemons(nd);
        }
    }

    /**
     * Gets the first instance of a daemon in this brain.
     *
     * @param c daemon's class must be assignable to this
     * @return daemon or <code>null</code> if no daemon found
     */
    public Daemon daemon(Class c) {
        for(Daemon d:getDaemons()) {
            if(c.isAssignableFrom(d.getClass())) {
                return d;
            }
        }
        return null;
    }

    /**
     * Sets the event source for this brain. The event source
     * will be passed on to all existing and new daemons
     * added to this brain.
     *
     * @param e event source
     */
    public void setEventSource(EventSource e) {
        _e = e;
        for(Daemon d:_daemons) {
            d.setEventSource(e);
        }
    }

    /**
     * Gets a chemical in this brain.
     *
     * @param name name of chemical to retrieve
     * @return chemical or <code>null</code> if none
     */
    public Chemical getChemical(String name) {
        return _chemicals.get(name);
    }

    /**
     * Produces a course of action based on specified inputs. Either
     * <code>important</code> or <code>a</code> should be non-null.
     *
     * @param b bot for which to produce result
     * @param important most important bot in <code>b</code>'s vision, or null
     * @param a current attack against <code>b</code>, or null
     * @return next course of action
     */
    public Runnable react(NHBot b, NHBot important, Attack a) {
        if(b==null&&a==null) {
            throw new IllegalArgumentException("no bot and no attack");
        }
        _in.b = b;
        _in.important = important;
        _in.attack = a;
        for(Daemon d:_daemons) {
            if(_in!=d.in) {
                throw new IllegalStateException("wrong input");
            }
            d.poll();
        }
        int max = -1;
        int ms = 0;
        for(int i=0;i<_daemons.length;i++) {
            //int str = _upd[i].strength*_daemons[i].getChemical().getLevel();
            int str = _daemons[i].strength*_daemons[i].getChemical().getLevel();
            //System.err.println(_daemons[i]+": "+_daemons[i].strength+"*"+_daemons[i].getChemical().getLevel());
            if(str>ms) {
                max = i;
                ms = str;
            }
        }
        Daemon choice = max>=0?_daemons[max]:null;
        //System.err.println(b+" chose "+choice);
        return choice;
    }

    public void verify() {
        for(Daemon d:_daemons) {
            if(d.in!=_in) {
                throw new IllegalStateException("dupe in for "+d);
            }
        }
    }

    public Brain clone() {
        Brain b = (Brain) DefaultNHBot.deepCopy(this);
        b.setDaemons(b._daemons);
        return b;
    }

    /**
     * Daemon represents one possible course of action.
     */
    public static abstract class Daemon implements java.io.Serializable, Runnable {
        /** available input */
        public Input in;
        /** decision strength */
        public int strength;


        /**
         * Computes hash code based only on class.
         */
        public int hashCode() {
            return getClass().hashCode();
        }

        /**
         * Computes equality based only on class.
         */
        public boolean equals(Object o) {
            return getClass().equals(o);
        }

        /**
         * Initializes this daemon with all chemicals known to
         * the brain. When new daemons are added, this method
         * will be invoked again. Chemical instances are constructed
         * based on the chemical specs of all daemons in this brain.
         *
         * @param chemicals chemicals
         */
        public void init(Map<String,Chemical> chemicals) {
        }

        /**
         * Gets all chemicals used by this daemon. Multiple chemicals
         * must be comma-separated. Linked chemicals may be specified
         * as <code>chem1=chem2</code>. For example, a daemon that
         * depends on three chemicals, two of which are linked, may
         * return the string <code>chemA, chemB=chemC</code>.
         *
         * @return chemical specification
         */
        public String getChemicalSpec() {
            return "";
        }

        /**
         * Instructs this daemon to update its strength based on available input.
         * This method should set <code>strength</code> appropriately. It may
         * also activate or deactivate chemicals. It should operate quickly;
         * leave any heavy operations for <code>run()</code> or only run them
         * periodically.
         */
        abstract public void poll();

        /**
         * Gets the chemical that regulates this daemon's strength.
         * The chemical does not have to be part of this daemon's
         * chemical spec or retrieved during <code>init()</code>.
         * For example, if you want to ensure a chemical is
         * independent of all other chemicals or tied to some external
         * activity, you could return a unique chemical here.
         *
         * @return non-null chemical
         */
        abstract public Chemical getChemical();

        /**
         * Performs this daemon's action. This means this daemon was selected
         * as the bot's course of action.
         */
        abstract public void run();

        /**
         * Extensions of Daemon may override this method to interact with
         * the event source.
         *
         * @param e event source for this daemon
         */
        public void setEventSource(EventSource e) {
        }
    }

    /**
     * Input for a daemon. Note that <i>either</i> <code>important</code>
     * <i>or</i> <code>attack</code> will be non-null. If <code>important</code>
     * is non-null, the bot is deciding on what to do for its next turn.
     * If <code>attack</code> is non-null, the bot is deciding whether or not
     * to intercept an attack.
     *
     * @see org.excelsi.aether.NHBot#intercept(Attack)
     */
    public static final class Input implements java.io.Serializable {
        /** bot on which the daemon is operating */
        public NHBot b;
        /** most important bot in <code>b</code>'s vision */
        public NHBot important;
        //public Decision d;
        /** current attack against <code>b</code>, if any */
        public Attack attack;
    }
}
