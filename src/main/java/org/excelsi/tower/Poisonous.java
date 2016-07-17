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


public class Poisonous extends Infliction {
    private Poisons _type;
    private String _source;


    public Poisonous() {
        this(Poisons.circulatory);
    }

    public Poisonous(Poisons type) {
        _type = type;
    }

    public void setType(Poisons type) {
        _type = type;
    }

    public Poisons getType() {
        return _type;
    }

    public void setSource(String source) {
        _source = source;
    }

    public String getSource() {
        return _source;
    }

    public GrammarType getPartOfSpeech() {
        return GrammarType.adjective;
    }

    public boolean inflict(NHSpace s) {
        return false;
    }

    public boolean inflict(NHBot b) {
        if(Rand.d100(101-b.getConstitution())) {
            b.addAffliction(new Poisoned(_type, -1, _source));
            return false;
        }
        return true;
    }
}
