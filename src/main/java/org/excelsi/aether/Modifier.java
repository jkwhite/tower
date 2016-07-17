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


import java.util.*;


public final class Modifier implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    public static final float[] ONE = {1f, 1f, 1f, 1f};
    private int _st;
    private int _qu;
    private int _ag;
    private int _in;
    private int _em;
    private int _pr;
    private int _co;
    private int _sd;
    private int _me;
    private int _re;
    private float _wt;
    private int _connection;
    private float _candela;
    private float[] _candelaColor = ONE;
    private int _hungerRate;
    private String _color;
    private TreeMap<String,Integer> _mix;
    private int _rate;


    public Modifier() {
    }

    public Modifier(int st) {
        _st = st;
    }

    public Modifier(int st, int qu) {
        _st = st;
        _qu = qu;
    }

    public void clear() {
        _st=0; _qu=0; _ag=0; _in=0; _em=0; _pr=0; _co=0; _sd=0; _me=0; _re=0;
        _wt=0; _connection=0; _candela=0; _candelaColor=ONE; _hungerRate=0;
        _color=null; _mix=null; _rate=0;
    }

    public int getStrength() {
        return _st;
    }

    public int getQuickness() {
        return _qu;
    }

    public int getAgility() {
        return _ag;
    }

    public int getIntuition() {
        return _in;
    }

    public int getEmpathy() {
        return _em;
    }

    public int getPresence() {
        return _pr;
    }

    public int getConstitution() {
        return _co;
    }

    public int getSelfDiscipline() {
        return _sd;
    }

    public int getMemory() {
        return _me;
    }

    public int getReasoning() {
        return _re;
    }

    public float getWeight() {
        return _wt;
    }

    public void setStrength(int st) {
        _st = st;
    }

    public void setQuickness(int qu) {
        _qu = qu;
    }

    public void setAgility(int ag) {
        _ag = ag;
    }

    public void setIntuition(int in) {
        _in = in;
    }

    public void setEmpathy(int em) {
        _em = em;
    }

    public void setPresence(int pr) {
        _pr = pr;
    }

    public void setConstitution(int co) {
        _co = co;
    }

    public void setSelfDiscipline(int sd) {
        _sd = sd;
    }

    public void setMemory(int me) {
        _me = me;
    }

    public void setReasoning(int re) {
        _re = re;
    }

    public void setWeight(float wt) {
        _wt = wt;
    }

    public void setConnected(int connection) {
        _connection = connection;
    }

    public int getConnected() {
        return _connection;
    }

    public void setCandela(float candela) {
        _candela = candela;
    }

    public float getCandela() {
        return _candela;
    }

    public void setCandelaColor(float[] c) {
        if(c.length!=4) {
            throw new IllegalArgumentException("candela color must be R,G,B,A: "+java.util.Arrays.toString(c));
        }
        _candelaColor = c;
    }

    public float[] getCandelaColor() {
        return _candelaColor;
    }

    public void setHungerRate(int hungerRate) {
        _hungerRate = hungerRate;
    }

    public int getHungerRate() {
        return _hungerRate;
    }

    public void setColor(String color) {
        _color = color;
    }

    public String getColor() {
        return _color;
    }

    public void setRate(int rate) {
        _rate = rate;
    }

    public int getRate() {
        return _rate;
    }

    public void set(String name, int value) {
        if(_mix==null) {
            _mix = new TreeMap<String,Integer>();
        }
        _mix.put(name, value);
    }

    public int get(String name) {
        if(_mix==null) {
            return 0;
        }
        Integer i = _mix.get(name);
        return i!=null?i.intValue():0;
    }

    public void add(Modifier m) {
        if(m==null) {
            return;
        }
        setStrength(getStrength()+m.getStrength());
        setAgility(getAgility()+m.getAgility());
        setIntuition(getIntuition()+m.getIntuition());
        setEmpathy(getEmpathy()+m.getEmpathy());
        setQuickness(getQuickness()+m.getQuickness());
        setPresence(getPresence()+m.getPresence());
        setConstitution(getConstitution()+m.getConstitution());
        setSelfDiscipline(getSelfDiscipline()+m.getSelfDiscipline());
        setMemory(getMemory()+m.getMemory());
        setReasoning(getReasoning()+m.getReasoning());
        setWeight(getWeight()+m.getWeight());
        setConnected(getConnected()+m.getConnected());
        setCandela(getCandela()+m.getCandela());
        setHungerRate(getHungerRate()+m.getHungerRate());
        setRate(getRate()+m.getRate());
        float[] cc = m.getCandelaColor();
        setCandelaColor(new float[]{(_candelaColor[0]+cc[0])/2f, (_candelaColor[1]+cc[1])/2f,
                (_candelaColor[2]+cc[2])/2f, (_candelaColor[3]+cc[3])/2f});
        if(_color==null) {
            setColor(m.getColor());
        }
        if(m._mix!=null) {
            if(_mix==null) {
                _mix = (TreeMap) m._mix.clone();
            }
            else {
                for(Map.Entry<String,Integer> e:m._mix.entrySet()) {
                    Integer i = _mix.get(e.getKey());
                    if(i==null) {
                        _mix.put(e.getKey(), e.getValue());
                    }
                    else {
                        _mix.put(e.getKey(), i.intValue()+e.getValue().intValue());
                    }
                }
            }
        }
    }

    public String toString() {
        List s = new ArrayList();
        s.add(_st); s.add(_qu); s.add(_ag); s.add(_me); s.add(_re);
        s.add(_em); s.add(_in); s.add(_pr); s.add(_co); s.add(_wt);
        return s.toString();
    }
}
