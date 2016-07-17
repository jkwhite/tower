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


import com.jme.app.FixedFramerateGame;
import com.jme.app.FixedLogicrateGame;
import com.jme.input.*;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.state.*;
import com.jme.system.*;
import com.jme.util.Timer;
import com.jmex.awt.*;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Canvas;
import java.awt.DisplayMode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

import org.excelsi.matrix.*;
import org.excelsi.aether.*;
import java.util.logging.Level;
import java.io.ObjectInputStream;
import com.jme.system.*;
import com.jme.system.lwjgl.LWJGLPropertiesDialog;
//import com.jme.util.LoggingSystem;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.io.StringWriter;
import javax.swing.JDialog;
import java.io.PrintWriter;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.JButton;


public class NH extends FixedFramerateGame {
//public class NH extends FixedLogicrateGame {
    private static NH _instance;
    public static boolean _uistats = false;
    public static boolean _dump = false;

    private Camera _cam;
    private transient Timer _timer;
    private State _state;
    private Data _data;
    private boolean _restore = false;
    private static boolean _textadventure = false;
    private int _dialogBehaviour = -1; //ALWAYS_SHOW_PROPS_DIALOG;
    private TimeStream _time;


    public NH() {
        _instance = this;
    }

    public static NH getInstance() {
        return _instance;
    }

    public void setState(State state) {
        if(!state.isInitialized()) {
            state.init(this, _timer, _cam);
        }
        _state = state;
    }

    public State getState() {
        return _state;
    }

    public void setTimeStream(TimeStream t) {
        _time = t;
    }

    private Dimension _lastDimension;
    public void toggleFullScreen() {
        int h = properties.getHeight();
        int w = properties.getWidth();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        if(_lastDimension!=null) {
            properties.set("WIDTH", ""+_lastDimension.width);
            properties.set("HEIGHT", ""+_lastDimension.height);
        }
        _lastDimension = d;
        properties.set("FULLSCREEN", ""+!properties.getFullscreen());
        recreateWindow();
    }

    private void recreateWindow() {
        int h = properties.getHeight();
        int w = properties.getWidth();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        if(!properties.getFullscreen() && (h>=d.getHeight() || w>=d.getWidth())) {
            DisplayMode[] modes = NHPropertiesDialog.getDisplayModes();
            DisplayMode sel = modes[0];
            boolean first = false;
            for(int i=modes.length-1;i>0;i--) {
                if(h==modes[i].getHeight() && w==modes[i].getWidth()) {
                    first = true;
                }
                if(first && modes[i-1].getHeight()<d.getHeight() && modes[i-1].getWidth()<d.getWidth()) {
                    sel = modes[i-1];
                    break;
                }
            }
            h = sel.getHeight();
            w = sel.getWidth();
        }
        //System.err.println("w="+w+", h="+h+"depth="+properties.getDepth()+", freq="+properties.getFreq()+", fs="+properties.getFullscreen());
        display.recreateWindow(w, h,
                properties.getDepth(), properties.getFreq(),
                properties.getFullscreen());
        _state.getHud().resize();
        properties.save(w, h, properties.getDepth(), properties.getFreq(), properties.getFullscreen(), "LWJGL");
    }

    public void uiSettings() {
        /*
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                adjustResolution();
            }
        });
        */
        //adjustResolution();
        //NHEventDispatcher.PostbackAction a = new NHEventDispatcher.PostbackAction(new AbstractGameAction() {
        GameAction a = new GameAction() {
            public void perform() {
                NH.getInstance().adjustResolution();
            }

            public String getDescription() { return "Toggle full-screen mode."; }

            public String toString() { return "Full screen"; }

            public boolean isRecordable() { return false; }

            public boolean isRepeat() { return false; }
        };
        //a.perform();
        EventQueue.getEventQueue().postback(a);
    }

    public void adjustResolution() {
        if(properties.getFullscreen()) {
            toggleFullScreen();
            properties.set("FULLSCREEN", ""+!properties.getFullscreen());
        }
        MouseInput.get().setCursorVisible(true);
        NHPropertiesDialog dialog = new NHPropertiesDialog(properties, (String)null);
        dialog.setExitOnCancel(false);

        while (dialog.isVisible()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Logger.global.warning("Error waiting for dialog system, using defaults.");
            }
        }
        if(!dialog.isCancelled()) {
            recreateWindow();
        }
        MouseInput.get().setCursorVisible(false);
    }

    public void setData(Data data) {
        _data = data;
        DataSource ds = new YamlDataSource();
        Universe u = new Universe();
        long start = System.currentTimeMillis();
        Universe.setUniverse(ds.populate(u, _data));
        if(_dump) {
            Universe.getUniverse().print();
            System.exit(0);
        }
        /*
        if(Persistence.exists()) {
            Game g = Persistence.load();
            if(g!=null) {
                Universe.getUniverse().setGame(g);
                _restore = true;
            }
        }
        */
    }

    private static boolean _inited = false;
    protected void initGame() {
        if(!_inited) {
            _inited = true;
            //com.jme.util.LoggingSystem.getLoggingSystem().setLevel(java.util.logging.Level.SEVERE);

            //Game g = Universe.getUniverse().getGame();
            //g.init();
            //Mechanics mech = new QuantumMechanics();
            //NHEnvironment.setMechanics(mech);

            Dawn dawn = new Dawn(null, Universe.getUniverse(), _restore);
            dawn.init(this, _timer, _cam);
            _state = dawn;

            /*
            Title sel = new Title(g, Universe.getUniverse(), _restore);
            sel.init(this, _timer, _cam);
            */

            //_state = sel;
            //setState(sel);
        }
    }

    protected void getAttributes() {
        String propfile = System.getProperty("user.home")+"/.towergui";

        properties = new PropertiesIO(propfile);
        boolean loaded = properties.load();

        if ((!loaded && _dialogBehaviour == FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG)
            || _dialogBehaviour == ALWAYS_SHOW_PROPS_DIALOG) {

            //LWJGLPropertiesDialog dialog = new LWJGLPropertiesDialog(properties, (String)null);
            NHPropertiesDialog dialog = new NHPropertiesDialog(properties, (String)null);

            while (dialog.isVisible()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Logger.global.warning("Error waiting for dialog system, using defaults.");
                }
            }

            //if (dialog.isCancelled()) {
                //System.exit(0);
            //}
        }
        else if(!loaded) {
            DisplayMode mode = NHPropertiesDialog.getBestWindowedMode();
            properties.set("FULLSCREEN", "false");
            properties.set("WIDTH", ""+mode.getWidth());
            properties.set("HEIGHT", ""+mode.getHeight());
            //String[] depths = getDepths(resolution, modes);
        }
    }

    protected void update(float interpolation) {
        _state.update(interpolation);
        com.jmex.audio.AudioSystem.getSystem().update();
    }

    protected void render(float interpolation) {
        _state.render(interpolation);
    }

    protected void initSystem() {
        int alphaBits = 0;
        int depthBits = 8;
        int stencilBits = 0;
        int samples = 2;
 
        display = DisplaySystem.getDisplaySystem(properties.getRenderer());
        display.setTitle("Tower");
        //Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        //display.setMinSamples(4);
        display.setMinDepthBits(depthBits);
        display.setMinStencilBits(stencilBits);
        display.setMinAlphaBits(alphaBits);
        display.setMinSamples(samples);
        try {
            display.createWindow(properties.getWidth(), properties.getHeight(),
                    properties.getDepth(), properties.getFreq(),
                    properties.getFullscreen());
        }
        catch(com.jme.system.JmeException e) {
            // may not support multisampling
            display.setMinSamples(0);
            display.createWindow(properties.getWidth(), properties.getHeight(),
                    properties.getDepth(), properties.getFreq(),
                    properties.getFullscreen());
        }
        /*
        TopFrame f = new TopFrame(display, new CanvasImpl(this), properties.getWidth(), properties.getHeight());
        f.setVisible(true);
        */
        _timer = Timer.getTimer();

        display.getRenderer().setBackgroundColor(ColorRGBA.black);

        Camera cam = display.getRenderer().createCamera(display.getWidth(), display.getHeight());
        cam.setFrustumPerspective(45.0f,
            (float) display.getWidth() /
            (float) display.getHeight(), 1, 1000);
        display.getRenderer().setCamera(cam);
        _cam = cam;
    }

    class CanvasImpl extends JMECanvasImplementor {
        private NH _nh;


        public CanvasImpl(NH nh) {
            _nh = nh;
        }

        public void doUpdate() {
            _nh.update(-1.0f);
        }

        public void doRender() {
            _nh.render(-1.0f);
        }
    }

    protected void cleanup() {
    }

    protected void reinit() {
    }

    public static void main(String[] args) {
        System.setSecurityManager(null);
        Logger.getLogger("").setLevel(Level.SEVERE);
        Logger.global.setLevel(Level.WARNING);
        // parse system properties first to allow args to override
        System.setProperty("tower.uifont", "Serif");
        System.setProperty("tower.monouifont", "Monospaced");
        if(System.getProperty("os.name").toLowerCase().indexOf("mac os")>=0) {
            //System.setProperty("tower.monouifont", "Monaco");
            //System.setProperty("tower.uifont", "Garamond Premier Pro");
        }
        System.setProperty("tower.font", "courier");
        File f = new File(System.getProperty("user.home")+"/.towerrc");
        if(f.exists()) {
            try {
                Properties ps = new Properties();
                ps.load(new BufferedInputStream(new FileInputStream(f)));
                for(Enumeration e=ps.propertyNames();e.hasMoreElements();) {
                    String prop = (String) e.nextElement();
                    System.setProperty("tower."+prop, ps.getProperty(prop));
                }
            }
            catch(IOException e) {
                Logger.global.severe("error reading .towerrc: "+e.getMessage());
            }
        }

        _uistats = false;
        List<URL> datas = new ArrayList<URL>();
        URL data = null;

        try {
            for(int i=0;i<args.length;i++) {
                if(args[i].equals("-s")) {
                    System.out.println(Persistence.loadScores().toString());
                    System.exit(0);
                }
                else if(args[i].equals("-uistats")) {
                    _uistats = true;
                }
                else if(args[i].equals("-dump")) {
                    _dump = true;
                }
                //if(i==args.length-1) {
                else {
                    try {
                        String s = args[i];
                        if(s.indexOf(":/")<0) {
                            File cf = new File(s).getCanonicalFile();
                            s = "file://"+cf;
                            if(cf.isDirectory()) {
                                s += "/";
                            }
                        }
                        data = new URL(s);
                        datas.add(data);
                    }
                    catch(MalformedURLException e) {
                        System.err.println("failed to load '"+args[i]+"': "+e.getMessage());
                    }
                    catch(IOException e) {
                        System.err.println("failed to load '"+args[i]+"': "+e.getMessage());
                    }
                }
                //else {
                    //System.err.println("unknown argument '"+args[i]+"'");
                    //System.exit(1);
                //}
            }
        }
        catch(IndexOutOfBoundsException e) {
            System.err.println("malformed option string");
            System.exit(1);
        }
        File resDir = new File(System.getProperty("user.home"), ".tower");
        if(resDir.isDirectory()) {
            try {
                datas.add(resDir.toURL());
            }
            catch(MalformedURLException e) {
                System.err.println("cannot load '"+resDir+"': "+e.toString());
            }
            for(File res:resDir.listFiles()) {
                if(res.getName().endsWith(".jar")) {
                    try {
                        datas.add(res.toURL());
                    }
                    catch(MalformedURLException e) {
                        System.err.println("cannot load '"+res+"': "+e.toString());
                    }
                }
            }
        }
        ClassLoader dataLoader = new URLClassLoader(datas.toArray(new URL[datas.size()]), NH.class.getClassLoader());
        /*
        if(data==null) {
            dataLoader = NH.class.getClassLoader();
        }
        else {
            dataLoader = new URLClassLoader(new URL[]{data}, NH.class.getClassLoader());
        }
        */
        Thread.currentThread().setContextClassLoader(dataLoader);

        final NH nh = new NH();
        try {
            //nh.setData(new Data(data));
            datas.add(null); // indicates classloader loader
            nh.setData(new Data((URL[])datas.toArray(new URL[0])));
        }
        catch(IOException e) {
            System.err.println("failed to load '"+data+"': "+e.getMessage());
            System.exit(1);
        }
        try {
            //nh.setDialogBehaviour(FixedFramerateGame.NEVER_SHOW_PROPS_DIALOG);
            //nh.setDialogBehaviour(FixedFramerateGame.FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
            if(Boolean.getBoolean("tower.textadventure")) {
                new TextAdventure().run();
            }
            else {
                nh.setDialogBehaviour(FixedFramerateGame.ALWAYS_SHOW_PROPS_DIALOG);
                //Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                nh.start();
                /*
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        }
                        catch(InterruptedException e) {
                        }
                        nh.setFrameRate(10);
                        //nh.setLogicTicksPerSecond(5);
                    }
                }.start();
                */
            }
        }
        catch(Throwable t) {
            showError(t);
        }
        if(EventQueue.getEventQueue().getLastError()!=null) {
            showError(EventQueue.getEventQueue().getLastError());
        }
        if(NH.getInstance()!=null&&NH.getInstance()._time!=null) {
            NH.getInstance()._time.interrupt();
        }
    }

    public void exit() {
        finish();
        System.exit(0);
    }

    protected void quit() {
        if (display != null) {
            display.close();
        }
        if(_time!=null&&_time.getError()!=null) {
            showError(_time.getError());
        }
        else {
            System.exit(0);
        }
    }

    public static void showError(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace();
        t.printStackTrace(new PrintWriter(sw));
        JDialog err = new JDialog();
        err.setTitle("Tower Structural Failure");
        err.getContentPane().setLayout(new BorderLayout());
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        JTextArea text = new JTextArea(sw.toString());
        text.setEditable(false);
        p.add(text, BorderLayout.CENTER);
        JButton ok = new JButton("Ok");
        ok.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.exit(-1);
            }
        });
        JPanel south = new JPanel();
        south.add(ok);
        p.add(south, BorderLayout.SOUTH);
        err.getContentPane().add(p, BorderLayout.CENTER);
        err.setSize(new java.awt.Dimension(640,480));
        java.awt.Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        err.setLocation(d.width/2-320, d.height/2-240);
        err.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        err.setVisible(true);
        //System.exit(-1);
    }

    public static class TopFrame extends JFrame {
        private Canvas _c;
        private JMECanvasImplementor _impl;

        public TopFrame(DisplaySystem d, JMECanvasImplementor impl, int w, int h) {
            _c = d.createCanvas(w, h);
            _impl = impl;
            setLayout(new BorderLayout());
            ((JMECanvas)_c).setImplementor(_impl);
            setSize(new java.awt.Dimension(w, h));
            getContentPane().add(_c, BorderLayout.CENTER);
        }
    }
}
