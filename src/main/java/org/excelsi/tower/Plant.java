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
import org.excelsi.aether.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;


public abstract class Plant extends Parasite implements Nourishable {
    private long _cycle = Time.now();
    private long _creation = Time.now();
    private String _color = getNormalColor();
    private List<Fragment> _fragments = null;


    public void attacked(Armament a) {
        Context.c().n().print(getSpace(), "Goodbye "+getName()+"!");
        getSpace().removeParasite(this);
    }

    @Override public Architecture getArchitecture() {
        return Architecture.random;
    }

    public boolean isMoveable() {
        return false;
    }

    public String getModel() {
        return "a?";
    }

    public String getColor() {
        return _color;
    }

    public void setColor(String color) {
        if(!color.equals(_color)) {
            String oc = _color;
            _color = color;
            notifyAttr("color", oc, _color);
        }
    }

    public void addFragment(Fragment i) {
        if(_fragments==null) {
            _fragments = new ArrayList<Fragment>(1);
        }
        _fragments.add(i);
    }

    public boolean isDead() {
        return _creation<Time.now()-3L*getLifespan()/4L;
    }

    public boolean isDust() {
        return _creation<Time.now()-getLifespan();
    }

    public boolean isDying() {
        return _creation<Time.now()-getLifespan()/2L;
    }

    public boolean isAlive() {
        return _creation>=Time.now()-getLifespan()/2L;
    }

    protected long getLifespan() {
        return 700L;
    }

    public int getHeight() {
        return 0;
    }

    public boolean notice(NHBot b) {
        if(b.isPlayer()) {
            // could have planned this better...
            String adj = isDust()?"dead ":isDead()?"dying ":isDying()?"sickly ":"";
            Context.c().n().print(b, "There is "+Grammar.nonspecific(adj+getName())+" here.");
            //System.err.println("is dying: "+isDying());
            //System.err.println("is dead: "+isDead());
            //System.err.println("is dust: "+isDust());
        }
        return true;
    }

    public void trigger(NHBot b) {
    }

    public void update() {
        long t = Time.now();
        long max = (t-_cycle)/getSpawnTime();
        boolean watersupply = false;
        for(MSpace m:getSpace().surrounding()) {
            if(m instanceof Water) {
                watersupply = true;
                _creation = Math.min(_creation+10, t);
                break;
            }
        }
        for(int i=0;i<max;i++) {
            Item f = fruit();
            if(f!=null) {
                if(isDying()) {
                    break;
                }
                if(_fragments!=null) {
                    for(Fragment fr:_fragments) {
                        f.addFragment((Fragment)DefaultNHBot.deepCopy(fr));
                    }
                }
                NHSpace sp = null;
                if(Rand.d100(50)) {
                    List<MSpace> sur = Arrays.asList(getSpace().surrounding());
                    Collections.shuffle(sur);
                    for(MSpace m:sur) {
                        if(m!=null&&m.isWalkable()) {
                            sp = (NHSpace) m;
                            break;
                        }
                    }
                }
                if(sp==null) {
                    sp = getSpace();
                }
                sp.add(f);
            }
            _cycle = t;
            /*if(isDying()) {
                setColor("brown");
                return;
            }
            else*/ if(isDead()) {
                //setColor("gray");
                break;
            }
            else if(isDust()) {
                //getSpace().removeParasite(this);
                break;
            }
        }
        if(isAlive()) {
            //System.err.println("alive");
            setColor(getNormalColor());
        }
        else if(isDust()) {
            //System.err.println("remove");
            //getSpace().removeParasite(this);
            setColor("black");
        }
        else if(isDead()) {
            //System.err.println("dead");
            setColor("dark-gray");
        }
        else if(isDying()) {
            //System.err.println("dying");
            setColor("dark-brown");
        }
    }

    public void nourish(Solution s) {
        if(isDead()) {
            return;
        }
        int amount = 0;
        if(_fragments==null) {
            _fragments = new ArrayList<Fragment>(1);
        }
        for(Solution.Ingredient i:s.getIngredients()) {
            amount += 300*i.getAmount();
            if(!_fragments.contains(i.getInfliction())) {
                _fragments.add((Fragment)DefaultNHBot.deepCopy(i.getInfliction()));
            }
        }
        _creation += amount;
    }

    public void nourish(Comestible c) {
        int amount = Math.abs(c.getNutrition())-c.getConsumed();
        _creation += amount;
    }

    protected String getNormalColor() {
        return "dark-green";
    }

    protected int getSpawnTime() {
        return 200;
    }

    protected abstract Item fruit();
    protected abstract String getName();
}
