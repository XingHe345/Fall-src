package dev.Fall.scripting.api.bindings;

import dev.Fall.Fall;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

@Exclude(Strategy.NAME_REMAPPING)
public class UserBinding {

    public String uid() {
        return String.valueOf(Fall.INSTANCE.getIntentAccount().client_uid);
    }

    public String username() {
        return String.valueOf(Fall.INSTANCE.getIntentAccount().username);
    }

    public String discordTag() {
        return String.valueOf(Fall.INSTANCE.getIntentAccount().discord_tag);
    }

}
