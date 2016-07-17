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
import java.util.Map;
import java.util.HashMap;


public class Extended extends DefaultNHBotAction {
    private static final Map<String,GameAction> _cmds = new HashMap<String,GameAction>();

    static {
        _cmds.put("scores", new Scores());
        _cmds.put("skills", new Patsy.Skills());
        _cmds.put("quit", new Patsy.Exit());
        _cmds.put("about", new About());
        _cmds.put("belt", new Belt());
        _cmds.put("help", new Help());
        _cmds.put("_gc", new GC());
    }


    public Extended() {
    }

    public static void addCommand(String cmd, GameAction action) {
        _cmds.put(cmd, action);
    }

    public static Map<String,GameAction> getCommands() {
        return new HashMap<String,GameAction>(_cmds);
    }

    public String getDescription() {
        return "Access extended commands.";
    }

    public void perform() {
        StringBuilder cmd = new StringBuilder();

        for(;;) {
            String c = N.narrative().replyc(getBot(), "#"+cmd);
            if(c.equals("BACK")) {
                if(cmd.length()>0) {
                    cmd.setLength(cmd.length()-1);
                }
                continue;
            }
            else if(c.equals("ENTER")) {
                break;
            }
            else if(c.equals("TAB")) {
                continue;
            }
            else if(c.equals("ESCAPE")) {
                N.narrative().clear();
                throw new ActionCancelledException();
            }
            else {
                cmd.append(c);
                int count = 0;
                String found = null;
                for(String k:_cmds.keySet()) {
                    if(k.startsWith(cmd.toString())&&!k.startsWith("_")) {
                        found = k;
                        count++;
                    }
                }
                if(count==1) {
                    cmd.setLength(0);
                    cmd.append(found);
                }
            }
        }
        N.narrative().clear();
        GameAction a = _cmds.get(cmd.toString());
        if(a==null) {
            N.narrative().print(getBot(), "'#"+cmd+"' is not a command.");
        }
        else {
            if(a instanceof NHBotAction) {
                ((NHBotAction)a).init();
                ((NHBotAction)a).setBot(getBot());
            }
            a.perform();
        }
    }

    static class Scores extends DefaultNHBotAction {
        public String getDescription() {
            return "Display high scores.";
        }

        public void perform() {
            N.narrative().showScores();
        }

        public String toString() {
            return "Scores";
        }
    }

    static class About extends DefaultNHBotAction {
        public String getDescription() {
            return "Display credits.";
        }

        public void perform() {
            N.narrative().display(getBot(), "Tower\nby John K White <dhcmrlchtdj@gmail.com>\n \n(c) 2006, 2007, 2008 John K White", true);
        }
    }

    static class Belt extends DefaultNHBotAction {
        public String getDescription() {
            return "Display list of kills.";
        }

        public void perform() {
            Patsy p = (Patsy) getBot();
            Map<String,Patsy.Notch> kills = p.getKills();
            int total = 0;
            for(Patsy.Notch n:kills.values()) {
                total += n.getCount();
            }
            boolean only = kills.size()<2;
            String end = total>0?": ":".";
            N.narrative().print(p, "Your belt has "+total+(total==1?" notch":" notches")+end);
            String last = null;
            for(Map.Entry<String,Patsy.Notch> e:kills.entrySet()) {
                if(last!=null) {
                    N.narrative().print(p, last+", ");
                }
                int c = e.getValue().getCount();
                String name = e.getKey();
                last = c+" "+(c==1?name:Grammar.pluralize(name));
            }
            if(last!=null) {
                if(only) {
                    N.narrative().print(p, last+".");
                }
                else {
                    N.narrative().print(p, "and "+last+".");
                }
            }
        }
    }

    static class GC extends DefaultNHBotAction {
        public void perform() {
            System.gc();
        }

        public String getDescription() {
            return "Runs an explicit full garbage collection.";
        }
    }
}
