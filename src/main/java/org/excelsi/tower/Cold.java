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


public class Cold extends Physical implements Armament {
    private static final Modifier MOD = new Modifier();
    static {
        Basis.claim(new Basis(Basis.Type.cups, 6));
        MOD.setCandela(2);
        MOD.setCandelaColor(new float[]{0f, 0f, 1f, 1f});
    }

    private int _temp;
    private int _uses = 1;


    public Cold() {
        this(32);
    }

    public Cold(int temp) {
        _temp = temp;
    }

    public void setUses(int uses) {
        _uses = uses;
    }

    public int getUses() {
        return _uses;
    }

    public String getAudio() {
        return "cold";
    }

    public int getPower() {
        return 10;
    }

    public int getModifiedPower() {
        return getPower();
    }

    public Modifier getModifier() {
        return MOD;
    }

    public int getRate() {
        return 90;
    }

    public Type getType() {
        return Type.missile;
    }

    public int getHp() {
        return 1;
    }

    public void setHp(int hp) {
    }

    public String getVerb() {
        return "freeze";
    }

    public String getSkill() {
        return "wands";
    }

    public Stat[] getStats() {
        return null;
    }

    public boolean inflict(NHSpace s) {
        return false;
    }

    public Attack invoke(NHBot attacker, NHBot defender, Attack a) {
        inflict(defender);
        return null;
    }

    public void invoke(NHBot attacker, NHSpace s, Attack a) {
        if(s instanceof Water) {
            int time = Rand.om.nextInt(10)+40;
            if(getStatus()==Status.blessed) {
                time *= 4;
            }
            else if(getStatus()==Status.cursed) {
                time /= 4;
            }
            s = (NHSpace) s.replace(new Ice((Water)s, time));
        }
        if(s.numItems()>0) {
            resolve(null, s, s);
        }
    }

    public boolean inflict(NHBot defender) {
        if(defender.getInventory()!=null) {
            resolve(defender, null, defender.getInventory());
        }
        if(Rand.d100(151-defender.getModifiedEmpathy())) {
            defender.start(new Frozen(defender, true));
        }
        if(getOwner()!=null&&Rand.d100(102-defender.getModifiedEmpathy())) {
            --_uses;
            if(_uses==1) {
                N.narrative().print(defender, Grammar.first(Grammar.noun(getOwner()))+" begins to thaw.");
            }
            else if(_uses==0) {
                getOwner().removeFragment(this);
                N.narrative().print(defender, Grammar.first(Grammar.noun(getOwner()))+" thaws.");
            }
        }
        return true;
    }

    public Item toItem() {
        return null;
    }

    public String getColor() {
        return "white";
    }

    public String getModel() {
        return "-";
    }

    private void resolve(NHBot defender, NHSpace s, Container container) {
        for(Item i:container.getItem()) {
            if(i instanceof Freezable && Rand.d100(25)) {
                Freezable f = (Freezable) i;
                if(f.isFreezable()&&f.getFreezingTemperature()>=_temp) {
                    String st;
                    if(i.getCount()>1) {
                        if(defender!=null) {
                            st = "One of "+Grammar.possessive(defender)+" "+Grammar.indefinite(i);
                        }
                        else {
                            st = "One of "+Grammar.noun(i);
                        }
                    }
                    else {
                        if(defender!=null) {
                            st = Grammar.first(Grammar.possessive(defender,i));
                        }
                        else {
                            st = Grammar.first(Grammar.noun(i));
                        }
                    }
                    if(defender!=null) {
                        N.narrative().print(defender, st+" "+f.getFreezingPhrase()+"!");
                    }
                    else {
                        N.narrative().print(s, st+" "+f.getFreezingPhrase()+"!");
                    }
                    f.freeze(container);
                    //defender.getInventory().consume(i);
                }
                else {
                    if(i instanceof Reinforcable && ((Reinforcable)i).isReinforced()) {
                        if(defender!=null) {
                            N.narrative().print(defender, Grammar.first(Grammar.noun(i))+"'s reinforcing protects it!");
                        }
                        else {
                            N.narrative().print(s, Grammar.first(Grammar.noun(i))+"'s reinforcing protects it!");
                        }
                    }
                }
            }
        }
    }
}
