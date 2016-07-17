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


public class Title extends JFrame implements State {
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


    public Title(Game g, BotFactory d, boolean restore) {
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
        if(_restore||Boolean.getBoolean("tower.nointro")) {
            if(_restore) {
                _hud.print((NHBot)null, "Welcome back.");
            }
            toDungeon();
        }
        else {
            //_hud.print((NHBot)null, "Press ? for a list of commands.");
            //toDungeon();
            init2();
            NH.getInstance().setState(this);
        }
    }

    private void sound() {
        /*
        SoundSystem.init(null, SoundSystem.OUTPUT_DEFAULT);
        int nb = SoundSystem.createStream("/Users/jkw/Music/iTunes/iTunes Music/Pink Floyd/From Oblivion/Pink_Floyd_From_Oblivion_105_Set_The_Controls_For_The_Heart_Of_The_Sun.mp3", false);
        if(SoundSystem.isStreamOpened(nb)) {
            SoundSystem.playStream(nb);
        }
        else {
            System.err.println("stream not opened");
        }
        */
    }

    private void createMenu() {
        int shortcut = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        bar.add(file);
        setJMenuBar(bar);
    }

    private void init1(AbstractGame app, Timer t, final Camera camera) {
        //createMenu();
        sound();
        _t = t;
        _input = new InputHandler();

        //REDO
        //InputSystem.createInputSystem("LWJGL");

        final AbsoluteMouse mouse = new AbsoluteMouse("Mouse Input", DisplaySystem.getDisplaySystem().getWidth(), DisplaySystem.getDisplaySystem().getHeight());
        mouse.setCullMode(Spatial.CULL_ALWAYS);
        //final RelativeMouse mouse = new RelativeMouse("Mouse Input");

        /* MOUSEMOUSE
        TextureState cursor = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        cursor.setEnabled(true);
        Texture tex = TextureManager.loadTexture(
                Thread.currentThread().getContextClassLoader().getResource("mouse.png"),
                Texture.MM_LINEAR, Texture.FM_LINEAR);
        cursor.setTexture(tex);
        mouse.setRenderState(cursor);
        */

        //MOUSE
        mouse.registerWithInputHandler(_input);

        //MOUSEMOUSE
        MouseInput.get().setHardwareCursor(Thread.currentThread().getContextClassLoader().getResource("mouse2.png"));

        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();
        //REDO
        //keyboard.setKeyInput(InputSystem.getKeyInput());
        //keyboard.setKeyInput(KeyInput.get());
        //_input.setKeyBindingManager(keyboard);

        // set up states
        _root = new Node("title");
        ZBufferState buf = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);
        _root.setRenderState(buf);
        DirectionalLight light = new DirectionalLight();
        light.setDirection(new Vector3f(1, -1, -1));
        //light.setDiffuse(new ColorRGBA(0.3f, 0.3f, 0.3f, 1.0f));
        //light.setSpecular(new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f));
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

        final int add = 28;
        _level = new MatrixNode("L"+_g.getCurrentLevel().getFloor(), _g.getCurrentLevel(), true);
        Vector3f loc = new Vector3f(_level.getPlayerTranslation()).addLocal(new Vector3f(6, 10, add+6));
        Vector3f left = new Vector3f(-1.0f, 0.0f, -1.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, -1.0f);
        Vector3f dir = new Vector3f(0.0f, -1.0f, 1.0f);
        camera.setFrame(loc, left, up, dir);

        CloseView cv = new CloseView("cv", _root, _level.getPlayerNode(), camera);
        cv.setSpeed(0.04f);
        cv.activate();
        _view = cv;
        _tower = new TowerNode(_g, _level, _view);

        final Node level = _tower;
        FogState fs = DisplaySystem.getDisplaySystem().getRenderer().createFogState();
        fs.setDensity(0.5f);
        fs.setEnabled(true);
        fs.setColor(new ColorRGBA(0.0f,0.0f,0.0f,1.0f));
        fs.setEnd(44);
        fs.setStart(22);
        fs.setDensityFunction(FogState.DF_LINEAR);
        fs.setApplyFunction(FogState.AF_PER_VERTEX);
        level.setRenderState(fs);

        _root.attachChild(level);

        // REDO
        //BWindow keys = new FocusedWindow(/*BLookAndFeel.getDefaultLookAndFeel(), */new BorderLayout());
        BWindow keys = new FocusedWindow(HUD.getStyle("glass"), new BorderLayout());
        keys.setSize(DisplaySystem.getDisplaySystem().getWidth(), DisplaySystem.getDisplaySystem().getHeight());
        _dispatcher = new NHEventDispatcher(app, Universe.getUniverse());
        _dispatcher.setPatsy(_g.getPlayer());
        keys.addListener(_dispatcher);
        final ArrayList state = new ArrayList();
        state.add("red sea");
        keys.addListener(new EventListener() {
            public void eventDispatched(BEvent e) {
                if(state.size()==1) {
                    //_root.detachChild(mouse);
                    state.remove(0);
                    MouseInput.get().setCursorVisible(false);
                    //System.err.println("DETACH");
                }
            }
        });


        //MOUSE
        _root.attachChild(mouse);

        //keys.addListener(new MouseUpdater(mouse));
        //_input.addAction(button, InputHandler.DEVICE_ALL);
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
        _hud = new HUD(_g.getPlayer(), _proot, _dispatcher.getInputSource());
        _hud.setKeyWindow(keys);
        _dispatcher.setNarrative(_hud);

        GameAction initial = null;
        if(!_restore) {
            initial = new AbstractGameAction() {
                public void perform() {
                    go();
                }
            };
        }
        TimeStream time = new TimeStream(_g, _dispatcher.getInputSource(), _hud, initial);
        //time.setPriority(Thread.MIN_PRIORITY);
        time.start();
        _timestream = time;
        NH.getInstance().setTimeStream(_timestream);

        _fog = new FogController(level, new FogController.Modulator() {
            public boolean modulate(FogState fs, float time) {
                fs.setStart(time);
                fs.setEnd(2*time);
                //return time<22;
                return time<999;
            }
        }, 0.9f) {
            protected void done() {
                level.clearRenderState(RenderState.RS_FOG);
                level.removeController(this);
                level.updateRenderState();
            }
        };
        level.addController(_fog);

        _root.updateRenderState();

        _initialized = true;

        AlphaState as1 = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
        as1.setBlendEnabled(true);
        //as1.setSrcFunction(AlphaState.SB_ZERO);
        //as1.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_COLOR);
        as1.setTestEnabled(true);
        //as1.setTestFunction(AlphaState.TF_GREATER);
        _root.setRenderState(as1);
        //mouse.setRenderState(as1);
        _root.updateRenderState();

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

    private static class MouseUpdater implements EventListener {
        private Mouse _mouse;

        public MouseUpdater(Mouse m) {
            _mouse = m;
        }

        public void eventDispatched(BEvent e) {
            //System.err.println(e);
            if(e instanceof MouseEvent) {
                //REDO
                //_mouse.update();
            }
        }
    }

    static class Reveal {
        public BCurveController revealer;
        public BWindow[] windows;
        public BWindow mask;
    }

    static Reveal createRevealer(String story, final PolledRootNode proot, final Title t, final TimeStream timest) {
        final Reveal reveal = new Reveal();
        String[] segments = story.split("#");
        final int INC = 24;
        int y = DisplaySystem.getDisplaySystem().getHeight()/2+segments.length*INC/2+50;
        int height = y+INC;
        reveal.windows = new BWindow[segments.length];
        int minX = DisplaySystem.getDisplaySystem().getWidth();
        int width = DisplaySystem.getDisplaySystem().getWidth();
        for(int i=0;i<segments.length;i++) {
            String s = segments[i];
            BWindow window = new BWindow(HUD.getStyle("story"), new BorderLayout());
            BLabel c = new BLabel(s);
            window.add(c, BorderLayout.CENTER);
            window.setBounds(300, 125, 400, 150);
            window.center();
            window.setLocation((int)((window.getX()+width)/2.5), y);
            proot.addWindow(window);
            reveal.windows[i] = window;
            y -= INC;
            minX = (int) Math.min(reveal.windows[i].getX(), minX);
        }
        height = height - y;
        BWindow revealer = new BWindow(HUD.getStyle("revealer"), new BorderLayout());
        revealer.setSize(DisplaySystem.getDisplaySystem().getWidth()-minX, height);
        revealer.setLocation(minX, 0);
        proot.addWindow(revealer);
        reveal.mask = revealer;
        final int done = y-height+INC;
        BCurveController curve = new BCurveController(revealer,
            new BezierCurve("r", new Vector3f[]{new Vector3f(minX, y+86, 0), new Vector3f(minX, y-height, 0)}),
            0.02f) {
            public void update(float dt) {
                super.update(dt);
                if(reveal.mask.getY()<=done) {
                    proot.removeController(reveal.revealer);
                    reveal.revealer = null;
                    proot.removeWindow(reveal.mask);
                    if(getSpeed()!=1f) {
                        // hack checking if user already pressed key to start game
                        timest.interrupt();
                        t.go();
                    }
                }
            }
        };
        reveal.revealer = curve;
        proot.addController(curve);
        return reveal;
    }

    private void init2() {
        final PolledRootNode proot = _proot;
        final Node level = _tower;
        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();

        String story = Universe.getUniverse().getStory();
        if(story==null) {
            story = "The Tales of Solar Sail.#Dark Stars in the Dazzling Sky.";
        }
        Reveal reveal = createRevealer(story, _proot, this, _timestream);
        _mask = reveal.mask;
        _revealer = reveal.revealer;
        _windows = reveal.windows;
    }

    static class GoAction extends KeyInputAction {
        private Title _t;


        public GoAction(Title t) {
            _t = t;
        }

        public void performAction(InputActionEvent e) {
            Title t = _t;
            _t = null; // GC
            if(t!=null) {
                t._input.removeAction(this);
                t.go();
                t = null;
            }
        }
    }

    public boolean isInitialized() {
        return _initialized;
    }

    public void update(float interpolation) {
        EventQueue.getEventQueue().play();
        _t.update();
        float tpf = _t.getTimePerFrame();
        _root.updateWorldData(tpf);
    }

    public void render(float interpolation) {
        DisplaySystem.getDisplaySystem().getRenderer().clearBuffers();
        DisplaySystem.getDisplaySystem().getRenderer().draw(_root);
    }

    public Node getRoot() {
        return _root;
    }

    private boolean _already = false;
    void go() {
        if(!_restore) {
            if(_revealer!=null) {
                _revealer.setSpeed(1f);
            }
            if(_windows!=null) {
                for(int i=0;i<_windows.length;i++) {
                    _proot.addController(new BSlideInOutController(_windows[i],
                                i%2==0?BSlideInOutController.EXIT_NORTH:
                                       BSlideInOutController.EXIT_SOUTH));
                }
            }
            if(!_already) {
                toDungeon();
            }
        }
    }

    void toDungeon() {
        if(!_restore) {
            _hud.print((NHBot)null, "Press ? for a list of commands.");
        }
        _already = true;
        _fog.setSpeed(12f);
        DungeonState ds = new DungeonState(_g, _root, _tower, _proot, _hud);
        ((CloseView)_view).setSpeed(4000f);
        ds.setView(_view);
        _dispatcher.setViewer(ds);
        _level = null; // GC
        NH.getInstance().setState(ds);
    }

    static class BFixArea extends BTextArea {
        /*
        public void setBackground(BBackground b) {
            super.setBackground(b);
            _background = b;
        }
        */
    }
}
