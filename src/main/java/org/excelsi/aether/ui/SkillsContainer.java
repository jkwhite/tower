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

import com.jmex.bui.*;
import com.jmex.bui.background.*;

import com.jmex.bui.layout.*;
import java.util.*;


public class SkillsContainer extends BContainer {
    private NHBot _bot;


    public SkillsContainer(NHBot bot) {
        super();
        _bot = bot;
        VGroupLayout mgr = new VGroupLayout();
        mgr.setJustification(VGroupLayout.LEFT);
        mgr.setOffAxisJustification(VGroupLayout.LEFT);
        setLayoutManager(mgr);
        refresh();
    }

    public void refresh() {
        removeAll();
        BLabel header = new BLabel("- Skills -", "header");
        add(header);
        BContainer bc = new BContainer();
        TableLayout mgr = new TableLayout(2, 2, 60);
        bc.setLayoutManager(mgr);
        Set<String> skills = _bot.getSkills().keySet();
        TreeSet<String> sorted = new TreeSet<String>(skills);
        for(String skill:sorted) {
            BContainer p = new BContainer(new BorderLayout());
            BLabel name = new BLabel(Grammar.first(skill));
            BLabel value = new BLabel(convert(_bot.getSkill(skill)));
            bc.add(name);
            bc.add(value);
        }
        add(bc);
    }

    private static String convert(int skill) {
        if(skill<20) {
            return "Novice";
        }
        else if(skill<40) {
            return "Apprentice";
        }
        else if(skill<60) {
            return "Journeyman";
        }
        else if(skill<80) {
            return "Expert";
        }
        else if(skill<90) {
            return "Master";
        }
        else {
            return "Legendary";
        }
    }
}
