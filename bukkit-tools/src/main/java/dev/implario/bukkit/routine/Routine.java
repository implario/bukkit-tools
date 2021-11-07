package dev.implario.bukkit.routine;

import lombok.Data;

import java.util.function.Consumer;

@Data
public class Routine {

    private int id;
    private long nextPassTime;
    private long interval;
    private Scheduler scheduler;
    private Consumer<Routine> action;
    private Consumer<Routine> killHandler;
    private long pass;
    private long passLimit;

    public void onKill(Consumer<Routine> action) {
        Consumer<Routine> killHandler = this.killHandler;
        this.killHandler = (r) -> {
            killHandler.accept(r);
            action.accept(r);
        };
    }

    public void cancel() {
        killHandler.accept(this);
    }

}
