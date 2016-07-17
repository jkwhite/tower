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


public abstract class Corpse extends Comestible implements Combustible {
    private transient NHBot _spirit;
    private float _size;
    private float _weight;
    private int _nutrition;


    public Corpse() {
    }

    public Corpse(Infliction i) {
        addFragment(i);
    }

    public int score() {
        return 0;
    }

    public float getSize() {
        return _size;
    }

    public void setSize(float size) {
        _size = size;
    }

    public String getColor() {
        return "gray";
    }

    public float getDecayRate() {
        //return 0.02f;
        return 1f;
    }

    public boolean isCombustible() {
        return true;
    }

    public int getCombustionTemperature() {
        return 200;
    }

    public String getCombustionPhrase() {
        return "cooks";
    }

    public void combust(Container c) {
        c.consume(this);
        Steak s = new Steak();
        s.setStatus(getStatus());
        String name = getName();
        if(name.startsWith("corpse of ")) {
            name = name.substring("corpse of ".length());
        }
        else {
            name = name.substring(0, name.lastIndexOf(" corpse"));
        }
        s.setName(name+" steak");
        s.setConsumed(getConsumed());
        for(Fragment f:getFragments()) {
            s.addFragment((Fragment)DefaultNHBot.deepCopy(f));
        }
        c.add(s);
    }

    public void setSpirit(NHBot spirit) {
        _spirit = spirit;
        _weight = spirit.getConstitution()/2;
        _size = 5f;
        int max = Hunger.RATE;
        switch(spirit.getSize()) {
            case small:
                _size = 2f;
                max /= 2;
                break;
            case large:
                _size = 10f;
                max *= 2;
                break;
            case huge:
                _size = 20f;
                max *= 4;
                break;
        }
        _nutrition = Math.max(max, spirit.getMaxHp());
    }

    public NHBot getSpirit() {
        return _spirit;
    }

    public float getWeight() {
        return _weight;
    }

    public int getNutrition() {
        return getStatus()==Status.blessed?Math.abs(_nutrition):_nutrition;
    }

    public void setNutrition(int nut) {
        _nutrition = nut;
    }

    public void invoke(NHBot b) {
        super.invoke(b);
        // humanoids are assumed to have problems digesting corpses
        if(b.getForm() instanceof Humanoid) {
            if(Rand.d100(101-b.getModifiedConstitution()/2)) {
                b.addAffliction(new Delay(new Nauseous(Rand.om.nextInt(25)+10), 5+Rand.om.nextInt(15)));
            }
            if(b.isAfflictedBy(Lycanthropy.NAME)) {
                if(Rand.d100(b.getSelfDiscipline())) {
                    b.removeAffliction(Lycanthropy.NAME);
                    N.narrative().print(b, Grammar.start(b, "feel")+" "+Grammar.possessive(b)+" lust is sated.");
                }
            }
        }
    }
}
