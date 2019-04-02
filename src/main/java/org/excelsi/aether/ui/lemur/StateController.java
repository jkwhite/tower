package org.excelsi.aether.ui.lemur;


import com.jme3.scene.Node;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.style.BaseStyles;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.SpringGridLayout;
import org.excelsi.aether.ui.SceneContext;
import org.excelsi.aether.ui.Resources;
import org.excelsi.aether.*;


public class StateController extends Enloggened implements Controller {
    @Override public boolean consume(Event e, Node gui, SceneContext c) {
        StateChangeEvent se = (StateChangeEvent) e;
        switch(se.getNewValue().getName()) {
            case "title":
                title(se.getNewValue(), gui, c);
                break;
            default:
                throw new IllegalStateException("unknown state "+se.getNewValue());
        }
        return true;
    }

    public void title(State s, Node gui, SceneContext c) {
        l().info("creating title");
        BorderLayout bl = new BorderLayout();
        SpringGridLayout sgl = new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even);
        Container w = new Container(sgl);
        gui.attachChild(w);
        IconComponent title = new IconComponent("ui/title-image3.png");
        Label l = new Label("");
        l.setIcon(title);
        l.setInsets(new Insets3f(20,20,20,20));
        title.setHAlignment(HAlignment.Center);
        title.setVAlignment(VAlignment.Center);
        Vector2f is = title.getIconSize();
        //l.setPreferredSize(new Vector3f(200, 200, 0));
        l.setTextHAlignment(HAlignment.Center);
        l.setTextVAlignment(VAlignment.Bottom);

        //l.setPreferredSize(new Vector3f(200, 200, 0));
        //l.setSize(new Vector3f(200, 200, 0));
        l.setFontSize(20);
        //w.addChild(l);

        //BoxLayout boxy = new BoxLayout(Axis.Y, FillMode.None);
        //Container cboxy = new Container(boxy);
        //cboxy.addChild(l);

        //bl.addChild(BorderLayout.Position.Center, l);
        //bl.addChild(BorderLayout.Position.South, new Label("South"));
        w.addChild(l, 0, 0);
        //w.addChild(new Label("Two"));
        //w.addChild(new Label("Three"));

        IconComponent text = new IconComponent("ui/title-text4.png");
        text.setHAlignment(HAlignment.Center);
        Label ltext = new Label("");
        ltext.setIcon(text);
        w.addChild(ltext);

        //bl.addChild(BorderLayout.Position.Center, cboxy);
        w.setLocalTranslation(0, Resources.props().height, 0);
        w.setPreferredSize(new Vector3f(Resources.props().width, Resources.props().height, 0));
        //myWindow.setLocalTranslation(300, 300, 0);
        /*
        myWindow.addChild(new Label("Tower"));
        Button clickMe = myWindow.addChild(new Button("New game"));
        clickMe.addClickCommands(new Command<Button>() {
                @Override public void execute( Button source ) {
                    System.out.println("The world is yours.");
                }
            });            
            */
    }
}
