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


public interface Armament extends java.io.Serializable {
    enum Type { melee, missile };
    int getPower();
    int getModifiedPower();
    int getRate();
    Type getType();
    int getHp();
    void setHp(int hp);
    String getVerb();
    Stat[] getStats();
    String getSkill();
    String getColor();
    String getModel();
    String getAudio();
    Attack invoke(NHBot attacker, NHBot defender, Attack a);
    void invoke(NHBot Attacker, NHSpace s, Attack a);
    Item toItem();
}
