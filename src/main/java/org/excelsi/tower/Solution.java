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


import org.excelsi.matrix.*;
import org.excelsi.aether.*;
import java.util.List;
import java.util.ArrayList;


public class Solution extends Parasite implements Drinkable {
    private static final long serialVersionUID = 1L;
    private List<Ingredient> _ingredients;
    private long _creation = -1;
    private String _color;
    private Basin _b;
    private int _amount;
    private String _verb = "evaporates";


    public Solution(Basin b) {
        _b = b;
    }

    public Solution(String color) {
        this(color, 0f);
    }

    public Solution(String color, float cycle) {
        _color = color;
        _creation = Time.now();
    }

    public void setEvaporationVerb(String verb) {
        _verb = verb;
    }

    public void update() {
        if(_b==null&&Time.now()>_creation+100) {
            if(Rand.d100(25)) {
                boolean nourished = false;
                for(Parasite p:getSpace().getParasites()) {
                    if(p instanceof Nourishable) {
                        ((Nourishable)p).nourish(this);
                        nourished = true;
                        break;
                    }
                }
                if(!nourished) {
                    N.narrative().print(owner(), "The solution "+_verb+".");
                }
                getSpace().removeParasite(this);
            }
        }
    }

    public String getModel() {
        return "i~";
    }

    public String getColor() {
        return _color;
    }

    public void setColor(String color) {
        _color = color;
    }

    /*
    public int add(Item i) {
        int ret = super.add(i);
        dissolve(i, null, this, true);
        return ret;
    }

    public int add(Item i, NHBot adder) {
        int ret = super.add(i, adder);
        dissolve(i, adder, this, true);
        return ret;
    }

    public void addLoot(Container loot) {
        if(loot!=null) {
            for(Item i:loot.getItem()) {
                dissolve(i, null, loot, true);
            }
            super.addLoot(loot);
        }
    }
    */

    public void immerse(NHBot b, Item i) {
        if(_ingredients!=null) {
            for(Ingredient ing:_ingredients) {
                Infliction inf = ing.getInfliction();
                if(_amount>1) {
                    inf = inf.deepCopy();
                }
                inf.apply(i, b);
            }
            if(--_amount==0) {
                _ingredients = null;
                if(getSpace()!=null) {
                    getSpace().removeParasite(this);
                }
            }
        }
    }

    public String getName() {
        return describe();
    }

    public int getAmount() {
        return _amount;
    }

    public void attacked(Armament a) {
    }

    public boolean isMoveable() {
        return false;
    }

    public int getHeight() {
        return 0;
    }

    public void trigger(NHBot b) {
    }

    public boolean notice(NHBot b) {
        N.narrative().print(owner(), "There is a shallow pool of "+describe()+" here.");
        return true;
    }

    public String describe() {
        StringBuilder ing1 = new StringBuilder();
        ArrayList<String> knowns = new ArrayList<String>();
        for(int i=0;i<getIngredients().size();i++) {
            String t = getIngredients().get(i).getInfliction().getText();
            if(getIngredients().get(i).getInfliction().isClassIdentified()) {
                knowns.add(t);
            }
            else {
                if(ing1.length()>0) {
                    ing1.append("-");
                }
                ing1.append(t);
            }
        }
        if(ing1.length()>0) {
            ing1.append(" ");
        }
        StringBuilder ing2 = new StringBuilder();
        if(knowns.size()==1) {
            ing2.append(knowns.get(0));
        }
        else if(knowns.size()==2) {
            ing2.append(knowns.get(0)+" and "+knowns.get(1));
        }
        else {
            for(int i=0;i<knowns.size();i++) {
                ing2.append(knowns.get(i));
                if(i<knowns.size()-1) {
                    ing2.append(", ");
                }
                if(i==knowns.size()-2) {
                    ing2.append("and ");
                }
                ing2.append(knowns.get(i));
            }
        }
        ing1.append("solution");
        if(ing2.length()>0) {
            ing1.append(" of ");
        }
        ing1.append(ing2);
        return ing1.toString();
    }

    public void add(Infliction c) {
        _creation = Time.now();
        if(_ingredients==null) {
            _ingredients = new ArrayList<Ingredient>(2);
        }
        _amount++;
        boolean found = false;
        for(Ingredient ing:_ingredients) {
            if(ing.getInfliction().equals(c)) {
                ing.setAmount(ing.getAmount()+1);
                found = true;
                break;
            }
        }
        if(!found) {
            _ingredients.add(new Ingredient(c));

            Basis trans = null;
            int totalAmt = 0;
            if(_ingredients.size()>1) {
                Class[] classes = new Class[_ingredients.size()];
                for(int idx=0;idx<classes.length;idx++) {
                    classes[idx] = _ingredients.get(idx).getInfliction().getClass();
                    totalAmt += _ingredients.get(idx).getAmount();
                }
                trans = Basis.transmute(classes);
            }
            if(trans!=null&&trans.classFor()!=null) {
                try {
                    _ingredients.clear();
                    final Infliction mog = (Infliction)trans.classFor().newInstance();
                    if(trans.getState()==Basis.State.explosive) {
                        _amount = 0;
                        N.narrative().print(owner(), "Poof! The mixture reacts violently!");
                        Armament arm;
                        if(mog instanceof Armament) {
                            arm = (Armament) mog;
                        }
                        else {
                            arm = new Explosion(mog);
                        }
                        mog.setClassIdentified(true);
                        final Armament ar = arm;
                        // enjoy your fun
                        NHEnvironment.getMechanics().resolve(new Inorganic("explosion of "+mog.getName()), owner(), Direction.north, new Attack() {
                            public Source getSource() { return new Source("the explosion"); }
                            public NHBot getAttacker() { return null; }
                            public boolean isPhysical() { return true; }
                            public int getRadius() { return 2; }
                            public Type getType() { return Type.ball; }
                            public Armament getWeapon() { return ar; }
                            public boolean affectsAttacker() { return false; }
                        }, null);
                    }
                    else {
                        Ingredient poof = new Ingredient(mog);
                        poof.setAmount(totalAmt);
                        _ingredients.add(poof);
                        if(poof.getInfliction().isClassIdentified()) {
                            N.narrative().print(owner(), "Poof! The mixture reacts.");
                        }
                        else {
                            N.narrative().print(owner(), "Poof! The mixture turns "+poof.getInfliction().getText()+".");
                        }
                    }
                }
                catch(InstantiationException e) {
                    throw new Error(e);
                }
                catch(IllegalAccessException e) {
                    throw new Error(e);
                }
            }
        }
        for(Ingredient ing:_ingredients) {
            ing.getInfliction().setDosage(ing.getAmount()/_amount);
        }
    }

    public List<Ingredient> getIngredients() {
        return _ingredients;
    }

    public void drink(NHBot b) {
        if(_ingredients!=null) {
            for(Ingredient ing:_ingredients) {
                Infliction inf = ing.getInfliction();
                if(_amount>1) {
                    inf = inf.deepCopy();
                }
                if(b.isPlayer()) {
                    inf.setClassIdentified(true);
                }
                inf.inflict(b);
            }
            if(--_amount==0) {
                _ingredients = null;
                if(getSpace()!=null) {
                    getSpace().removeParasite(this);
                }
            }
        }
    }

    protected void soak(boolean say) {
    }

    private NHSpace owner() {
        return _b!=null?_b:getSpace();
    }

    private void dissolve(Item i, NHBot dissolver, Container c, boolean all) {
        if(i instanceof Soluble) {
            ((Soluble)i).dissolve(dissolver, c, null, false, all);
        }
    }

    public static class Ingredient implements java.io.Serializable {
        private Infliction _i;
        private int _amount;


        public Ingredient(Infliction i) {
            _i = i;
            _amount = 1;
        }

        public void setAmount(int amt) {
            _amount = amt;
        }

        public int getAmount() {
            return _amount;
        }

        public Infliction getInfliction() {
            return _i;
        }
    }
}
