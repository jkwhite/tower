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


public abstract class Scroll extends VariegatedItem implements Combustible, Laminatable, Soluble {
    private static final Stat[] STATS = new Stat[]{Stat.me, Stat.re};
    private boolean _laminated;


    static {
        variegate("scroll", new String[]{"LORE MIP SUM", "BLOT", "CIRRUS URSA",
            "UL ANBA TOR", "UN RUNAM", "HRONIR", "READ ME", "OVED WENGA MUIRTTY",
            "AWD POK", "OLAPOLYOKE", "THEGNON PSIWIG", "ESTROM YANDEL THOTH WELF",
            "RENLILULEKH", "GAWINATHET THIME CHRONOSTAY", "DRETH MATAMOIL",
            "VLEDU FLESHISH IFF", "ANANG ANDI", "GHALIN SPIRSAPE", "WONSLID ESWOLM",
            "WANGUIRUS GAMERAM", "IRIOMOTEYAMANEKO", "OYAM FLOR", "ECH ACHREB SOLDEG"});
        Extended.addCommand("scribe", new Scribe());
    }


    public boolean accept(NHSpace s) {
        return true;
    }

    public final String getCreationSkill() {
        return "scribing";
    }

    public Maneuver getDifficulty() {
        return Maneuver.medium;
    }

    public boolean dissolve(NHBot b, Container c, Item solution, boolean deliberate, boolean all) {
        int count = getCount();
        boolean contained = b!=null&&b.getInventory().contains(this);
        if(!all) {
            count = 1;
        }
        if(!isLaminated()) {
            if(all) {
                //c.remove(this);
                setCount(0);
            }
            else {
                //c.consume(this);
                setCount(0);
            }
            if(solution==null) {
                c.consume(this);
            }
            if(deliberate||!contained) {
                if(b!=null) {
                    if(count==1) {
                        N.narrative().print(b, "The ink on the "+getName()+" dissolves.");
                    }
                    else {
                        N.narrative().print(b, "The ink on the "+Grammar.pluralize(getName())+" dissolves.");
                    }
                }
            }
            else {
                if(count==1) {
                    N.narrative().print(b, Grammar.first(Grammar.possessive(b, this))+" "+(count>1?"get":"gets")+" wet!");
                }
                else {
                    N.narrative().print(b, Grammar.first(Grammar.possessive(b))+" "+Grammar.pluralize(getName())+" "+(count>1?"get":"gets")+" wet!");
                }
            }
            BlankParchment bp = new BlankParchment();
            bp.setCount(count);
            bp.setStatus(getStatus());
            if(solution!=null) {
                switch(solution.getStatus()) {
                    case blessed:
                        bp.setStatus(bp.getStatus().better());
                        break;
                    case cursed:
                        bp.setStatus(bp.getStatus().worse());
                        break;
                }
            }
            c.add(bp);
            if(deliberate&&contained) {
                N.narrative().print(b, Grammar.key(b.getInventory(), bp));
            }
            return true;
        }
        else {
            Status newStatus = null;
            if(solution!=null) {
                for(Fragment f:solution.getFragments()) {
                    if(f instanceof WaterInfliction) {
                        // special case for bless/curse
                        newStatus = ((WaterInfliction)f).getStatus();
                        if(newStatus==Status.uncursed) {
                            newStatus = null;
                        }
                        break;
                    }
                }
            }
            if(deliberate&&newStatus==null) {
                N.narrative().print(b, "The "+getName()+"'s lamination protects it.");
            }
            if(newStatus!=null) {
                if(newStatus==Status.blessed&&getStatus()==Status.cursed) {
                    newStatus = Status.uncursed;
                }
                else if(newStatus==Status.cursed&&getStatus()==Status.blessed) {
                    newStatus = Status.uncursed;
                }
                setStatus(newStatus);
                N.narrative().print(b, "The "+getName()+" glows "+newStatus.getColor()+".");
            }
            return newStatus!=null;
        }
    }

    public boolean isCombustible() {
        return true;
    }

    public int getCombustionTemperature() {
        return isLaminated()?800:451;
    }

    public final String getCombustionPhrase() {
        return "catches fire and burns";
    }

    public void combust(Container c) {
        c.consume(this);
    }

    public String getName() {
        if(isIdentified()||isClassIdentified()) {
            return super.getName();
        }
        else {
            return getCategory()+" labeled '"+getVariation()+"'";
        }
    }

    public String getObscuredName() {
        return getCategory();
    }

    public final void setLaminated(boolean laminated) {
        if(_laminated!=laminated) {
            if(laminated) {
                addFragment(new Laminated());
            }
            else {
                removeFragment(Laminated.NAME);
            }
            _laminated = laminated;
        }
    }

    public Stat[] getStats() {
        return STATS;
    }

    public final boolean isLaminated() {
        return _laminated;
    }

    public final StackType getStackType() {
        return StackType.stackable;
    }

    public final SlotType getSlotType() {
        return SlotType.none;
    }

    public String getColor() {
        return "white";
    }

    public final String getModel() {
        return "?";
    }

    public final String getCategory() {
        return "scroll";
    }

    public float getSize() {
        return 0.7f;
    }

    public float getWeight() {
        return 0.1f;
    }

    public void invoke(NHBot b) {
        b.getInventory().consume(this);
    }

    public boolean equals(Object o) {
        return super.equals(o) && _laminated == ((Scroll)o)._laminated;
    }

    public int hashCode() {
        return super.hashCode()^(_laminated?1:0);
    }

    public void sacrifice(NHBot b, Altar a) {
        N.narrative().print(b, "The altar hungrily consumes your sacrifice.");
    }

    protected final boolean checkSacrifice(NHBot b) {
        MSpace[] sur = b.getEnvironment().getMSpace().surrounding();
        for(int i=0;i<sur.length;i++) {
            if(sur[i] instanceof Altar && ! sur[i].isOccupied()) {
                Altar a = (Altar) sur[i];
                for(Item it:a.getItem()) {
                    if(it instanceof Corpse) {
                        sacrifice(b, a);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static class Scribe extends DefaultNHBotAction implements SpaceAction {
        public String getDescription() {
            return "Write a scroll.";
        }

        public boolean isPerformable(NHBot b) {
            return b.getSkill("scribing")>0;
        }

        public void perform() {
            final Item parchment = N.narrative().choose(getBot(), new ItemConstraints(getBot().getInventory(), "enscribe",
                        new InstanceofFilter(BlankParchment.class)), false);
            final Catalyst catalyst = (Catalyst) N.narrative().choose(getBot(), new ItemConstraints(getBot().getInventory(), "use as a catalyst",
                        new ItemFilter() {
                            public boolean accept(Item item, NHBot bot) {
                                return item instanceof Catalyst;
                            }
                        }), false);
            if(!catalyst.supports(parchment)) {
                N.narrative().print(getBot(), "You don't think those go together...");
                throw new ActionCancelledException();
            }
            getBot().start(new ProgressiveAction() {
                int time = 8;
                public int getInterruptRate() { return 100; }
                public void stopped() {}
                public void interrupted() {
                    N.narrative().print(getBot(), "You stop writing.");
                }
                public String getExcuse() { return "writing"; }
                public boolean iterate() {
                    if(--time==0) {
                        int skill = getBot().getSkill("scribing");
                        boolean success = Rand.d100(skill);
                        getBot().getInventory().consume(parchment);
                        N.narrative().print(getBot(), "You finish writing.");
                        //N.narrative().more();
                        if(success) {
                            Item scroll = catalyst.catalyze(parchment, getBot().getInventory());
                            if(catalyst.isUnstable()) {
                                N.narrative().printf(getBot(), "The scroll reacts spontaneously!");
                                scroll.invoke(getBot());
                            }
                            else {
                                getBot().getInventory().add(scroll);
                            }
                            N.narrative().print(getBot(), Grammar.key(getBot().getInventory(), scroll));
                        }
                        else {
                            N.narrative().print(getBot(), "This scroll is rather hard to read...");
                            IllegibleScroll is = new IllegibleScroll();
                            is.setStatus(catalyst.getStatus());
                            getBot().getInventory().add(is);
                            //N.narrative().more();
                            N.narrative().print(getBot(), Grammar.key(getBot().getInventory(), is));
                        }
                        getBot().skillUp("scribing");
                    }
                    return time!=0;
                }
            });
        }
    }
}
