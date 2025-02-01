package dev.Fall.commands.impl;

import dev.Fall.Fall;
import dev.Fall.commands.Command;

public final class ScriptCommand extends Command {

    public ScriptCommand() {
        super("scriptreload", "Reloads all scripts", ".scriptreload");
    }

    @Override
    public void execute(String[] args) {
        Fall.INSTANCE.getScriptManager().reloadScripts();
    }

}
