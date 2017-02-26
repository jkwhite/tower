package org.excelsi.aether;


public abstract class KnowledgeEvent extends StageEvent {
    private final String _kind;
    private final NHBot _b;


    @Override public String getType() { return "knowledge"; }


    public KnowledgeEvent(final Object source, final Stage stage, final NHBot bot, final String kind) {
        super(source, stage);
        _b = bot;
        _kind = kind;
    }

    public NHBot getBot() {
        return _b;
    }

    public String getKind() {
        return _kind;
    }
}
