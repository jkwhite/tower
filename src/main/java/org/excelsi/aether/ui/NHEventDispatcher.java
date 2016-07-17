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


import com.jme.app.AbstractGame;
import com.jme.input.*;
import com.jme.input.*;
import com.jme.input.action.*;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.state.*;
import com.jme.system.*;
import com.jme.util.Timer;

import com.jmex.bui.*;
import com.jmex.bui.background.*;
import com.jmex.bui.event.*;
import com.jmex.bui.layout.BorderLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.excelsi.matrix.*;
import org.excelsi.aether.*;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;


/** 
 * Listens for key events from BUI and translates them
 * into actions.
 */
public class NHEventDispatcher implements EventListener, ActionSource, java.io.Serializable {
    interface Keymap {
        boolean equals(Object o);
        int hashCode();
    }

    static class NewKeymap2 implements Keymap {
        public static final int SHIFT = 1;
        public static final int CTRL = 2;

        private int _code;
        private String _key;


        public NewKeymap2(String key) {
            _key = key;
            boolean ctrl = false, meta = false;
            //System.err.print("created key "+key);
            if(key.startsWith("C-")) {
                ctrl = true;
                key = key.substring("C-".length()).toUpperCase();
            }
            if(key.startsWith("M-")) {
                meta = true;
                key = key.substring("M-".length());
            }
            if(key.length()==1) {
                char c = key.charAt(0);
                _code = (int) c;
            }
            else {
                if(SPECIAL.containsKey(key)) {
                    _code = SPECIAL.get(key);
                    //System.err.println("MAP KEY "+key+" TO "+_code);
                }
            }
            if(ctrl) {
                _code -= 64;
            }
            if(meta) {
                _code += 256;
            }
            //System.err.println(" with code "+_code);
        }

        public NewKeymap2(int code, boolean isMetaDown) {
            _code = code;
            if(isMetaDown) {
                _code += 256;
            }
        }

        public int hashCode() {
            return _code;
        }

        public String getKey() {
            return _key;
        }

        public boolean equals(Object o) {
            NewKeymap2 nk = (NewKeymap2) o;
            return _code == nk._code;
        }

        public String toString() {
            return _key+" ("+_code+")";
        }
    }

    /** different host environments handle a shift keycode differently */
    private static final boolean OS_DESHIFT = System.getProperty("os.name").indexOf("Linux")>=0;
    //private static final boolean OS_DESHIFT = true;

    private static final Map<Integer, String> KEYMAP = new HashMap<Integer, String>();
    static {
        KEYMAP.put(KeyInput.KEY_ESCAPE, "ESCAPE");
        KEYMAP.put(KeyInput.KEY_RETURN, "ENTER");
        KEYMAP.put(KeyInput.KEY_TAB, "TAB");
        KEYMAP.put(KeyInput.KEY_BACK, "BACK");
        KEYMAP.put(KeyInput.KEY_LEFT, "LEFT");
        KEYMAP.put(KeyInput.KEY_RIGHT, "RIGHT");
        KEYMAP.put(KeyInput.KEY_UP, "UP");
        KEYMAP.put(KeyInput.KEY_DOWN, "DOWN");
        KEYMAP.put(KeyInput.KEY_HOME, "HOME");
        KEYMAP.put(KeyInput.KEY_PGUP, "PGUP");
        KEYMAP.put(KeyInput.KEY_PGDN, "PGDN");
        KEYMAP.put(KeyInput.KEY_END, "END");
    }

    private static final Map<String, Integer> SPECIAL = new HashMap<String, Integer>();
    static {
        SPECIAL.put("ESCAPE", 27);
        SPECIAL.put("UP", 200);
        SPECIAL.put("LEFT", 203);
        SPECIAL.put("RIGHT", 205);
        SPECIAL.put("DOWN", 208);
        SPECIAL.put("PGDN", 209);
        SPECIAL.put("PGUP", 201);
        SPECIAL.put("HOME", 199);
        SPECIAL.put("END", 207);
        SPECIAL.put("ENTER", (int) '\n');
        SPECIAL.put("TAB", (int) '\t');
        SPECIAL.put("BACK", (int) '\b');
    }

    private Universe _universe;
    private Narrative _narrative;
    private Patsy _patsy;
    private View _viewer;
    private Map<Keymap, GameAction> _actions = new HashMap<Keymap, GameAction>();
    private Map<Keymap, GameAction> _overlays = new HashMap<Keymap, GameAction>();
    private Time _time;
    private InputQueue _input = new InputQueue(this);
    private AbstractGame _app;


    public NHEventDispatcher(AbstractGame app, Universe universe) {
        _universe = universe;
        _app = app;
        mapKeys(_actions, universe.getKeymap());
    }

    protected void mapKeys(Map<Keymap,GameAction> actions, Map<String,String> keymap) {
        int i=0;
        for(Map.Entry<String,String> binding:keymap.entrySet()) {
            String key = binding.getKey();
            String value = binding.getValue();
            ++i;
            if(key==null||key.length()==0) {
                throw new IllegalArgumentException("keybinding "+i+" is null");
            }
            if(value==null) {
                throw new IllegalArgumentException("null binding for key '"+key+"'");
            }
            Keymap m = new NewKeymap2(key);

            String a = _universe.actionFor(value);
            if(a==null) {
                throw new IllegalArgumentException("no such action '"+value+"'");
            }
            try {
                GameAction action = null;
                if(value.equals("switchview")) {
                    // special case
                    action = new PostbackAction(new AbstractGameAction() {
                        public void perform() {
                            nextView();
                        }

                        public String getDescription() { return "Change the camera angle."; }

                        public String toString() { return "Switch view"; }
                    });
                }
                else if(value.equals("zoomin")) {
                    // special case
                    action = new PostbackAction(new AbstractGameAction() {
                        public void perform() {
                            _viewer.zoomIn();
                        }

                        public String getDescription() { return "Move the camera closer."; }

                        public String toString() { return "Zoom in"; }
                    });
                }
                else if(value.equals("zoomout")) {
                    // special case
                    action = new PostbackAction(new AbstractGameAction() {
                        public void perform() {
                            _viewer.zoomOut();
                        }

                        public String getDescription() { return "Move the camera farther away."; }

                        public String toString() { return "Zoom out"; }
                    });
                }
                else if(value.equals("togglefs")) {
                    // special case
                    action = new PostbackAction(new AbstractGameAction() {
                        public void perform() {
                            NH.getInstance().toggleFullScreen();
                        }

                        public String getDescription() { return "Toggle full-screen mode."; }

                        public String toString() { return "Full screen"; }
                    });
                }
                else if(value.equals("uisettings")) {
                    // special case
                    action = new PostbackAction(new AbstractGameAction() {
                        public void perform() {
                            NH.getInstance().adjustResolution();
                        }

                        public String getDescription() { return "Toggle full-screen mode."; }

                        public String toString() { return "Full screen"; }
                    });
                }
                else {
                    Class aclass = Thread.currentThread().getContextClassLoader().loadClass(a);
                    if(aclass == KeyExitAction.class) {
                        // special case
                        action = new PostbackAction(new AbstractGameAction() {
                            public void perform() {
                                _app.finish();
                            }
                        });
                    }
                    else if(aclass == KeyScreenShotAction.class) {
                        // special case
                        action = new PostbackAction(new AbstractGameAction() {
                            private int count = 0;
                            public String getDescription() { return "Take a screen shot."; }
                                
                            public void perform() {
                                new KeyScreenShotAction("screen-"+count).performAction(null);
                                ++count;
                            }

                            public String toString() { return "Screenshot"; }
                        });
                    }
                    else if(InputAction.class.isAssignableFrom(aclass)) {
                        final KeyInputAction delegate = (KeyInputAction) aclass.newInstance();
                        action = new PostbackAction(new AbstractGameAction() {
                            public void perform() {
                                delegate.performAction(null);
                            }
                        });
                    }
                    else if(NHBotAction.class.isAssignableFrom(aclass)) {
                        action = new DelegatingAction((NHBotAction) aclass.newInstance());
                        ((DelegatingAction)action).setKeybinding(key);
                    }
                    else if(GameAction.class.isAssignableFrom(aclass)) {
                        action = (GameAction) aclass.newInstance();
                    }
                    if(action == null) {
                        throw new IllegalArgumentException("unknown action type '"+aclass.getName()+"'");
                    }
                }
                actions.put(m, action);
                Logger.global.fine("bound key '"+key+"' to "+value);
            }
            catch(ClassNotFoundException e) {
                throw new IllegalArgumentException("class '"+a+"' not found for action '"+value+"'");
            }
            catch(InstantiationException e) {
                throw new IllegalArgumentException("class '"+a+"' not instantiable for action '"+value+"': "+e.getMessage());
            }
            catch(IllegalAccessException e) {
                throw new IllegalArgumentException("class '"+a+"' has no public default constructor for action '"+value+"': "+e.getMessage());
            }
        }
    }

    public void setViewer(View v) {
        _viewer = v;
    }

    public void setPatsy(Patsy p) {
        _patsy = p;
    }

    public void setNarrative(Narrative n) {
        _narrative = n;
    }

    //private StringBuffer _history = new StringBuffer(1024);
    private StringBuilder _count = new StringBuilder();
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
                    //System.err.println("RETURN");
                    return;
                }
                char ch = ke.getKeyChar();
                if(ch>='1'&&ch<='0') {
                    _count.append(ch);
                    return;
                }
                //System.err.println("Looking up "+ke.getKeyCode()+"/"+(int)ke.getKeyChar()+" (char "+ke.getKeyChar()+")");
                //System.err.println("MOD: :"+ke.getModifiers());
                //System.err.println("META: "+((ke.getModifiers()&KeyEvent.ALT_DOWN_MASK)!=0));
                Keymap m = new NewKeymap2((int)ke.getKeyChar(), (ke.getModifiers()&KeyEvent.ALT_DOWN_MASK)!=0);

                String val = KEYMAP.get(ke.getKeyCode());
                if(val==null) {
                    val = ""+ke.getKeyChar();
                }
                //System.err.println("GOT KEYVAL: "+m);
                GameAction a = _overlays.get(m);
                //System.err.println("ZERO EVAL TO: "+a);
                if(a==null) {
                    a = _actions.get(m);
                    //System.err.println("FIRST EVAL TO: "+a);
                    if(a==null) {
                        m = new NewKeymap2(ke.getKeyCode(), (ke.getModifiers()&KeyEvent.ALT_DOWN_MASK)!=0);
                        a = _overlays.get(m);
                        if(a==null) {
                            a = _actions.get(m);
                        }
                    }
                }
                //System.err.println("EVAL TO: "+a);
                _input.addAction(a, val);
            }
        }
    }

    public GameAction actionFor(String key) {
        Keymap m = new NewKeymap2(key);
        GameAction a = _overlays.get(m);
        if(a==null) {
            a = _actions.get(m);
        }
        if(a!=null&&a instanceof DelegatingAction) {
            a = ((DelegatingAction)a).getDelegate(); // unwrap
        }
        return a;
    }

    public InputSource getInputSource() {
        return _input;
    }

    void nextView() {
        String view = _viewer.next();
        if(view!=null) {
            Map<String,String> overlay = Universe.getUniverse().getOverlaykeys().get(view);
            if(overlay!=null) {
                mapKeys(_overlays, overlay);
            }
            else {
                _overlays.clear();
            }
        }
    }

    final class DelegatingAction implements GameAction {
        private NHBotAction _delegate;
        private String _keybinding;


        public DelegatingAction(NHBotAction delegate) {
            _delegate = delegate;
        }

        public void setKeybinding(String keybinding) {
            _keybinding = keybinding;
        }

        public void setNarrative(Narrative n) {
        }

        public boolean isRepeat() {
            return _delegate.isRepeat();
        }

        public boolean isRecordable() {
            return _delegate.isRecordable();
        }

        public String getDescription() {
            return _delegate.getDescription();
        }

        public NHBotAction getDelegate() {
            return _delegate;
        }

        public Narrative getNarrative() {
            return _narrative;
        }

        public void perform() {
            if(_narrative==null||_patsy==null||_patsy.isDead()) {
                return;
            }
            try {
                _delegate.init();
                _delegate.setBot(_patsy);
                _delegate.perform();
            }
            catch(ActionCancelledException e) {
                throw e;
            }
            catch(QuitException e) {
                throw e;
            }
            catch(InputInterruptedException e) {
                throw new QuitException(e);
            }
            catch(Throwable t) {
                Logger.global.severe("reality simulation failed in "+_delegate);
                t.printStackTrace();
                _narrative.clear();
                //_narrative.more();
                _narrative.print(_patsy, "Your rough-housing wakes God and the universe vanishes.");
                //_narrative.more();
                _narrative.print(_patsy, "Epitaph: '"+t.getMessage()+"'.");
                try {
                    _narrative.quit("Killed by a waking god.", false);
                }
                catch(QuitException expected) {
                }
                NH.showError(t);
            }
        }
    }

    static class PostbackAction extends AbstractGameAction {
        private GameAction _real;


        public PostbackAction(GameAction real) {
            _real = real;
        }

        public void perform() {
            EventQueue.getEventQueue().postback(_real);
            throw new ActionCancelledException();
        }

        public String getDescription() {
            return _real.getDescription();
        }

        public String toString() {
            return _real.toString();
        }
    }
}
