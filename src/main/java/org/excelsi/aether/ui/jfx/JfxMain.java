package org.excelsi.aether.ui.jfx;


import java.util.function.Function;

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
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;

import com.jme3x.jfx.GuiManager;
import com.jme3x.jfx.cursor.proton.ProtonCursorProvider;
import com.jme3x.jfx.FXMLHud;
import com.jme3x.jfx.window.FXMLWindow;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.Group;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.FontSmoothingType;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import org.lwjgl.opengl.Display;

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
import org.excelsi.aether.KeyEvent;
import org.excelsi.aether.BusInputSource;
import org.excelsi.aether.Universe;
import org.excelsi.aether.Bulk;
import org.excelsi.aether.ui.LogicEvent;
import org.excelsi.aether.ui.UIConstants;
import org.excelsi.aether.ui.Resources;
import org.excelsi.aether.ui.UI;
import org.excelsi.aether.ui.JmeEventHandler;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class JfxMain extends SimpleApplication implements EventBus.Handler {
    private static final Logger LOG = LoggerFactory.getLogger(JfxMain.class);
    private Runnable _events;
    private EventBus.Handler _jmeEvents;
    private String _jfxSubscription;
    private String _jmeSubscription;
    private GuiManager _guiManager;


    public static void main(String[] args){
        PropertyConfigurator.configure(JfxMain.class.getClassLoader().getResource("log4j.properties"));
        JfxMain app = new JfxMain();
        app.setPauseOnLostFocus(false);
        app.start();
    }

    public JfxMain() {
        super(new FlyCamAppState(), new AudioListenerState(), new DebugKeysAppState(), new StatsAppState());
    }

    public void simpleInitApp() {
        assetManager.registerLocator("/", ClasspathLocator.class);

        final GuiManager guiManager = new GuiManager(this.guiNode, this.assetManager, this, true, new ProtonCursorProvider(this, this.assetManager, this.inputManager));
        guiManager.setEverListeningRawInputListener(new RawInputListener() {
            public void beginInput() {
            }

            public void endInput() {
            }

            public void onJoyAxisEvent(JoyAxisEvent e) {
            }

            public void onJoyButtonEvent(JoyButtonEvent e) {
            }

            private long _lastRepeat;
            public void onKeyEvent(KeyInputEvent e) {
                if(e.isRepeating()) {
                    long repeat = System.currentTimeMillis();
                    if(_lastRepeat+300>repeat) {
                        return;
                    }
                    _lastRepeat = repeat;
                }
                LOG.debug(System.nanoTime()+" "+String.format("key char %s for code %d, repeating %s, string %s", e.getKeyChar(), e.getKeyCode(), e.isRepeating(), e.toString()));
                boolean meta = e.getKeyCode()==219;
                if(e.isPressed() && !meta) {
                    final KeyEvent ke = new KeyEvent(this, e.getKeyChar()+"");
                    EventBus.instance().post("keys", ke);
                }
            }

            public void onMouseButtonEvent(MouseButtonEvent e) {
            }

            public void onMouseMotionEvent(MouseMotionEvent e) {
            }

            public void onTouchEvent(TouchEvent e) {
            }
        });
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

    @Override public void simpleUpdate(final float tpf) {
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
        _guiManager.getRootGroup().getChildren().get(0).fireEvent(new LogicEvent(e));
    }

    private void initState() {
        _events = new Runnable() {
            @Override public void run() {
                EventBus.instance().consume(_jfxSubscription, JfxMain.this);
            }
        };
        _jmeEvents = new JmeEventHandler(getCamera(), assetManager, UI.controllerFactory(), UI.nodeFactory(assetManager), rootNode);
        EventBus.instance().subscribe(UIConstants.QUEUE_JME, UIConstants.QUEUE_JME);
        _jfxSubscription = EventBus.instance().subscribe("keys", UIConstants.QUEUE_JFX);
        _jmeSubscription = EventBus.instance().subscribe("changes", UIConstants.QUEUE_JME);

        final Logic logic = new Logic(
            new Historian(
                new Context(
                    new BlockingNarrative(EventBus.instance()),
                    new Universe(),
                    new Bulk(),
                    new BusInputSource()
                )
                .state(new ScriptedState("dawn", new Script("script/dawn.groovy")))
            )
        );
        logic.start();
    }
}
