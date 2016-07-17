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


import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Set;
import java.util.EnumSet;


public class Basis implements java.io.Serializable {
    public enum Type { swords, disks, cups, wands, arcana };
    public enum State { stable, unstable, explosive };

    private static Map<Basis, Class> _basii = new HashMap<Basis, Class>();
    private static Map<Class, Basis> _classes = new HashMap<Class, Basis>();

    /**
     * Registers the invoking class with the specified basis.
     *
     * @param b basis
     * @throws IllegalArgumentException if another class has already registered the basis
     */
    public static void claim(Basis b) {
        try {
            Class c = Class.forName(new Exception().getStackTrace()[1].getClassName());
            if(_basii.containsKey(b)) {
                throw new IllegalArgumentException("basis "+b+" already maps to "+_basii.get(b));
            }
            _basii.put(b, c);
            _classes.put(c, b);
        }
        catch(ClassNotFoundException e) {
            throw new Error(e);
        }
    }

    private static List<Trans> _mutations;
    static {
        _mutations = new ArrayList<Trans>();

        /*
        _mutations.add(new Trans(Type.swords, State.stable, Type.swords, Type.swords));
        _mutations.add(new Trans(Type.disks, State.stable, Type.swords, Type.disks));
        _mutations.add(new Trans(Type.wands, State.unstable, Type.swords, Type.wands));
        _mutations.add(new Trans(Type.cups, State.explosive, Type.swords, Type.cups));

        _mutations.add(new Trans(Type.disks, State.stable, Type.disks, Type.disks));
        _mutations.add(new Trans(Type.swords, State.stable, Type.disks, Type.swords));
        _mutations.add(new Trans(Type.cups, State.unstable, Type.disks, Type.cups));
        _mutations.add(new Trans(Type.wands, State.explosive, Type.disks, Type.wands));

        _mutations.add(new Trans(Type.wands, State.stable, Type.wands, Type.wands));
        _mutations.add(new Trans(Type.cups, State.stable, Type.wands, Type.cups));
        _mutations.add(new Trans(Type.swords, State.unstable, Type.wands, Type.swords));
        _mutations.add(new Trans(Type.disks, State.explosive, Type.wands, Type.disks));

        _mutations.add(new Trans(Type.cups, State.stable, Type.cups, Type.cups));
        _mutations.add(new Trans(Type.wands, State.stable, Type.cups, Type.wands));
        _mutations.add(new Trans(Type.disks, State.unstable, Type.cups, Type.disks));
        _mutations.add(new Trans(Type.swords, State.explosive, Type.cups, Type.swords));
        */
        /*
                swords  disks  wands  cups
        swords    s       s      w      c
        disks     s       d      d      d
        wands     w       d      w      c
        cups      c       d      c      c
        */

        _mutations.add(new Trans(Type.swords, State.stable, Type.swords, Type.swords));
        _mutations.add(new Trans(Type.swords, State.stable, Type.swords, Type.disks));
        _mutations.add(new Trans(Type.wands, State.unstable, Type.swords, Type.wands));
        _mutations.add(new Trans(Type.cups, State.explosive, Type.swords, Type.cups));

        _mutations.add(new Trans(Type.disks, State.stable, Type.disks, Type.disks));
        _mutations.add(new Trans(Type.swords, State.stable, Type.disks, Type.swords));
        _mutations.add(new Trans(Type.disks, State.unstable, Type.disks, Type.cups));
        _mutations.add(new Trans(Type.disks, State.explosive, Type.disks, Type.wands));

        _mutations.add(new Trans(Type.wands, State.stable, Type.wands, Type.wands));
        _mutations.add(new Trans(Type.cups, State.stable, Type.wands, Type.cups));
        _mutations.add(new Trans(Type.wands, State.unstable, Type.wands, Type.swords));
        _mutations.add(new Trans(Type.disks, State.explosive, Type.wands, Type.disks));

        _mutations.add(new Trans(Type.cups, State.stable, Type.cups, Type.cups));
        _mutations.add(new Trans(Type.cups, State.stable, Type.cups, Type.wands));
        _mutations.add(new Trans(Type.disks, State.unstable, Type.cups, Type.disks));
        _mutations.add(new Trans(Type.cups, State.explosive, Type.cups, Type.swords));
    }

    private Type _t;
    private int _n;
    private boolean _inverted;
    private State _state;


    public Basis(Type t, int num) {
        this(t, num, false);
    }

    public Basis(Type t, int num, boolean inverted) {
        this(t, num, inverted, State.stable);
    }

    public Basis(Type t, int num, boolean inverted, State s) {
        _t = t;
        _n = num;
        _inverted = inverted;
        _state = s;
    }

    private Basis(Type t, State s) {
        _t = t;
        _state = s;
    }

    private Basis(Basis b) {
        this(b._t, b._n, b._inverted, b._state);
    }

    public boolean equals(Object o) {
        Basis b = (Basis) o;
        return _t==b._t && _n==b._n && _inverted==b._inverted && _state==b._state;
    }

    public int hashCode() {
        return _t.hashCode() ^ _n;
    }

    public String toString() {
        String s = _n+" of "+_t;
        if(_inverted) {
            s += ", inverted";
        }
        s += " ("+_state+")";
        return s;
    }

    public static void print() {
        Set<Type> ts = EnumSet.allOf(Type.class);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        for(Type t:ts) {
            if(t!=Type.arcana) {
                pw.printf("%-15s", t);
            }
        }
        pw.println();
        for(int i=0;i<10;i++) {
            for(Type t:ts) {
                if(t!=Type.arcana) {
                    Basis b = new Basis(t, i);
                    String claim = "(none)";
                    Class c = classFor(b);
                    if(c!=null) {
                        claim = c.getName().substring(c.getName().lastIndexOf('.')+1);
                    }
                    pw.printf("%-15s", claim);
                }
            }
            pw.println();
        }
        pw.close();
        System.err.println(sw.toString());
    }

    public static Basis transmute(Basis... basii) {
        if(basii==null||basii.length<2) {
            throw new IllegalArgumentException("need at least two basises");
        }
        Type[] types = new Type[basii.length];
        for(int i=0;i<types.length;i++) {
            types[i] = basii[i]._t;
        }
        for(Trans t:_mutations) {
            if(Arrays.deepEquals(t.getTypes(), types)) {
                Basis result = new Basis(t.getResult());
                int n = 0;
                State s = State.stable;
                boolean inv = false;
                for(Basis b:basii) {
                    n ^= b._n;
                    //if(b._volatile) {
                        //vol = !vol;
                    //}
                    if(b._state!=State.stable) {
                        s = b._state;
                    }
                    if(b._inverted) {
                        inv = !inv;
                    }
                }
                result._n = n;
                //result._state = s;
                result._inverted = inv;
                System.err.println("T: "+Arrays.asList(basii)+" => "+result);
                return result;
            }
        }
        //System.err.println("T: "+Arrays.asList(basii)+" => null");
        //Basis to = new Basis(basii[basii.length-1]._t, 0, false, State.explosive);

        return null;
    }

    public static Basis transmute(Class... classes) {
        Basis[] basii = new Basis[classes.length];
        for(int i=0;i<basii.length;i++) {
            basii[i] = _classes.get(classes[i]);
            if(basii[i]==null) {
                throw new IllegalArgumentException("no basis for "+classes[i]);
            }
        }
        Basis b = transmute(basii);
        //return b!=null?classFor(b):null;
        return b;
    }

    public State getState() {
        return _state;
    }

    public Class classFor() {
        return classFor(this);
    }

    public static Class classFor(Basis b) {
        //return _basii.get(b);
        return _basii.get(new Basis(b._t, b._n));
    }

    public static Class[] getRegisteredClasses() {
        return (Class[]) _classes.keySet().toArray(new Class[_classes.size()]);
    }

    private static class Trans {
        private Type[] _types;
        private Basis _result;


        public Trans(Type r, State state, Type... types) {
            _types = types;
            _result = new Basis(r, state);
        }

        public Basis getResult() {
            return _result;
        }

        public Type[] getTypes() {
            return _types;
        }
    }
}
