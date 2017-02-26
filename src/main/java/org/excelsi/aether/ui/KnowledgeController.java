package org.excelsi.aether.ui;


import org.excelsi.aether.Event;
import org.excelsi.aether.KnowledgeEvent;
import org.excelsi.aether.SpaceKnowledgeEvent;
import org.excelsi.aether.BotKnowledgeEvent;
import org.excelsi.aether.NHBot;
import org.excelsi.aether.NHSpace;
import org.excelsi.matrix.Bot;
import org.excelsi.matrix.MSpace;

import com.jme3.scene.Node;


public class KnowledgeController extends Enloggened implements Controller {
    @Override public final void handle(final SceneContext c, final Event e) {
        final KnowledgeEvent ke = (KnowledgeEvent) e;
        //log().info("got event: "+ke);
        if(ke instanceof SpaceKnowledgeEvent) {
            handleSpace(c, (SpaceKnowledgeEvent)ke);
        }
        else if(ke instanceof BotKnowledgeEvent) {
            handleBot(c, (BotKnowledgeEvent)ke);
        }
    }

    private void handleSpace(final SceneContext c, final SpaceKnowledgeEvent e) {
        final Node lev = c.getNode(e.getStage());
        //System.err.println("STAGE: "+e.getStage()+" lev="+lev);
        if(lev!=null) {
            switch(e.getKind()) {
                case "seen":
                case "discovered":
                    //System.err.println(e.getKind()+" adding: "+e.getSpaces());
                    for(MSpace m:e.getSpaces()) {
                        Spaces.createSpace(c, lev, (NHSpace)m);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void handleBot(final SceneContext c, final BotKnowledgeEvent e) {
        final Node lev = c.getNode(e.getStage());
        //System.err.println("STAGE: "+e.getStage()+" lev="+lev);
        if(lev!=null) {
            switch(e.getKind()) {
                case "noticed":
                    //System.err.println(e.getKind()+" adding: "+e.getBots());
                    for(Bot b:e.getBots()) {
                        final NHBot nb = (NHBot)b;
                        Bots.attachBot(c, lev, (NHBot)b);
                    }
                    break;
                case "missed":
                    for(Bot b:e.getBots()) {
                        final NHBot nb = (NHBot)b;
                        Bots.detachBot(c, lev, (NHBot)b);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
