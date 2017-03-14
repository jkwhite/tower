package org.excelsi.aether.ui.jfx;


import java.util.function.Function;

import com.jme3.app.LegacyApplication;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.audio.AudioListenerState;
import com.jme3.app.DebugKeysAppState;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.input.InputManager;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.input.KeyNames;
import com.jme3.input.KeyInput;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.profile.AppStep;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import com.jme3.renderer.RenderManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;

import com.jme3x.jfx.GuiManager;
import com.jme3x.jfx.cursor.proton.ProtonCursorProvider;
import com.jme3x.jfx.FXMLHud;
import com.jme3x.jfx.window.FXMLWindow;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import org.lwjgl.opengl.Display;

import org.excelsi.matrix.Typed;
import org.excelsi.aether.EventBus;
import org.excelsi.aether.Event;
import org.excelsi.aether.Historian;
import org.excelsi.aether.Context;
import org.excelsi.aether.NullState;
import org.excelsi.aether.Title;
import org.excelsi.aether.Script;
import org.excelsi.aether.ScriptedState;
import org.excelsi.aether.BlockingNarrative;
import org.excelsi.aether.Logic;
import org.excelsi.aether.ActionEvent;
import org.excelsi.aether.KeyEvent;
import org.excelsi.aether.Events;
import org.excelsi.aether.BusInputSource;
import org.excelsi.aether.Universe;
import org.excelsi.aether.HoverAction;
import org.excelsi.aether.PickAction;
import org.excelsi.aether.Bulk;
import org.excelsi.aether.ui.LogicEvent;
import org.excelsi.aether.ui.Nodes;
import org.excelsi.aether.ui.UIConstants;
import org.excelsi.aether.ui.Resources;
import org.excelsi.aether.ui.UI;
import org.excelsi.aether.ui.JmeEventHandler;
import org.excelsi.aether.ui.SceneContext;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


//public class JfxMain extends LegacyApplication implements EventBus.Handler {
public class JfxMain extends SimpleApplication implements EventBus.Handler {
    private static final Logger LOG = LoggerFactory.getLogger(JfxMain.class);
    private Runnable _events;
    private EventBus.Handler _jmeEvents;
    private String _jfxSubscription;
    private String _jmeSubscription;
    private GuiManager _guiManager;
    private SceneContext _ctx;

    //private com.jme3.scene.Node rootNode = new com.jme3.scene.Node("Root Node");
    //private com.jme3.scene.Node guiNode = new com.jme3.scene.Node("Gui Node");
    //protected BitmapText fpsText;
    //protected BitmapFont guiFont;
    //private boolean showSettings = true;


    public static void main(String[] args){
        PropertyConfigurator.configure(JfxMain.class.getClassLoader().getResource("log4j.properties"));
        JfxMain app = new JfxMain();
        app.setPauseOnLostFocus(false);
        app.start();
    }

    public JfxMain() {
        //super(new FlyCamAppState(), new AudioListenerState(), new DebugKeysAppState(), new StatsAppState());
        super(new FlyCamAppState(), new AudioListenerState(), new DebugKeysAppState());
    }

    /*
    @Override
    public void start() {
        // set some default settings in-case
        // settings dialog is not shown
        boolean loadSettings = false;
        if (settings == null) {
            setSettings(new AppSettings(true));
            loadSettings = true;
        }

        // show settings dialog
        if (showSettings) {
            if (!JmeSystem.showSettingsDialog(settings, loadSettings)) {
                return;
            }
        }
        //re-setting settings they can have been merged from the registry.
        setSettings(settings);
        super.start();
    }
    */

    /*
    @Override
    public void initialize() {
        super.initialize();

        // Several things rely on having this
        guiFont = loadGuiFont();

        guiNode.setQueueBucket(Bucket.Gui);
        guiNode.setCullHint(CullHint.Never);
        viewPort.attachScene(rootNode);
        guiViewPort.attachScene(guiNode);

        stateManager.attach(new StatsAppState(guiNode, guiFont));

        if (inputManager != null) {

            // We have to special-case the FlyCamAppState because too
            // many SimpleApplication subclasses expect it to exist in
            // simpleInit().  But at least it only gets initialized if
            // the app state is added.
            //if (stateManager.getState(FlyCamAppState.class) != null) {
                //flyCam = new FlyByCamera(cam);
                //flyCam.setMoveSpeed(1f); // odd to set this here but it did it before
                //stateManager.getState(FlyCamAppState.class).setCamera( flyCam );
            //}
//
            //if (context.getType() == Type.Display) {
                //inputManager.addMapping(INPUT_MAPPING_EXIT, new KeyTrigger(KeyInput.KEY_ESCAPE));
            //}
//
            //if (stateManager.getState(StatsAppState.class) != null) {
                //inputManager.addMapping(INPUT_MAPPING_HIDE_STATS, new KeyTrigger(KeyInput.KEY_F5));
                //inputManager.addListener(actionListener, INPUT_MAPPING_HIDE_STATS);
            //}
//
            //inputManager.addListener(actionListener, INPUT_MAPPING_EXIT);
        }

        if (stateManager.getState(StatsAppState.class) != null) {
            // Some of the tests rely on having access to fpsText
            // for quick display.  Maybe a different way would be better.
            stateManager.getState(StatsAppState.class).setFont(guiFont);
            fpsText = stateManager.getState(StatsAppState.class).getFpsText();
        }

        // call user code
        simpleInitApp();
    }
    */

    /**
     *  Creates the font that will be set to the guiFont field
     *  and subsequently set as the font for the stats text.
     */
    //protected BitmapFont loadGuiFont() {
        //return assetManager.loadFont("Interface/Fonts/Default.fnt");
    //}

    public void simpleInitApp() {
        //if(inputManager==null) {
            //return;
        //}
        assetManager.registerLocator("/", ClasspathLocator.class);

        final GuiManager guiManager = new GuiManager(this.guiNode, this.assetManager, this, true,
            new ProtonCursorProvider(this, this.assetManager, this.inputManager));
        guiManager.setEverListeningRawInputListener(new RawInputListener() {
            public void beginInput() {
            }

            public void endInput() {
            }

            public void onJoyAxisEvent(JoyAxisEvent e) {
            }

            public void onJoyButtonEvent(JoyButtonEvent e) {
            }

            private final KeyNames _knames = new KeyNames();
            private long _lastRepeat;
            private boolean _ctrlDown = false;
            private boolean _shiftDown = false;
            public void onKeyEvent(KeyInputEvent e) {
                mouseInput.setCursorVisible(false);
                if(e.isRepeating()) {
                    long repeat = System.currentTimeMillis();
                    if(_lastRepeat+300>repeat) {
                        return;
                    }
                    _lastRepeat = repeat;
                }
                final String kname = _knames.getName(e.getKeyCode());
                LOG.info(String.format("key char %s for code %d, repeating %s, string %s", e.getKeyChar(), e.getKeyCode(), e.isRepeating(), e.toString())+": "+kname);
                boolean meta = e.getKeyCode()==219;
                if(e.getKeyCode()==KeyInput.KEY_LCONTROL||e.getKeyCode()==KeyInput.KEY_RCONTROL) {
                    _ctrlDown = e.isPressed();
                }
                else if(e.getKeyCode()==KeyInput.KEY_LSHIFT||e.getKeyCode()==KeyInput.KEY_RSHIFT) {
                    _shiftDown = e.isPressed();
                }
                else if(e.isPressed() && !meta) {
                    String key = kname;
                    char c = e.getKeyChar();
                    //String nkey = key.toString(); //key.length()>1?key:(e.getKeyChar()+"");
                    String nkey = key.length()>1?key:(e.getKeyChar()+"");
                    //System.err.println("1 nkey: '"+nkey+"'");
                    if(nkey.length()==1 && Character.isAlphabetic(nkey.charAt(0))) {
                        if(_shiftDown) {
                            nkey = key.toUpperCase();
                        }
                        //else {
                            //nkey = key.toLowerCase();
                        //}
                    }
                    if(_ctrlDown) {
                        nkey = "C-"+nkey;
                    }
                    //System.err.println("2 nkey: '"+nkey+"'");

                    final KeyEvent ke = new KeyEvent(null, nkey);
                    EventBus.instance().post("keys", ke);
                }
            }

            public void onMouseButtonEvent(MouseButtonEvent e) {
                if(e.isPressed()) {
                    mouseInput.setCursorVisible(true);
                    //System.err.println("MOUSEBUTTON: "+e);
                    Vector3f origin    = _ctx.getCamera().getWorldCoordinates(inputManager.getCursorPosition(), 0.0f);
                    Vector3f direction = _ctx.getCamera().getWorldCoordinates(inputManager.getCursorPosition(), 0.3f);
                    direction.subtractLocal(origin).normalizeLocal();

                    Ray ray = new Ray(origin, direction);
                    CollisionResults results = new CollisionResults();
                    _ctx.getRoot().collideWith(ray, results);
                    if(results.size()>0) {
                        CollisionResult closest = results.getClosestCollision();
                        final Typed col = Nodes.findTyped(closest.getGeometry());
                        System.err.println("PCOLLISION: "+col);
                        EventBus.instance().post("actions", new ActionEvent(null, new PickAction(col)));
                    }
                }
            }

            private Typed _lastHover;
            public void onMouseMotionEvent(MouseMotionEvent e) {
                mouseInput.setCursorVisible(true);
                //System.err.println("MOUSE: "+e);
                Vector3f origin    = _ctx.getCamera().getWorldCoordinates(inputManager.getCursorPosition(), 0.0f);
                Vector3f direction = _ctx.getCamera().getWorldCoordinates(inputManager.getCursorPosition(), 0.3f);
                direction.subtractLocal(origin).normalizeLocal();

                Ray ray = new Ray(origin, direction);
                CollisionResults results = new CollisionResults();
                _ctx.getRoot().collideWith(ray, results);
                if(results.size()>0) {
                    CollisionResult closest = results.getClosestCollision();
                    final Typed col = Nodes.findTyped(closest.getGeometry());
                    if(col!=_lastHover) {
                        System.err.println("HCOLLISION: "+col);
                        EventBus.instance().post("actions", new ActionEvent(null, new HoverAction(col)));
                        _lastHover = col;
                    }
                }
                else {
                }
            }

            public void onTouchEvent(TouchEvent e) {
                mouseInput.setCursorVisible(true);
                //System.err.println("TOUCH: "+e);
            }
        });
        //NO
        //this.inputManager.addRawInputListener(guiManager.getInputRedirector());
        try {
            Thread.sleep(200);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        flyCam.setEnabled(false);
        inputManager.setCursorVisible(true);
        mouseInput.setCursorVisible(true);
        _guiManager = guiManager;

        /* ESC key quits */
        inputManager.deleteMapping(INPUT_MAPPING_EXIT);

        Platform.runLater(new Runnable() {
            public void run() {
                Scene scene = guiManager.getRootGroup().getScene();
                scene.getStylesheets().add("/ui/hud.css");

                final FXMLLoader loader = new FXMLLoader();
                try {
                    final Node root = loader.load(getClass().getResource("/ui/root.fxml"), Resources.jfxResources());
                    _guiManager.getRootGroup().getChildren().add(root);
                    initState();
                }
                catch(Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /*
    @Override
    public void update() {
        if (prof!=null) prof.appStep(AppStep.BeginFrame);

        super.update(); // makes sure to execute AppTasks
        if (speed == 0 || paused) {
            return;
        }

        float tpf = timer.getTimePerFrame() * speed;

        // update states
        if (prof!=null) prof.appStep(AppStep.StateManagerUpdate);
        stateManager.update(tpf);

        // simple update and root node
        simpleUpdate(tpf);

        if (prof!=null) prof.appStep(AppStep.SpatialUpdate);
        rootNode.updateLogicalState(tpf);
        guiNode.updateLogicalState(tpf);

        rootNode.updateGeometricState();
        guiNode.updateGeometricState();

        // render states
        if (prof!=null) prof.appStep(AppStep.StateManagerRender);
        stateManager.render(renderManager);

        if (prof!=null) prof.appStep(AppStep.RenderFrame);
        renderManager.render(tpf, context.isRenderable());
        simpleRender(renderManager);
        stateManager.postRender();

        if (prof!=null) prof.appStep(AppStep.EndFrame);
    }
    */

    public void simpleUpdate(final float tpf) {
        super.simpleUpdate(tpf);
        if(_jfxSubscription!=null && EventBus.instance().hasEvents(_jfxSubscription)) {
            Platform.runLater(_events);
        }
        if(_jmeSubscription!=null && EventBus.instance().hasEvents(_jmeSubscription)) {
            EventBus.instance().consume(_jmeSubscription, _jmeEvents);
        }
    }

    @Override public void handleEvent(final Event e) {
        Scene scene = _guiManager.getRootGroup().getScene();
        //scene.getRoot().fireEvent(new LogicEvent(e));
        _guiManager.getRootGroup().getChildren().get(0).fireEvent(new LogicEvent(_ctx, e));
    }

    private void initState() {
        _events = new Runnable() {
            @Override public void run() {
                EventBus.instance().consume(_jfxSubscription, JfxMain.this);
            }
        };
        final Universe u = new Universe();
        Universe.setUniverse(u);
        final Context ctx = new Context(
            new BlockingNarrative(EventBus.instance()),
            u,
            new Bulk(),
            new BusInputSource()
        ).state(new ScriptedState("dawn", new Script("script/dawn.groovy")));

        _ctx = new SceneContext(ctx, getCamera(), rootNode, UI.nodeFactory(assetManager));
        _jmeEvents = new JmeEventHandler(getCamera(), assetManager, UI.controllerFactory(), UI.nodeFactory(assetManager), rootNode, _ctx);
        EventBus.instance().subscribe(UIConstants.QUEUE_JME, UIConstants.QUEUE_JME);
        _jfxSubscription = EventBus.instance().subscribe("keys", UIConstants.QUEUE_JFX);
        _jmeSubscription = EventBus.instance().subscribe("changes", UIConstants.QUEUE_JME);
        EventBus.instance().subscribe(Events.TOPIC_MECHANICS, UIConstants.QUEUE_JME);

        final Logic logic = new Logic(
            new Historian(ctx)
        );
        logic.start();
    }

    /*
    public void setDisplayFps(boolean show) {
        if (stateManager.getState(StatsAppState.class) != null) {
            stateManager.getState(StatsAppState.class).setDisplayFps(show);
        }
    }

    public void setDisplayStatView(boolean show) {
        if (stateManager.getState(StatsAppState.class) != null) {
            stateManager.getState(StatsAppState.class).setDisplayStatView(show);
        }
    }

    public void simpleRender(RenderManager rm) {
    }
    */
}
