package org.excelsi.aether;


import java.util.Random;
import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.aether.ActionCancelledException;
import org.excelsi.aether.Grammar;
import org.excelsi.aether.Patsy;
import org.excelsi.aether.EventSource;


public class World implements State {
    private final int _initial;
    private final EventBusRelayer _relay = new EventBusRelayer();
    private Stage _level;
    private Patsy _player;
    private StageGenerator _gen = new ExpanseLevelGenerator();


    public World() {
        this(1);
    }

    public World(int initial) {
        _initial = initial;
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

        connectOnce();
        setLevel(c, c.getBulk().findLevel(_initial), null);
        while(c.getState()==this) {
            try {
                //System.err.println("tick: "+_level);
                Time.tick();
                _level.tick(c);
            }
            catch(ActionCancelledException e) {
                if(e.getMessage()!=null) {
                    c.n().print(_player, e.getMessage());
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setLevel(final Context c, final Stage level, final MSpace start) {
        final Stage old = _level;
        _level = level;
        if(old!=null) {
            disconnect(old.getEventSource());
        }

        connect(_level.getEventSource());
        EventBus.instance().post("changes", new ChangeEvent<Bulk,Stage>(this, "level", c.getBulk(), old, _level));
        EventBus.instance().post("keys", new ChangeEvent<Bulk,Stage>(this, "level", c.getBulk(), old, _level));

        if(old!=null) {
            switchLevel((Level)level, start);
        }
        else {
            MSpace m = level.getMatrix().getSpace(level.getMatrix().width()/2,level.getMatrix().height()-12);
            if(m==null||!m.isWalkable()) {
                m = ((Level)level).findRandomEmptySpace();
            }
            m.setOccupant(_player);
        }
    }

    public Stage getLevel() {
        return _level;
    }

    private final void switchLevel(Level l, MSpace start) {
        //Logger.global.info("setting level "+level);
        MSpace[] surr = _player.getEnvironment().getMSpace().surrounding();
        int partition = 0;
        boolean ascended = false;
        if(_player.getEnvironment().getMSpace() instanceof Stairs) {
            Stairs str = (Stairs)_player.getEnvironment().getMSpace();
            partition = str.getPartition();
            ascended = str.isAscending();
        }
        //System.err.println("move with partition "+partition);
        _player.getEnvironment().getMSpace().clearOccupant();
        //Level l = getFloor(level);
        //final boolean ascended = _currentLevel<level;
        //_currentLevel = level;
        MSpace s = start;
        if(s==null) {
            for(MSpace sp:l.spaces()) {
                if(sp instanceof Stairs) {
                    Stairs st = (Stairs) sp;
                    if(ascended!=st.isAscending() && st.getPartition()==partition) {
                        //System.err.println("found stairs "+sp);
                        s = sp;
                        break;
                    }
                }
            }
        }
        if(s==null||s.isOccupied()) {
            s = l.findRandomNormalEmptySpace();
            if(s==null||s.isOccupied()) {
                s = l.findNearestEmpty(Ground.class, (MatrixMSpace) l.findRandomEmptySpace());
            }
        }
        MSpace[] nsurr = s.surrounding();
        for(MSpace a:surr) {
            if(a!=null&&a.isOccupied()) {
                NHBot b = (NHBot) a.getOccupant();
                if(b.threat(_player)==Threat.familiar||Rand.d100(50)) {
                    for(MSpace pl:nsurr) {
                        if(pl!=null&&pl.isWalkable()&&!pl.isOccupied()&&b.canOccupy((NHSpace)pl)) {
                            a.clearOccupant();
                            pl.setOccupant(b);
                            break;
                        }
                    }
                }
            }
        }
        //_player.setLevel(getCurrentLevel());
        //Logger.global.info(_player+" moved to level "+l.getFloor());
        //if(getCurrentLevel().getFloor()!=_currentLevel) {
            //throw new IllegalStateException("level mismatch: "+getCurrentLevel().getFloor()+"/"+_currentLevel);
        //}
        s.setOccupant(_player);

        //for(GameListener gl:_listeners) {
            //if(ascended) {
                //gl.ascended(this);
            //}
            //else {
                //gl.descended(this);
            //}
        //}
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
