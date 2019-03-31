package org.excelsi.aether.ui.lemur;


import java.util.function.Function;

import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.audio.AudioListenerState;
import com.jme3.app.DebugKeysAppState;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
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
import com.jme3.input.event.MouseButtonEvent;
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

import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.style.BaseStyles;

import org.excelsi.aether.*;
import org.excelsi.aether.ui.*;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class LemurMain extends SimpleApplication implements EventBus.Handler {
    private static final Logger LOG = LoggerFactory.getLogger(LemurMain.class);
    private Runnable _events;
    private EventBus.Handler _jmeEvents;
    private String _jfxSubscription;
    private String _jmeSubscription;
    private SceneContext _ctx;

    public static void main( String... args ) {
        //GuiDemo main = new GuiDemo();
        //main.start();
        PropertyConfigurator.configure(LemurMain.class.getClassLoader().getResource("log4j.properties"));
        LemurMain app = new LemurMain();
        app.setPauseOnLostFocus(false);
        app.start();
    }           

    public LemurMain() {
        super(new FlyCamAppState(), new AudioListenerState(), new DebugKeysAppState());
    }
    
    public void simpleInitApp() {
        GuiGlobals.initialize(this);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
        Container myWindow = new Container();
        guiNode.attachChild(myWindow);
        myWindow.setLocalTranslation(300, 300, 0);
        myWindow.addChild(new Label("Hello, World."));
        Button clickMe = myWindow.addChild(new Button("Click Me"));
        clickMe.addClickCommands(new Command<Button>() {
                @Override public void execute( Button source ) {
                    System.out.println("The world is yours.");
                }
            });            

        assetManager.registerLocator("/", ClasspathLocator.class);

        initState();
    }

    public void simpleInitAppOld() {
            
        // Initialize the globals access so that the defualt
        // components can find what they need.
        GuiGlobals.initialize(this);
            
        // Load the 'glass' style
        BaseStyles.loadGlassStyle();
            
        // Set 'glass' as the default style when not specified
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
    
        // Create a simple container for our elements
        Container myWindow = new Container();
        guiNode.attachChild(myWindow);
            
        // Put it somewhere that we will see it
        // Note: Lemur GUI elements grow down from the upper left corner.
        myWindow.setLocalTranslation(300, 300, 0);
    
        // Add some elements
        myWindow.addChild(new Label("Hello, World."));
        Button clickMe = myWindow.addChild(new Button("Click Me"));
        clickMe.addClickCommands(new Command<Button>() {
                @Override
                public void execute( Button source ) {
                    System.out.println("The world is yours.");
                }
            });            
    }    

    public void simpleUpdate(final float tpf) {
        super.simpleUpdate(tpf);
        if(_jfxSubscription!=null && EventBus.instance().hasEvents(_jfxSubscription)) {
            //Platform.runLater(_events);
            LOG.info("received JFX events");
            _events.run();
        }
        if(_jmeSubscription!=null && EventBus.instance().hasEvents(_jmeSubscription)) {
            LOG.info("received JME events");
            EventBus.instance().consume(_jmeSubscription, _jmeEvents);
        }
    }

    @Override public void handleEvent(final Event e) {
        LOG.info("got event: "+e);
    }

    private void initState() {
        _events = new Runnable() {
            @Override public void run() {
                EventBus.instance().consume(_jfxSubscription, LemurMain.this);
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
        LOG.info("started logic");
    }
}
