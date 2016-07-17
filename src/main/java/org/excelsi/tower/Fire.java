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


public class Fire extends Physical implements Armament {
    private static final Modifier MOD = new Modifier();
    static {
        Basis.claim(new Basis(Basis.Type.wands, 1));
        MOD.setCandela(4);
        MOD.setCandelaColor(new float[]{1f, 0f, 0f, 1f});
    }

    private int _temp;
    private int _uses = 1;


    public Fire() {
        this(451);
    }

    public Fire(int temp) {
        _temp = temp;
    }

    public int getPower() {
        return _temp/15;
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

    public String getAudio() {
        return "fire";
    }

    public void setHp(int hp) {
    }

    public int getUses() {
        return _uses;
    }

    public void setUses(int uses) {
        _uses = uses;
    }

    public String getVerb() {
        return "scorch";
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

    public boolean inflict(NHBot defender) {
        return inflict(defender, null);
    }

    public boolean inflict(NHBot defender, Source s) {
        if(defender instanceof Elemental) {
            if(((Elemental)defender).getElements().contains(Element.fire)) {
                return true;
            }
        }
        if(defender.modifier().get(Element.fire.toString())>0) {
            return true;
        }
        boolean ret = resolve(defender);
        if(getOwner()!=null&&Rand.d100(102-defender.getModifiedEmpathy())) {
            --_uses;
            if(_uses==1) {
                N.narrative().print(defender, Grammar.first(Grammar.noun(getOwner()))+"'s flames flicker.");
            }
            else if(_uses==0) {
                getOwner().removeFragment(this);
                N.narrative().print(defender, Grammar.first(Grammar.noun(getOwner()))+"'s flames extinguish in trails of smoke.");
            }
        }
        if(defender.getHp()<=0) {
            if(s==null) {
                NHBot b = (NHBot) Actor.current();
                if(b!=defender) {
                    s = new Source(b);
                }
            }
            defender.die("Burned to a crisp"+(s!=null?(" by "+s):""));
        }
        return ret;
    }

    public Attack invoke(NHBot attacker, NHBot defender, Attack a) {
        resolve(defender);
        if(defender.getHp()<=0) {
            defender.die("Burned to a crisp by "+Grammar.nonspecific(attacker));
        }
        return null;
    }

    public void invoke(NHBot attacker, NHSpace s, Attack a) {
        if(s instanceof Ice) {
            ((Ice)s).liquify();
        }
        if(s.numItems()>0 && !(s instanceof Water)) {
            resolve(null, s, s);
        }
        if(s instanceof Combustible) {
            ignite((Combustible)s, _temp);
        }
    }

    static void ignite(Combustible c, int temp) {
        if(c.isCombustible()&&c.getCombustionTemperature()<=temp
            && Rand.d100(25)) {
            c.combust(null);
        }
    }

    public Item toItem() {
        return null;
    }

    //public String getName() {
        //return "flaming";
    //}

    public String getColor() {
        return "bright-red";
    }

    public String getModel() {
        return "-";
    }

    private boolean resolve(NHBot defender) {
        if(Rand.d100(151-defender.getModifiedEmpathy())) {
            N.narrative().print(defender, "Fire "+(Rand.om.nextBoolean()?"sears ":"scorches ")+Grammar.noun(defender)+"!");
            int power = getPower();
            if(getOwner() instanceof Armament) {
                power = (power + ((Armament)getOwner()).getPower())/2;
            }
            defender.setHp(Math.max(0, defender.getHp()-Math.max(1, (int) (power+Rand.om.nextGaussian()*power))));
        }
        if(defender.getInventory()!=null) {
            resolve(defender, null, defender.getInventory());
        }
        if(defender.getHp()<=0&&!defender.isPlayer()) {
            //N.narrative().print(defender, Grammar.start(defender)+" "+Grammar.conjugate(defender, "die")+".");
        }
        return true;
    }

    protected void resolve(NHBot defender, NHSpace s, Container container) {
        for(Item i:container.getItem()) {
            if(i instanceof Combustible && Rand.d100(25)) {
                Combustible c = (Combustible) i;
                if(c.isCombustible()&&c.getCombustionTemperature()<=_temp) {
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
                            st = Grammar.first(Grammar.possessive(defender, i));
                        }
                        else {
                            st = Grammar.first(Grammar.noun(i));
                        }
                    }
                    if(defender!=null) {
                        N.narrative().print(defender, st+" "+c.getCombustionPhrase()+"!");
                    }
                    else {
                        N.narrative().print(s, st+" "+c.getCombustionPhrase()+"!");
                    }
                    c.combust(container);
                    //defender.getInventory().consume(i);
                }
                else {
                    if(i instanceof Laminatable && ((Laminatable)i).isLaminated()) {
                        if(defender!=null) {
                            N.narrative().print(defender, Grammar.first(Grammar.noun(i))+"'s lamination protects it!");
                        }
                        else {
                            N.narrative().print(s, Grammar.first(Grammar.noun(i))+"'s lamination protects it!");
                        }
                    }
                }
            }
        }
    }
}
