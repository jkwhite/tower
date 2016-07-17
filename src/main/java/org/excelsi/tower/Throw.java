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


import org.excelsi.matrix.Direction;
import org.excelsi.aether.*;


public class Throw extends ItemAction {
    private Direction _d = null;


    public Throw(Item i, Direction d) {
        super("throw", i);
        _d = d;
    }

    public Throw() {
        super("throw", new ItemFilter() {
            public boolean accept(Item i, NHBot bot) {
                //return true; //i instanceof Missile;
                return bot==null||!bot.isEquipped(i);
            }
        });
    }

    public String getDescription() {
        return "Throw an item or shoot a missile weapon.";
    }

    protected void act() {
        if(_d==null) {
            _d = N.narrative().direct(getBot(), "Which direction?");
        }
        getBot().getEnvironment().face(_d);
        if(getBot().isEquipped(getItem())) {
            N.narrative().print(getBot(), "You're currently using that!");
            throw new ActionCancelledException();
        }
        final Item thrown = getBot().getInventory().split(getItem());
        MissileAttack ma = null;
        if(thrown instanceof Missile) {
            final Missile m = (Missile) thrown;
            // combine with equipped weapon if applicable
            Item equipped = getBot().getWielded();
            if(equipped!=null&&m.matches((Armament)equipped)) {
                final Armament arm = (Armament) equipped;
                ma = new MissileAttack(getBot(), arm, m);
            }
            else {
                ma = new MissileAttack(getBot(), (Armament)thrown);
            }
        }
        else if(thrown instanceof Armament && ! (thrown instanceof Armor)) {
            ma = new MissileAttack(getBot(), (Armament)thrown);
        }
        else {
            ma = new MissileAttack(getBot(), new Armament() {
                public int getPower() { return 0; }
                public int getModifiedPower() { return 0; }
                public int getRate() { return 100; }
                public int getHp() { return 0; }
                public void setHp(int hp) { thrown.setHp(hp); }
                public String getVerb() { return "hit"; }
                public String getAudio() { return "hit_small"; }
                public String getSkill() { return Weapon.THROWN; }
                public Attack invoke(NHBot attacker, NHBot defender, Attack a) { if(thrown.invokesIncidentally()) { thrown.invoke(defender); } return null; }
                public void invoke(NHBot attacker, NHSpace s, Attack a) { }
                public Item toItem() { return thrown; }
                public String getColor() { return thrown.getColor(); }
                public String getModel() { return thrown.getModel(); }
                public Type getType() { return Type.missile; }
                public Stat[] getStats() { return thrown.getStats(); }
            });
        }
        boolean wisp = ma.getWeapon().toItem() instanceof Corpse && ((Corpse)ma.getWeapon().toItem()).getSpirit()!=null;
        getBot().getEnvironment().project(_d, ma);
        // this is because Corpse.spirit is transient and throwing does a
        // serialization via Inventory.split()
        if(wisp) {
            N.narrative().print(getBot(), "A wisp of smoke escapes "+Grammar.noun(ma.getWeapon().toItem())+".");
        }
        _d = null;
    }

    public static class MissileAttack implements Attack {
        private Armament _w;
        private NHBot _t;
        private int _radius;


        public MissileAttack(NHBot thrower, Armament weapon) {
            _t = thrower;
            _w = weapon;
            _radius = thrower!=null?thrower.getModifiedStrength()/5:Integer.MAX_VALUE;
            if(weapon.toItem()!=null) {
                _radius = Math.max(1, _radius-(int)weapon.toItem().getWeight());
            }
        }

        public MissileAttack(NHBot thrower, final Armament arm, final Armament m) {
            _t = thrower;
            _radius = thrower.getModifiedStrength()/4;
            _w = new Armament() {
                public int getPower() { return arm.getPower()+m.getPower(); }
                public int getModifiedPower() { return arm.getModifiedPower()+m.getModifiedPower(); }
                public int getRate() { return (arm.getRate()+m.getRate())/2; }
                public int getHp() { return arm.getHp(); }
                public void setHp(int hp) { arm.setHp(hp); }
                public String getVerb() { return arm.getVerb(); }
                public String getAudio() { return arm.getAudio(); }
                public String getSkill() { return arm.getSkill(); }
                public Attack invoke(NHBot attacker, NHBot defender, Attack a) { arm.invoke(attacker, defender, a); m.invoke(attacker, defender, a); return null; }
                public void invoke(NHBot attacker, NHSpace s, Attack a) { arm.invoke(attacker, s, a); m.invoke(attacker, s, a); }
                public Item toItem() { return m.toItem(); }
                public String getColor() { return m.getColor(); }
                public String getModel() { return m.getModel(); }
                public Type getType() { return Type.missile; }
                public Stat[] getStats() { return arm.getStats(); }
            };
        }

        public Source getSource() {
            //return Grammar.noun((Item)_w);
            Item i = _w.toItem();
            if(i!=null) {
                return new Source(i);
            }
            else {
                return new Source("it");
            }
        }

        public NHBot getAttacker() {
            return _t;
        }

        public boolean isPhysical() {
            return true;
        }

        public Armament getWeapon() {
            return _w;
        }

        public Type getType() {
            return Type.missile;
        }

        public int getRadius() {
            return _radius;
        }

        public boolean affectsAttacker() {
            return false;
        }
    }
}
