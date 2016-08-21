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


public class Grammar {
    private static final boolean DEBUG = Boolean.getBoolean("tower.creator");

    private Grammar() {
    }

    public static String format(NHBot source, String fmt, Object... args) {
        StringBuilder r = new StringBuilder(fmt.length()+32); // guess
        int i = 0, last = 0;
        int a = 0;
        //System.err.println("STRING: "+fmt);
        boolean upper = true;
        boolean first = true;
        while((i=fmt.indexOf('%', i))>=0) {
            if(i>0) {
                r.append(fmt.substring(last, i));
            }
            ++i;
            //System.err.println("PARTIAL: "+r);
            //System.err.println("CONTROL: "+fmt.charAt(i));
            switch(fmt.charAt(i)) {
                case 'a':
                    Object oo = args[a++];
                    if(oo instanceof Item) {
                        r.append(Grammar.nonspecific((Item)oo));
                    }
                    else {
                        r.append(Grammar.nonspecific((NHBot)oo));
                    }
                    break;
                case 'A':
                    Source s = (Source) args[a];
                    String v = (String) args[a+1];
                    a += 2;
                    r.append(s.toString(v));
                    break;
                case 'c':
                    r.append(Grammar.conjugate(source, (String) args[a]));
                    break;
                case 'K':
                    if(first) {
                        upper = false;
                    }
                    r.append(Grammar.key((Inventory)args[a], (Item)args[a+1]));
                    a += 2;
                    break;
                case 'M':
                    r.append(Grammar.pronoun((NHBot)args[a], (Item)args[a+1]));
                    a += 2;
                    break;
                case 'n':
                    Object o = args[a++];
                    if(o instanceof Item) {
                        r.append(Grammar.noun((Item)o));
                    }
                    else {
                        r.append(Grammar.noun((NHBot)o));
                    }
                    break;
                case 'P':
                    r.append(Grammar.possessive(source, (Item) args[a++]));
                    break;
                case 'p':
                    r.append(Grammar.possessive((NHBot)args[a++]));
                    break;
                case 't':
                    r.append(Grammar.that((Item)args[a++]));
                    break;
                case 'V':
                    String verb = (String) args[a+1];
                    if("feel".equals(verb)) {
                        verb = "look";
                    }
                    r.append(Grammar.noun((NHBot)args[a]));
                    r.append(" ");
                    r.append(Grammar.conjugate((NHBot)args[a], verb));
                    a += 2;
                    break;
                case 'v':
                    r.append(Grammar.noun(source));
                    r.append(" ");
                    r.append(Grammar.toBe(source));
                    break;
            }
            last = i+1;
            first = false;
        }
        r.append(fmt.substring(last));
        if(upper) {
            r.setCharAt(0, Character.toUpperCase(r.charAt(0)));
        }
        return r.toString();
    }

    public static String conjugate(NHBot bot, String verb) {
        final String[] ESES = {"s", "h", "o", "x"};
        String part1 = verb;
        String part2 = null;
        if(bot==null||!bot.isPlayer()) {
            if(verb.endsWith(" to")) {
                part1 = verb.substring(0, verb.indexOf(" to"));
                part2 = " to";
            }
            for(int i=0;i<ESES.length;i++) {
                if(part1.endsWith(ESES[i])) {
                    part1 = part1+"es";
                    return part2==null?part1:part1+part2;
                }
            }
            if(part1.endsWith("y")) {
                part1 = part1.substring(0, part1.length()-1)+"ies";
                return part2==null?part1:part1+part2;
            }
            return part2==null?part1+"s":part1+"s"+part2;
        }
        return verb;
    }

    public static String noun(NHBot bot) {
        if(!bot.isPlayer()) {
            return pov().isBlind()?"it":(bot.isUnique()||bot.getName()!=null?bot.toString():"the "+bot.toString());
        }
        return "you";
    }

    public static String specific(NHBot bot) {
        if(!bot.isPlayer()) {
            return pov().isBlind()?"it":(bot.isUnique()||bot.getName()!=null?bot.toString():"this "+bot.toString());
        }
        return "you";
    }

    public static String simple(Item item) {
        return pov().isBlind()?item.getObscuredName():item.getName();
    }

    public static String noun(Item item) {
        return "the "+(pov().isBlind()?item.getObscuredName():item.getName());
    }

    public static String nounp(Item item) {
        return item.getCount()==1?noun(item):"the "+pluralize(simple(item));
    }

    public static String that(Item item) {
        return "that "+(pov().isBlind()?item.getObscuredName():item.getName());
    }

    public static String keyName(Inventory in, Item i) {
        String key = in.keyFor(i);
        if(key!=null&&Character.isLetter(key.charAt(0))) {
            return key+" - ";
        }
        else {
            return "";
        }
    }

    public static String keyValue(Inventory in, Item i) {
        String it = pov().isBlind()?Grammar.nonspecificObscure(i):i.toString();
        return it;
    }

    public static String key(Inventory in, Item i) {
        String key = in.keyFor(i);
        String it = pov().isBlind()?Grammar.nonspecificObscure(i):i.toString();
        if(key!=null&&Character.isLetter(key.charAt(0))) {
            return key+" - "+it;
        }
        else {
            return it;
        }
    }

    public static Request parseRequest(String text) {
        text = text.trim();
        int count = 1;
        boolean multi = false;
        Status status = Status.uncursed;
        if(text.length()==0) {
        }
        else if(text.startsWith("a ")) {
            text = text.substring("a ".length());
        }
        else if(text.startsWith("an ")) {
            text = text.substring("an ".length());
        }
        else if(Character.isDigit(text.charAt(0))) {
            try {
                count = Integer.parseInt(text.substring(0, text.indexOf(' ')));
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            }
            multi = true;
            text = text.substring(text.indexOf(' ')+1);
        }
        if(text.startsWith("cursed ")) {
            status = Status.cursed;
            text = text.substring("cursed ".length());
        }
        else if(text.startsWith("blessed ")) {
            status = Status.blessed;
            text = text.substring("blessed ".length());
        }
        if(multi) {
            text = Grammar.singularize(text);
        }
        return new Request(count, status, text);
    }

    public static String nonspecific(NHBot bot) {
        if(bot.isUnique()||bot.getName()!=null) {
            return pov().isBlind()?"it":bot.toString();
        }
        else {
            String t;
            if(pov().isBlind()) {
                t = "it";
            }
            else {
                t = bot.toString();
            }
            return nonspecific(t);
        }
    }

    public static String nonspecific(String t) {
        if(t==null||t.length()==0) {
            return "a nonspecific entity";
        }
        char c = t.charAt(0);
        if(c=='a' || c=='e' || c=='i' || c=='o' || c=='u') {
            return "an "+t;
        }
        return "a "+t;
    }

    public static String nonspecific(Item item) {
        if(item.getCount()==1) {
            return singular(item);
        }
        else {
            StringBuilder b = new StringBuilder();
            preFragment(item, b);
            StringBuilder b2 = new StringBuilder();
            b2.append(item.getCount());
            b2.append(" ");
            b2.append(pluralize(b.toString()));
            postFragment(item, b2);
            return b2.toString();
        }
    }

    public static String indefinite(Item item) {
        if(item.getCount()==1) {
            StringBuilder b = new StringBuilder();
            preFragment(item, b);
            postFragment(item, b);
            return b.toString();
        }
        else {
            StringBuilder b = new StringBuilder();
            preFragment(item, b);
            StringBuilder b2 = new StringBuilder();
            b2.append(pluralize(b.toString()));
            postFragment(item, b2);
            return b2.toString();
        }
    }

    public static String nonspecificObscure(Item item) {
        if(item.getCount()==1) {
            return singularObscure(item);
        }
        else {
            StringBuilder b = new StringBuilder();
            StringBuilder b2 = new StringBuilder();
            b2.append("several ");
            b2.append(pluralize(item.getObscuredName()));
            return b2.toString();
        }
    }

    public static String singularObscure(Item item) {
        StringBuilder b = new StringBuilder();
        String name = item.getObscuredName();
        if(!pov().isBlind()&&item.isUnique()&&item.isClassIdentified()) {
            return "the "+name;
        }
        char c = name.charAt(0);
        if(c=='a' || c=='e' || c=='i' || c=='o' || c=='u') {
            return "an "+name;
        }
        else if(Character.isLetter(c)) {
            return "a "+name;
        }
        else {
            return name;
        }
    }

    public static String singular(Item item) {
        if(pov()!=null&&pov().isBlind()) {
            return singularObscure(item);
        }
        StringBuilder b = new StringBuilder();
        preFragment(item, b);
        postFragment(item, b);
        String name = b.toString();
        if(item.isUnique()&&item.isClassIdentified()) {
            return "the "+name;
        }
        char c = name.charAt(0);
        if(c=='a' || c=='e' || c=='i' || c=='o' || c=='u') {
            return "an "+name;
        }
        else if(Character.isLetter(c)) {
            return "a "+name;
        }
        else {
            return name;
        }
    }

    public static String specific(Item item) {
        StringBuilder b = new StringBuilder("the ");
        preFragment(item, b);
        postFragment(item, b);
        return b.toString();
    }

    private static void preFragment(Item item, StringBuilder b) {
        // first add hardcoded modifiers
        if(item.getStatus()!=null&&item.isStatusIdentified()) {
            b.append(item.getStatus());
            b.append(" ");
        }
        float dmg = ((float)item.getHp())/item.getMaxHp();
        String[] dmgs = item.getDamageNames();
        if(dmgs.length!=5) {
            throw new IllegalStateException("wrong dmgs length for "+item);
        }
        if(dmg<=0.10f) {
            b.append(dmgs[0]);
        }
        else if(dmg<=0.25f) {
            b.append(dmgs[1]);
        }
        else if(dmg<=0.5f) {
            b.append(dmgs[2]);
        }
        else if(dmg<=0.66f) {
            b.append(dmgs[3]);
        }
        else if(dmg<=0.75f) {
            b.append(dmgs[4]);
        }
        // then general modifiers
        for(Fragment f:item.getFragments()) {
            if(item.partOfSpeech(f)==Fragment.GrammarType.adjective) {
                b.append(f.getText());
                b.append(" ");
            }
        }
        b.append(item.getName());
    }

    private static void postFragment(Item item, StringBuilder b) {
        boolean added = false;
        for(Fragment f:item.getFragments()) {
            Fragment.GrammarType pos = item.partOfSpeech(f);
            if(pos==Fragment.GrammarType.nounPhrase) {
                if(!added) {
                    b.append(" of ");
                    b.append(f.getText());
                    added = true;
                }
                else {
                    b.append(" and ");
                    b.append(f.getText());
                }
            }
            else if(pos==Fragment.GrammarType.phrase) {
                b.append(" ");
                b.append(f.getText());
            }
        }
        if(DEBUG) {
            b.append(" ["+item.getHp()+"/"+item.getMaxHp()+"]");
        }
    }

    public static String first(String s) {
        return s.substring(0,1).toUpperCase()+s.substring(1);
    }

    public static String start(NHBot bot) {
        return first(noun(bot));
    }

    public static String start(NHBot bot, String verb) {
        if(!bot.isPlayer()&&verb.equals("feel")) {
            verb = "look";
        }
        return start(bot)+" "+Grammar.conjugate(bot, verb);
    }

    public static String startToBe(NHBot b) {
        return start(b)+" "+toBe(b);
    }

    public static String pluralize(String noun) {
        String sing = noun;
        String pl;
        int complex = noun.indexOf(" of ");
        if(complex==-1) {
            complex = noun.indexOf(" labeled "); // TODO: hack for unidentified scrolls
        }
        if(complex>0) {
            // compound noun
            sing = noun.substring(0, complex);
        }
        // special cases
        if("armor".equals(sing)||"gold".equals(sing)||"junk".equals(sing)||"infantry".equals(sing)) {
            pl = sing;
        }
        else if("wolf".equals(sing)) {
            pl = "wolves";
        }
        else {
            char e = sing.charAt(sing.length()-1);
            switch(e) {
                case 'y':
                    pl = sing.substring(0, sing.length()-1)+"ies";
                    break;
                case 's':
                case 'h':
                    if(sing.endsWith("th")) {
                        pl = sing+"s";
                    }
                    else {
                        pl = sing+"es";
                    }
                    break;
                case 'o':
                    pl = sing+"es";
                    break;
                default:
                    pl = sing+"s";
                    break;
            }
        }
        if(complex>0) {
            pl += noun.substring(complex);
        }
        return pl;
    }

    public static String singularize(String noun) {
        String pl = noun;
        String sing;
        int complex = noun.indexOf(" of ");
        if(complex>0) {
            // compound noun
            pl = noun.substring(0, complex);
        }
        if(pl.endsWith("ies")) {
            sing = pl.substring(0, pl.length()-"ies".length())+"y";
        }
        else if(pl.endsWith("ses")) {
            sing = pl.substring(0, pl.length()-"es".length());
        }
        else {
            sing = pl.substring(0, pl.length()-1);
        }
        if(complex>0) {
            sing += noun.substring(complex);
        }
        return sing;
    }

    public static String toBe(NHBot bot) {
        return bot.isPlayer()?"are":"is";
    }

    public static String toBe(Item item) {
        return item.getCount()>1||item.isAlwaysPlural()?"are":"is";
    }

    public static String pronoun(NHBot bot) {
        if(bot.isPlayer()) {
            return "you";
        }
        switch(bot.getGender()) {
            case male:
                return "him";
            case female:
                return "her";
            case neuter:
            default:
                return "it";
        }
    }

    public static String pronoun(NHBot bot, Item i) {
        String s = null;
        if(bot.isPlayer()) {
            s = "your ";
        }
        else {
            switch(bot.getGender()) {
                case male:
                    s = "his ";
                    break;
                case female:
                    s = "her ";
                    break;
                default:
                case neuter:
                    s = "its ";
                    break;
            }
        }
        //return s+indefinite(i);
        return s+(bot==pov()?indefinite(i):i.getObscuredName());
    }

    public static String possessive(NHBot bot) {
        return bot.isPlayer()?"your":(pov().isBlind()?"its":Grammar.noun(bot)+"'s");
    }

    public static String possessiveIndirect(NHBot bot) {
        return bot.isPlayer()?"your":"its";
    }

    public static String possessive(NHBot bot, Item it) {
        String name = bot==pov()?indefinite(it):it.getObscuredName();
        return possessive(bot)+" "+name;
    }

    private static NHBot _player;
    public static void setPov(final NHBot player) {
        _player = player;
    }

    public static NHBot pov() {
        if(_player==null) {
            Universe u = Universe.getUniverse();
            if(u!=null) {
                _player = u.getGame().getPlayer();
                return _player;
            }
        }
        return _player;
    }
}
