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
import java.util.ArrayList;


public class TestConsume extends junit.framework.TestCase {
    public void testRotten() {
        NHBot j = new BasicBot();
        j.setCommon("jackal");
        j.setForm(new Canid());
        Corpse c = ((Mortal)j.getForm()).toCorpse();
        c.setSpirit(j);
        Patsy p = new Patsy();
        p.getInventory().add(c);
        Consume con = new Consume(c);
        con.setBot(p);
        con.perform();
        int tries = 0;
        while(p.isOccupied()) {
            p.tick();
            if(++tries>100) {
                fail("took too long");
            }
        }
        assertTrue("didn't get confused", p.isAfflictedBy(Confused.NAME));
    }

    public void testInventoryPill() {
        Pill p = new Pill(new Cyanide());
        Patsy doomed = new Patsy();
        doomed.getInventory().add(p);
        Consume c = new Consume(p);
        c.setBot(doomed);
        c.perform();
        assertTrue("not dead yet", doomed.isDead());
        assertEquals("didn't consume", 0, doomed.getInventory().size());
    }

    public void testInventoryFood() {
        final ArrayList events = new ArrayList();
        FoodRation pan = new FoodRation();
        pan.setCount(2);
        Patsy p = new Patsy();
        p.addListener(new NHEnvironmentAdapter() {
            public void actionStarted(NHBot b, ProgressiveAction action) {
                super.actionStarted(b, action);
                events.add("born bought discouraged");
            }

            public void actionStopped(NHBot b, ProgressiveAction action) {
                super.actionStopped(b, action);
                events.add("born bored discovered");
            }
        });
        p.getInventory().add(pan);
        Consume c = new Consume(pan);
        c.setBot(p);
        int oldHunger = p.getHunger();
        c.perform();
        int ticks = 0;
        while(p.isOccupied()) {
            p.tick();
            if(++ticks>5) {
                fail("shouldn't take that long: "+ticks);
            }
        }
        assertTrue("just didn't satisfy: "+p.getHunger(), oldHunger>p.getHunger());
        assertEquals("didn't get events: "+events, 2, events.size());
    }

    public void testFoodOnGround() {
        final ArrayList events = new ArrayList();
        Floor f = new Floor();
        FoodRation fr = new FoodRation();
        fr.setStatus(Status.blessed);
        f.add(fr);
        Patsy p = new Patsy();
        f.setOccupant(p);
        p.addListener(new NHEnvironmentAdapter() {
            public void actionStarted(NHBot b, ProgressiveAction action) {
                events.add("in between, we're recorders");
            }

            public void actionStopped(NHBot b, ProgressiveAction action) {
                events.add("in between, we try");
            }
        });
        Consume c = new Consume(fr);
        c.setBot(p);
        int oldHunger = p.getHunger();
        c.perform();
        int ticks = 0;
        while(p.isOccupied()) {
            p.tick();
            if(++ticks>5) {
                fail("shouldn't take that long: "+ticks);
            }
        }
        assertTrue("just didn't satisfy: "+p.getHunger(), oldHunger>p.getHunger());
        assertEquals("didn't get events: "+events, 2, events.size());
        assertEquals("didn't remove from ground", 0, f.numItems());
    }

    public void testFoodOnGroundNarrative() {
        Rand.load();
        final ArrayList events = new ArrayList();
        Floor f = new Floor();
        FoodRation fr = new FoodRation();
        fr.setStatus(Status.cursed);
        f.add(fr);
        Patsy p = new Patsy();
        f.setOccupant(p);
        Consume c = new Consume();
        c.setBot(p);
        int oldHunger = p.getHunger();
        c.perform();
        int ticks = 0;
        while(p.isOccupied()) {
            p.tick();
            if(++ticks>5) {
                fail("shouldn't take that long: "+ticks);
            }
        }
        assertFalse("cursed: "+p.getHunger(), oldHunger>p.getHunger());
        assertEquals("didn't remove from ground", 0, f.numItems());
        assertTrue("didn't get afflictions", p.getAfflictions().size()>0);
        ticks = 0;
        while(p.getAfflictions().size()>0) {
            p.tick();
            if(++ticks>200) {
                fail("shouldn't take that long");
            }
        }
        Consume.Vomit v = (Consume.Vomit) f.getItem()[0];
        assertEquals("weight", 0.1f, v.getWeight());
        assertEquals("size", 0.1f, v.getSize());
        assertTrue("nutrition"+v.getNutrition(), v.getNutrition()<=Hunger.RATE/4);
    }

    public void testInventoryFoodNarrative() {
        final ArrayList events = new ArrayList();
        FoodRation pan = new FoodRation();
        Patsy p = new Patsy();
        Floor f = new Floor();
        f.setOccupant(p);
        p.getInventory().add(pan);
        Consume c = new Consume();
        c.setBot(p);
        int oldHunger = p.getHunger();
        c.perform();
        int ticks = 0;
        while(p.isOccupied()) {
            p.tick();
            if(++ticks>5) {
                fail("shouldn't take that long: "+ticks);
            }
        }
        assertTrue("just didn't satisfy: "+p.getHunger(), oldHunger>p.getHunger());
    }

    public void testTuring() {
        Rand.load();
        Patsy t = new Patsy();
        Potion potion = new Potion(new WaterInfliction());
        Pill pill = new Pill(new Cyanide());
        assertTrue("wrong pill type: "+pill.getFragments().get(0), pill.getFragments().get(0) instanceof Cyanide);
        potion.immerse(t, pill);
        assertTrue("wrong potion type: "+potion.getFragments().get(0), potion.getFragments().get(0) instanceof Cyanide);
        Apple a = new Apple();
        t.getInventory().add(a);
        potion.immerse(t, a);
        assertTrue("wrong apple type: "+a.getFragments().get(0), a.getFragments().get(0) instanceof Cyanide);
        Consume c = new Consume(a);
        c.setBot(t);
        c.perform();
        assertTrue("no, that didn't happen", t.isDead());
    }
}
