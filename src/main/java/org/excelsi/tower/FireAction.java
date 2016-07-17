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
import org.excelsi.matrix.*;


public class FireAction extends DefaultNHBotAction {
    private Item _g;
    private Direction _d;


    public FireAction(Gun g, Direction d) {
        _g = g;
        _d = d;
    }

    public FireAction() {
    }

    public String getDescription() {
        return "Fire a missile weapon.";
    }

    public String toString() {
        return "Fire";
    }

    public void perform() {
        try {
            if(_g==null) {
                _g = getBot().getWielded();
            }
            if(_g instanceof Gun) {
                final Gun g = (Gun) _g;
                if(_d==null) {
                    _d = N.narrative().direct(getBot(), "Which direction?");
                }
                getBot().getEnvironment().face(_d);
                if(!g.discharge(getBot(), getBot().getInventory())) {
                    return;
                }
                final Armament ray = g.toRay();
                if(!getBot().isPlayer()) {
                    //N.narrative().print(getBot(), Grammar.start(getBot(), ray.getVerb())+" "+Grammar.pronoun(getBot(), g)+"!");
                }
                getBot().getEnvironment().project(_d, new Attack() {
                    public Source getSource() { return new Source(g); }
                    public NHBot getAttacker() { return getBot(); }
                    public boolean isPhysical() { return true; }
                    public boolean affectsAttacker() { return false; }
                    public int getRadius() { return 20; }
                    public Armament getWeapon() { return ray; }
                    public Type getType() { return Type.bolt; }
                });
            }
            else if(_g instanceof Armament && ((Armament)_g).getType()==Armament.Type.missile) {
                Item quiv = getBot().getQuivered();
                if(quiv==null) {
                    N.narrative().print(getBot(), "Your quiver is empty.");
                    throw new ActionCancelledException();
                }
                else if(!((Missile)quiv).matches((Armament)_g)) {
                    N.narrative().print(getBot(), "You can't fire "+Grammar.pluralize(quiv.getName())+" from "+Grammar.nonspecific(_g.getName())+"!");
                    throw new ActionCancelledException();
                }
                else {
                    Throw t = new Throw(quiv, _d);
                    t.setBot(getBot());
                    t.perform();
                }
            }
            else {
                N.narrative().print(getBot(), "You're not wielding a missile weapon.");
                throw new ActionCancelledException();
            }
        }
        finally {
            _d = null;
            _g = null;
        }
    }
}
