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


public enum Stat {
    st("Strength", "stronger"),
    qu("Quickness", "quicker"),
    ag("Agility", "more agile"),
    co("Constitution", "tougher"),
    sd("Self-Discipline", "more focused"),
    em("Empathy", "more centered"),
    in("Intuition", "more aware"),
    re("Reasoning", "smarter"),
    me("Memory", "brighter"),
    pr("Presence", "more awake"),
    wt("Weight", "heavier");

    
    private String _longName;
    private String _adj;


    private Stat(String longName, String adj) {
        _longName = longName;
        _adj = adj;
    }

    public String getLongName() {
        return _longName;
    }

    public String getMethodName() {
        return getLongName().replaceAll("-", "");
    }

    public String getAdjective() {
        return _adj;
    }

    public static Stat fromString(String str) {
        return Enum.valueOf(Stat.class, str.toLowerCase());
    }
}
