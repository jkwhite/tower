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


import org.excelsi.matrix.Direction;


public class N {
    private static transient ThreadLocal<Narrative> _nar = new InheritableThreadLocal<Narrative>();
    static {
        _nar.set(new Narrative() {
            public void print(NHBot source, Object message) { System.err.println(message.toString()); }
            public void printf(NHBot source, String message, Object... args) { }
            public void printf(NHSpace source, String message, Object... args) { }
            public void printfm(NHBot source, String message, Object... args) { }
            public void print(NHSpace source, String message) { System.err.println(message); }
            public void printc(NHBot source, String message) {}
            public void display(NHBot source, String text, boolean centered) {}
            public void backspace() {}
            public void showLoot(NHSpace space) {}
            public void look(NHBot bot, NHSpace start) {}
            public void showInventory() {}
            public void showSkills() {}
            public void showScores() {}
            public void clear() {}
            public boolean isClear() { return true; }
            public void save() {}
            public void quit(String cause, boolean end) {}
            public void more() {}
            public void previous() {}
            public boolean confirm(NHBot source, String message) { return true; }
            public NHSpace chooseSpace(NHBot source, NHSpace space) { return null; }
            public Item choose(NHBot source, ItemConstraints constraints, boolean remove) {
                for(Item i:source.getInventory().getItem()) {
                    if(constraints.getFilter().accept(i, source)) {
                        return i;
                    }
                }
                throw new ActionCancelledException();
            }
            public Item[] choose(NHBot source, NHSpace space) {return new Item[0];}
            public Item[] chooseMulti(NHBot source, ItemConstraints constraints, boolean remove) {return new Item[0];}
            public Object[] choose(NHBot source, String query, String heading, Object[] choices, String[] keys, int max) { return null; }
            public String reply(NHBot source, String query) {return "";}
            public String replyc(NHBot source, String query) {return "";}
            public Direction direct(NHBot source, String message) {return Direction.north;}
            public void help() {}
            public void repeat() {}
            public void uiSettings() {}
        });
    }


    public static Narrative narrative() {
        return _nar.get();
    }

    public static void set(Narrative n, NHBot pov) {
        _nar.set(new NarrativeFilter(n, pov));
    }

    private N() {
    }

    private static class NarrativeFilter implements Narrative {
        private Narrative _delegate;
        private NHBot _pov;


        public NarrativeFilter(Narrative delegate, NHBot pov) {
            _delegate = delegate;
            _pov = pov;
        }

        public void printc(NHBot source, String message) {
            if(source.isPlayer()) {
                _delegate.printc(source, message);
            }
            else {
                if(_pov.getEnvironment().getVisibleBots().contains(source)) {
                    _delegate.printc(source, message);
                }
            }
        }

        public void print(NHSpace source, String message) {
            if(_pov.getEnvironment().getVisible().contains(source)) {
                _delegate.print(source, message);
            }
        }

        public void printf(NHBot source, String message, Object... args) {
            if(_pov.getEnvironment().getVisibleBots().contains(source)) {
                _delegate.printf(source, message, args);
            }
        }

        public void printf(NHSpace source, String message, Object... args) {
            if(_pov.getEnvironment().getVisible().contains(source)) {
                _delegate.printf(source, message, args);
            }
        }

        public void printfm(NHBot source, String message, Object... args) {
            if(_pov.getEnvironment().getVisibleBots().contains(source)) {
                _delegate.printfm(source, message, args);
            }
        }

        public void print(NHBot source, Object message) {
            if(source==null||source.isPlayer()) {
                _delegate.print(source, message);
            }
            else {
                if(_pov.getEnvironment().getVisibleBots().contains(source)) {
                    _delegate.print(source, message);
                }
            }
        }

        public void backspace() {
            _delegate.backspace();
        }

        public void look(NHBot bot, NHSpace start) {
            if(bot.isPlayer()) {
                _delegate.look(bot, start);
            }
            else {
                throw new UnsupportedOperationException();
            }
        }

        public void showLoot(NHSpace space) {
            if(_pov.isPlayer()) {
                _delegate.showLoot(space);
            }
        }

        public void showInventory() {
            if(_pov.isPlayer()) {
                _delegate.showInventory();
            }
        }

        public void showSkills() {
            if(_pov.isPlayer()) {
                _delegate.showSkills();
            }
        }

        public void display(NHBot source, String text, boolean centered) {
            if(source.isPlayer()) {
                _delegate.display(source, text, centered);
            }
        }

        public void clear() {
            _delegate.clear();
        }

        public boolean isClear() {
            return _delegate.isClear();
        }

        public void quit(String cause, boolean end) {
            _delegate.quit(cause, end);
        }

        public void save() {
            _delegate.save();
        }

        public void more() {
            _delegate.more();
        }

        public void previous() {
            _delegate.previous();
        }

        public boolean confirm(NHBot source, String message) {
            if(source.isPlayer()) {
                return _delegate.confirm(source, message);
            }
            else {
                return true;
            }
        }

        public Direction direct(NHBot source, String message) {
            if(source.isPlayer()) {
                return _delegate.direct(source, message);
            }
            else {
                throw new UnsupportedOperationException();
            }
        }

        public NHSpace chooseSpace(NHBot source, NHSpace space) {
            if(source.isPlayer()) {
                return _delegate.chooseSpace(source, space);
            }
            return null;
        }

        public Item choose(NHBot source, ItemConstraints c, boolean remove) {
            if(source.isPlayer()) {
                return _delegate.choose(source, c, remove);
            }
            else {
                for(Item i:source.getInventory().getItem()) {
                    if(c.getFilter().accept(i, source)) {
                        return i;
                    }
                }
                throw new ActionCancelledException();
            }
        }

        public Item[] chooseMulti(NHBot source, ItemConstraints c, boolean remove) {
            if(source.isPlayer()) {
                return _delegate.chooseMulti(source, c, remove);
            }
            else {
                for(Item i:source.getInventory().getItem()) {
                    if(c.getFilter().accept(i, source)) {
                        return new Item[]{i};
                    }
                }
                throw new ActionCancelledException();
            }
        }

        public Item[] choose(NHBot source, NHSpace space) {
            if(source.isPlayer()) {
                return _delegate.choose(source, space);
            }
            else {
                throw new UnsupportedOperationException();
            }
        }

        public Object[] choose(NHBot source, String query, String heading, Object[] choices, String[] keys, int max) {
            if(source.isPlayer()) {
                return _delegate.choose(source, query, heading, choices, keys, max);
            }
            else {
                throw new UnsupportedOperationException();
            }
        }

        public String reply(NHBot source, String query) {
            return _delegate.reply(source, query);
        }

        public String replyc(NHBot source, String query) {
            return _delegate.replyc(source, query);
        }

        public void showScores() {
            _delegate.showScores();
        }

        public void help() {
            _delegate.help();
        }

        public void repeat() {
            _delegate.repeat();
        }

        public void uiSettings() {
            _delegate.uiSettings();
        }
    }
}
