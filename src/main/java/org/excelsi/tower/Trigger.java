package org.excelsi.tower;


import org.excelsi.aether.*;
import org.excelsi.matrix.Direction;
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.Bot;
import java.util.*;


public class Trigger implements Mixin<Level> {
    private final String _desc;
    private final T _t;


    public Trigger(final String desc) {
        _desc = desc;
        _t = parse(desc);
    }

    @Override public void mix(final Level level) {
        level.getEventSource().addNHSpaceListener(new NHSpaceAdapter() {
            @Override public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
                    //System.err.println("SOURCE: "+source+" FROM: "+from+" TO: "+to+" BOT: "+b);
                    //Thread.dumpStack();
                if(source==to) {
                    //System.err.println("testing '"+_desc+"'");
                    if(_t.c.test((NHBot)b)) {
                        //System.err.println("executing '"+_desc+"'");
                        _t.a.execute((NHBot)b);
                        if(_t.remove) {
                            level.getEventSource().removeNHSpaceListener(this);
                        }
                    }
                }
            }
        });
    }

    private T parse(final String s) {
        Parser p = new Parser(s);
        Condition c;
        Action a;
        String src = p.next();
        switch(p.next()) {
            case "approaching":
                Direction d = Direction.named(p.next());
                p.next();
                int dist = Integer.parseInt(p.next());
                c = (b)->{
                    return b.isPlayer() && ((MatrixMSpace)b.getEnvironment().getMSpace()).getJ()<dist;
                };
                break;
            default:
                c = (b)->{ return false; };
        }
        final String action = p.next();
        switch(action) {
            case "narrative":
                String res = Data.resource("/script/"+p.next()+".txt");
                a = (b)->{ System.err.println("SHOWING: "+res); Context.c().n().chronicle(res); };
                break;
            default:
                System.err.println("************ UNKNOWN: '"+action+"'");
                a = (b)->{};
                break;
        }
        final String then = p.next();
        boolean remove = "once".equals(then);
        return new T(c, a, remove);
    }

    @FunctionalInterface
    interface Condition {
        boolean test(NHBot b);
    }

    @FunctionalInterface
    interface Action {
        void execute(NHBot b);
    }

    static class T {
        public final Condition c;
        public final Action a;
        public final boolean remove;

        public T(Condition c, Action a, boolean remove) {
            this.c = c;
            this.a = a;
            this.remove = remove;
        }
    }

    static class Parser {
        private final List<String> _tokens;
        private int _pos = 0;


        public Parser(String s) {
            _tokens = new ArrayList<>();
            _tokens.addAll(Arrays.asList(s.split("[, ]+")));
        }

        public String peek() {
            return _pos<_tokens.size()?_tokens.get(_pos):"";
        }

        public void pushback() {
            if(_pos>0) {
                _pos--;
            }
        }

        public void advance() {
            if(_pos<_tokens.size()-1) {
                _pos++;
            }
        }

        public boolean end() {
            return _pos>=_tokens.size();
        }

        public String next() {
            final String s = peek();
            advance();
            return s;
        }
    }
}
