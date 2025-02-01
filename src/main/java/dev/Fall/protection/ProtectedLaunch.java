package dev.Fall.protection;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.Fall.Fall;
import dev.Fall.commands.CommandHandler;
import dev.Fall.commands.impl.*;
import dev.Fall.config.ConfigManager;
import dev.Fall.config.DragManager;
import dev.Fall.intent.api.account.GetUserInfo;
import dev.Fall.intent.api.account.IntentAccount;
import dev.Fall.module.BackgroundProcess;
import dev.Fall.module.Module;
import dev.Fall.module.ModuleCollection;
import dev.Fall.module.impl.combat.*;
import dev.Fall.module.impl.exploit.*;
import dev.Fall.module.impl.misc.*;
import dev.Fall.module.impl.movement.*;
import dev.Fall.module.impl.player.Timer;
import dev.Fall.module.impl.player.*;
import dev.Fall.module.impl.render.*;
import dev.Fall.scripting.api.ScriptManager;
import dev.Fall.ui.altmanager.GuiAltManager;
import dev.Fall.ui.altmanager.helpers.KingGenApi;
import dev.Fall.utils.misc.NetworkingUtils;
import dev.Fall.utils.objects.DiscordAccount;
import dev.Fall.utils.render.EntityCulling;
import dev.Fall.utils.render.Theme;
import dev.Fall.utils.server.PingerUtils;
import dev.Fall.viamcp.ViaMCP;
import net.minecraft.client.Minecraft;
import store.intent.intentguard.annotation.Bootstrap;
import store.intent.intentguard.annotation.Native;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

@Native
public class ProtectedLaunch {

    private static final HashMap<Object, Module> modules = new HashMap<>();

    @Native
    @Bootstrap
    public static void start() {
        // Setup Intent API access
        Fall.INSTANCE.setIntentAccount(GetUserInfo.loginFailure);
        Fall.INSTANCE.setModuleCollection(new ModuleCollection());

        // Combat
        modules.put(KillAura.class, new KillAura());
        modules.put(Velocity.class, new Velocity());
        modules.put(Criticals.class, new Criticals());
        modules.put(AutoSnowball.class, new AutoSnowball());
        modules.put(KeepSprint.class, new KeepSprint());
        modules.put(SuperKnockback.class, new SuperKnockback());
        modules.put(Backtrack.class, new Backtrack());

        // Exploit
        modules.put(Disabler.class, new Disabler());


        // Misc
        modules.put(Spammer.class, new Spammer());
        modules.put(AutoHypixel.class, new AutoHypixel());
        modules.put(MCF.class, new MCF());
        modules.put(PacketFix.class, new PacketFix());
        modules.put(GermMod.class, new GermMod());
        modules.put(Antibot.class, new Antibot());

        // Movement
        modules.put(Sprint.class, new Sprint());
        modules.put(Scaffold.class, new Scaffold());
        modules.put(Speed.class, new Speed());
        modules.put(Flight.class, new Flight());
        modules.put(Step.class, new Step());
        modules.put(InventoryMove.class, new InventoryMove());
        modules.put(Jesus.class, new Jesus());
        modules.put(NoWeb.class, new NoWeb());
        modules.put(Spider.class, new Spider());
        modules.put(TargetStrafe.class, new TargetStrafe());
        modules.put(Nofall.class, new Nofall());
        modules.put(LongJump.class, new LongJump());

        // Player
        modules.put(ChestStealer.class, new ChestStealer());
        modules.put(InvManager.class, new InvManager());
        modules.put(AutoArmor.class, new AutoArmor());
        modules.put(SpeedMine.class, new SpeedMine());
        modules.put(Blink.class, new Blink());
        modules.put(Timer.class, new Timer());
//        modules.put(Freecam.class, new Freecam());
        modules.put(FastPlace.class, new FastPlace());
        modules.put(SafeWalk.class, new SafeWalk());
        modules.put(NoSlow.class, new NoSlow());
        modules.put(AutoTool.class, new AutoTool());
        modules.put(AntiVoid.class, new AntiVoid());
        modules.put(Stuck.class, new Stuck());
//        modules.put(KillEffects.class, new KillEffects());
        modules.put(FlagDetection.class, new FlagDetection());
        modules.put(NoJumpDelay.class, new NoJumpDelay());
        modules.put(Teams.class, new Teams());

        // Render
        modules.put(AntiInvis.class, new AntiInvis());
        modules.put(ArrayListMod.class, new ArrayListMod());
        modules.put(NotificationsMod.class, new NotificationsMod());
        modules.put(ScoreboardMod.class, new ScoreboardMod());
        modules.put(HUDMod.class, new HUDMod());
        modules.put(ClickGUIMod.class, new ClickGUIMod());
       modules.put(Radar.class, new Radar());
        modules.put(Animations.class, new Animations());
        modules.put(SpotifyMod.class, new SpotifyMod());
        modules.put(Ambience.class, new Ambience());
        modules.put(Potion.class, new Potion());
        modules.put(ActionTimer.class, new ActionTimer());
//        modules.put(GlowESP.class, new GlowESP());
        modules.put(MusicBar.class, new MusicBar());
        modules.put(MusicPlayer.class, new MusicPlayer());
        modules.put(FPSCounter.class, new FPSCounter());
        modules.put(BPSCounter.class, new BPSCounter());
        modules.put(PositionRender.class, new PositionRender());
        modules.put(Ping.class, new Ping());

        //Compatible phone users remove this
        modules.put(Brightness.class, new Brightness());
        modules.put(ESP2D.class, new ESP2D());
        modules.put(PostProcessing.class, new PostProcessing());
        modules.put(TargetHUDMod.class, new TargetHUDMod());
        modules.put(Glint.class, new Glint());
//        modules.put(Breadcrumbs.class, new Breadcrumbs());
//        modules.put(Hitmarkers.class, new Hitmarkers());
        modules.put(NoHurtCam.class, new NoHurtCam());
        modules.put(ItemPhysics.class, new ItemPhysics());
        modules.put(XRay.class, new XRay());
        modules.put(EntityCulling.class, new EntityCulling());
        modules.put(PlayerInfo.class, new PlayerInfo());
        modules.put(InventoryHud.class, new InventoryHud());
//        modules.put(DragonWings.class, new DragonWings());
//        modules.put(JumpCircle.class, new JumpCircle());
        modules.put(Chams.class, new Chams());

        Fall.INSTANCE.getModuleCollection().setModules(modules);

        Theme.init();


        Fall.INSTANCE.setPingerUtils(new PingerUtils());

        Fall.INSTANCE.setScriptManager(new ScriptManager());

        CommandHandler commandHandler = new CommandHandler();
        commandHandler.commands.addAll(Arrays.asList(
                new FriendCommand(), new CopyNameCommand(), new BindCommand(), new UnbindCommand(),
                new ScriptCommand(), new SettingCommand(), new HelpCommand(),
                new VClipCommand(), new ClearBindsCommand(), new ClearConfigCommand(),
                new LoadCommand(), new ToggleCommand(), new LoadConfigCommand()
        ));
        Fall.INSTANCE.setCommandHandler(commandHandler);
        Fall.INSTANCE.getEventProtocol().register(new BackgroundProcess());

        Fall.INSTANCE.setConfigManager(new ConfigManager());
        ConfigManager.defaultConfig = new File(Minecraft.getMinecraft().mcDataDir + "/Fall/Config.json");
        Fall.INSTANCE.getConfigManager().collectConfigs();
        if (ConfigManager.defaultConfig.exists()) {
            Fall.INSTANCE.getConfigManager().loadConfig(Fall.INSTANCE.getConfigManager().readConfigData(ConfigManager.defaultConfig.toPath()), true);
        }


        DragManager.loadDragData();

        Fall.INSTANCE.setAltManager(new GuiAltManager());


        //String apiKey = Fluid.INSTANCE.getIntentAccount().api_key;
        //Fluid.INSTANCE.getIrcUtil().connect(apiKey);

        //Cloud.setApiKey(apiKey);
        //Fluid.INSTANCE.getCloudDataManager().refreshData();


        Fall.INSTANCE.kingGenApi = new KingGenApi();

        try {
            ViaMCP.create();

            ViaMCP.INSTANCE.initAsyncSlider();
            ViaMCP.initAsyncSlider(1,1, 110, 20);
        } catch (Exception e) {
            e.printStackTrace();
        }

        downloadDiscordImages();
    }

    private static void downloadDiscordImages() {
        if (Fall.INSTANCE.getIntentAccount().discord_id != null && !Fall.INSTANCE.getIntentAccount().discord_id.isEmpty()) {
            IntentAccount intentAccount = Fall.INSTANCE.getIntentAccount();
            NetworkingUtils.HttpResponse response = NetworkingUtils.httpsConnection("https://api.senoe.win/discord/user/" + intentAccount.discord_id);
            if (response != null && response.getResponse() == 200) {
                DiscordAccount discordAccount = new DiscordAccount();
                JsonObject responseObject = JsonParser.parseString(response.getContent()).getAsJsonObject();

                if (responseObject.has("avatar")) {
                    String avatarIDActual = responseObject.get("avatar").isJsonNull() ? null : responseObject.get("avatar").getAsString();
                    if (avatarIDActual == null) return;
                    String url = "https://cdn.discordapp.com/avatars/" + intentAccount.discord_id + "/" + avatarIDActual + ".png?size=64";
                    discordAccount.setDiscordAvatar(NetworkingUtils.downloadImage(url));
                }

                if (responseObject.has("banner")) {
                    if (responseObject.get("banner").isJsonNull()) {
                        discordAccount.setBannerColor(responseObject.get("banner_color").isJsonNull() ? "000000" : responseObject.get("banner_color").getAsString().substring(1));
                    } else {
                        // Load the banner image
                        String bannerID = responseObject.get("banner").getAsString();
                        if (bannerID == null) return;
                        String finalURL = "https://cdn.discordapp.com/banners/" + intentAccount.discord_id + "/" + bannerID + ".png?size=256";
                        discordAccount.setDiscordBanner(NetworkingUtils.downloadImage(finalURL));
                    }
                }
                Fall.INSTANCE.setDiscordAccount(discordAccount);
            }
        }
    }

    @SafeVarargs
    private static void addModules(Class<? extends Module>... classes) {
        for (Class<? extends Module> moduleClass : classes) {
            try {
                modules.put(moduleClass, moduleClass.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
