package org.excelsi.aether;


public class MechanicsEvent extends ContextualEvent<Mechanics> {
    public enum Type { start, end };


    private final Type _t;
    private final Mechanics _m;
    private final Attack _a;
    private final NHBot _attacker;
    private final NHBot _defender;
    private final NHSpace[] _path;
    private final Outcome _outcome;


    public MechanicsEvent(Mechanics m, Attack a, NHBot attacker, NHBot defender, NHSpace[] path) {
        super(attacker, m);
        _t = Type.start;
        _m = m;
        _a = a;
        _attacker = attacker;
        _defender = defender;
        _path = path;
        _outcome = null;
    }

    public MechanicsEvent(Mechanics m, Attack a, NHBot attacker, NHBot defender, Outcome outcome) {
        super(attacker, m);
        _t = Type.end;
        _m = m;
        _a = a;
        _attacker = attacker;
        _defender = defender;
        _path = null;
        _outcome = outcome;
    }

    @Override public String getType() {
        return "mechanics";
    }

    public Type getMechanicsType() {
        return _t;
    }

    public Attack getAttack() {
        return _a;
    }

    public NHBot getAttacker() {
        return _attacker;
    }

    public NHBot getDefender() {
        return _defender;
    }

    public NHSpace[] getPath() {
        return _path;
    }

    public Outcome getOutcome() {
        return _outcome;
    }
}
