package org.excelsi.aether;


import static org.excelsi.aether.Events.TOPIC_UI;
import org.excelsi.matrix.Direction;


public class BlockingNarrative implements NNarrative {
    private final EventBus _e;


    public BlockingNarrative(final EventBus e) {
        _e = e;
    }

    @Override public void pause() {
        EventBus.instance().await(TOPIC_UI, new PauseEvent(null));
    }

    @Override public void title(String title) {
        _e.post(TOPIC_UI, new TitleEvent(null, title));
    }

    @Override public void message(String m) {
        _e.post(TOPIC_UI, new MessageEvent(null, MessageEvent.Type.ephemeral, m));
    }

    @Override public void print(NHBot source, Object m, DisplayHints h) {
        _e.post(TOPIC_UI, new MessageEvent(source, MessageEvent.Type.ephemeral, m, h));
    }

    @Override public void print(NHSpace source, Object m, DisplayHints h) {
        _e.post(TOPIC_UI, new MessageEvent(source, MessageEvent.Type.ephemeral, m, h));
    }

    @Override public void printf(NHBot source, String message, Object... args) {
        // NEXT: implement POV based on old narrative
        final String msg = Grammar.format(source, message, args);
        _e.post(TOPIC_UI, new MessageEvent(source, MessageEvent.Type.ephemeral, msg));
    }

    @Override public boolean confirm(String m) {
        return _e.await(TOPIC_UI, new QueryEvent(null, QueryEvent.Type.bool, m)).<Boolean>getAnswer();
    }

    @Override public boolean confirm(final NHBot source, String m) {
        return _e.await(TOPIC_UI, new QueryEvent(source, QueryEvent.Type.bool, m)).<Boolean>getAnswer();
    }

    @Override public void poster(String m) {
        _e.post(TOPIC_UI, new MessageEvent(null, MessageEvent.Type.permanent, m));
    }

    @Override public void chronicle(String m) {
        _e.await(TOPIC_UI, new MessageEvent(null, MessageEvent.Type.narrative, m, DisplayHints.MODAL));
    }

    @Override public void poster(NHBot source, String m) {
        _e.post(TOPIC_UI, new MessageEvent(source, MessageEvent.Type.permanent, m));
    }

    @Override public <E> E choose(NHBot source, SelectionMenu<E> m) {
        return _e.await(TOPIC_UI, new SelectEvent<E>(source, m)).getMenu().getChoice().item();
    }

    @Override public void show(NHBot source, Object shown) {
        _e.post(TOPIC_UI, new InfoEvent(source, shown));
    }

    @Override public void show(NHBot source, Object shown, DisplayHints hints) {
        if(hints.isModal()) {
            _e.await(TOPIC_UI, new InfoEvent(source, shown, hints));
        }
        else {
            _e.post(TOPIC_UI, new InfoEvent(source, shown, hints));
        }
    }

    @Override public Direction direct(NHBot b, String msg) {
        return _e.await(TOPIC_UI, new QueryEvent(b, QueryEvent.Type.direction, msg)).<Direction>getAnswer();
    }
}
