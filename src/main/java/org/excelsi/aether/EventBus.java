package org.excelsi.aether;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Predicate;


public final class EventBus {
    private static final EventBus _b = new EventBus();

    private int _nextId;
    private final Map<String,List<String>> _topics = new HashMap<>();
    private final Map<String,EQueue<Event>> _queues = new HashMap<>();


    public static EventBus i() {
        return instance();
    }

    public static EventBus instance() {
        return _b;
    }

    public void control(final String queue, final boolean dedupe) {
        final EQueue<Event> q = _queues.get(queue);
        if(q==null) {
            _queues.put(queue, new EQueue<>(1000));
        }
        q.setDedupe(dedupe);
    }

    public String subscribe(final String topic, final String consumer) {
        final String subscription = topic+"-"+consumer+"-"+_nextId++;
        List<String> subs = _topics.get(topic);
        if(subs==null) {
            subs = new ArrayList<>();
            _topics.put(topic, subs);
        }
        //subs.add(subscription);
        //_queues.put(subscription, new ArrayBlockingQueue<>(1000));
        subs.add(consumer);
        if(!_queues.containsKey(consumer)) {
            _queues.put(consumer, new EQueue<>(1000));
        }
        //return subscription;
        return consumer;
    }

    public <E extends Event> E await(final String topic, final E e) {
        synchronized(e) {
            post(topic, e);
            try {
                e.wait();
            }
            catch(InterruptedException ex) {
            }
        }
        return e;
    }

    public void post(final String topic, final Event e) {
        final List<String> subs = _topics.get(topic);
        if(subs!=null) {
            for(final String sub:subs) {
                final Queue<Event> q = _queues.get(sub);
                synchronized(q) {
                    q.add(e);
                    q.notify();
                }
            }
        }
    }

    public boolean hasEvents(final String subscription) {
        final Queue<Event> q = _queues.get(subscription);
        if(q==null) {
            throw new IllegalArgumentException("no such subscription '"+subscription+"'");
        }
        return !q.isEmpty();
    }

    public Event poll(final String subscription) {
        final Queue<Event> q = _queues.get(subscription);
        while(q.isEmpty()) {
            synchronized(q) {
                try {
                    q.wait();
                }
                catch(InterruptedException e) {
                }
            }
        }
        if(!q.isEmpty()) {
            return q.remove();
        }
        else {
            throw new IllegalStateException("empty queue after notify: "+subscription);
        }
    }

    public Event poll(final String subscription, final long timeout) {
        final Queue<Event> q = _queues.get(subscription);
        if(q.isEmpty()) {
            synchronized(q) {
                try {
                    q.wait(timeout);
                }
                catch(InterruptedException e) {
                }
            }
        }
        return !q.isEmpty() ? q.remove() : null;
    }

    public void consume(final String subscription, Handler h) {
        final Queue<Event> q = _queues.get(subscription);
        while(!q.isEmpty()) {
            h.handleEvent(q.remove());
        }
    }

    public interface Handler {
        void handleEvent(Event e);
    }

    private EventBus() {
    }

    private static class EQueue<E> extends ArrayBlockingQueue<E> {
        private boolean _dedupe;
        private E _lastAdded;


        public EQueue(int size) {
            this(size, false);
        }

        public EQueue(int size, boolean dedupe) {
            super(size);
            _dedupe = dedupe;
        }

        @Override public boolean add(E e) {
            if(_dedupe) {
                if(isEmpty()) {
                    _lastAdded = e;
                    return super.add(e);
                }
                else {
                    if(_lastAdded.equals(e)) {
                        return false;
                    }
                    else {
                        _lastAdded = e;
                        return super.add(e);
                    }
                }
            }
            else {
                return super.add(e);
            }
        }

        public void setDedupe(boolean dedupe) {
            _dedupe = dedupe;
        }

        public boolean getDedupe() {
            return _dedupe;
        }
    }
}
