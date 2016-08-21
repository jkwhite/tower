package org.excelsi.aether;


import java.util.Random;
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
        _player = (Patsy) c.getUniverse().createBot((b)->{return "Traveler".equals(b.getProfession());});
        //_player = new Patsy();
        //_player.setModel("@");
        _player.setInputSource(c.getInputSource());
        Grammar.setPov(_player);

        //final Stage l1 = _gen.generate(
            //new LevelRecipe()
            //.name("The Lower Reaches")
            //.ordinal(1)
            //.width(80)
            //.height(24)
            //.random(new Random())
        //);
        //_bulk.addLevel(l1);
        setLevel(c.getBulk().findLevel(1));
        while(c.getState()==this) {
            try {
                _level.tick(c);
                //Thread.sleep(250);
            }
            catch(ActionCancelledException e) {
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setLevel(final Stage level) {
        level.getMatrix().getSpace(level.getMatrix().width()/2,level.getMatrix().height()/2).setOccupant(_player);
        final Stage old = _level;
        _level = level;
        EventBus.instance().post("changes", new ChangeEvent<Stage>(this, "level", old, _level));
        EventBus.instance().post("keys", new ChangeEvent<Stage>(this, "level", old, _level));
        connect(_level.getEventSource());
    }

    private void connect(final EventSource s) {
        s.addMatrixListener(_relay);
        s.addContainerListener(_relay);
        s.addNHSpaceListener(_relay);
        s.addNHEnvironmentListener(_relay);
    }
}
