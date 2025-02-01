package dev.Fall.commands.impl;

import dev.Fall.Fall;
import dev.Fall.commands.Command;
import dev.Fall.config.ConfigManager;
import dev.Fall.config.LocalConfig;
import dev.Fall.utils.misc.FileUtils;
import dev.Fall.utils.player.ChatUtil;

public class LoadConfigCommand extends Command {
    public LoadConfigCommand() {
        super("config", "load / save your current config", ".config [load/save] [config name]");
    }

    @Override
    public void execute(String[] args) {
        if(args.length == 0) usage();
        if(args[0].equals("load")) {
            ChatUtil.print("Try to Load...");
                for (LocalConfig i : ConfigManager.localConfigs) {
                    if(i.getName().equals(args[1])) {
                        Fall.INSTANCE.getConfigManager().loadConfig(FileUtils.readFile(i.getFile()), true);
                        ChatUtil.print("loaded config.");
                    }
                }
        }

        if(args[0].equals("save")) {
            ChatUtil.print("Try to Save...");
            Fall.INSTANCE.getConfigManager().saveConfig(args[1]);
        }
    }
}
