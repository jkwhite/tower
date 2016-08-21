package org.excelsi.aether;


public class HighScores implements State {
    @Override public String getName() {
        return "highscores";
    }

    @Override public void run(final Context c) {
        c.n().title("High scores");
        c.n().pause();
        c.setState(new Title());
    }
}
