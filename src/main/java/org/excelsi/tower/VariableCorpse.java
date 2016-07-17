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


public class VariableCorpse extends Corpse {
    private int _findRate;
    private float _size;
    private String _color;


    public VariableCorpse() {
    }

    public VariableCorpse(String color, int findRate, float size, Infliction... infs) {
        _color = color;
        _findRate = findRate;
        _size = size;
        if(infs!=null) {
            for(Infliction i:infs) {
                addFragment(i);
            }
        }
    }

    public int getFindRate() { return _findRate; }
    public String getColor() { return _color; }
    public float getSize() { return _size; }

    public void setFindRate(int findRate) { _findRate = findRate; }
    public void setColor(String color) { _color = color; }
    public void setSize(float size) { _size = size; }
}
