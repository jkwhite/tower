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


public class TestPoisoned extends junit.framework.TestCase {
    public void testPoisonous() {
        Rand.load();
        Poisonous p = new Poisonous(Poisons.random());
        Watermelon w = new Watermelon();
        w.addFragment(p);
        Consume c = new Consume(w);
        Patsy b = new Patsy();
        b.getInventory().add(w);
        b.setMaxHp(100);
        b.setHp(100);
        b.setConstitution(10);
        c.setBot(b);
        c.perform();
        int tries = 0;
        while(b.isOccupied()) {
            b.tick();
            if(++tries==1000) {
                fail("shouldn't take that long");
            }
        }
        assertTrue("didn't get poisoned", b.getAfflictions().get(0) instanceof Poisoned);
        assertFalse("shouldn't die", b.isDead());
        assertEquals("grammar", Fragment.GrammarType.adjective, p.getPartOfSpeech());
    }

    public void testLuck() {
        Patsy p = new Patsy();
        p.addAffliction(new Poisoned(Poisons.luck));
        Rand.load();
        p.tick();
        assertTrue("didn't kill", p.isDead());
    }

    public void testNervous() {
        poison(Poisons.nervous);
    }

    public void testRespiratory() {
        poison(Poisons.respiratory);
    }

    public void testCirculatory() {
        poison(Poisons.circulatory);
    }

    public void testRandom() {
        Poisons p;
        int i=0;
        do {
            p = Poisons.random();
        } while(p==Poisons.luck||++i<20);
        poison(p);
    }

    public void testCompound() {
        Poisoned po = new Poisoned(Poisons.circulatory, 20);
        Patsy p = new Patsy();
        p.setStrength(50);
        p.setConstitution(50);
        p.setAgility(50);
        p.setQuickness(50);
        p.setMaxHp(1000);
        p.setHp(1000);
        p.addAffliction(po);
        p.tick();
        p.addAffliction(new Poisoned(Poisons.nervous));
        assertEquals("shouldn't have extra affliction", 1, p.getAfflictions().size());
        p.tick();
        int times = 0;
        while(p.isAfflictedBy(Poisoned.NAME)) {
            p.tick();
            if(++times>100) {
                fail(p+" should have cured by now");
            }
        }
    }

    private void poison(Poisons poi) {
        Poisoned po = new Poisoned(poi);
        Patsy p = new Patsy();
        p.setStrength(50);
        p.setConstitution(50);
        p.setAgility(50);
        p.setQuickness(50);
        p.setMaxHp(1000);
        p.setHp(1000);
        p.addAffliction(po);
        int times = 0;
        while(p.isAfflictedBy(Poisoned.NAME)) {
            p.tick();
            if(++times>100) {
                fail(p+" should have cured by now");
            }
        }
        if(p.getStrength()==50&&p.getConstitution()==50&&p.getAgility()==50
            &&p.getQuickness()==50) {
            fail(p+" didn't harm any stats");
        }
    }
}
