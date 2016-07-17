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


public class CallItem extends DefaultNHBotAction {
    public String getDescription() {
        return "Name someone or some thing.";
    }

    public void perform() {
        NHSpace s = N.narrative().chooseSpace(getBot(), getBot().getEnvironment().getMSpace());
        if(s==null|!s.isOccupied()) {
            N.narrative().print(getBot(), "There's nothing there.");
        }
        else {
            NHBot b = s.getOccupant();
            String noun = Grammar.noun(b);
            if(b.isPlayer()) {
                noun = "yourself";
            }
            String name = N.narrative().reply(getBot(), "What do you want to call "+noun+"?");
            b.setName(name);
        }
        // calls are free
        throw new ActionCancelledException();
    }
}
