import org.excelsi.aether.Context
import org.excelsi.aether.ScriptedState


sstate = { name, script ->
    new ScriptedState(name, script)
}

state = { name, run ->
    return new State() {
        String getName() {
            name
        }

        void run(Context c) {
            run(c)
        }
    }
}
