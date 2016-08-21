package org.excelsi.aether;


import java.util.ArrayList;
import java.util.List;
import org.excelsi.aether.NHBot;
import org.excelsi.aether.Rand;


public class ActQueue implements java.io.Serializable {
    private List<ActTimer> _timers = new ArrayList<ActTimer>();


    public void add(NHBot b) {
        ActTimer t = new ActTimer();
        t.bot = b;
        t.randomize();
        _timers.add(t);
    }

    public void remove(NHBot b) {
        for(ActTimer t:_timers) {
            if(t.bot==b) {
                _timers.remove(t);
                break;
            }
        }
    }

    public NHBot[] getBots() {
        synchronized(_timers) {
            NHBot[] bots = new NHBot[_timers.size()];
            for(int i=0;i<bots.length;i++) {
                bots[i] = _timers.get(i).bot;
            }
            return bots;
        }
    }

    public NHBot next() {
        NHBot next = null;
        for(;;) {
            for(ActTimer t:_timers) {
                if(t.timer<=0) {
                    t.reset();
                    next = t.bot;
                    return next;
                }
            }
            for(ActTimer t:_timers) {
                --t.timer;
            }
        }
    }

    @Override public String toString() {
        return "actqueue::{timers:"+_timers+"}";
    }

    private static class ActTimer implements java.io.Serializable {
        public NHBot bot;
        public int timer;

        public void reset() {
            //timer = Math.max(21 - bot.getModifiedQuickness()/5, 1);
            //timer = Math.max(26 - bot.getModifiedQuickness()/4, 1);
            timer = Math.max(1,4-bot.getModifiedQuickness()/33);
        }

        public void randomize() {
            reset();
            timer = Rand.om.nextInt(timer+1);
        }

        @Override public String toString() {
            return "timer::{bot:"+bot+", timer:"+timer+"}";
        }
    }
}
