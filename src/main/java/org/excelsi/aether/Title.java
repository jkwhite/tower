package org.excelsi.aether;


public class Title implements State {
    @Override public String getName() {
        return "title";
    }

    @Override public void run(final Context c) {
        c.n().title("");
        final Runnable r = c.n().choose(new SelectionMenu<Runnable>(
            new MenuItem<Runnable>("n", "New game", ()->{
                //c.setState(new Prelude(Data.resource("prelude-text")));
                c.setState(new World());
            }),
            new MenuItem<Runnable>("l", "Load game", null),
            new MenuItem<Runnable>("h", "High scores", ()->{ c.setState(new HighScores()); }),
            new MenuItem<Runnable>("q", "Quit", ()->{ c.setState(new Quit()); })
        ));
        r.run();
    }
}
