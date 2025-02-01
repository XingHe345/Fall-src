package dev.Fall.commands.impl;

import dev.Fall.Fall;
import dev.Fall.commands.Command;
import dev.Fall.module.Module;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ToggleCommand extends Command {

    public ToggleCommand() {
        super("toggle", "Toggles a module", ".t [module]", "t");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            usage();
        } else {
            String moduleName = Arrays.stream(args).skip(0).collect(Collectors.joining(" "));
            Module module = Fall.INSTANCE.getModuleCollection().getModuleByName(moduleName);
            if (module != null) {
                module.toggle();
                sendChatWithPrefix("Toggled " + module.getDisplayName() + "!");
            } else {
                sendChatWithPrefix("The module \"" + moduleName + "\" does not exist!");
            }
        }
    }

}
