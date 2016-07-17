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


public class SmallStone extends Gem implements Missile, Useable {
    private static final Stat[] STATS = new Stat[]{Stat.st, Stat.st, Stat.ag};


    public SmallStone() {
        setClassIdentified(true);
    }

    public float getLevelWeight() { return 0f; }

    public boolean canCatalyze() {
        return false;
    }

    public float getShininess() {
        return 0f;
    }

    public void randomize() {
        super.randomize();
        setCount(Rand.om.nextInt(10)+1);
    }

    public String getName() {
        return "small stone";
    }

    public String getObscuredName() {
        return getName();
    }

    public String getColor() {
        return "gray";
    }

    public float getWeight() {
        return 0.05f;
    }

    public int score() {
        return 1;
    }

    public float getSize() {
        return 0.1f;
    }

    public int getPower() {
        return 4;
    }

    public int getModifiedPower() {
        switch(getStatus()) {
            case blessed:
                return 6;
            case uncursed:
                return 4;
            case cursed:
                return 2;
        }
        return 4;
    }

    public int getRate() {
        return 60;
    }

    public Type getType() {
        return Type.missile;
    }

    public String getVerb() {
        return "hit";
    }

    public String getSkill() {
        return Weapon.THROWN;
    }

    public Stat[] getStats() {
        return STATS;
    }

    public Attack invoke(NHBot attacker, NHBot defender, Attack a) {
        return null;
    }

    public void invoke(NHBot attacker, NHSpace s, Attack a) {
    }

    public Item toItem() {
        return this;
    }

    public boolean matches(Armament launcher) {
        return launcher instanceof Sling;
    }

    public boolean invokesIncidentally() {
        return false;
    }

    public int getSharpeningPower() {
        return 4;
    }

    public int getModifiedSharpeningPower() {
        switch(getStatus()) {
            case blessed:
                return getSharpeningPower()+getSharpeningPower()/2;
            case uncursed:
                return getSharpeningPower();
            case cursed:
                return -getSharpeningPower();
        }
        return getSharpeningPower();
    }

    public void use(final NHBot b) {
        boolean sh = false;
        for(Item i:b.getInventory().getItem()) {
            if(i instanceof Sharpenable && i.getHp()<i.getMaxHp()) {
                sh = true;
            }
        }
        if(!sh) {
            N.narrative().print(b, Grammar.start(b, "have")+" nothing that needs sharpening.");
            throw new ActionCancelledException();
        }
        final Item i = N.narrative().choose(b, new ItemConstraints(b.getInventory(),
            "sharpen", new InstanceofFilter(Sharpenable.class)), false);
        final int p = getModifiedSharpeningPower();
        N.narrative().print(b, Grammar.start(b, "sharpen")+" the "+i.getName()+"'s edge.");
        b.start(new ProgressiveAction() {
            int time = 15;
            public boolean iterate() {
                if(--time==0) {
                    i.setHp(Math.min(i.getHp()+p, i.getMaxHp()));
                    b.getInventory().consume(SmallStone.this);
                    if(p<0) {
                        N.narrative().print(b, "The blade's edge looks weaker...");
                    }
                    N.narrative().print(b, Grammar.key(b.getInventory(), i));
                    return false;
                }
                return true;
            }

            public int getInterruptRate() {
                return 100;
            }

            public void stopped() {
            }

            public void interrupted() {
                N.narrative().print(b, Grammar.start(b, "stop")+" sharpening.");
            }

            public String getExcuse() {
                return null;
            }
        });
    }
}
