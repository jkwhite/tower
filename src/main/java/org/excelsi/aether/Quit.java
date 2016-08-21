package org.excelsi.aether;


public class Quit implements State {
    @Override public String getName() {
        return "quit";
    }

    @Override public void run(final Context c) {
        c.n().title("Dusk");
        c.n().message("Be seeing you.");
        try {
            Thread.sleep(200);
        }
        catch(InterruptedException e) {
        }
        System.exit(0);
    }
}
