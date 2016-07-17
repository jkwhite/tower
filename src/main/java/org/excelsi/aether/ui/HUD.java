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


import com.jme.input.*;
import com.jme.input.action.*;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.state.*;
import com.jme.system.*;
import com.jme.util.Timer;

import com.jmex.bui.*;
import com.jmex.bui.event.*;
import com.jmex.bui.background.*;
import com.jmex.bui.layout.*;
import com.jmex.bui.util.Dimension;
import com.jme.math.Vector3f;

import java.util.Stack;
import java.util.logging.Logger;

import org.excelsi.matrix.*;
import org.excelsi.aether.*;
import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;


public class HUD implements Narrative {
    private static final ColorRGBA BG = new ColorRGBA(0f, 0f, 0f, 1f);
    private static final ColorRGBA FG = new ColorRGBA(1f, 1f, 1f, 1f);
    private static final boolean OLD_NARRATIVE = Boolean.getBoolean("narrative");
    private static final float MESSAGE_APPEAR = 0.2f;
    private static final int RESERVED = 36;
    private static final int RESERVED_TOP = 18;


    private BWindow _help;
    private BWindow _keyWindow;
    private HelpContainer _helpContainer;
    private PolledRootNode _proot;
    private NarrativeFrame _narrative;
    //private Status _status;
    private StatusWindow _statusWindow;
    private boolean _statusVisible;
    private InventoryFrame _inventory;
    private InventoryFrame _selectInventory;
    private KeyedSelector _keyedSelector;
    private KeyedSelector _actionSelector;
    private SkillsContainer _skills;
    private BWindow _inventoryWindow;
    private BWindow _inventoryActionWindow;
    private BWindow _selectInventoryWindow;
    private BWindow _keyedSelectorWindow;
    private BWindow _skillsWindow;
    private boolean _inventoryVisible;
    private boolean _inventoryActionVisible;
    private boolean _selectInventoryVisible;
    private boolean _keyedSelectorVisible;
    private boolean _skillsVisible;
    private LookWindow _look;
    private boolean _lookVisible;
    private TextWindow _textWindow;
    private BWindow _scores;
    private BWindow _narwin;
    private BWindow _shade;
    private NHBot _bot;
    private InputSource _input;
    private static final Thread _ui = Thread.currentThread(); // ui uses main thread
    private static Map<String, BStyleSheet> _styles = new HashMap<String,BStyleSheet>();

    private static boolean _enableInventory = true;
    private static boolean _enableSkills = true;
    private static boolean _enableStatus = true;
    private static boolean _enableLook = true;


    public static void setWindowState(boolean inventory, boolean skills, boolean status, boolean look) {
        _enableInventory = inventory;
        _enableSkills = skills;
        _enableStatus = status;
        _enableLook = look;
    }

    public HUD(NHBot b, PolledRootNode proot, InputSource input) {
        _proot = proot;
        if(b==null) {
            throw new IllegalArgumentException("null bot");
        }
        _bot = b;
        _input = input;
        //_status = new Status(b);
        _narrative = new NarrativeFrame(_input);
        //_ui = Thread.currentThread();
    }

    public static BStyleSheet getStyle(String name) {
        if(!new java.io.File(name).isAbsolute()) {
            String n = new Exception().getStackTrace()[1].getClassName();
            name = n.substring(0, n.lastIndexOf('.')).replace('.', '/')+"/"+name;
        }

        BStyleSheet style = _styles.get(name);
        if(style==null) {
            try {
                InputStream is = HUD.class.getClassLoader().getResourceAsStream(name+".bss");
                if(is==null) { // use default
                    Logger.global.warning("no such style '"+name+".bss'");
                    is = HUD.class.getClassLoader().getResourceAsStream("org/excelsi/aether/ui/default.bss");
                }
                StringWriter sw = new StringWriter();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while((line=br.readLine())!=null) {
                    int idx = line.indexOf("${");
                    int idx2 = line.indexOf("}");
                    if(idx>=0&&idx2>idx) {
                        String prop = line.substring(idx+2, idx2);
                        line=line.substring(0, idx)+System.getProperty(prop)+line.substring(idx2+1);
                    }
                    sw.write(line);
                    sw.write("\n");
                }
                sw.close();
                style = new BStyleSheet(new StringReader(sw.toString()), new BStyleSheet.DefaultResourceProvider());
                _styles.put(name, style);
            }
            catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
        return style;
    }

    public void setKeyWindow(BWindow keys) {
        _keyWindow = keys;
    }

    private BSlideInOutController _statusIn;
    private BSlideInOutController _narIn;
    public void enable() {
        final BWindow nw = new BWindow(getStyle("narrative"), new BorderLayout());
        nw.setLocation(0, DisplaySystem.getDisplaySystem().getHeight()-RESERVED_TOP);
        nw.setSize(DisplaySystem.getDisplaySystem().getWidth(), RESERVED_TOP);
        nw.add(_narrative, BorderLayout.NORTH);
        _narwin = nw;
        _narrative.redo();
        _proot.addWindow(nw);
        _narIn = new BSlideInOutController(nw, BSlideInOutController.ENTER_NORTH, 3.0f) {
            protected void done() {
                _proot.removeController(_narIn);
            }
        };
        _proot.addController(_narIn);
        int iloc;

        if(_enableLook) {
            iloc = 500;
            _look = new LookWindow(getStyle("look"));
            _look.setSize(iloc, _look.getHeight());
            _look.setLocation(DisplaySystem.getDisplaySystem().getWidth()-iloc, 100+DisplaySystem.getDisplaySystem().getHeight());
            _proot.addWindow(_look);
        }

        if(_enableInventory) {
            iloc = 500;
            _inventoryWindow = new BWindow(getStyle("inventory"), new BorderLayout());
            _inventory = new BotInventoryFrame(_bot);
            _inventoryWindow.add(_inventory, BorderLayout.NORTH);
            iloc = (int) (DisplaySystem.getDisplaySystem().getWidth()*0.4);
            iloc = 500;
            _inventoryWindow.setSize(iloc, DisplaySystem.getDisplaySystem().getHeight()-RESERVED-RESERVED_TOP);
            _inventoryWindow.setLocation(DisplaySystem.getDisplaySystem().getWidth(), RESERVED);
            _proot.addWindow(_inventoryWindow);

            _selectInventoryWindow = new BWindow(getStyle("look"), new BorderLayout());
            _selectInventory = new BotInventoryFrame(_bot);
            _selectInventoryWindow.add(_selectInventory, BorderLayout.NORTH);
            iloc = (int) (DisplaySystem.getDisplaySystem().getWidth()*0.4);
            iloc = 500;
            _selectInventoryWindow.setSize(iloc, DisplaySystem.getDisplaySystem().getHeight()-RESERVED-RESERVED_TOP);
            _selectInventoryWindow.setLocation(-500, RESERVED);
            _proot.addWindow(_selectInventoryWindow);

            _inventoryActionWindow = new BWindow(getStyle("look"), new BorderLayout());
            _actionSelector = new KeyedSelector(new Object[0]);
            _actionSelector.setPluralize(false);
            _inventoryActionWindow.add(_actionSelector, BorderLayout.NORTH);
            iloc = (int) (DisplaySystem.getDisplaySystem().getWidth()*0.4);
            iloc = 300;
            _inventoryActionWindow.setSize(iloc, DisplaySystem.getDisplaySystem().getHeight()-RESERVED-RESERVED_TOP);
            _inventoryActionWindow.setLocation(DisplaySystem.getDisplaySystem().getWidth(), RESERVED);
            _proot.addWindow(_inventoryActionWindow);
        }

        if(_enableSkills) {
            _skillsWindow = new BWindow(getStyle("skills"), new BorderLayout());

            _skills = new SkillsContainer(_bot);
            _skillsWindow.add(_skills, BorderLayout.NORTH);
            iloc = (int) (DisplaySystem.getDisplaySystem().getWidth()*0.2);
            iloc = 330;
            _skillsWindow.setSize(iloc, DisplaySystem.getDisplaySystem().getHeight()-RESERVED-RESERVED_TOP);
            _skillsWindow.setLocation(-iloc, RESERVED);
            _proot.addWindow(_skillsWindow);
        }

        if(_enableStatus) {
            int w = DisplaySystem.getDisplaySystem().getWidth();
            boolean two = w<=1024;
            int h = two?36:18;
            two = true;
            h = 36;
            _statusWindow = new StatusWindow(_bot, getStyle("status"), two);
            _statusVisible = true;
            _statusWindow.setLocation(0, 0);
            _statusWindow.setSize(w, h);
            _statusIn = new BSlideInOutController(_statusWindow, BSlideInOutController.ENTER_SOUTH, 3.0f) {
                protected void done() {
                    _proot.removeController(_statusIn);
                }
            };
            _proot.addController(_statusIn);
            _proot.addWindow(_statusWindow);
        }

        _keyedSelectorWindow = new BWindow(getStyle("inventory"), new BorderLayout());
        _keyedSelector = new KeyedSelector();
        _keyedSelectorWindow.add(_keyedSelector, BorderLayout.NORTH);
        iloc = (int) (DisplaySystem.getDisplaySystem().getWidth()*0.4);
        iloc = 500;
        _keyedSelectorWindow.setSize(iloc, DisplaySystem.getDisplaySystem().getHeight()-RESERVED-RESERVED_TOP);
        _keyedSelectorWindow.setLocation(-500, RESERVED);
        _proot.addWindow(_keyedSelectorWindow);

        _textWindow = new TextWindow(getStyle("text"));
        _textWindow.setSize(500, 400);

        EventQueue.getEventQueue().addGameListener(Universe.getUniverse().getGame(), new GameListener() {
            public void ascended(Game g) {
                addListeners();
            }

            public void descended(Game g) {
                addListeners();
            }
        });
        addListeners();
    }

    public void resize() {
        if(_narwin!=null) {
            _narwin.setLocation(0, DisplaySystem.getDisplaySystem().getHeight()-RESERVED_TOP);
            _narwin.setSize(DisplaySystem.getDisplaySystem().getWidth(), RESERVED_TOP);
        }

        int iloc = 500;
        if(_look!=null) {
            _look.setSize(iloc, _look.getHeight());
            _look.setLocation(DisplaySystem.getDisplaySystem().getWidth()-iloc, 100+DisplaySystem.getDisplaySystem().getHeight());
        }
        if(_inventoryWindow!=null) {
            _inventoryWindow.setSize(iloc, DisplaySystem.getDisplaySystem().getHeight()-RESERVED-RESERVED_TOP);
            _inventoryWindow.setLocation(DisplaySystem.getDisplaySystem().getWidth(), RESERVED);
            _selectInventoryWindow.setSize(iloc, DisplaySystem.getDisplaySystem().getHeight()-RESERVED-RESERVED_TOP);
            _selectInventoryWindow.setLocation(-500, RESERVED);
            iloc = 300;
            _inventoryActionWindow.setSize(iloc, DisplaySystem.getDisplaySystem().getHeight()-RESERVED-RESERVED_TOP);
            _inventoryActionWindow.setLocation(DisplaySystem.getDisplaySystem().getWidth(), RESERVED);
        }
        if(_skillsWindow!=null) {
            iloc = 330;
            _skillsWindow.setSize(iloc, DisplaySystem.getDisplaySystem().getHeight()-RESERVED-RESERVED_TOP);
            _skillsWindow.setLocation(-iloc, RESERVED);
        }
        if(_statusWindow!=null) {
            int w = DisplaySystem.getDisplaySystem().getWidth();
            boolean two = w<=1024;
            int h = two?36:18;
            two = true;
            h = 36;
            _statusWindow.setLocation(0, 0);
            _statusWindow.setSize(w, h);
        }
        if(_keyedSelectorWindow!=null) {
            iloc = 500;
            _keyedSelectorWindow.setSize(iloc, DisplaySystem.getDisplaySystem().getHeight()-RESERVED-RESERVED_TOP);
            _keyedSelectorWindow.setLocation(-500, RESERVED);
        }
        _keyWindow.requestFocus();
    }

    private void addListeners() {
        EventQueue.getEventQueue().addNHEnvironmentListener(_bot, new NHEnvironmentAdapter() {
            public void attributeChanged(Bot b, String attribute, Object newValue) {
                reset();
            }

            public void moved(Bot b) {
                reset();
            }

            public void faced(Bot b, Direction old, Direction d) {
                reset();
            }

            public void attacked(NHBot b, Outcome outcome) {
                reset();
            }

            public void actionStarted(NHBot b, ProgressiveAction action) {
                reset();
            }

            public void actionStopped(NHBot b, ProgressiveAction action) {
                reset();
            }

            public void equipped(NHBot b, Item i) {
                reset();
            }

            public void unequipped(NHBot b, Item i) {
                reset();
            }

            private void reset() {
                _narrative.resetPrevious();
            }
        });
    }

    public void print(final NHSpace source, final String message) {
        print(source, message, new Condition(message));
    }

    public void print(final NHSpace source, final String message, final Condition cond) {
        if(isUIThread()) {
            pprint(source, message, new MNarrative(new MessageWindow(message, source, NH.getInstance().getState()), cond));
        }
        else {
            if(OLD_NARRATIVE) {
                _narrative.willAppend(1+message.length());
                if(_narrative.estimatedLength()>80) {
                    more();
                }
            }
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    print(source, message, cond);
                }
            });
        }
    }

    public static class MNarrative {
        public final MessageWindow w;
        public final Condition c;
        public final boolean p;


        public MNarrative(MessageWindow mw, Condition cond) {
            this(mw, cond, false);
        }

        public MNarrative(MessageWindow mw, Condition cond, boolean player) {
            w = mw;
            c = cond;
            p = player;
        }
    }

    public static class Condition {
        float _sec;
        boolean _ok;
        boolean _stay;


        public Condition() {
            _ok = false;
        }

        public Condition(Object msg) {
            this(Math.max(0.8f, msg.toString().length()/15f));
        }

        public Condition(float sec) {
            _ok = true;
            _sec = sec;
        }

        public void stay() {
            _stay = true;
        }

        public void release() {
            _stay = false;
        }

        public void ok() {
            _ok = true;
        }

        public boolean test(float dt) {
            return !_stay&&_ok&&dt>_sec;
        }
    }

    private class Remover extends Controller {
        private PolledRootNode _hud;
        private BWindow _w;
        private MessageWindow _text;
        private float _total;
        private Condition _cond;
        private MNarrative _m;


        public Remover(PolledRootNode p, MessageWindow w, MNarrative m) {
            _hud = p;
            _w = w;
            _text = w;
            _cond = m.c;
            _m = m;
        }

        public void update(float dt) {
            _text.updateLocation();
            _total += getSpeed()*dt;
            if(_cond.test(_total)) {
                if(_m.p) {
                    if(--_playerMessageCount==0) {
                        _lastPlayerMsg = null;
                    }
                }
                //_text.setText("");
                _text.hideText();
                BSlideInOutController c = new BSlideInOutController(_w, BSlideInOutController.SHRINK,
                    MESSAGE_APPEAR) {
                    protected void done() {
                        _hud.removeController(this);
                        _hud.removeWindow(_w);
                    }
                };
                _hud.addController(c);
                //_hud.removeWindow(_w);
                _hud.removeController(this);
            }
        }
    }

    public void print(final NHBot source, final Object message) {
        print(source, message, new Condition(message.toString()));
    }

    private class MessageWindow extends BWindow {
        private BTextArea _text;
        private BContainer _key;
        private BLabel _kn, _kv;
        private BContainer _root;
        private Object _message;
        private Object _source;
        private DungeonState _d;


        public MessageWindow(Object message, Object source, State s) {
            super(HUD.getStyle("narrative-popup"), new BorderLayout());
            BContainer bc = new BContainer(new BorderLayout());
            if(message instanceof Item) {
                Item item = (Item) message;
                String kn = Grammar.keyName(_bot.getInventory(), item);
                String kv = Grammar.keyValue(_bot.getInventory(), item);
                _key = new BContainer(new BorderLayout());
                _kn = new BLabel(kn, "keylabel");
                _text = new BTextArea(kv);
                if(kn.length()>0) {
                    _key.add(_kn, BorderLayout.WEST);
                }
                _key.add(_text, BorderLayout.CENTER);
                bc.add(_key, BorderLayout.CENTER);
                //_text = new BTextArea(message.toString());
                //bc.add(_text, BorderLayout.CENTER);
            }
            else {
                _text = new BTextArea(message.toString());
                bc.add(_text, BorderLayout.CENTER);
            }
            _root = bc;
            add(bc, BorderLayout.CENTER);
            _message = message;
            _source = source;
            if(s instanceof DungeonState) {
                _d = (DungeonState) s;
            }
        }

        private float _lx, _ly;
        private float _tol = 30.0f;
        public void updateLocation() {
            Vector3f wf = null;
            boolean centered = false;
            if(_source instanceof NHBot) {
                wf = _d.getBotTranslation((NHBot)_source);
                if(wf==null&&((NHBot)_source).isPlayer()) {
                    centered = true;
                }
            }
            else if(_source instanceof NHSpace) {
                wf = _d.getSpaceTranslation((NHSpace)_source);
            }
            else if(_source==null) {
                // level message
                centered = true;
            }
            if(centered||wf!=null) {
                Vector3f sc;
                if(!centered) {
                    sc = DisplaySystem.getDisplaySystem().getScreenCoordinates(wf);
                }
                else {
                    sc = new Vector3f(DisplaySystem.getDisplaySystem().getWidth()/2,
                            DisplaySystem.getDisplaySystem().getHeight()/2, 0);
                }
                float sx = sc.x+20, sy = sc.y-30;
                if(_lx==0&&_ly==0) {
                    _lx = sx;
                    _ly = sy;
                    setLocation((int)sx, (int)sy);
                }
                else if(Math.abs(sx-_lx)>=_tol||Math.abs(sy-_ly)>=_tol) {
                    _lx = (_lx+sx)/2f;
                    _ly = (_ly+sy)/2f;
                    _tol = 1f;
                    //System.err.println("upd: x="+(int)sx+", y="+(int)sy);
                    setLocation((int)_lx, (int)_ly);
                }
            }
        }

        /*
        public void redim() {
            System.err.println("text is: "+_message);
            _text.setText(_message);
            setSize(200, 25);
            Dimension dim = _text.getPreferredSize(0,0);
            setSize(1+dim.width,1+dim.height);
            System.err.println("size: "+getWidth()+"x"+getHeight());
            _text.setText("");
        }
        */

        boolean _more = false;
        private boolean _growing = false;

        public void setGrowing(boolean growing) {
            _growing = growing;
        }

        public void more() {
            //_text.setText(_message+" --More--");
            _more = true;
            if(!_growing) {
                doMore();
            }
        }

        public void doMore() {
            if(_more) {
                _more = false;
                _text.appendText(" --More--", new ColorRGBA(0f, 0f, 0f, 0.5f), BTextArea.BOLD);
                setSize(getWidth(), getHeight()+24);
            }
        }

        public Object getText() {
            return _message;
        }

        public void hideText() {
            if(_kn!=null) {
                _kn.setText("");
                _text.setText("");
            }
            else {
                _text.setText("");
            }
        }

        public void setText(String text) {
            _text.setText(text);
        }

        public void restore() {
            //setText(_message);
            if(_kn!=null) {
                Item item = (Item) _message;
                String kn = Grammar.keyName(_bot.getInventory(), item);
                String kv = Grammar.keyValue(_bot.getInventory(), item);
                _kn.setText(kn);
                _text.setText(kv);
            }
            else {
                _text.setText(_message.toString());
            }
            _growing = false;
            doMore();
        }

        //public void clear() {
            //_text.setText("");
        //}

        public Dimension getPreferredTextSize() {
            Dimension d;
            if(_key!=null) {
                d = _key.getPreferredSize(200,25);
            }
            else {
                d = _text.getPreferredSize(200,25);
            }
            d.width+=2;
            d.height+=2;
            return d;
        }
    }

    private MNarrative _lastPlayerMsg = null;
    private int _playerMessageCount = 0;

    private void checkMore() {
        final MNarrative m = _lastPlayerMsg;
        if(m!=null) {
            //System.err.println("lastPlayerMessage="+_lastPlayerMsg.w.getText());
            m.c.stay();
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    m.w.more();
                }
            });
            _input.nextKey();
            m.c.release();
            _lastPlayerMsg = null;
        }
    }

    private void pprint(final Object source, final Object message, final MNarrative m) {
        State state = NH.getInstance().getState();
        if(state instanceof DungeonState) {
            DungeonState d = (DungeonState) state;
            Vector3f wf = null;
            boolean centered = false;
            if(source instanceof NHBot) {
                wf = d.getBotTranslation((NHBot)source);
                if(wf==null&&((NHBot)source).isPlayer()) {
                    centered = true;
                }
            }
            else if(source instanceof NHSpace) {
                wf = d.getSpaceTranslation((NHSpace)source);
            }
            else if(source==null) {
                // level message
                centered = true;
            }
            if(centered||wf!=null) {
                Vector3f sc;
                if(!centered) {
                    sc = DisplaySystem.getDisplaySystem().getScreenCoordinates(wf);
                }
                else {
                    sc = new Vector3f(DisplaySystem.getDisplaySystem().getWidth()/2,
                            DisplaySystem.getDisplaySystem().getHeight()/2, 0);
                }
                final MessageWindow w = m.w;
                //w.setLocation((int)sc.x+20, (int)sc.y-30);
                _proot.addWindow(w);
                w.updateLocation();
                Dimension dim = w.getPreferredTextSize();
                w.setSize(1+dim.width,1+dim.height);
                //w.setText("");
                w.hideText();
                w.setLocation(w.getX()+dim.width/2, w.getY()+dim.height/2);
                w.setGrowing(true);
                BSlideInOutController c = new BSlideInOutController(w, BSlideInOutController.GROW,
                    MESSAGE_APPEAR) {
                    public void update(float dt) {
                        super.update(dt);
                        //w.updateLocation();
                    }

                    protected void done() {
                        //_textWindow.setCentered(centered);
                        w.restore();
                        //w.setText(message);
                        _proot.removeController(this);
                    }
                };
                _proot.addController(new Remover(_proot, w, m));
                _proot.addController(c);
            }
        }
        else {
            if(source instanceof NHBot) {
                nprint((NHBot)source, message);
            }
            else {
                nprint((NHSpace)source, message.toString());
            }
        }
    }

    public void print(final NHBot source, final Object message, final Condition cond) {
        if(isUIThread()) {
            pprint(source, message, new MNarrative(new MessageWindow(message, source, NH.getInstance().getState()), cond));
        }
        else {
            MNarrative om = null;
            if(OLD_NARRATIVE) {
                _narrative.willAppend(1+message.toString().length());
                if(_narrative.estimatedLength()>80) {
                    more();
                }
            }
            else {
                om = new MNarrative(new MessageWindow(message, source, NH.getInstance().getState()), cond);
                if(source!=null&&source.isPlayer()) {
                    checkMore();
                    _lastPlayerMsg = om;
                    _playerMessageCount++;
                }
            }
            final MNarrative mn = om;
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    pprint(source, message, mn);
                }
            });
        }
    }

    public void nprint(final NHSpace source, final String message) {
        if(_narrative.estimatedLength()>80) {
            more();
        }
        if(isUIThread()) {
            //_narrative.print(source, message);
            _narrative.append(message);
        }
        else {
            _narrative.willAppend(1+message.length());
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    //print(source, message);
                    if(_narrative.length()>0) {
                        _narrative.append(" ");
                    }
                    _narrative.append(message);
                }
            });
        }
    }

    public void nprint(final NHBot source, final Object message) {
        if(_narrative.estimatedLength()>80) {
            more();
        }
        if(isUIThread()) {
            //_narrative.print(source, message);
            _narrative.append(message.toString());
        }
        else {
            _narrative.willAppend(1+message.toString().length());
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    //print(source, message);
                    if(_narrative.length()>0) {
                        _narrative.append(" ");
                    }
                    _narrative.append(message.toString());
                }
            });
        }
    }

    public void printfm(final NHBot source, final String message, final Object... args) {
        printf(source, message, args);
    }

    public void printf(final NHBot source, final String message, final Object... args) {
        final String formatted = Grammar.format(source, message, args);
        print(source, formatted);
    }

    public void printf(final NHSpace source, final String message, final Object... args) {
        //final String formatted = message;
        final String formatted = Grammar.format(_bot, message, args);
        print(source, formatted);
    }

    public void printc(final NHBot source, final String message) {
        if(isUIThread()) {
            _narrative.printc(source, message);
        }
        else {
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    printc(source, message);
                }
            });
        }
    }

    public void display(NHBot source, String text, boolean centered) {
        showText(source, text, centered);
        _input.nextKey();
        hideText();
    }

    public void showText(final NHBot source, final String text, final boolean centered) {
        if(isUIThread()) {
            _textWindow.setText("");
            _textWindow.setSize(500, 300);
            _textWindow.setLocation(DisplaySystem.getDisplaySystem().getWidth()/2,
                    DisplaySystem.getDisplaySystem().getHeight()/2);
            _proot.addWindow(_textWindow);
            BSlideInOutController c = new BSlideInOutController(_textWindow, BSlideInOutController.GROW_CENTER,
                0.4f) {
                protected void done() {
                    _textWindow.setCentered(centered);
                    _textWindow.setText(text);
                    _proot.removeController(this);
                }
            };
            _proot.addController(c);
        }
        else {
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    showText(source, text, centered);
                }
            });
        }
    }

    public void hideText() {
        if(isUIThread()) {
            _textWindow.setText("");
            BSlideInOutController c = new BSlideInOutController(_textWindow, BSlideInOutController.SHRINK_CENTER,
                0.4f) {
                protected void done() {
                    _proot.removeController(this);
                    _proot.removeWindow(_textWindow);
                }
            };
            _proot.addController(c);
        }
        else {
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    hideText();
                }
            });
        }
    }

    public void backspace() {
        if(isUIThread()) {
            _narrative.backspace();
        }
        else {
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    backspace();
                }
            });
        }
    }

    public void clear() {
        clear(true);
    }

    public void clear(final boolean record) {
        if(isUIThread()) {
            _narrative.clear(record);
        }
        else {
            _narrative.willClear();
            _lastPlayerMsg = null;
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    clear(record);
                }
            });
        }
    }

    public boolean isClear() {
        return _narrative.isClear();
    }

    public boolean confirm(NHBot source, String message) {
        //return _narrative.confirm(source, message);
        if(_narrative.estimatedLength()>0) {
            more();
        }
        Condition cond = new Condition();
        print(source, message+" [yn]", cond);
        try {
            for(;;) {
                String k = _input.nextKey();
                if(k.equals("y")) {
                    clear(false);
                    return true;
                }
                else if(k.equals("n")||k.equals("ESCAPE")) {
                    clear(false);
                    return false;
                }
            }
        }
        finally {
            cond.ok();
        }
    }

    private static final String CURSOR = "_";
    public String reply(NHBot source, String query) {
        //return _narrative.reply(source, query);
        if(_narrative.estimatedLength()>0) {
            more();
        }
        nprint(source, query+" ");
        StringBuffer b = new StringBuffer();
        for(;;) {
            printc(source, CURSOR);
            String k = _input.nextKey();
            backspace();
            if(k.equals("ENTER")) {
                clear(false);
                return b.toString();
            }
            else if(k.equals("ESCAPE")) {
                throw new ActionCancelledException();
            }
            else if(k.equals("BACK")) {
                if(b.length()>0) {
                    backspace();
                    b.setLength(b.length()-1);
                }
            }
            else {
                printc(source, k);
                b.append(k);
            }
        }
    }

    public String replyc(NHBot source, String query) {
        clear(false);
        nprint(source, query);
        return _input.nextKey();
    }

    private boolean _nodirect = Boolean.getBoolean("tower.nodirect");
    public Direction direct(NHBot source, String message) {
        //return _narrative.direct(source, message);
        if(_narrative.estimatedLength()>0) {
            more();
        }
        Condition cond = new Condition();
        try {
            for(;;) {
                if(_nodirect) {
                    nprint(source, message);
                }
                else {
                    print(source, message, cond);
                }
                String k = _input.nextKey();
                cond.ok();
                clear(false);
                if(k.equals("ESCAPE")) {
                    throw new ActionCancelledException();
                }
                try {
                    GameAction a = _input.actionFor(k);
                    if(a instanceof Director) {
                        return ((Director)a).getDirection();
                    }
                }
                catch(IllegalArgumentException e) {
                    // why doesn't java logging support stack traces?
                    Logger.global.fine("illegal direction: "+e.toString());
                }
                catch(NullPointerException e) {
                    Logger.global.fine("illegal direction: "+e.toString());
                }
                print(source, "That is not a direction.");
                //more();
            }
        }
        finally {
            cond.ok();
        }
    }

    public NHSpace chooseSpace(NHBot b, NHSpace start) {
        Overlay o = new Overlay(start);
        o.move(b.getEnvironment().getFacing());
        for(;;) {
            String k = _input.nextKey();
            clear(false);
            if(k.equals("ESCAPE")) {
                clear(false);
                o.remove();
                throw new ActionCancelledException();
            }
            NHSpace s = o.getSpace();
            if(k.equals("ENTER")) {
                clear(false);
                o.remove();
                return s;
            }
            try {
                String d = Universe.getUniverse().getKeymap().get(k);
                Direction move = Enum.valueOf(Direction.class, d);
                o.move(move);
                //b.getEnvironment().face(o.getSpace());
                if(!b.getEnvironment().getVisible().contains(o.getSpace())) {
                    //print(b, "You can't see that far.");
                    print(o.getSpace(), "You can't see that far.");
                    continue;
                }
                boolean disp = false;
            }
            catch(IllegalArgumentException e) {
                //e.printStackTrace();
                clear(false);
                print(b, "That is not a direction.");
                more();
            }
            catch(NullPointerException e) {
                //e.printStackTrace();
                clear(false);
                print(b, "That is not a direction.");
                more();
            }
        }
    }

    public Item[] choose(NHBot source, NHSpace space) {
        space.getLoot().setKeyed(true);
        showLook(source, space, true);
        Item[] chosen = _look.getInventory().choose(_input, -1);
        //_look.getInventory().restoreKeyed();
        hideLook();
        space.getLoot().setKeyed(false);
        return chosen;
    }

    public Item[] chooseMulti(NHBot source, ItemConstraints c, boolean remove) {
        String valid = c.getContainer().validKeys(c.getFilter(), source);
        boolean origVis = _selectInventoryVisible;
        StringBuilder count = null;

        if(c.isAcceptNull()) {
            valid += "-";
        }
        if(valid.length()>0) {
            String fullMsg = c.getMessage()+" ["+valid+" or ?]";
            nprint(source, fullMsg);
            if(isAutoShowInventory()) {
                toggleSelectInventory(c.getFilter());
            }
        }
        Item[] chosen = _selectInventory.choose(_input, -1);
        if(origVis!=_selectInventoryVisible) {
            toggleSelectInventory();
        }
        return chosen;
    }

    public Item choose(NHBot source, ItemConstraints c, boolean remove) {
        String valid = c.getContainer().validKeys(c.getFilter(), source);
        boolean origVis = _selectInventoryVisible;
        StringBuilder count = null;

        if(c.isAcceptNull()) {
            valid += "-";
        }
        if(valid.length()>0) {
            String fullMsg = c.getMessage()+" ["+valid+" or ?]";
            nprint(source, fullMsg);
            if(isAutoShowInventory()) {
                toggleSelectInventory(c.getFilter());
            }
            while(true) {
                String key = _input.nextKey();
                if(key.equals("ESCAPE")) {
                    clear(false);
                    nprint(source, "Never mind.");
                    if(origVis!=_selectInventoryVisible) {
                        toggleSelectInventory();
                    }
                    throw new ActionCancelledException();
                }
                if(" ".equals(key)) {
                    _selectInventory.next();
                }
                if(Character.isDigit(key.charAt(0))) {
                    if(count==null) {
                        count = new StringBuilder();
                    }
                    count.append(key.charAt(0));
                    continue;
                }
                Item it = null;
                if(key.equals("?")) {
                    toggleSelectInventory(c.getFilter());
                    if(_selectInventoryVisible) {
                        try {
                            Item[] sel = _selectInventory.choose(_input, 1);
                            if(sel.length==1) {
                                it = sel[0];
                            }
                        }
                        catch(ActionCancelledException e) {
                            toggleSelectInventory();
                            clear(false);
                            print(source, "Never mind.");
                            throw new ActionCancelledException();
                        }
                    }
                }
                else {
                    clear(false);
                }
                if(it==null) {
                    it = c.getContainer().itemFor(key);
                }
                if(it==null&&!c.isAcceptNull()) {
                    nprint(source, "You don't have that object.");
                }
                else if(!c.getFilter().accept(it, source)) {
                    nprint(source, c.getFailMessage());
                }
                else {
                    if(origVis!=_selectInventoryVisible) {
                        toggleSelectInventory();
                    }
                    clear(false);
                    if(remove) {
                        try {
                            int cnt = it.getCount();
                            if(count!=null) {
                                cnt = Integer.parseInt(count.toString());
                            }
                            int oc = it.getCount();
                            if(cnt>oc) {
                                nprint(source, "You don't have that many "+Grammar.pluralize(it.getName())+".");
                            }
                            else {
                                Item sp = it;
                                if(cnt<oc) {
                                    sp = c.getContainer().split(it);
                                    sp.setCount(cnt);
                                    it.setCount(oc-cnt);
                                }
                                else {
                                    c.getContainer().remove(it);
                                }
                                return sp;
                            }
                        }
                        catch(NumberFormatException e) {
                            nprint(source, "That's not a number.");
                        }
                    }
                    else {
                        return it;
                    }
                }
                more();
                nprint(source, fullMsg);
            }
        }
        else {
            if(_narrative.estimatedLength()>0) {
                more();
            }
            clear(false);
            String text = c.getMessage();
            print(source, "You have nothing to "+text.substring(text.lastIndexOf("to ")+3, text.length()-1)+".");
            throw new ActionCancelledException();
        }
    }

    public Object[] choose(NHBot source, String query, String heading, Object[] choices, String[] keys, int max) {
        _keyedSelector.setElements(heading, choices, keys);
        nprint(source, query);
        toggleKeyedSelector();
        Object[] choice = null;
        try {
            choice = _keyedSelector.choose(_input, max);
            clear();
        }
        finally {
            toggleKeyedSelector();
        }
        return choice;
    }

    public void look(NHBot b, NHSpace start) {
        _look.getInventory().setShowKeys(false);
        Overlay o = new Overlay(start);
        o.move(b.getEnvironment().getFacing());
        o.getSpace().look(b, true, false);
        for(;;) {
            String k = _input.nextKey();
            clear(false);
            if(k.equals("ESCAPE")||k.equals("ENTER")||k.equals(" ")) {
                clear(false);
                hideLook();
                o.remove();
                throw new ActionCancelledException();
            }
            try {
                String d = Universe.getUniverse().getKeymap().get(k);
                Direction move = Enum.valueOf(Direction.class, d);
                o.move(move);
                b.getEnvironment().face(o.getSpace());
                NHSpace s = o.getSpace();
                hideLook();
                if(!b.getEnvironment().getVisible().contains(o.getSpace())) {
                    if(o.getSpace().isOccupied()&&b.getEnvironment().getVisibleBots().contains(o.getSpace().getOccupant())) {
                        print(b, "Something is lurking in shadows...");
                    }
                    else {
                        print(b, "You can't see that far.");
                    }
                    continue;
                }
                boolean disp = false;
                disp = s.look(b, true, false);
                if(!disp) {
                    hideLook();
                    clear(false);
                }
            }
            catch(IllegalArgumentException e) {
                //e.printStackTrace();
                clear(false);
                print(b, "That is not a direction.");
                //more();
            }
            catch(NullPointerException e) {
                //e.printStackTrace();
                clear(false);
                print(b, "That is not a direction.");
                //more();
            }
        }
    }

    public void showInventory() {
        toggleInventory();
        String key = _input.nextKey();
        if((" ".equals(key)||"ENTER".equals(key))
            && _inventory.hasNext()) {
            _inventory.next();
            key = _input.nextKey();
        }
        Item i = _bot.getInventory().itemFor(key);
        if(i!=null) {
            List<ItemAction> ga = new java.util.ArrayList<ItemAction>();
            List<String> keys = new java.util.ArrayList<String>();
            for(Map.Entry<String,String> e:Universe.getUniverse().getActionmap().entrySet()) {
                try {
                    GameAction g = (GameAction) Class.forName(e.getValue()).newInstance();
                    if(g instanceof ItemAction) {
                        ItemAction bg = (ItemAction) g;
                        if(bg.accepts(i, _bot)) {
                            ga.add(bg);
                            keys.add(Universe.getUniverse().keyFor(e.getKey()));
                        }
                    }
                }
                catch(Exception ex) {
                }
            }
            for(Map.Entry<String,GameAction> e:Extended.getCommands().entrySet()) {
                if(e.getValue() instanceof ItemAction) {
                    ItemAction bg = (ItemAction) e.getValue();
                    if(!ga.contains(bg)) {
                        if(bg.accepts(i, _bot)) {
                            ga.add(bg);
                            keys.add(null);
                        }
                    }
                }
            }
            if(ga.size()>0) {
                _actionSelector.setElements(Grammar.first(Grammar.simple(i)), ga.toArray(), keys.toArray(new String[0]));
                toggleInventoryAction();
                Object[] choice = null;
                try {
                    choice = _actionSelector.choose(_input, 1);
                }
                finally {
                    toggleInventoryAction();
                    toggleInventory();
                }
                if(choice.length==1) {
                    ItemAction c = (ItemAction) choice[0];
                    c.setBot(_bot);
                    c.setItem(i);
                    c.perform();
                }
            }
            else {
                toggleInventory();
            }
        }
        else {
            toggleInventory();
        }
    }

    public void showSkills() {
        toggleSkills();
        _input.nextKey();
        toggleSkills();
    }

    private ContainerListener _lootUpdater = new ContainerListener() {
        public void itemDropped(Container space, Item item, int idx, boolean incremented) {
        }

        public void itemAdded(Container space, Item item, int idx, boolean incremented) {
        }

        public void itemAdded(Container space, Item item, int idx, boolean incremented, NHBot adder, NHSpace origin) {
        }

        public void itemTaken(Container space, Item item, int idx) {
            if(space.numItems()==0) {
                showLoot(null);
            }
            else {
                _look.getInventory().refresh();
            }
        }

        public void itemDestroyed(Container space, Item item, int idx) {
        }

        public void itemsDestroyed(Container space, Item[] item) {
        }
    };
    private NHSpace _lastLoot;

    public void showLoot(NHSpace space) {
        if(_lookVisible) {
            hideLook();
            if(_lastLoot!=null) {
                //_lastLoot.removeContainerListener(_lootUpdater);
                try {
                    EventQueue.getEventQueue().removeContainerListener(_lastLoot, _lootUpdater);
                }
                catch(IllegalArgumentException e) {
                    Logger.global.warning(e.toString());
                }
            }
            _lastLoot = null;
        }
        if(space!=null) {
            _lastLoot = space;
            //space.addContainerListener(_lootUpdater);
            EventQueue.getEventQueue().addContainerListener(space, _lootUpdater);
            toggleLook(space);
        }
    }

    public void save() {
        Persistence.save(Universe.getUniverse().getGame());
        print(_bot, "Be seeing you.");
        more();
        NH.getInstance().exit();
    }

    //private Title.Reveal _ender = null;
    public void quit(final String cause, boolean end) {
        if(isUIThread()) {
            throw new Error();
        }
        Persistence.clear(Universe.getUniverse().getGame());
        int score = _bot.score();
        Scores s = Persistence.loadScores();
        int rank = s.insert(_bot, cause);
        Persistence.saveScores(s);
        if(end) {
            /*
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    //BWindow w = new BWindow(BLookAndFeel.getDefaultLookAndFeel(), new VGroupLayout());
                    BWindow w = new BWindow(getStyle("black"), new VGroupLayout());
                    w.setSize(DisplaySystem.getDisplaySystem().getWidth(), DisplaySystem.getDisplaySystem().getHeight());
                    //w.setBackground(new TintedBackground(new ColorRGBA(0f, 0f, 0f, 1.0f)));
                    _proot.addWindow(w);
                    w.center();
                    String con = Universe.getUniverse().getConclusion();
                    if(con==null) {
                        con = "Dead Cities,#Red Seas,#and Lost Ghosts.";
                    }
                    _ender = Title.createRevealer(con, _proot);
                }
            });
            boolean esc = false;
            while(_ender==null&&!esc) {
                if(_input.nextKey().equals("ESCAPE")) {
                    esc = true;
                }
            }
            for(BWindow w:_ender.windows) {
                _proot.removeWindow(w);
            }
            _proot.removeWindow(_ender.mask);
            */
        }
        else {
            Condition c = new Condition();
            print(_bot, "You left the Tower with "+score+" points.", c);
            //more();
            _input.nextKey();
            c.ok();
        }
        showScores(s, rank, new Scores.Score(_bot, cause));
        _input.nextKey();
        _input.nextKey();

        EventQueue.getEventQueue().postback(new AbstractGameAction() {
            public void perform() {
                NH.getInstance().finish();
            }
        });
        throw new QuitException();
    }

    public void hideScores() {
        if(isUIThread()) {
            _proot.removeWindow(_scores);
            _proot.removeWindow(_shade);
            _scores = null;
            _shade = null;
        }
        else {
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    hideScores();
                }
            });
        }
    }

    public void showScores() {
        Scores s = Persistence.loadScores();
        showScores(s, -1, null);
        _input.nextKey();
        hideScores();
    }

    public void showScores(final Scores scores, final int rank, final Scores.Score last) {
        if(isUIThread()) {
            BWindow w = new BWindow(getStyle("shade"), new VGroupLayout());
            w.setSize(DisplaySystem.getDisplaySystem().getWidth(), DisplaySystem.getDisplaySystem().getHeight());
            _proot.addWindow(w);
            w.center();
            _shade = w;

            w = new BWindow(getStyle("scores"), new BorderLayout());
            BContainer p = new BContainer(new TableLayout(6, 5, 20));
            p.add(new BLabel(" "), "sellabel");
            p.add(new BLabel("Rank", "sellabel"));
            p.add(new BLabel("Score", "sellabel"));
            p.add(new BLabel("Name", "sellabel"));
            p.add(new BLabel(" ", "sellabel"));
            p.add(new BLabel("Level", "sellabel"));
            int modrank = rank;
            List<Scores.Score> score = scores.getScores();
            int orig = score.size();
            if(rank==-1&&last!=null) {
                modrank = score.size();
                score.add(last);
            }
            for(int i=0;i<score.size();i++) {
                Scores.Score s = score.get(i);
                String r = i<orig?((1+i)+""):" ";
                String sel = " ";
                String lab = "";
                if(modrank==i) {
                    sel = "*";
                    lab = "sel";
                }
                p.add(new BLabel(sel, lab+"label"));
                p.add(new BLabel(r, lab+"rlabel"));
                p.add(new BLabel(s.getScore()+"", lab+"rlabel"));
                p.add(new BLabel(s.getName()+" the "+s.getProfession(), lab+"label"));
                p.add(new BLabel(s.getCause(), lab+"label"));
                String floor = s.getFloor();
                if(!s.getMaxFloor().equals(floor)) {
                    floor = floor+" (max "+s.getMaxFloor()+")";
                }
                floor = s.getAreaName()+", Lv. "+floor;
                p.add(new BLabel(floor, lab+"rlabel"));
            }
            w.add(p, BorderLayout.CENTER);
            _proot.addWindow(w);
            w.setSize(p.getPreferredSize(0,0).width+10, p.getPreferredSize(0,0).height+10);
            w.setLocation(DisplaySystem.getDisplaySystem().getWidth()/2-w.getWidth()/2,
                    DisplaySystem.getDisplaySystem().getHeight()/2-w.getHeight()/2);
            _scores = w;
        }
        else {
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    showScores(scores, rank, last);
                }
            });
        }
    }

    public void help() {
        showHelp();
        for(;;) {
            //_input.nextKey();
            _input.nextKey();
            if(!_helpContainer.hasNext()) {
                break;
            }
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    _helpContainer.next();
                }
            });
        }
        hideHelp();
    }

    public void hideHelp() {
        if(isUIThread()) {
            _proot.removeWindow(_help);
            _help = null;
        }
        else {
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    hideHelp();
                }
            });
        }
    }

    public void showHelp() {
        if(isUIThread()) {
            HelpContainer h = new HelpContainer(_input);
            BWindow hw = new BWindow(getStyle("help"), new BorderLayout());
            hw.setLocation(0, RESERVED);
            hw.setSize(DisplaySystem.getDisplaySystem().getWidth(), DisplaySystem.getDisplaySystem().getHeight()-RESERVED-RESERVED_TOP);
            hw.add(h, BorderLayout.NORTH);
            h.load();
            _proot.addWindow(hw);
            _help = hw;
            _helpContainer = h;
        }
        else {
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    showHelp();
                }
            });
        }
    }

    public void repeat() {
        _input.playback();
        throw new ActionCancelledException();
    }

    public void uiSettings() {
        NH.getInstance().uiSettings();
    }

    public void append(final String text) {
        if(isUIThread()) {
            _narrative.append(text);
        }
        else {
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    append(text);
                }
            });
        }
    }

    public void more() {
        append(" --More--");
        _input.nextKey();
        clear();
    }

    public void previous() {
        if(isUIThread()) {
            _narrative.previous();
        }
        else {
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    previous();
                }
            });
        }
    }

    public NarrativeFrame getNarrativeFrame() {
        return _narrative;
    }

    private Object _selectInventoryLock = new Object();
    public void toggleSelectInventory() {
        toggleSelectInventory(null);
    }

    public void toggleSelectInventory(final ItemFilter filter) {
        if(isUIThread()) {
            try {
                if(_selectInventoryVisible) {
                    _proot.addController(new BSlideInOutController(_selectInventoryWindow,
                                BSlideInOutController.EXIT_WEST, 0.2f));
                    _selectInventoryVisible = false;
                }
                else {
                    int iloc = (int) (DisplaySystem.getDisplaySystem().getWidth()*0.4);
                    iloc = 500;
                    _selectInventoryWindow.setLocation(0, _selectInventoryWindow.getY());
                    //_selectInventory.refresh(filter, _bot);
                    _selectInventory.setInventory(_bot.getInventory(), filter, _bot);
                    _selectInventoryWindow.pack();
                    _selectInventoryWindow.setSize(iloc, _selectInventoryWindow.getHeight());
                    _selectInventoryWindow.setLocation(_selectInventoryWindow.getX(), DisplaySystem.getDisplaySystem().getHeight()-_selectInventoryWindow.getHeight()-RESERVED_TOP);
                    _proot.addController(new BSlideInOutController(_selectInventoryWindow,
                                BSlideInOutController.ENTER_WEST, 0.2f));
                    _selectInventoryVisible = true;
                }
            }
            finally {
                synchronized(_selectInventoryLock) {
                    _selectInventoryLock.notify();
                }
            }
        }
        else {
            synchronized(_selectInventoryLock) {
                EventQueue.getEventQueue().postback(new AbstractGameAction() {
                    public void perform() {
                        toggleSelectInventory(filter);
                    }
                });
                try {
                    _selectInventoryLock.wait();
                }
                catch(InterruptedException e) {
                    throw new Error(e);
                }
            }
        }
    }

    private Object _keyedSelectorLock = new Object();
    public void toggleKeyedSelector() {
        if(isUIThread()) {
            try {
                if(_keyedSelectorVisible) {
                    _proot.addController(new BSlideInOutController(_keyedSelectorWindow,
                                BSlideInOutController.EXIT_WEST, 0.2f));
                    _keyedSelectorVisible = false;
                }
                else {
                    int iloc = (int) (DisplaySystem.getDisplaySystem().getWidth()*0.4);
                    iloc = 500;
                    _keyedSelectorWindow.setLocation(0, _keyedSelectorWindow.getY());
                    _keyedSelector.refresh();
                    _keyedSelectorWindow.pack();
                    _keyedSelectorWindow.setSize(iloc, _keyedSelectorWindow.getHeight());
                    _keyedSelectorWindow.setLocation(_keyedSelectorWindow.getX(), DisplaySystem.getDisplaySystem().getHeight()-_keyedSelectorWindow.getHeight()-RESERVED_TOP);
                    _proot.addController(new BSlideInOutController(_keyedSelectorWindow,
                                BSlideInOutController.ENTER_WEST, 0.2f));
                    _keyedSelectorVisible = true;
                }
            }
            finally {
                synchronized(_keyedSelectorLock) {
                    _keyedSelectorLock.notify();
                }
            }
        }
        else {
            synchronized(_keyedSelectorLock) {
                EventQueue.getEventQueue().postback(new AbstractGameAction() {
                    public void perform() {
                        toggleKeyedSelector();
                    }
                });
                try {
                    _keyedSelectorLock.wait();
                }
                catch(InterruptedException e) {
                    throw new Error(e);
                }
            }
        }
    }

    private Object _inventoryLock = new Object();
    private Object _inventoryActionLock = new Object();
    public void toggleInventory() {
        toggleInventory(null);
    }

    public void toggleInventoryAction() {
        if(isUIThread()) {
            try {
                if(_inventoryActionVisible) {
                    _proot.addController(new BSlideInOutController(_inventoryActionWindow,
                                BSlideInOutController.EXIT_EAST, 0.2f));
                    _inventoryActionVisible = false;
                }
                else {
                    int iloc = (int) (DisplaySystem.getDisplaySystem().getWidth()*0.4);
                    iloc = 300;
                    _inventoryActionWindow.setLocation(DisplaySystem.getDisplaySystem().getWidth()-iloc-500,
                            _inventoryActionWindow.getY());
                    _actionSelector.refresh();
                    _inventoryActionWindow.pack();
                    _inventoryActionWindow.setSize(iloc, _inventoryActionWindow.getHeight());
                    _inventoryActionWindow.setLocation(_inventoryActionWindow.getX(), DisplaySystem.getDisplaySystem().getHeight()-_inventoryActionWindow.getHeight()-RESERVED_TOP);
                    //_inventory.refresh(filter, _bot);
                    _proot.addController(new BSlideInOutController(_inventoryActionWindow,
                                BSlideInOutController.ENTER_EAST, 0.2f));
                    _inventoryActionVisible = true;
                }
            }
            finally {
                synchronized(_inventoryActionLock) {
                    _inventoryActionLock.notify();
                }
            }
        }
        else {
            synchronized(_inventoryActionLock) {
                EventQueue.getEventQueue().postback(new AbstractGameAction() {
                    public void perform() {
                        toggleInventoryAction();
                    }
                });
                try {
                    _inventoryActionLock.wait();
                }
                catch(InterruptedException e) {
                    throw new Error(e);
                }
            }
        }
    }

    public void toggleInventory(final ItemFilter filter) {
        if(isUIThread()) {
            try {
                if(_inventoryVisible) {
                    _proot.addController(new BSlideInOutController(_inventoryWindow,
                                BSlideInOutController.EXIT_EAST, 0.2f));
                    _inventoryVisible = false;
                }
                else {
                    int iloc = (int) (DisplaySystem.getDisplaySystem().getWidth()*0.4);
                    iloc = 500;
                    _inventoryWindow.setLocation(DisplaySystem.getDisplaySystem().getWidth()-iloc,
                            _inventoryWindow.getY());
                    _inventory.setInventory(_bot.getInventory(), filter, _bot);
                    //_inventory.refresh(filter, _bot);
                    _proot.addController(new BSlideInOutController(_inventoryWindow,
                                BSlideInOutController.ENTER_EAST, 0.2f));
                    _inventoryVisible = true;
                }
            }
            finally {
                synchronized(_inventoryLock) {
                    _inventoryLock.notify();
                }
            }
        }
        else {
            synchronized(_inventoryLock) {
                EventQueue.getEventQueue().postback(new AbstractGameAction() {
                    public void perform() {
                        toggleInventory(filter);
                    }
                });
                try {
                    _inventoryLock.wait();
                }
                catch(InterruptedException e) {
                    throw new Error(e);
                }
            }
        }
    }

    public void toggleSkills() {
        if(isUIThread()) {
            if(_skillsVisible) {
                _proot.addController(new BSlideInOutController(_skillsWindow,
                            BSlideInOutController.EXIT_WEST, 0.2f));
                _skillsVisible = false;
            }
            else {
                int iloc = (int) (DisplaySystem.getDisplaySystem().getWidth()*0.4);
                _skillsWindow.setLocation(0, _skillsWindow.getY());
                _skills.refresh();
                _proot.addController(new BSlideInOutController(_skillsWindow,
                            BSlideInOutController.ENTER_WEST, 0.2f));
                _skillsVisible = true;
            }
        }
        else {
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    toggleSkills();
                }
            });
        }
    }

    private BSlideInOutController _lookIn;
    private BSlideInOutController _lookOut;
    public void hideLook() {
        if(isUIThread()) {
            if(_lookVisible) {
                if(_lookIn!=null) {
                    _proot.removeController(_lookIn);
                    _lookIn = null;
                }
                _lookOut = new BSlideInOutController(_look, BSlideInOutController.EXIT_NORTH, 0.2f) {
                    protected void done() {
                        _proot.removeController(_lookOut);
                        _lookOut = null;
                    }
                };
                _proot.addController(_lookOut);
                _lookVisible = false;
            }
        }
        else {
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    hideLook();
                }
            });
        }
    }

    public void showLook(final NHBot bot, final NHSpace space, final boolean keys) {
        if(isUIThread()) {
            if(!_lookVisible) {
                if(_lookOut!=null) {
                    _proot.removeController(_lookOut);
                    _lookOut = null;
                }
                _look.setLoot(space.getLoot());
                _look.getInventory().setShowKeys(keys);
                _look.pack();
                _look.setLocation(_look.getX(), DisplaySystem.getDisplaySystem().getHeight()-_look.getHeight()-RESERVED_TOP);
                _lookIn = new BSlideInOutController(_look, BSlideInOutController.ENTER_NORTH, 0.2f) {
                    protected void done() {
                        _proot.removeController(_lookIn);
                        _lookIn = null;
                    }
                };
                _proot.addController(_lookIn);
                _lookVisible = true;
            }
            else {
                //System.err.println("ALREADY LOOKING");
            }
        }
        else {
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    showLook(bot, space, keys);
                }
            });
        }
    }

    public void toggleLook(final NHSpace space) {
        if(isUIThread()) {
            _look.getInventory().setShowKeys(false);
            Inventory loot = space.getLoot();
            if(_lookVisible) {
                if(_lookIn!=null) {
                    _proot.removeController(_lookIn);
                    _lookIn = null;
                }
                _lookOut = new BSlideInOutController(_look, BSlideInOutController.EXIT_NORTH, 0.2f) {
                        protected void done() {
                        _proot.removeController(_lookOut);
                        _lookOut = null;
                    }
                };
                _proot.addController(_lookOut);
                _lookVisible = false;
            }
            else {
                if(_lookOut!=null) {
                    _proot.removeController(_lookOut);
                    _lookOut = null;
                }
                _look.setLoot(loot);
                _look.pack();
                //System.err.println("ADDING LOOK: "+_look.getHeight());
                _look.setLocation(_look.getX(), DisplaySystem.getDisplaySystem().getHeight()-_look.getHeight()-RESERVED_TOP);
                _lookIn = new BSlideInOutController(_look, BSlideInOutController.ENTER_NORTH, 0.2f) {
                    protected void done() {
                        _proot.removeController(_lookIn);
                        _lookIn = null;
                    }
                };
                _proot.addController(_lookIn);
                _lookVisible = true;
            }
        }
        else {
            EventQueue.getEventQueue().postback(new AbstractGameAction() {
                public void perform() {
                    toggleLook(space);
                }
            });
        }
    }

    private static int _autoinv = -1;
    public boolean isAutoShowInventory() {
        if(_autoinv==-1) {
            _autoinv = Boolean.getBoolean("tower.noshowinventory")?0:1;
        }
        return _autoinv==1&&!_input.isPlayback();
    }

    static boolean isUIThread() {
        return Thread.currentThread()==_ui;
    }
}
