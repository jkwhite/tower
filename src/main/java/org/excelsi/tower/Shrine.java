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


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.Direction;
import org.excelsi.matrix.Actor;
import org.excelsi.aether.*;
import java.util.List;


/**
 * Shrines hold items, which they will bless, curse, or uncurse over time.
 */
public class Shrine extends Floor {
    public static final long TIME = 100;
    private static final long serialVersionUID = 1L;
    private Item _enshrined;
    private Status _orig;
    private long _etime;
    private boolean _forward;


    public Shrine() {
        super("white");
        _forward = Rand.d100(33);
    }

    public String getModel() {
        return "a]";
    }

    public boolean isTransparent() {
        return false;
    }

    public boolean isDestroyable() {
        return true;
    }

    public void destroy() {
        N.narrative().print(this, "The shrine crumbles!");
        final NHBot b = (NHBot) Actor.current();
        if(_enshrined!=null) {
            String name = _enshrined.getName();
            if(_enshrined.isUnique()&&_enshrined.isClassIdentified()) {
                name = "the "+name;
            }
            N.narrative().print(this, "Shards of "+name+" fill the air!");
            Armament arm;
            if(_enshrined instanceof Armament) {
                arm = (Armament) _enshrined;
            }
            else {
                arm = new Explosion(_enshrined);
            }
            final Armament ar = arm;
            // enjoy your fun
            NHEnvironment.getMechanics().resolve(Actor.context(), new Inorganic("exploding "+_enshrined.getName()), this, Direction.north, new Attack() {
                public Source getSource() { return new Source("the "+_enshrined.getName()+" explosion"); }
                public NHBot getAttacker() { return b; }
                public boolean isPhysical() { return true; }
                public int getRadius() { return 3; }
                public Type getType() { return Type.ball; }
                public Armament getWeapon() { return ar; }
                public boolean affectsAttacker() { return false; }
            }, null);
        }
        else {
            if(b!=null) {
                int amt = Rand.om.nextInt(3)+1;
                List<Item> its = b.getInventory().randomized();
                while(amt-->0&&its.size()>0) {
                    Item i = its.get(Rand.om.nextInt(its.size()));
                    its.remove(i); // don't affect same item twice
                    if(_forward) {
                        i.setStatus(i.getStatus().better());
                    }
                    else {
                        i.setStatus(i.getStatus().worse());
                    }
                }
                if(_forward) {
                    N.narrative().print(b, Grammar.start(b, "feel")+" a deep sense of relief.");
                }
                else {
                    N.narrative().print(b, Grammar.start(b, "feel")+" a deep sense of forboding.");
                }
            }
        }
        replace(new Floor());
    }

    public boolean look(final Context c, boolean nothing, boolean lootOnly) {
        boolean ret = super.look(c, nothing, lootOnly);
        if(!lootOnly) {
            if(_enshrined==null) {
                c.n().print(this, "There is a shrine here.");
            }
            else {
                c.n().print(this, "There is a shrine here, containing "+Grammar.nonspecific(_enshrined)+".");
            }
            return true;
        }
        return ret;
    }

    public int add(Item i, NHBot adder) {
        if(_enshrined==null) {
            if(N.narrative().confirm(adder, "Put "+Grammar.specific(i)+" into the shrine?")) {
                enshrine(i, adder);
                return 0;
            }
        }
        return super.add(i, adder);
    }

    public boolean pickup(NHBot b) {
        if(_enshrined!=null) {
            if(N.narrative().confirm(b, "Remove "+Grammar.specific(_enshrined)+" from the shrine?")) {
                _enshrined.setStatus(convert());
                b.getInventory().add(_enshrined);
                if(b.isPlayer()) {
                    N.narrative().print(b, Grammar.key(b.getInventory(), _enshrined));
                }
                _enshrined = null;
                if(Rand.d100(50)) {
                    N.narrative().print(b, "The shrine crumbles!");
                    Floor f = new Floor();
                    replace(f);
                    Rock r = new Rock();
                    r.setCount(2);
                    f.add(r);
                    SmallStone ss = new SmallStone();
                    ss.setCount(Rand.om.nextInt(3)+3);
                    f.add(ss);
                }
                return true;
            }
            throw new ActionCancelledException();
        }
        return false;
    }

    public boolean isEmpty() {
        return _enshrined==null;
    }

    public void enshrine(Item i, NHBot adder) {
        if(_enshrined!=null) {
            throw new IllegalStateException("cannot enshrine "+i+": already have "+_enshrined);
        }
        _enshrined = i;
        _etime = Time.now();
        _orig = _enshrined.getStatus();
        N.narrative().print(adder, Grammar.start(adder, "put")+" "+Grammar.specific(i)+" into the shrine.");
        if(!adder.isBlind()) {
            N.narrative().print(adder, "The shrine glows with a "+i.getStatus().getColor()+" light.");
            _enshrined.setStatusIdentified(true);
        }
    }

    public void update() {
        if(_enshrined!=null) {
            if((Time.now()-_etime)%TIME==0) {
                N.narrative().print(this, "The shrine emits a "+(_forward?"short":"long")+" high-pitched tone.");
            }
            _enshrined.setStatus(convert());
        }
    }

    private Status convert() {
        Status s = _orig;
        for(int cyc=(int)Math.floor((Time.now()-_etime)/TIME);cyc>0;cyc--) {
            switch(s) {
                case cursed:
                    s = _forward?Status.uncursed:Status.blessed;
                    break;
                case uncursed:
                    s = _forward?Status.blessed:Status.cursed;
                    break;
                case blessed:
                    s = _forward?Status.cursed:Status.uncursed;
                    break;
            }
        }
        return s;
    }
}
