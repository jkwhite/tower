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


public class Writing extends Parasite {
    private static final String RANDOM = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz?????????????";

    private String _text;
    private int _decay;
    private int _times;


    public Writing(String text, int decay) {
        _text = text;
        _decay = decay;
    }

    public void trigger(NHBot b) {
        if(_decay>0) {
            StringBuilder t = new StringBuilder(_text);
            for(int i=0;i<100-_decay;i++) {
                int idx = Rand.om.nextInt(_text.length());
                t.setCharAt(idx, RANDOM.charAt(Rand.om.nextInt(RANDOM.length())));
            }
            _text = t.toString();
            _times += _decay;
            if(_times>100) {
                getSpace().removeParasite(this);
            }
        }
        notice(b);
    }

    public void update() {
    }

    public boolean notice(NHBot b) {
        if(b.isPlayer()) {
            N.narrative().print(b, "There is something written here.");
            if(getSpace().getOccupant()==b) {
                N.narrative().print(b, "You read: \""+_text+"\".");
            }
            return true;
        }
        return false;
    }

    public String getColor() { return "white"; }
    public String getModel() { return " "; }
    public int getHeight() { return 0; }
    public void attacked(Armament a) {}

    public boolean isMoveable() {
        return false;
    }
}
