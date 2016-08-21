package org.excelsi.aether.ui;


import java.util.HashMap;
import java.util.Map;

import org.excelsi.aether.Level;
import org.excelsi.aether.MoveEvent;
import org.excelsi.aether.OrientEvent;
import static org.excelsi.aether.ui.ControllerFactory.constant;
import com.jme3.asset.AssetManager;


public class UI {
    public static NodeFactory nodeFactory(final AssetManager assets) {
        final Map<String,NodeFactory> nfs = new HashMap<>();
        nfs.put("level", new LevelNodeFactory());
        nfs.put("space", new SpaceNodeFactory(assets));
        nfs.put("bot", new BotNodeFactory(assets));
        return new CompositeNodeFactory(nfs, new PlaceholderNodeFactory(assets));
    }

    public static ControllerFactory controllerFactory() {
        return new TypeControllerFactory(
            Maps.<String,ControllerFactory>map(
                "bot", new InstanceControllerFactory(
                    Maps.<Class,ControllerFactory>map(
                        MoveEvent.class, constant(new BotController()),
                        OrientEvent.class, constant(new OrientController()))
                ),
                "level", constant(new LevelController()),
                "item", constant(new ItemController())
            )
        );
    }
}
