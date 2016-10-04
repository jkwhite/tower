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
import static org.excelsi.tower.Solution.Ingredient;
import org.excelsi.aether.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


public class Basin extends Floor implements Immersion, Drinkable, Surface {
    private static final long serialVersionUID = 1L;
    private static final int MAX = 2;

    private Solution _solution = new Solution(this);


    public Basin() {
        this(Rand.d100(33));
    }
    
    public Basin(boolean water) {
        super("dark-gray");
        if(water) {
            WaterInfliction w = new WaterInfliction();
            int r = Rand.om.nextInt(100);
            if(r<10) {
                w.setStatus(Status.cursed);
            }
            else if(r<20) {
                w.setStatus(Status.blessed);
            }
            _solution.add(w);
        }
    }

    public boolean isDestroyable() {
        return true;
    }

    public void destroy() {
        if(isEmpty()) {
            N.narrative().print(this, "The basin shatters.");
            replace(new Floor());
        }
        else {
            N.narrative().print(this, "The basin shatters, spilling its contents.");
            ArrayList<MSpace> rep = new ArrayList<MSpace>(5);
            rep.add(replace(new Floor()));
            rep.addAll(Arrays.asList(this.cardinal()));
            for(MSpace r:rep) {
                if(r!=null&&r.isWalkable()) {
                    Solution w = new Solution("gray", 0f);
                    String color = null;
                    if(_solution.getIngredients()!=null) {
                        for(Ingredient i:_solution.getIngredients()) {
                            w.add(i.getInfliction());
                            if(color==null||Rand.d100(50)) {
                                if(i.getInfliction() instanceof Fermionic) {
                                    color = ((Fermionic)i.getInfliction()).getColor();
                                }
                            }
                        }
                    }
                    if(color!=null) {
                        w.setColor(color);
                    }
                    ((NHSpace)r).addParasite(w);
                }
            }
        }
    }

    public String getModel() {
        return "a#";
    }

    public int getDepth() {
        return -4;
    }

    public void drink(NHBot b) {
        if(_solution.getAmount()==0) {
            N.narrative().print(b, "The basin is empty.");
        }
        else {
            _solution.drink(b);
        }
    }

    public void immerse(NHBot b, Item i) {
        if(_solution.getAmount()==0) {
            N.narrative().print(b, "The basin is empty.");
        }
        else {
            _solution.immerse(b, i);
        }
    }

    public String getName() {
        return "basin";
    }

    public boolean isEmpty() {
        return _solution.getAmount()==0;
    }

    @Override public boolean look(final Context c, boolean nothing, boolean lootOnly) {
        boolean ret = super.look(c, nothing, lootOnly);
        if(!lootOnly) {
            if(_solution.getAmount()==0) {
                c.n().print(this, "There is an empty basin here.");
            }
            else {
                String ing = _solution.describe();
                String half = "";
                if(_solution.getAmount()<MAX) {
                    half = "partially ";
                }
                c.n().print(this, "There is a basin here, "+half+"filled with "+ing+".");
            }
            return true;
        }
        return ret;
    }

    public void pour(NHBot adder, Potion p) {
        if(_solution.getAmount()==MAX) {
            N.narrative().print(adder, "The basin is already full.");
            throw new ActionCancelledException();
        }
        else if(_solution.getAmount()+p.getCount()>MAX) {
            N.narrative().print(adder, "That many "+Grammar.pluralize(p.getCategory())+" won't fit.");
            throw new ActionCancelledException();
        }
        N.narrative().print(adder, Grammar.start(adder, "empty")+" "+Grammar.specific(p)+" into the basin.");
        ArrayList<Infliction> toRemove = new ArrayList<Infliction>();
        for(int idx=0;idx<p.getCount();idx++) {
            for(Infliction inf:p.getInflictions()) {
                _solution.add(inf.deepCopy());
                if(idx==0) {
                    toRemove.add(inf);
                }
            }
        }
        for(Infliction r:toRemove) {
            p.removeFragment(r);
        }
    }
}
