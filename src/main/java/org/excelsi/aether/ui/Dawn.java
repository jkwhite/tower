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


import com.jme.scene.*;
import com.jmex.bui.*;
import com.jmex.bui.background.*;
import com.jmex.bui.layout.*;
import com.jmex.bui.event.*;
import com.jme.util.Timer;
import com.jme.app.AbstractGame;
import com.jme.system.DisplaySystem;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Camera;
import com.jme.input.*;
import com.jme.curve.BezierCurve;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.input.action.*;
import com.jme.light.*;
import com.jme.scene.state.*;
import com.jme.curve.*;
import com.jme.image.*;
//import com.jmex.sound.fmod.SoundSystem;

import com.jmex.bui.layout.BorderLayout;
import org.excelsi.aether.*;
//import com.jme.scene.state.TextureState;
//import com.jme.image.Texture;
import com.jme.util.TextureManager;
import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;


public class Dawn extends JFrame implements State {
    private boolean _initialized;
    private Node _root;
    private Timer _t;
    private InputHandler _input;
    private BotFactory _data;
    private Game _g;
    private PolledRootNode _proot;
    private BWindow[] _windows;
    private BWindow _mask;
    private Controller _revealer;
    private Controller _fog;
    private Controller _light;
    private DirectionalLight _ambient;
    private NHEventDispatcher _dispatcher;
    private View _view;
    private MatrixNode _level;
    private TowerNode _tower;
    private HUD _hud;
    private TimeStream _timestream;
    private boolean _restore;
    private String _choice;
    private Runnable _action;
    private AbstractGame _app;
    private Camera _camera;


    public Dawn(Game g, BotFactory d, boolean restore) {
        _g = g;
        _data = d;
        _restore = restore;
    }

    public HUD getHud() {
        return _hud;
    }

    public void init(com.jme.app.AbstractGame app, Timer t, final Camera camera) {
        init1(app, t, camera);
        _initialized = true;
    }

    private void createMenu() {
        int shortcut = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        bar.add(file);
        setJMenuBar(bar);
    }

    private void init1(final AbstractGame app, final Timer t, final Camera camera) {
        //createMenu();
        _t = t;
        _input = new InputHandler();
        _camera = camera;
        _app = app;

        final AbsoluteMouse mouse = new AbsoluteMouse("Mouse Input", DisplaySystem.getDisplaySystem().getWidth(), DisplaySystem.getDisplaySystem().getHeight());
        mouse.setCullMode(Spatial.CULL_ALWAYS);
        mouse.registerWithInputHandler(_input);

        MouseInput.get().setHardwareCursor(Thread.currentThread().getContextClassLoader().getResource("mouse2.png"));

        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();

        // set up states
        _root = new Node("title");
        ZBufferState buf = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);
        _root.setRenderState(buf);
        DirectionalLight light = new DirectionalLight();
        light.setDirection(new Vector3f(1, -1, -1));
        light.setDiffuse(new ColorRGBA(0.05f, 0.05f, 0.05f, 1.0f));
        light.setSpecular(new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f));
        light.setAmbient(new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
        light.setEnabled(true);

        LightState lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        lightState.setEnabled(true);
        lightState.attach(light);
        lightState.setTwoSidedLighting(true);
        _root.setRenderState(lightState);
        _ambient = light;

        PolledRootNode proot = new PolledRootNode(t, _input, false);
        proot.setInitialRepeatDelay(Long.MAX_VALUE/2L);
        _root.attachChild(proot);
        _proot = proot;

        final int wid = 800;
        final int wid2 = 712;
        final int wid3 = 256;
        final int hei = 324;
        BWindow title = new BWindow(HUD.getStyle("inventory"), new BorderLayout());
        //title.setSize(wid, (int) (DisplaySystem.getDisplaySystem().getHeight()*0.8));
        //title.setSize(DisplaySystem.getDisplaySystem().getWidth(), 324);
        title.setSize(DisplaySystem.getDisplaySystem().getWidth(), DisplaySystem.getDisplaySystem().getHeight());
        //title.setLocation(DisplaySystem.getDisplaySystem().getWidth()/2-wid/2, (int) (DisplaySystem.getDisplaySystem().getHeight()*0.1));
        //title.setLocation(0, DisplaySystem.getDisplaySystem().getHeight()-hei-100);
        title.setLocation(0, 0);
        _proot.addWindow(title);

        BWindow tower = new BWindow(HUD.getStyle("tower"), new BorderLayout());
        tower.setSize(712, 324);
        tower.setLocation(DisplaySystem.getDisplaySystem().getWidth()/2-wid2/2, DisplaySystem.getDisplaySystem().getHeight()-hei-100);
        _proot.addWindow(tower);

        BWindow menu = new BWindow(HUD.getStyle("mainmenu"), new TableLayout(1));
        menu.setSize(wid3, (int) (DisplaySystem.getDisplaySystem().getHeight()/2.3));
        menu.setLocation(DisplaySystem.getDisplaySystem().getWidth()/2-wid3/2, 0);
        final KeyedSelector ks = new KeyedSelector(null,
            new String[]{"New game", "Load game", "High scores", "Quit"}, new String[]{"n", "l", "h", "q"},
            VGroupLayout.LEFT);
        menu.add(ks);
        _proot.addWindow(menu);
        ks.refresh();

        BWindow credits = new BWindow(HUD.getStyle("credits"), new BorderLayout());
        BLabel cred = new BLabel("(c) 2007-2013 John K White");
        credits.add(cred, BorderLayout.CENTER);
        credits.setSize(DisplaySystem.getDisplaySystem().getWidth(), 32);
        credits.setLocation(0,0);
        _proot.addWindow(credits);

        final BWindow keys = new FocusedWindow(HUD.getStyle("glass"), new BorderLayout());
        keys.setSize(DisplaySystem.getDisplaySystem().getWidth(), DisplaySystem.getDisplaySystem().getHeight());
        final ArrayList state = new ArrayList();
        state.add("red sea");
        final LinkedList<String> titlekeys = new LinkedList<String>();
        keys.addListener(new EventListener() {
            public void eventDispatched(BEvent e) {
                if(e instanceof KeyEvent) {
                    KeyEvent ke = (KeyEvent) e;
                    if(ke.getType()==ke.KEY_PRESSED) {
                        int code = ke.getKeyCode();
                        if(code==KeyInput.KEY_LSHIFT||code==KeyInput.KEY_RSHIFT
                            ||code==KeyInput.KEY_LCONTROL||code==KeyInput.KEY_RCONTROL
                            ||code==KeyInput.KEY_LMENU||code==KeyInput.KEY_RMENU
                            ||code==KeyInput.KEY_LWIN||code==KeyInput.KEY_RWIN) {
                            // don't process modifier keys
                            return;
                        }
                        String val = ""+ke.getKeyChar();
                        titlekeys.add(val);
                        synchronized(titlekeys) {
                            titlekeys.notify();
                        }
                    }
                }
                if(state.size()==1) {
                    state.remove(0);
                    MouseInput.get().setCursorVisible(false);
                }
            }
        });

        Thread chooser = new Thread("Dawn input") {
            public void run() {
                KeySource iks = new KeySource() {
                    public String nextKey() throws InputInterruptedException {
                        while(titlekeys.isEmpty()) {
                            synchronized(titlekeys) {
                                try {
                                    titlekeys.wait();
                                }
                                catch(InterruptedException e) {
                                }
                            }
                        }
                        return titlekeys.remove(0);
                    }
                };
                while(true) {
                    Object[] res = ks.choose(iks, 1);
                    if(res.length==0) continue;
                    String choice = (String) res[0];
                    if("Quit".equals(choice)) {
                        _action = new Runnable() { public void run() { NH.getInstance().exit(); } };
                    }
                    else if("New game".equals(choice)) {
                        _action = new Runnable() { public void run() {
                            _proot.removeWindow(keys);
                            Game g = Universe.getUniverse().getGame();
                            g.init();
                            Mechanics mech = new QuantumMechanics();
                            NHEnvironment.setMechanics(mech);
                            Title sel = new Title(g, Universe.getUniverse(), false);
                            sel.init(_app, _t, _camera);
                            NH.getInstance().setState(sel);
                        } };
                    }
                    else if("Load game".equals(choice)) {
                        final BWindow shade = new BWindow(HUD.getStyle("shade"), new VGroupLayout());
                        final BWindow loadmenu = new BWindow(HUD.getStyle("loadgame"), new TableLayout(1));
                        Persistence.Summary[] gs = Persistence.list();
                        final KeyedSelector games = new KeyedSelector("Game", gs, null, VGroupLayout.LEFT);
                        _action = new Runnable() { public void run() {
                            //loadmenu.setSize(wid3, DisplaySystem.getDisplaySystem().getHeight()/2);
                            //loadmenu.setLocation(DisplaySystem.getDisplaySystem().getWidth()/2-wid3/4, 0);
                            shade.setSize(DisplaySystem.getDisplaySystem().getWidth(), DisplaySystem.getDisplaySystem().getHeight());
                            _proot.addWindow(shade);
                            shade.center();
                            games.refresh();
                            loadmenu.add(games);
                            //loadmenu.setSize(games.getPreferredSize(0,0).width+10, games.getPreferredSize(0,0).height+10);
                            loadmenu.setSize(800, 400);
                            loadmenu.setLocation(DisplaySystem.getDisplaySystem().getWidth()/2-loadmenu.getWidth()/2,
                                    DisplaySystem.getDisplaySystem().getHeight()/2-loadmenu.getHeight()/2);
                            _proot.addWindow(loadmenu);
                        } };
                        final Object[] lres = games.choose(iks, 1);
                        _action = new Runnable() { public void run() {
                            _proot.removeWindow(shade);
                            _proot.removeWindow(loadmenu);
                            ks.refresh();
                            if(lres!=null&&lres.length==1) {
                                Persistence.Summary ch = (Persistence.Summary) lres[0];
                                Game g = Persistence.load(ch);
                                Universe.getUniverse().setGame(g);
                                Mechanics mech = new QuantumMechanics();
                                NHEnvironment.setMechanics(mech);
                                Title sel = new Title(g, Universe.getUniverse(), true);
                                sel.init(_app, _t, _camera);
                                //NH.getInstance().setState(sel);
                            }
                        } };
                    }
                    else if("High scores".equals(choice)) {
                        final BWindow shade = new BWindow(HUD.getStyle("shade"), new VGroupLayout());
                        shade.setSize(DisplaySystem.getDisplaySystem().getWidth(), DisplaySystem.getDisplaySystem().getHeight());
                        _proot.addWindow(shade);
                        shade.center();
                        final BWindow w = new BWindow(HUD.getStyle("scores"), new BorderLayout());
                        _action = new Runnable() { public void run() {
                            ScoreContainer sc = new ScoreContainer(Persistence.loadScores(), -1, null);
                            w.add(sc, BorderLayout.CENTER);
                            _proot.addWindow(w);
                            w.setSize(sc.getPreferredSize(0,0).width+10, sc.getPreferredSize(0,0).height+10);
                            w.setLocation(DisplaySystem.getDisplaySystem().getWidth()/2-w.getWidth()/2,
                                    DisplaySystem.getDisplaySystem().getHeight()/2-w.getHeight()/2);
                        } };
                        String nk = iks.nextKey();
                        _action = new Runnable() { public void run() {
                            _proot.removeWindow(shade);
                            _proot.removeWindow(w);
                            ks.refresh();
                        } };
                    }
                }
            }
        };
        chooser.start();


        //MOUSE
        _root.attachChild(mouse);

        MouseInput.get().addListener(new MouseInputListener() {
            public void onButton(int button, boolean pressed, int x, int y) {
                attach();
                //System.err.println("button: "+button+", pressed: "+pressed+", x="+x+", y="+y);
                Vector3f v = DisplaySystem.getDisplaySystem().getWorldCoordinates(new Vector2f(x, y), 0f);
                //System.err.println(v);
            }

            public void onMove(int dx, int dy, int nx, int ny) {
                attach();
            }

            public void onWheel(int wd, int x, int y) {
                attach();
            }

            public void attach() {
                if(state.size()==0) {
                    //_root.attachChild(mouse);
                    state.add("red sea");
                    MouseInput.get().setCursorVisible(true);
                    //System.err.println("ATTACH");
                }
            }
        });

        _proot.addWindow(keys);
        keys.requestFocus();
    }

    private static class FocusedWindow extends BWindow {
        public FocusedWindow(BStyleSheet s, BLayoutManager lm) {
            super(s, lm);
        }

        public boolean acceptsFocus() {
            return true;
        }

        public boolean isOverlay() {
            return true;
        }

        public boolean isModal() {
            return true;
        }

        public BComponent getHitComponent(int mx, int my) {
            return this;
        }
    }

    public boolean isInitialized() {
        return _initialized;
    }

    public void update(float interpolation) {
        _t.update();
        float tpf = _t.getTimePerFrame();
        _root.updateWorldData(tpf);
        if(_action!=null) {
            _action.run();
            _action = null;
        }
    }

    public void render(float interpolation) {
        DisplaySystem.getDisplaySystem().getRenderer().clearBuffers();
        DisplaySystem.getDisplaySystem().getRenderer().draw(_root);
    }

    public Node getRoot() {
        return _root;
    }
}
