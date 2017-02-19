package org.excelsi.aether;


import java.util.Random;
import org.excelsi.matrix.MSpace;
import org.excelsi.aether.ActionCancelledException;
import org.excelsi.aether.Grammar;
import org.excelsi.aether.Patsy;
import org.excelsi.aether.EventSource;


public class World implements State {
    private Stage _level;
    private Patsy _player;
    private StageGenerator _gen = new ExpanseLevelGenerator();
    private final EventBusRelayer _relay = new EventBusRelayer();


    public World() {
    }

    @Override public String getName() {
        return "world";
    }

    @Override public void run(final Context c) {
        c.n().title("The Lower Reaches");
        // move player creation to dawn script
        //_player = (Patsy) c.getUniverse().createBot((b)->{return "Traveler".equals(b.getProfession());});
        _player = c.getPov();
        _player.getInventory().setKeyed(true);
        _player.setInputSource(c.getInputSource());
        Grammar.setPov(_player);
        EventBus.instance().post("keys", new BotAttributeChangeEvent<String>(this, _player, "created", "", ""));

        setLevel(c, c.getBulk().findLevel(1));
        connectOnce();
        while(c.getState()==this) {
            try {
                System.err.println("tick: "+_level);
                _level.tick(c);
            }
            catch(ActionCancelledException e) {
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        System.err.println("***************************** DONE **********************");
    }

    public void setLevel(final Context c, final Stage level) {
        MSpace m = level.getMatrix().getSpace(level.getMatrix().width()/2,level.getMatrix().height()/2);
        if(m==null) {
            m = ((Level)level).findRandomEmptySpace();
        }
        m.setOccupant(_player);
        final Stage old = _level;
        _level = level;
        EventBus.instance().post("changes", new ChangeEvent<Bulk,Stage>(this, "level", c.getBulk(), old, _level));
        EventBus.instance().post("keys", new ChangeEvent<Bulk,Stage>(this, "level", c.getBulk(), old, _level));
        if(old!=null) {
            disconnect(old.getEventSource());
        }
        connect(_level.getEventSource());
    }

    public Stage getLevel() {
        return _level;
    }

    private void connectOnce() {
        NHEnvironment.getMechanics().addMechanicsListener(_relay);
    }

    private void connect(final EventSource s) {
        s.addMatrixListener(_relay);
        s.addContainerListener(_relay);
        s.addNHSpaceListener(_relay);
        s.addNHEnvironmentListener(_relay);
    }

    private void disconnect(final EventSource s) {
        s.removeMatrixListener(_relay);
        s.removeContainerListener(_relay);
        s.removeNHSpaceListener(_relay);
        s.removeNHEnvironmentListener(_relay);
    }
}
