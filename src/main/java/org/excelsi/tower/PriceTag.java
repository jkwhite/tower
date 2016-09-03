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
import org.excelsi.matrix.Actor;


public class PriceTag implements Fragment {
    public static final String NAME = "price tag";
    private Item _owner;
    private int _amount;
    private int _discount = -1;


    public PriceTag(Item item) {
        this((Rand.om.nextInt(2)+5)*(item.getCount()*item.score()));
    }

    public PriceTag(int amount) {
        _amount = Math.max(1,amount);
    }

    public boolean intercepts(Attack a) {
        return false;
    }

    public void apply(Fragment f) {
        if(f instanceof WaterInfliction) {
            NHBot b = (NHBot) Actor.current();
            if(b!=null) {
                b.start(new DisruptAction(b));
            }
            else {
                _owner.removeFragment(this);
                //N.narrative().println(_b, "The "+_owner.getName()+"'s RF tag fizzles.");
            }
        }
    }

    @Override public Performable intercept(NHBot attacker, NHBot defender, Attack a) {
        return null;
    }

    public void setDiscount(int percent) {
        _discount = percent;
    }

    public boolean isDiscounted() {
        return _discount!=-1;
    }

    public int getAmount() {
        return _amount;
    }

    public void setAmount(int amount) {
        _amount = amount;
    }

    public int getModifiedAmount() {
        return _amount*(100-_discount)/100;
    }

    public Modifier getModifier() {
        return new Modifier();
    }

    public int getPowerModifier() {
        return 0;
    }

    public GrammarType getPartOfSpeech() {
        return GrammarType.phrase;
    }

    public String getText() {
        return "($"+_amount+")";
    }

    public void setIdentified(boolean id) {
    }

    public boolean isIdentified() {
        return true;
    }

    public void setClassIdentified(boolean id) {
    }

    public boolean isClassIdentified() {
        return true;
    }

    public String getName() {
        return NAME;
    }

    public int getOccurrence() {
        return 0;
    }

    public void setOwner(Item owner) {
        _owner = owner;
    }

    public Item getOwner() {
        return _owner;
    }

    public class DisruptAction implements ProgressiveAction {
        private NHBot _b;


        public DisruptAction(NHBot b) {
            _b = b;
        }

        public boolean iterate() {
            getOwner().removeFragment(PriceTag.this);
            N.narrative().print(_b, "The "+getOwner().getName()+"'s RF tag fizzles.");
            return false;
        }

        public Item getItem() {
            return getOwner();
        }

        public int getInterruptRate() {
            return 100;
        }

        public void stopped() {
        }

        public void interrupted() {
        }

        public String getExcuse() {
            return null;
        }
    }
}
