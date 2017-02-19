package org.excelsi.aether;


import org.excelsi.aether.EverythingAdapter;
import org.excelsi.aether.Container;
import org.excelsi.aether.Item;
import org.excelsi.aether.NHBot;
import org.excelsi.aether.NHSpace;
import org.excelsi.matrix.Bot;
import org.excelsi.matrix.Direction;
import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.MSource;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import static org.excelsi.aether.Events.TOPIC_CHANGES;
import static org.excelsi.aether.Events.TOPIC_MECHANICS;


public class EventBusRelayer extends EverythingAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(EventBusRelayer.class);


    @Override public void attackStarted(Mechanics m, Attack attack, NHBot attacker, NHBot defender, NHSpace[] path) {
        post(TOPIC_MECHANICS, new MechanicsEvent(m, attack, attacker, defender, path));
    }

    @Override public void attackEnded(Mechanics m, Attack attack, NHBot attacker, NHBot defender, Outcome outcome) {
        post(TOPIC_MECHANICS, new MechanicsEvent(m, attack, attacker, defender, outcome));
    }

    @Override public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
        post(TOPIC_CHANGES, new MoveEvent(source, (NHBot)b, (NHSpace)from, (NHSpace)to));
    }

    @Override public void faced(Bot b, Direction old, Direction d) {
        post(TOPIC_CHANGES, new OrientEvent(b.getEnvironment().getSpace(), (NHBot)b, old, d));
    }

    @Override public void died(Bot b, MSource s) {
        post(TOPIC_CHANGES, new BotAttributeChangeEvent<Boolean>(b.getEnvironment().getSpace(), b, "dead", false, true));
    }

    @Override public void occupied(MSpace s, Bot b) {
        post(TOPIC_CHANGES, new AddEvent<NHBot,NHSpace>(
            (NHBot)b,
            "bot",
            (NHBot)b,
            (NHSpace)s
        ));
    }

    @Override public void unoccupied(MSpace s, Bot b) {
        post(TOPIC_CHANGES, new RemoveEvent<NHBot,NHSpace>(
            (NHBot)b,
            "bot",
            (NHBot)b,
            (NHSpace)s
        ));
    }

    @Override public void itemDropped(Container space, Item item, int idx, boolean incremented) {
        post(TOPIC_CHANGES, new ContainerAddEvent((NHSpace)space, item, idx, incremented, ContainerAddEvent.Type.dropped));
    }

    @Override public void itemAdded(Container space, Item item, int idx, boolean incremented) {
        post(TOPIC_CHANGES, new ContainerAddEvent((NHSpace)space, item, idx, incremented, ContainerAddEvent.Type.added));
    }

    @Override public void itemAdded(Container space, Item item, int idx, boolean incremented, NHBot adder, NHSpace origin) {
        post(TOPIC_CHANGES, new ContainerAddEvent((NHSpace)space, item, idx, incremented, ContainerAddEvent.Type.added));
    }

    @Override public void itemTaken(Container space, Item item, int idx) {
        post(TOPIC_CHANGES, new ContainerRemoveEvent((NHSpace)space, item, idx, false, ContainerRemoveEvent.Type.taken));
    }

    @Override public void itemDestroyed(Container space, Item item, int idx) {
        post(TOPIC_CHANGES, new ContainerRemoveEvent((NHSpace)space, item, idx, false, ContainerRemoveEvent.Type.destroyed));
    }

    @Override public void itemsDestroyed(Container container, Item[] items) {
        //post(TOPIC_CHANGES, new ContainerRemoveEvent((NHSpace)space, item, idx, false, ContainerRemoveEvent.Type.destroyed));
        throw new UnsupportedOperationException();
    }

    private static void post(final String topic, final Event e) {
        LOG.debug("posting to {}: {}", topic, e);
        EventBus.instance().post(topic, e);
    }
}
