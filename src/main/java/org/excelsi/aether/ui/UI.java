package org.excelsi.aether.ui;


import java.util.HashMap;
import java.util.Map;

import org.excelsi.aether.Action;
import org.excelsi.aether.Level;
import org.excelsi.aether.Event;
import org.excelsi.aether.AddEvent;
import org.excelsi.aether.RemoveEvent;
import org.excelsi.aether.MoveEvent;
import org.excelsi.aether.OrientEvent;
import org.excelsi.aether.KeyEvent;
import org.excelsi.aether.BotAttributeChangeEvent;
import static org.excelsi.aether.ui.ControllerFactory.constant;
import com.jme3.asset.AssetManager;


public class UI {
    public static NodeFactory nodeFactory(final AssetManager assets) {
        final Map<String,NodeFactory> nfs = new HashMap<>();
        nfs.put("level", new LevelNodeFactory());
        nfs.put("space", new SpaceNodeFactory(assets));
        nfs.put("bot", new BotNodeFactory(assets));
        nfs.put("item", new ItemNodeFactory(assets));
        return new CompositeNodeFactory(nfs, new PlaceholderNodeFactory(assets));
    }

    public static ControllerFactory controllerFactory() {
        return new TypeControllerFactory(
            Maps.<String,ControllerFactory>map(
                "bot", new InstanceControllerFactory(
                    Maps.<Class,ControllerFactory>map(
                        AddEvent.class, constant(new BotController()),
                        RemoveEvent.class, constant(new BotController()),
                        MoveEvent.class, constant(new BotController()),
                        OrientEvent.class, constant(new OrientController()),
                        BotAttributeChangeEvent.class, constant(new BotAttributeController()))
                ),
                "level", constant(new LevelController()),
                "mechanics", constant(new MechanicsController()),
                "space", constant(new SpaceController()),
                "knowledge", constant(new KnowledgeController()),
                "item", constant(new ItemController())
            )
        );
    }

    public static Action findAction(final LogicEvent le) {
        final Event e = le.e();
        if(e instanceof KeyEvent) {
            final KeyEvent ke = (KeyEvent) e;
            final Keymap m = new Keymap(ke.key());
            final String aname = le.ctx().ctx().getUniverse().getKeymap().get(ke.key());
            //LOG.info("found action name: '"+aname+"'");
            if(aname==null) {
                return null;
            }
            final Action a = le.ctx().ctx().getUniverse().getActions().get(aname);
            //LOG.debug("found action: "+a+" for key: "+e);
            if(a!=null) {
                try {
                    final Action inst = a.getClass().newInstance();
                    return inst;
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    private UI() {}
}
