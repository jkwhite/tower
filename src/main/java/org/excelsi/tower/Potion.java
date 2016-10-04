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
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.excelsi.matrix.Direction;


/**
 * Potions are bottles possibly carrying inflictions in a liquid form.
 */
public class Potion extends Inflicter implements Combustible, Freezable, Reinforcable, Immersion {
    static {
        Extended.addCommand("pour", new Pour());
        Extended.addCommand("dip", new Dip());
    }

    public Potion() {
    }

    public Potion(Infliction i) {
        super(i);
    }

    public String getObscuredName() {
        return getCategory();
    }

    public boolean isEmpty() {
        return getInflictions().size()==0;
    }

    public boolean isFragile() {
        return true;
    }

    public float getShininess() {
        return 3f;
    }

    public boolean invokesIncidentally() {
        return true;
    }

    public void immerse(NHBot b, Item i) {
        Potion t = this;
        if(isEmpty()) {
            N.narrative().print(b, "This potion is empty.");
            return;
        }
        if(i instanceof Soluble) {
            List<Infliction> infs = getInflictions();
            if(infs.size()>0&&!infs.get(0).isReplaceable()) {
                N.narrative().print(b, "Nothing happens.");
            }
            else {
                if(getCount()>1) {
                    t = (Potion) b.getInventory().split(this);
                }
                boolean consumed = ((Soluble)i).dissolve(b, b.getInventory(), t, true, false);
                if(t!=this) {
                    if(!consumed) {
                        b.getInventory().add(t);
                    }
                }
                else {
                    if(consumed) {
                        b.getInventory().consume(this);
                    }
                }
            }
        }
        else {
            if(getCount()>1) {
                t = (Potion) b.getInventory().split(this);
            }
            boolean mod = false;
            for(Infliction inf:t.getInflictions()) {
                if(inf.apply(i, b)) {
                    t.removeFragment(inf);
                    mod = true;
                }
            }
            if(!mod) {
                N.narrative().print(b, "Nothing happens.");
            }
            if(t!=this) {
                b.getInventory().add(t);
            }
        }
    }

    public final String getCombustionPhrase() {
        return "boils and explodes";
    }

    public final String getFreezingPhrase() {
        return "freezes and shatters";
    }

    public boolean isCombustible() {
        return !isEmpty();
    }

    public void combust(Container c) {
        c.consume(this);
    }

    public void freeze(Container c) {
        c.consume(this);
    }

    public boolean isFreezable() {
        return !isEmpty();
    }

    public int getCombustionTemperature() {
        return isReinforced()?300:220;
    }

    public int getFreezingTemperature() {
        return isReinforced()?-100:32;
    }

    public void reinforce(ReinforcingMaterial m) {
        if(isReinforced()) {
            throw new IllegalStateException(this+" is already reinforced");
        }
        addFragment(new Reinforced());
    }

    public boolean isReinforced() {
        return hasFragment(Reinforced.class);
    }

    public String getName() {
        if(getInflictions().size()>0) {
            return super.getName();
        }
        else {
            return "empty potion";
        }
    }

    public final StackType getStackType() {
        return StackType.stackable;
    }

    public final SlotType getSlotType() {
        return SlotType.none;
    }

    public Stat[] getStats() {
        return null;
    }

    public String getColor() {
        if(getInflictions().size()>0) {
            return ((Variegated)getInflictions().get(0)).getColor();
        }
        else {
            return "gray";
        }
    }

    public final String getCategory() {
        return "potion";
    }

    public float getSize() {
        return 0.5f;
    }

    public float getWeight() {
        return isEmpty()?0.05f:0.3f;
    }

    public final String getModel() {
        return "!";
    }

    public void invoke(NHBot b) {
        Potion t = this;
        boolean readd = false;
        if(b.getInventory()!=null&&b.getInventory().contains(this)) {
            if(getCount()>1) {
                t = (Potion) b.getInventory().split(this);
                readd = true;
            }
            else {
                t = this;
                b.getInventory().consume(this);
                readd = true;
            }
        }
        if(getInflictions().size()>0) {
            t.inflict(b, true);
        }
        if(readd) {
            b.getInventory().add(t);
        }
    }

    public int getFindRate() {
        return getFragments().size()>0?100:10;
    }

    public static class Pour extends ItemAction implements SpaceAction {
        public Pour() {
            super("pour", new ItemFilter() {
                public boolean accept(Item i, NHBot b) {
                    return i instanceof Potion && !((Potion)i).isEmpty();
                }
            });
        }

        public String getDescription() {
            return "Empties a potion's contents into a container or the floor.";
        }

        public boolean isPerformable(NHBot b) {
            return b.getEnvironment().getMSpace() instanceof Surface;
        }

        @Override protected void act(final Context c) {
            Direction d = c.n().direct(c.actor(), "Where?");
            NHSpace s = (NHSpace) c.actor().getEnvironment().getMSpace().move(d);
            Potion p = (Potion) getItem();
            setItem(null);
            p = (Potion) c.actor().getInventory().split(p);
            if(s instanceof Surface) {
                ((Surface)s).pour(c.actor(), p);
                c.actor().getInventory().add(p);
            }
            else {
                if(s.isTransparent()) {
                    Solution sol = null;
                    boolean found = false;
                    for(Parasite pa:s.getParasites()) {
                        if(pa instanceof Solution) {
                            found = true;
                            sol = (Solution) pa;
                            break;
                        }
                    }
                    if(sol==null) {
                        sol = new Solution("gray");
                    }
                    N.narrative().print(c.actor(), Grammar.start(c.actor(), "pour")+" out the "+Grammar.specific(p)+".");
                    ArrayList<Infliction> toRemove = new ArrayList<Infliction>();
                    for(int idx=0;idx<p.getCount();idx++) {
                        for(Infliction inf:p.getInflictions()) {
                            sol.add(inf.deepCopy());
                            if(inf instanceof Fermionic) {
                                sol.setColor(((Fermionic)inf).getColor());
                            }
                            if(idx==0) {
                                toRemove.add(inf);
                            }
                        }
                    }
                    for(Infliction r:toRemove) {
                        p.removeFragment(r);
                    }
                    if(!found) {
                        s.addParasite(sol);
                    }
                    c.actor().getInventory().add(p);
                }
                else {
                    c.n().print(c.actor(), "That would prove difficult.");
                    // because it was split out above
                    c.actor().getInventory().add(p);
                    throw new ActionCancelledException();
                }
            }
        }
    }
}
