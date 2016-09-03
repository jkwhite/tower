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


public abstract class Light extends Tool {
    private boolean _enabled;
    private int _uses;
    private Modifier _mod = new Modifier();


    public Light(int uses) {
        _uses = uses;
    }

    public void setLit(boolean enabled) {
        if(_enabled!=enabled) {
            _enabled = enabled;
            if(_enabled) {
                addFragment(new Lit());
            }
            else {
                removeFragment(Lit.NAME);
            }
        }
    }

    public boolean isLit() {
        return _enabled;
    }

    public boolean equals(Object o) {
        return super.equals(o) && ((Light)o)._enabled == _enabled;
    }

    public void setUses(int uses) {
        if(uses!=_uses) {
            _uses = uses;
            if(_uses==0) {
                setLit(false);
                DefaultNHBot b = (DefaultNHBot) Actor.current();
                if(b!=null) {
                    _mod.setCandela(isLit()?getCandela():0);
                    b.clearModifier();
                    N.narrative().printf(b, "%n goes out!", this);
                    b.notifyListeners("candela", null);
                }
            }
        }
    }

    public int getUses() {
        return _uses;
    }

    public Modifier getPackedModifier() {
        _mod.setCandela(isLit()?getCandela():0);
        _mod.setCandelaColor(isLit()?getCandelaColor():Modifier.ONE);
        return _mod;
    }

    abstract public float getCandela();

    protected float[] getCandelaColor() { return new float[]{1f, 1f, 1f, 1f}; }

    public void update(Container c) {
        if(isLit()) {
            int u = getUses();
            if(u>0) {
                setUses(--u);
                if(u<=10 && u>0 && u%2==0) {
                    DefaultNHBot b = (DefaultNHBot) Actor.current();
                    if(b!=null) {
                        N.narrative().printf(b, "%n's light flickers.", this);
                    }
                }
            }
        }
    }

    public void use(final NHBot b) {
        synchronized(b) {
            DefaultNHBot db = (DefaultNHBot) b;
            // TODO: this should really not be public
            // need to find a way to automatically clear cache.
            if(getUses()==0) {
                N.narrative().printf(b, "%n is spent...", this);
                throw new ActionCancelledException();
            }
            if(isLit()) {
                setLit(false);
                N.narrative().print(b, Grammar.start(b, "douse")+" the "+Grammar.indefinite(this)+".");
            }
            else {
                N.narrative().print(b, Grammar.start(b, "light")+" the "+Grammar.indefinite(this)+".");
                setLit(true);
            }
            _mod.setCandela(isLit()?getCandela():0);
            db.clearModifier();
            db.notifyListeners("candela", null);
        }
    }

    static class Lit implements Fragment {
        public static final String NAME = "lit";

        private Item _owner;


        public void apply(Fragment f) {
        }

        public boolean intercepts(Attack a) {
            return false;
        }

        @Override public Performable intercept(NHBot attacker, NHBot defender, Attack a) {
            return null;
        }

        public GrammarType getPartOfSpeech() {
            return GrammarType.phrase;
        }

        public String getText() {
            return "(lit)";
        }

        public void setIdentified(boolean id) {
        }

        public boolean isIdentified() {
            return true;
        }

        public int getPowerModifier() {
            return 0;
        }

        public Modifier getModifier() {
            return null;
        }

        public void setClassIdentified(boolean id) {
        }

        public boolean isClassIdentified() {
            return true;
        }

        public String getName() {
            return NAME;
        }

        public int getOccurrence() {
            return 0;
        }

        public void setOwner(Item owner) {
            _owner = owner;
        }

        public Item getOwner() {
            return _owner;
        }
    }
}
