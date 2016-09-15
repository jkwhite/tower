package org.excelsi.aether;


import static org.excelsi.aether.Events.TOPIC_UI;


public class BlockingNarrative implements NNarrative {
    private final EventBus _e;


    public BlockingNarrative(final EventBus e) {
        _e = e;
    }

    @Override public void pause() {
        EventBus.instance().await(TOPIC_UI, new PauseEvent(this));
    }

    @Override public void title(String title) {
        _e.post(TOPIC_UI, new TitleEvent(this, title));
    }

    @Override public void message(String m) {
        _e.post(TOPIC_UI, new MessageEvent(this, MessageEvent.Type.ephemeral, m));
    }

    @Override public void print(NHBot source, Object m) {
        _e.post(TOPIC_UI, new MessageEvent(source, MessageEvent.Type.ephemeral, m.toString()));
    }

    @Override public void printf(NHBot source, String message, Object... args) {
        // NEXT: implement POV based on old narrative
    }

    @Override public boolean confirm(String m) {
        return _e.await(TOPIC_UI, new QueryEvent(this, QueryEvent.Type.bool, m)).<Boolean>getAnswer();
    }

    @Override public void poster(String m) {
        _e.post(TOPIC_UI, new MessageEvent(this, MessageEvent.Type.permanent, m));
    }

    @Override public <E> E choose(SelectionMenu<E> m) {
        return _e.await(TOPIC_UI, new SelectEvent<E>(this, m)).getMenu().getChoice().item();
    }
}
