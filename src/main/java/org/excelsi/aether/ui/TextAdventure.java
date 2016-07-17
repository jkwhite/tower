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
package org.excelsi.aether.ui;


import org.excelsi.matrix.*;
import org.excelsi.aether.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;


public class TextAdventure implements Narrative {
    private Game _g;
    private BufferedReader _buf;


    public void run() {
        Game g = Universe.getUniverse().getGame();
        _g = g;
        g.init();
        Mechanics mech = new QuantumMechanics();
        NHEnvironment.setMechanics(mech);

        println(Universe.getUniverse().getStory());

        InputQueue iq = new InputQueue(null);
        N.set(this, _g.getPlayer());

        try {
            InputStreamReader in = new InputStreamReader(System.in);
            _buf = new BufferedReader(in);
            for(;;) {
                print("> ");
                String s = _buf.readLine();
                if(s==null) {
                    break;
                }
                s = s.trim();
                String act = Universe.getUniverse().getKeymap().get(s);
                GameAction action = null;
                if(act != null) {
                    String actClass = Universe.getUniverse().getActionmap().get(act);
                    try {
                        action = (GameAction) Thread.currentThread().getContextClassLoader().loadClass(actClass).newInstance();
                        System.err.println("Mapped "+s+" => "+action);
                    }
                    catch(Throwable t) {
                    }
                    if(action instanceof NHBotAction) {
                        ((NHBotAction)action).setBot(_g.getPlayer());
                    }
                }
                _g.tick(action);
            }
        }
        catch(IOException e) {
            throw new Error(e);
        }
    }

    private void println(String text) {
        System.out.println(text);
    }

    private void print(String text) {
        System.out.print(text);
    }

    public void printf(final NHBot source, final String message, final Object... args) {
    }

    public void printf(final NHSpace source, final String message, final Object... args) {
    }

    public void printfm(final NHBot source, final String message, final Object... args) {
    }

    public void print(NHSpace source, String message) {
        println(message);
    }

    public void print(NHBot source, Object message) {
        println(message.toString());
    }

    public void printc(NHBot source, String message) {
        print(message);
    }

    public void display(NHBot source, String text, boolean centered) {
        println(text);
    }

    public void backspace() {
    }

    public void showLoot(NHSpace space) {
        if(space!=null) {
            println("You're standing on a "+space);
            if(space.numItems()>0) {
                println("There's some stuff here.");
            }
        }
    }

    public void look(NHBot bot, NHSpace start) {
    }

    public void showInventory() {
        println("You've got some stuff.");
    }

    public void showSkills() {
    }

    public void showScores() {
    }

    public void clear() {
    }

    public boolean isClear() {
        return false;
    }

    public void quit(String cause, boolean end) {
        System.exit(0);
    }

    public void save() {
    }

    public void more() {
    }

    public void previous() {
    }

    public boolean confirm(NHBot source, String message) {
        println(message);
        try {
            String y = _buf.readLine().trim();
            return y.equalsIgnoreCase("y");
        }
        catch(IOException e) {
            throw new Error(e);
        }
    }

    public Item[] chooseMulti(NHBot source, ItemConstraints c, boolean remove) {
        return new Item[0];
    }

    public Item choose(NHBot source, ItemConstraints constraints, boolean remove) {
        return null;
    }

    public Object[] choose(NHBot source, String query, String header, Object[] choices, String[] keys, int max) {
        return null;
    }

    public NHSpace chooseSpace(NHBot source, NHSpace space) {
        return null;
    }

    public Item[] choose(NHBot source, NHSpace space) {
        return new Item[0];
    }

    public void help() {
    }

    public void repeat() {
    }

    public void uiSettings() {
    }

    public String reply(NHBot source, String query) {
        println(query);
        try {
            return _buf.readLine().trim();
        }
        catch(IOException e) {
            throw new Error(e);
        }
    }

    public String replyc(NHBot source, String query) {
        println(query);
        try {
            return _buf.readLine().trim();
        }
        catch(IOException e) {
            throw new Error(e);
        }
    }

    public Direction direct(NHBot source, String message) {
        return Direction.north;
    }
}
