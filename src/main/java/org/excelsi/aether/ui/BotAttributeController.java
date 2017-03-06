package org.excelsi.aether.ui;


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.aether.NHBot;
import org.excelsi.aether.Item;
import org.excelsi.aether.ChangeEvent;
import org.excelsi.aether.BotAttributeChangeEvent;
import org.excelsi.aether.AddEvent;
import org.excelsi.aether.AttributeChangeEvent;
import org.excelsi.aether.RemoveEvent;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


public class BotAttributeController extends ChangeController {
    @Override protected void added(final SceneContext c, final AddEvent b) {
    }

    @Override protected void removed(final SceneContext c, final RemoveEvent b) {
    }

    @Override protected void changed(final SceneContext c, final ChangeEvent e) {
        // TODO: move dead to bot space controller?
        final BotAttributeChangeEvent be = (BotAttributeChangeEvent) e;
        final Node bot = c.getNode(be.getBot().getId());
        log().info(be.getBot()+" attr="+be.getAttribute()+" from="+be.getFrom()+" to="+be.getTo());
        switch(be.getAttribute()) {
            case "dead":
                Nodes.detachFromParent(bot);
                break;
            case "wielded":
                Bots.wield((SlotNode)bot.getChild("ornaments"), (NHBot)be.getBot(), c);
                break;
            case "worn":
                Bots.wear((SlotNode)bot.getChild("ornaments"), (NHBot)be.getBot(), (Item)be.getTo(), c);
                break;
            case "tookOff":
                Bots.takeOff((SlotNode)bot.getChild("ornaments"), (NHBot)be.getBot(), (Item)be.getTo(), c);
                break;
        }
    }

    @Override protected void attributeChanged(SceneContext c, AttributeChangeEvent e) {
    }
}
