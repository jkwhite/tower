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


public abstract class Comestible extends Inflicter implements Dehydratable {
    private int _consumed = 0;
    private long _creation;


    public Comestible() {
        randomize();
    }

    public void randomize() {
        super.randomize();
        _creation = Time.now();
    }

    public final StackType getStackType() {
        return StackType.stackable;
    }

    public void dehydate(Pill into) {
        into.addFragment(new Nutrient(getModifiedNutrition()));
    }

    public final void addConsumed(int consumed) {
        if(consumed<0) {
            throw new IllegalArgumentException("consumed < 0: "+consumed);
        }
        _consumed = Math.min(_consumed+consumed, Math.abs(getNutrition()));
    }

    public final int getConsumed() {
        return _consumed;
    }

    public final void setConsumed(int consumed) {
        if(consumed<0) {
            throw new IllegalArgumentException("consumed < 0: "+consumed);
        }
        _consumed = consumed;
    }

    public final int getTemporalNutrition() {
        return Math.max(0, (int) ((float)getNutrition()-(Time.now()-_creation)*getDecayRate()));
    }

    public final float getDecay() {
        return 1f-Math.min(1000f, (Time.now()-_creation)*getDecayRate())/1000f;
    }

    public final int getModifiedNutrition() {
        //return getTemporalNutrition()-_consumed*(getTemporalNutrition()>0?1:-1);
        //return (int) (getTemporalNutrition()*((getNutrition()-_consumed)/(float)getNutrition()));
        return (int) (getDecay()*(getNutrition()-_consumed));
    }

    public int getNutrition() {
        return 1;
    }

    public float getDecayRate() {
        //return 0.01f;
        return 0.5f;
    }

    public int getFindRate() {
        return 40;
    }

    public float getShininess() {
        return 0f;
    }

    public String getColor() {
        return "brown";
    }

    public final String getModel() {
        return "%";
    }

    public final String getCategory() {
        return "comestible";
    }

    public final SlotType getSlotType() {
        return SlotType.none;
    }

    public Stat[] getStats() {
        return null;
    }

    public void invoke(NHBot b) {
        // don't automatically remove inflictions on food
        // because you shouldn't be able to eat half a
        // bad food to remove the infliction
        inflict(b, false);
    }

    public void update(Container c) {
        // allow a minimum of 100 turns before food vanishes
        //if(getTemporalNutrition()==0&&_creation+100<Time.now()) {
        if(getDecay()<=0f) {
            if(c instanceof NHSpace) {
                NHSpace s = (NHSpace) c;
                if(s.hasParasite(Nourishable.class)) {
                    for(Parasite p:s.getParasites()) {
                        if(p instanceof Nourishable) {
                            ((Nourishable)p).nourish(this);
                        }
                    }
                }
            }
            c.consume(this);
        }
    }

    public boolean equals(Object o) {
        return super.equals(o) && _consumed==((Comestible)o)._consumed;
    }

    public int hashCode() {
        return super.hashCode()^_consumed;
    }

    public String getName() {
        String n = super.getName();

        if(_consumed>0) {
            n = "partially-eaten "+n;
        }

        /*
        long nut = getTemporalNutrition();
        if(nut<getNutrition()/5) {
            n = "rancid "+n;
        }
        else if(nut<getNutrition()/4) {
            n = "rotten "+n;
        }
        else if(nut<getNutrition()/3) {
            n = "moldly "+n;
        }
        else if(nut<getNutrition()/2) {
            n = "stale "+n;
        }
        */
        float dec = getDecay();
        if(dec<=0.2f) {
            n = "rancid "+n;
        }
        else if(dec<0.4f) {
            n = "rotten "+n;
        }
        else if(dec<0.6f) {
            n = "moldy "+n;
        }
        else if(dec<0.8f) {
            n = "stale "+n;
        }
        return n;
    }

    public String getShortName() {
        return super.getName();
    }

    static class Nutrient extends Infliction {
        private int _nutrition;


        public Nutrient(int nut) {
            setClassIdentified(true);
            _nutrition = nut;
        }

        public boolean inflict(NHSpace s) {
            return false;
        }

        public boolean inflict(NHBot b) {
            int n = _nutrition;
            switch(getStatus()) {
                case cursed:
                    n = 1;
                    break;
                default:
            }
            b.setHunger(b.getHunger()-n);
            return false;
        }

        public GrammarType getPartOfSpeech() {
            return GrammarType.adjective;
        }
    }
}
