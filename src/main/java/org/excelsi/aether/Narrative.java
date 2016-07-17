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


public interface Narrative extends java.io.Serializable {
    void print(NHBot source, Object message);
    void printf(NHBot source, String message, Object... args);
    void printf(NHSpace source, String message, Object... args);
    void printfm(NHBot source, String message, Object... args);
    void print(NHSpace source, String message);
    void printc(NHBot source, String message);
    void display(NHBot source, String text, boolean centered);
    void backspace();
    void showLoot(NHSpace space);
    void look(NHBot bot, NHSpace start);
    void showInventory();
    void showSkills();
    void clear();
    void save();
    void quit(String record, boolean end);
    void showScores();
    void more();
    void previous();
    boolean isClear();
    boolean confirm(NHBot source, String message);
    Item choose(NHBot source, ItemConstraints constraints, boolean remove);
    Item[] chooseMulti(NHBot source, ItemConstraints constraints, boolean remove);
    Item[] choose(NHBot source, NHSpace space);
    Object[] choose(NHBot input, String query, String heading, Object[] choices, String[] keys, int max);
    NHSpace chooseSpace(NHBot source, NHSpace space);
    String reply(NHBot source, String query);
    String replyc(NHBot source, String query);
    Direction direct(NHBot source, String message);
    void help();
    void repeat();
    void uiSettings();
}
