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
import com.jmex.bui.BLabel;
import com.jmex.bui.BContainer;
import com.jmex.bui.layout.*;
import java.text.MessageFormat;
import java.io.PrintWriter;
import java.io.StringWriter;
import com.jme.system.DisplaySystem;
import java.util.HashSet;


public class Status extends BContainer implements ContainerListener {
    private NHBot _bot;
    private BLabel _stat;
    private BLabel _stat2;
    private BLabel _level;
    private StringBuilder _text = new StringBuilder();
    private StringBuilder _text2 = new StringBuilder();
    private StringBuilder _ltext = new StringBuilder();


    public Status(NHBot b, boolean two) {
        _bot = b;
        //setLayoutManager(new HGroupLayout());
        VGroupLayout v = new VGroupLayout();
        v.setGap(0);
        setLayoutManager(v);
        addListeners();
        StringBuilder sb = new StringBuilder();
        format1(sb);
        _stat = new BLabel(sb.toString());
        //_stat.setSize((int)(0.65*DisplaySystem.getDisplaySystem().getWidth()), 18);
        _stat.setSize(DisplaySystem.getDisplaySystem().getWidth(), 18);
        add(_stat);

        sb = new StringBuilder();
        format2(sb);
        _stat2 = new BLabel(sb.toString());
        _stat2.setSize(DisplaySystem.getDisplaySystem().getWidth(), 18);
        add(_stat2);

        //formatLevel(sb);
        //_level = new BLabel(sb.toString(), "level_label");
        //_level.setSize((int)(0.25*DisplaySystem.getDisplaySystem().getWidth()), 18);
        //add(_level);
        EventQueue.getEventQueue().addGameListener(Universe.getUniverse().getGame(), new GameListener() {
            public void ascended(Game g) {
                addListeners();
            }

            public void descended(Game g) {
                addListeners();
            }
        });
    }

    private void addListeners() {
        EventQueue.getEventQueue().addNHEnvironmentListener(_bot, new NHEnvironmentAdapter() {
            private Hunger.Degree _lastHunger = null;

            public void collided(Bot active, Bot passive) {
                updateStatus();
            }

            public void attributeChanged(Bot b, String attribute, Object newValue) {
                // hunger happens all the time so optimize it
                if("hunger".equals(attribute)) {
                    Hunger.Degree nh = Hunger.Degree.degreeFor(((Integer)newValue).intValue());
                    if(nh==_lastHunger) {
                        return;
                    }
                    _lastHunger = nh;
                }
                updateStatus();
            }

            public void afflicted(NHBot b, Affliction a) {
                updateStatus();
            }

            public void cured(NHBot b, Affliction a) {
                updateStatus();
            }

            public void equipped(NHBot b, Item i) {
                updateStatus();
            }

            public void unequipped(NHBot b, Item i) {
                updateStatus();
            }

            public void modifierAdded(NHBot b, Modifier m) {
                updateStatus();
            }

            public void modifierRemoved(NHBot b, Modifier m) {
                updateStatus();
            }

            public void died(Bot b, MSource s) {
            }
        });
        EventQueue.getEventQueue().addContainerListener(_bot.getInventory(), this);
    }

    private String _old = null;
    private String _old2 = null;
    public void updateStatus() {
        //System.err.println("REASON: "+new Exception().getStackTrace()[1].getMethodName());
        //format(_text);
        format1(_text);
        format2(_text2);
        String t = _text.toString();
        if(_old==null||!t.equals(_old)) {
            _stat.setText(t);
            _old = t;
        }
        String t2 = _text2.toString();
        if(_old2==null||!t2.equals(_old2)) {
            _stat2.setText(t2);
            _old2 = t2;
        }
        //formatLevel(_ltext);
        //_level.setText(_ltext.toString());
    }

    public void itemDropped(Container space, Item item, int idx, boolean incremented) {
        updateStatus();
    }

    public void itemAdded(Container space, Item item, int idx, boolean incremented) {
        updateStatus();
    }

    public void itemAdded(Container space, Item item, int idx, boolean incremented, NHBot adder, NHSpace origin) {
        updateStatus();
    }

    public void itemTaken(Container space, Item item, int idx) {
        updateStatus();
    }

    public void itemDestroyed(Container space, Item item, int idx) {
        updateStatus();
    }

    public void itemsDestroyed(Container space, Item[] item) {
        updateStatus();
    }

    private HashSet<String> _unique = new HashSet<String>();
    private void format1(StringBuilder sb) {
        _unique.clear();
        NHBot b = _bot;
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.printf("St %-3d Qu %-3d Ag %-3d Co %-3d Em %-3d In %-3d Pr %-3d Re %-3d Me %-3d SD %-3d   %3d/%-3d  ",
                b.getModifiedStrength(),
                b.getModifiedQuickness(),
                b.getModifiedAgility(),
                b.getModifiedConstitution(),
                b.getModifiedEmpathy(),
                b.getModifiedIntuition(),
                b.getModifiedPresence(),
                b.getModifiedReasoning(),
                b.getModifiedMemory(),
                b.getModifiedSelfDiscipline(),
                b.getHp(), b.getMaxHp()
                );

        for(Affliction a:b.getAfflictions()) {
            String st = a.getStatus();
            if(st!=null) {
                _unique.add(st);
                //pw.printf(" %10s", st);
            }
        }
        for(String s:_unique) {
            pw.printf(" %10s", s);
        }
        sb.setLength(0);
        sb.append(sw.toString());
    }

    private void format2(StringBuilder sb) {
        NHBot b = _bot;
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.printf("%3s the %-12s %31s, Lv. %-2s",
                b.getName(),
                b.getProfession(),
                ((Patsy)b).getLevel().getName(),
                ((Patsy)b).getLevel().getDisplayedFloor()
                );

        for(Item i:b.getInventory().getItem()) {
            if(i.getDisplayType()==Item.DisplayType.status) {
                pw.printf(" %s: %s", i.getModel(), i.getCount());
            }
        }
        sb.setLength(0);
        sb.append(sw.toString());
    }

    private void format(StringBuilder sb) {
        NHBot b = _bot;
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.printf("%3s the %-12s %31s, Lv. %-2s  %3d/%-3d  St %-3d Qu %-3d Ag %-3d Co %-3d Em %-3d In %-3d Pr %-3d Re %-3d Me %-3d SD %-3d",
                b.getName(),
                b.getProfession(),
                ((Patsy)b).getLevel().getName(),
                ((Patsy)b).getLevel().getDisplayedFloor(),
                b.getHp(), b.getMaxHp(),
                b.getModifiedStrength(),
                b.getModifiedQuickness(),
                b.getModifiedAgility(),
                b.getModifiedConstitution(),
                b.getModifiedEmpathy(),
                b.getModifiedIntuition(),
                b.getModifiedPresence(),
                b.getModifiedReasoning(),
                b.getModifiedMemory(),
                b.getModifiedSelfDiscipline()
                );

        for(Item i:b.getInventory().getItem()) {
            if(i.getDisplayType()==Item.DisplayType.status) {
                pw.printf(" %s: %s", i.getModel(), i);
            }
        }
        for(Affliction a:b.getAfflictions()) {
            String st = a.getStatus();
            if(st!=null) {
                //sb.append(" ");
                //sb.append(st);
                pw.printf(" %10s", st);
            }
        }
        sb.setLength(0);
        sb.append(sw.toString());
    }

    private void formatLevel(StringBuilder sb) {
        NHBot b = _bot;
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.printf("%30s Lv %-2d",
                ((Patsy)b).getLevel().getName(),
                ((Patsy)b).getLevel().getFloor()
                );
        sb.setLength(0);
        sb.append(sw.toString());
    }


    private void pad(StringBuffer sb, int length) {
        while(sb.length()<length) {
            sb.append(" ");
        }
    }
}
