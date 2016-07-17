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


public enum Connected {
    negative,
    none,
    weak,
    strong,
    full;


    public Connected stronger() {
        switch(this) {
            case negative:
                return none;
            case none:
                return weak;
            case weak:
                return strong;
            case strong:
                return full;
            case full:
                return full;
        }
        return null;
    }

    public Connected weaker() {
        switch(this) {
            case negative:
                return negative;
            case none:
                return negative;
            case weak:
                return none;
            case strong:
                return weak;
            case full:
                return strong;
        }
        return null;
    }
}
