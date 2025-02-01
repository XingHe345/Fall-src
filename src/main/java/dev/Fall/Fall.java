package dev.Fall;

import dev.Fall.commands.CommandHandler;
import dev.Fall.config.ConfigManager;
import dev.Fall.config.DragManager;
import dev.Fall.event.EventProtocol;
import dev.Fall.intent.api.account.IntentAccount;
import dev.Fall.intent.cloud.CloudDataManager;
import dev.Fall.module.Module;
import dev.Fall.module.ModuleCollection;
import dev.Fall.scripting.api.ScriptManager;
import dev.Fall.ui.altmanager.GuiAltManager;
import dev.Fall.ui.altmanager.helpers.KingGenApi;
import dev.Fall.ui.searchbar.SearchBar;
import dev.Fall.ui.sidegui.SideGUI;
import dev.Fall.utils.Utils;
import dev.Fall.utils.client.ReleaseType;

import dev.Fall.utils.objects.DiscordAccount;
import dev.Fall.utils.objects.Dragging;
import dev.Fall.utils.server.PingerUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Setter
public class Fall implements Utils {

    public static final Fall INSTANCE = new Fall();

    public static final String NAME = "Fall";
    public static final String VERSION = "1.1";
    public static final ReleaseType RELEASE = ReleaseType.DEV;
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final File DIRECTORY = new File(mc.mcDataDir, NAME);

    private final EventProtocol eventProtocol = new EventProtocol();
    private final CloudDataManager cloudDataManager = new CloudDataManager();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final SideGUI sideGui = new SideGUI();
    private final SearchBar searchBar = new SearchBar();
    private ModuleCollection moduleCollection;
    private ScriptManager scriptManager;
    private IntentAccount intentAccount;
    public Logger logger;
    private ConfigManager configManager;
    private GuiAltManager altManager;
    private CommandHandler commandHandler;
    private PingerUtils pingerUtils;
    //private DiscordRPC discordRPC;
    public KingGenApi kingGenApi;
    private DiscordAccount discordAccount;

    public static boolean updateGuiScale;
    public static int prevGuiScale;

    public String getVersion() {
        return VERSION + (RELEASE != ReleaseType.PUBLIC ? " (" + RELEASE.getName() + ")" : "");
    }

    public final Color getClientColor() {
        //return new Color(236, 133, 209);
        ;return new Color(255, 255, 255);
    }

    public final Color getAlternateClientColor() {
        return new Color(255, 255, 255);
    }

    public boolean isEnabled(Class<? extends Module> c) {
        Module m = INSTANCE.moduleCollection.get(c);
        return m != null && m.isEnabled();
    }

    public Dragging createDrag(Module module, String name, float x, float y) {
        DragManager.draggables.put(name, new Dragging(module, name, x, y));
        return DragManager.draggables.get(name);
    }

}
