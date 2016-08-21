package org.excelsi.aether;


import java.util.function.Function;

import com.jme3.app.SimpleApplication;
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

//import org.lwjgl.opengl.Display;

import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;


public class LGui extends SimpleApplication {
    public static void main(String[] args){
        LGui app = new LGui();
        app.setPauseOnLostFocus(false);
        app.start();
    }

    @Override public void simpleInitApp() {
        assetManager.registerLocator("/", ClasspathLocator.class);

        GuiGlobals.initialize(this);
        
        BaseStyles.loadGlassStyle();

        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

        // Create a simple container for our elements
        Container myWindow = new Container();
        guiNode.attachChild(myWindow);

        // Put it somewhere that we will see it.
        // Note: Lemur GUI elements grow down from the upper left corner.
        myWindow.setLocalTranslation(300, 300, 0);

        // Add some elements
        myWindow.addChild(new Label("Hello, World."));
        Button clickMe = myWindow.addChild(new Button("Click Me"));
        clickMe.addClickCommands(new Command<Button>() {
                @Override
                public void execute( Button source ) {
                    System.err.println("The world is yours.");
                }
            });

        flyCam.setEnabled(false);
        inputManager.setCursorVisible(true);
        mouseInput.setCursorVisible(true);
    }
}
