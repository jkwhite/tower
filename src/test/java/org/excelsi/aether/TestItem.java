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
package org.excelsi.aether;


import java.util.ArrayList;
import java.util.Arrays;


public class TestItem extends junit.framework.TestCase {
    public void testIdentify() {
        Item i = createItem("heel");
        Fragment f = new AbstractFragment() {
            public String getName() { return "errant"; }
            public String getText() { return isIdentified()?"demonic":getName(); }
            public GrammarType getPartOfSpeech() { return GrammarType.adjective; }
        };
        i.setStatus(Status.cursed);
        i.addFragment(f);
        assertEquals("was the work of something else i guess", "an errant heel", i.toString());
        i.setIdentified(true);
        assertEquals("wrong ident name", "a cursed demonic heel", i.toString());
    }

    public void testPhraseNaming() {
        Item p = new Place();
        assertEquals("wrong name", "place", p.getName());
        assertEquals("wrong nonspecific", "a place", p.toString());

        p.addFragment(new WithoutAPostcard());
        assertEquals("wrong fragment nonspec", "a place without a postcard", p.toString());
    }

    public void testAdjectiveNaming() {
        Item i = createItem("heart");
        i.addFragment(new Dead());
        assertEquals("wrong adj frag", "the dead heart", Grammar.specific(i));
    }

    public void testNounPhraseNaming() {
        Item i = createItem("piece");
        i.addFragment(new Diamonds());
        i.addFragment(new QuicksilverMoon());
        assertEquals("wrong noun phrase frag", "a piece of diamonds and quicksilver moon", i.toString());
    }

    public void testPluralization() {
        Item i1 = createItem("inch horse");
        i1.setCount(3);
        Item i2 = createItem("faced monster");
        i2.setCount(2);
        assertEquals("wrong plurals", "3 inch horses, 2 faced monsters", i1+", "+i2);
    }

    public void testFragPluralization() {
        Item i1 = createItem("horse");
        Fragment f1 = new AbstractFragment() {
            public String getName() { return "inch"; }
            public GrammarType getPartOfSpeech() { return GrammarType.adjective; }
        };
        i1.addFragment(f1);
        i1.setCount(3);
        Item i2 = createItem("monster");
        Fragment f2 = new AbstractFragment() {
            public String getName() { return "faced"; }
            public GrammarType getPartOfSpeech() { return GrammarType.adjective; }
        };
        i2.setCount(2);
        i2.addFragment(f2);
        assertEquals("wrong frag plurals", "3 inch horses, 2 faced monsters", i1+", "+i2);
    }

    public void testOccurrence() {
        Item my = createItem("robot");
        Fragment friend = new AbstractFragment() {
            public String getName() { return "friend"; }
            public GrammarType getPartOfSpeech() { return GrammarType.adjective; }
            public int getOccurrence() { return 40; }
        };
        assertEquals("wrong init find rate", 50, my.getOccurrence());
        my.addFragment(friend);
        assertEquals("wrong mod find rate", (int) (50*40/100f), my.getOccurrence());
    }

    static Item createItem(String name) {
        return createItem(name, "");
    }

    static Item createItem(String name, final String category) {
        Item i = new Item() {
            public String getStats() { return null; }
            public String getModel() { return category; }
            public String getColor() { return null; }
            public String getCategory() { return category; }
            public SlotType getSlotType() { return SlotType.hand; }
            public float getSize() { return 0f; }
            public float getWeight() { return 0f; }
            public void invoke(NHBot invoker) { }
        };
        i.setName(name);
        return i;
    }

    static private class Place extends Item {
        public String getStats() { return null; }
        public String getModel() { return "the sentences of cynics"; }
        public String getColor() { return "are the sentences of childhood"; }
        public String getCategory() { return ""; }
        public SlotType getSlotType() { return SlotType.hand; }
        public float getSize() { return 0f; }
        public float getWeight() { return 0f; }
        public void invoke(NHBot invoker) { }
    }

    static private class WithoutAPostcard extends AbstractFragment {
        public GrammarType getPartOfSpeech() {
            return GrammarType.phrase;
        }
    }

    static private class Diamonds extends AbstractFragment {
        public GrammarType getPartOfSpeech() {
            return GrammarType.nounPhrase;
        }
    }

    static private class QuicksilverMoon extends AbstractFragment {
        public GrammarType getPartOfSpeech() {
            return GrammarType.nounPhrase;
        }
    }

    static private class Dead extends AbstractFragment {
        public GrammarType getPartOfSpeech() {
            return GrammarType.adjective;
        }
    }
}
