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


import org.excelsi.aether.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.util.logging.Logger;


public class Persistence {
    public static final String SAVEFILE = System.getProperty("user.home")+File.separator+".towersave";
    public static final String BACKUPFILE = System.getProperty("user.home")+File.separator+".towerbackup";
    public static final String SCOREFILE = System.getProperty("user.home")+File.separator+".towerscore";
    public static final String PREFIX = "Tower-save-";
    public static final int MAX_SCORES = 20;


    public static String saveDir() {
        String home = System.getProperty("user.home");
        if(System.getProperty("os.name").toLowerCase().contains("mac os")) {
            if(new File(home, "Documents").isDirectory()) {
                home = home + File.separator + "Documents";
            }
        }
        return home;
    }

    public static String fileForGame(Game g) {
        String home = saveDir();
        if(g.getUuid()==null) {
            String uuid = g.getPlayer().getName()+"-"+g.getPlayer().getProfession()+"-"+System.currentTimeMillis();
            g.setUuid(uuid);
        }
        return home+File.separator+PREFIX+g.getUuid();
    }

    public static Summary[] list() {
        String home = saveDir();
        List<Summary> sums = new ArrayList<Summary>();
        for(File f:new File(home).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(PREFIX);
            }})) {
            ObjectInputStream is = null;
            try {
                is = new ThreadContextObjectInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(f))));
                Summary s = (Summary) is.readObject();
                s.setFile(f);
                sums.add(s);
            }
            catch(Exception e) {
                e.printStackTrace();
                Logger.global.fine(e.toString());
            }
            finally {
                try { is.close(); } catch(IOException e) {}
            }
        }
        return (Summary[]) sums.toArray(new Summary[0]);
    }

    //public static boolean exists() {
        //return new File(SAVEFILE).exists();
    //}

    public static Game load(Summary s) {
        return load(s.getFile());
    }

    public static Game load(File f) {
        ObjectInputStream is = null;
        try {
            is = new ThreadContextObjectInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(f), 32768)));
            is.readObject(); // summary
            Game g = (Game) is.readObject();
            return g;
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
            Logger.global.severe("couldn't load saved game from "+f);
            return null;
            //throw new Error(e);
        }
        catch(IOException e) {
            e.printStackTrace();
            Logger.global.severe("couldn't load saved game from "+f);
            return null;
            //throw new Error(e);
        }
        finally {
            if(is!=null) {
                try {
                    is.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void save(Game g) {
        write(g, fileForGame(g));
    }

    //public static void backup(Game g) {
        //write(g, BACKUPFILE);
    //}

    private static void write(Game g, String file) {
        ObjectOutputStream save = null;
        Summary s = new Summary();
        s.setName(g.getPlayer().getName());
        s.setProf(g.getPlayer().getProfession());
        s.setFloor(g.getCurrentLevel().getName()+", Lv. "+g.getCurrentLevel().getDisplayedFloor());
        try {
            save = new ObjectOutputStream(new GZIPOutputStream(new BufferedOutputStream(
                new FileOutputStream(file), 32768)));
            save.writeObject(s);
            save.writeObject(g);
        }
        catch(IOException e) {
            throw new Error(e);
        }
        finally {
            if(save!=null) {
                try {
                    save.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void clear(Game g) {
        if(g.getUuid()!=null) {
            new File(fileForGame(g)).delete();
        }
    }

    public static Scores loadScores() {
        if(!new File(SCOREFILE).exists()) {
            return new Scores(MAX_SCORES);
        }
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new BufferedInputStream(
                new FileInputStream(SCOREFILE)));
            return (Scores) in.readObject();
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
            return new Scores(MAX_SCORES);
        }
        catch(IOException e) {
            e.printStackTrace();
            return new Scores(MAX_SCORES);
        }
        finally {
            if(in!=null) {
                try {
                    in.close();
                }
                catch(IOException e) {
                }
            }
        }
    }

    public static void saveScores(Scores s) {
        ObjectOutputStream save = null;
        try {
            save = new ObjectOutputStream(new BufferedOutputStream(
                new FileOutputStream(SCOREFILE)));
            save.writeObject(s);
        }
        catch(IOException e) {
            throw new Error(e);
        }
        finally {
            if(save!=null) {
                try {
                    save.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Persistence() {
    }

    static class Summary implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private String _name;
        private String _prof;
        private String _floor;
        private transient File _file;

        public Summary() {
        }

        private void setFile(File f) { _file = f; }

        private File getFile() { return _file; }

        public void setName(String name) { _name = name; }

        public String getName() { return _name; }

        public void setProf(String prof) { _prof = prof; }

        public String getProf() { return _prof; }

        public void setFloor(String floor) { _floor = floor; }

        public String getFloor() { return _floor; }

        public String toString() {
            return _name+" the "+_prof+", "+_floor;
        }
    }
}
