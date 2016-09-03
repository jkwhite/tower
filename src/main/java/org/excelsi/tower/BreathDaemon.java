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
import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.Direction;
import java.util.logging.Logger;


public class BreathDaemon extends AttackDaemon {
    private long _last = 0;
    private BreathType _t;
    private Infliction _infliction;
    private int _time = 10;


    public void BreathDaemon() {
    }

    public void setType(BreathType t) {
        _t = t;
    }

    public BreathType getType() {
        return _t;
    }

    public void setInfliction(Infliction i) {
        _infliction = i;
    }

    public Infliction getInfliction() {
        return _infliction;
    }

    public void setTime(int time) {
        _time = time;
    }

    public int getTime() {
        return _time;
    }

    public void poll(final Context c) {
        super.poll(c);
        if(strength>=0&&in.important!=null) {
            MSpace f = in.b.getEnvironment().getMSpace();
            MSpace t = in.important.getEnvironment().getMSpace();
            if(f.isCardinalTo(t)||f.isDiagonalTo(t)) {
                Direction m = f.directionTo(t);
                MSpace o = f.move(m);
                while(o!=null&&o!=t) {
                    if(o.isOccupied()&&in.b.threat((NHBot)o.getOccupant())!=Threat.kos) {
                        break;
                    }
                    o = o.move(m);
                }
                if(o!=t) {
                    strength = -1;
                }
                else {
                    if(Time.now()>_last+_time) {
                        strength *= 2;
                    }
                    else {
                        strength = -1;
                    }
                }
            }
            else {
                strength = -1;
            }
        }
    }

    @Override public void perform(final Context c) {
        Attack a = null;

        if(_t!=null) {
            switch(_t) {
                case fire:
                    a = new BoltAttack(new Fire(451), in.b, new Source(in.b));
                    break;
                case cold:
                    a = new BoltAttack(new Cold(), in.b, new Source(in.b));
                    break;
                case lightning:
                    a = new BoltAttack(new Lightning(), in.b, new Source(in.b));
                    break;
                case acid:
                    a = new BoltAttack(new Acid(), in.b, new Source(in.b));
                    break;
            }
        }
        else if(_infliction!=null) {
            a = new Attack() {
                public Type getType() { return Attack.Type.bolt; }
                public int getRadius() { return 1; }
                public boolean affectsAttacker() { return false; }
                public boolean isPhysical() { return false; }
                public NHBot getAttacker() { return in.b; }
                public Source getSource() { return new Source(in.b); }
                public Armament getWeapon() {
                    return new Armament() {
                        public int getPower() { return 0; }
                        public int getModifiedPower() { return 0; }
                        public int getRate() { return 100; }
                        public Armament.Type getType() { return Armament.Type.missile; }
                        public int getHp() { return 1; }
                        public void setHp(int hp) {}
                        public String getVerb() { return "hit"; }
                        public Stat[] getStats() { return new Stat[]{Stat.ag}; }
                        public String getSkill() { return "wands"; }
                        public String getColor() { return _infliction instanceof Fermionic ? ((Fermionic)_infliction).getColor():"white"; }
                        public String getModel() { return "-"; }
                        public String getAudio() { return "laser_high"; }
                        public Attack invoke(NHBot attacker, NHBot defender, Attack a) {
                            _infliction.inflict(defender);
                            return null;
                        }
                        public void invoke(NHBot attacker, NHSpace s, Attack a) {}
                        public Item toItem() {return null;}
                    };
                }
            };
        }
        if(in.important!=null&&a!=null) {
            _last = Time.now();
            in.b.getEnvironment().face(in.important);
            in.b.getEnvironment().project(in.b.getEnvironment().getFacing(), a);
        }
        if(a==null) {
            Logger.global.severe("invalid breath type for "+in.b);
        }
    }
}
