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


import org.excelsi.matrix.*;
import org.excelsi.aether.*;
import java.util.ArrayList;
import java.util.List;


public class Exit extends Floor {
    private static final long serialVersionUID = 1L;


    public Exit() {
        addMSpaceListener(new MSpaceAdapter() {
            public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
                if(to==Exit.this) {
                    Patsy bot = (Patsy) b;
                    bot.addScore(1000000);
                    NHSpace n = bot.getEnvironment().getMSpace();
                    try {
                        Thread.sleep(700);
                    }
                    catch(InterruptedException e) {
                        throw new Error(e);
                    }
                    bot.setModel(" ");
                    try {
                        Thread.sleep(800);
                    }
                    catch(InterruptedException e) {
                        throw new Error(e);
                    }
                    //n.replace(new Floor());
                    bot.setDeath("Escaped.");
                    //N.narrative().display(bot, "Without a trace, you vanish.\n \nYou have escaped the Tower.", false);
                    N.narrative().quit("Escaped.", true);
                }
            }
        });
    }
    
    public String getModel() {
        return "0";
    }

    public String getColor() {
        return "light-green";
    }

    public int getDepth() {
        return 0;
    }

    public boolean look(NHBot b) {
        N.narrative().print(b, "There is an escape here.");
        return true;
    }

    public int add(Item i, NHBot adder) {
        super.add(i, adder);
        destroy(i);
        return -1;
    }
}
