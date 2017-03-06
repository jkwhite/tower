package org.excelsi.aether.ui.jfx;


import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Group;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.text.Text;
import javafx.scene.layout.BorderPane;

import com.jme3.scene.Spatial;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitor;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.excelsi.aether.Patsy;
import org.excelsi.aether.KeyEvent;
import org.excelsi.aether.Action;
import org.excelsi.aether.ActionEvent;
import org.excelsi.aether.EventBus;
import org.excelsi.aether.AbstractAction;
import org.excelsi.aether.Context;
import org.excelsi.aether.Quit;
import org.excelsi.aether.DefaultNHBot;
import org.excelsi.aether.ui.View;
import org.excelsi.aether.ui.SceneContext;
import org.excelsi.aether.ui.UIAction;
import org.excelsi.aether.ui.Keymap;


public class JfxWorld extends HudNode {
    private static final Logger LOG = LoggerFactory.getLogger(JfxWorld.class);
    //private final Map<Keymap,Action> _actions;


    public JfxWorld() {
        /*
        _actions = new HashMap<>();
        _actions.put(new Keymap("t"), new DebugAction());
        _actions.put(new Keymap("q"), new QuitAction());
        _actions.put(new Keymap("h"), new Patsy.West());
        _actions.put(new Keymap("j"), new Patsy.South());
        _actions.put(new Keymap("k"), new Patsy.North());
        _actions.put(new Keymap("l"), new Patsy.East());

        _actions.put(new Keymap("y"), new Patsy.Northwest());
        _actions.put(new Keymap("u"), new Patsy.Northeast());
        _actions.put(new Keymap("b"), new Patsy.Southwest());
        _actions.put(new Keymap("n"), new Patsy.Southeast());

        _actions.put(new Keymap(","), new DefaultNHBot.Pickup());
        _actions.put(new Keymap(":"), new DefaultNHBot.LookHere());

        _actions.put(new Keymap("="), new ZoomInAction());
        _actions.put(new Keymap("-"), new ZoomOutAction());
        _actions.put(new Keymap("0"), new SceneDumpAction());
        LOG.debug("actionmap: "+_actions);
        */
        addLogicHandler((le)->{
            if(le.e() instanceof KeyEvent) {
                if(onKey(le.ctx(), (KeyEvent)le.e())) {
                    le.consume();
                }
            }
        });
    }

    private boolean onKey(final SceneContext c, final KeyEvent e) {
        final Keymap m = new Keymap(e.key());
        //final Action a = _actions.get(m);
        final String dk = Keymap.encode(e.key());
        final String aname = c.ctx().getUniverse().getKeymap().get(dk);
        //LOG.info("found action name: '"+aname+"' for key '"+dk+"'");
        if(aname==null) {
            LOG.info("no action name for key: "+e);
            return false;
        }
        final Action a = c.ctx().getUniverse().getActions().get(aname);
        LOG.debug("found action: "+a+" for key: "+e);
        if(a!=null) {
            try {
                final Action inst = a.getClass().newInstance();
                if(inst instanceof UIAction) {
                    EventBus.instance().post("jme", new ActionEvent(this, inst));
                }
                else {
                    EventBus.instance().post("actions", new ActionEvent(this, inst));
                }
            }
            catch(Exception ex) {
                LOG.error("failed creating "+a+": "+ex, ex);
            }
            return true;
        }
        return false;
    }

    private static class SceneDumpAction extends UIAction {
        @Override public void perform() {
        }

        @Override public void perform(final Context c) {
        }

        @Override public void perform(final SceneContext c) {
            final int[] lodCounts = new int[11];
            c.getRoot().breadthFirstTraversal(new SceneGraphVisitor() {
                @Override public void visit(final Spatial child) {
                    //System.err.println("child: "+child+", class: "+child.getClass());
                    if(child instanceof Geometry) {
                        final Geometry g = (Geometry) child;
                        System.err.println(g+", lod: "+g.getLodLevel()+" / "+g.getMesh().getNumLodLevels());
                        lodCounts[g.getLodLevel()]++;
                    }
                }
            });
            System.err.println("lod counts: "+Arrays.toString(lodCounts));
        }
    }

    private static class DebugAction extends AbstractAction {
        @Override public void perform() {
        }

        @Override public void perform(final Context c) {
            System.err.println("____ DEBUG ACTION START ____");
            Thread.dumpStack();
            System.err.println("_____ DEBUG ACTION END _____");
        }
    }

    private static class QuitAction extends AbstractAction {
        @Override public void perform(final Context c) {
            if(c.n().confirm("Really quit?")) {
                c.state(new Quit());
            }
        }

        @Override public void perform() {
            System.err.println("____ QUIT ACTION START ____");
            Thread.dumpStack();
            System.err.println("_____ QUIT ACTION END _____");
        }
    }
}
