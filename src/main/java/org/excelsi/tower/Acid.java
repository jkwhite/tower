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


public class Acid extends Fire {
    static {
        Basis.claim(new Basis(Basis.Type.disks, 3));
    }

    public Acid() {
    }

    public int getPower() {
        return 15;
    }

    public int getModifiedPower() {
        return getPower();
    }

    public int getRate() {
        return 90;
    }

    public String getVerb() {
        return "sear";
    }

    public boolean inflict(NHBot defender) {
        boolean ret = resolve(defender);
        if(getOwner()!=null) {
           // &&Rand.d100(101-attacker.getModifiedEmpathy())) {
            setUses(getUses()-1);
            if(getUses()==0) {
                getOwner().removeFragment(this);
                N.narrative().print(defender, Grammar.first(Grammar.noun(getOwner()))+"'s acid drips away.");
            }
        }
        if(defender.getHp()<0) {
            defender.die("Dissolved in acid.");
        }
        return ret;
    }

    public Attack invoke(NHBot attacker, NHBot defender, Attack a) {
        resolve(defender);
        if(defender.getHp()<0) {
            defender.die("Dissolved in acid by "+Grammar.nonspecific(attacker));
        }
        return null;
    }

    public Item toItem() {
        return null;
    }

    //public String getName() {
        //return "flaming";
    //}

    public String getColor() {
        return "bright-green";
    }

    public String getModel() {
        return "-";
    }

    private boolean resolve(NHBot defender) {
        if(Rand.d100(101-defender.getModifiedEmpathy())) {
            N.narrative().print(defender, "Acid "+(Rand.om.nextBoolean()?"burns ":"burns ")+Grammar.noun(defender)+"!");
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
}
