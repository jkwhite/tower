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


import java.util.logging.Logger;


public class Settings extends DefaultNHBotAction {
    public Settings() {
    }

    public String getDescription() {
        return "Changes settings.";
    }

    public void perform() {
        //N.narrative().uiSettings();
        //Object[] choice = Context.c().n().choose(getBot(), "", "Game",
            //new String[]{"Settings", "Save & Quit", "Quit"}, null, 1);
        final String s = Context.c().n().choose(getBot(), new SelectionMenu<String>(
            new MenuItem<String>("u", "Settings", "settings"),
            new MenuItem<String>("S", "Save & Quit", "sq"),
            new MenuItem<String>("Q", "Quit", "quit")));
        //String s = (String) choice[0];
        if("settings".equals(s)) {
            N.narrative().uiSettings();
        }
        else if("sq".equals(s)) {
            Patsy.Save save = new Patsy.Save();
            save.perform();
        }
        else if("quit".equals(s)) {
            Patsy.Exit e = new Patsy.Exit();
            e.setBot(getBot());
            e.perform();
        }
    }
}
