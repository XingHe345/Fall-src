package dev.Fall.ui.clickguis.compact;

import dev.Fall.Fall;
import dev.Fall.module.Category;
import dev.Fall.module.Module;
import dev.Fall.module.ModuleCollection;
import dev.Fall.module.impl.movement.InventoryMove;
import dev.Fall.module.impl.render.ClickGUIMod;
import dev.Fall.module.settings.Setting;
import dev.Fall.ui.clickguis.Record.panel.module.ModulePanel;
import dev.Fall.ui.clickguis.Record.panel.module.NewModuleRect;
import dev.Fall.ui.musicplayer.utils.Blur;
import dev.Fall.utils.animations.Animation;
import dev.Fall.utils.animations.Direction;
import dev.Fall.utils.animations.impl.DecelerateAnimation;
import dev.Fall.utils.misc.HoveringUtil;
import dev.Fall.utils.objects.Drag;
import dev.Fall.utils.render.RenderUtil;
import dev.Fall.utils.render.RoundedUtil;
import dev.Fall.utils.render.StencilUtil;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class CompactClickgui extends GuiScreen {

    private final Animation openingAnimation = new DecelerateAnimation(250, 1);
    private static final Drag drag = new Drag(40, 40);
    private final ModulePanel modulePanel = new ModulePanel();
    private static float rectWidth = 400;
    private static float rectHeight = 300;
    public boolean typing;
    private HashMap<Category, ArrayList<NewModuleRect>> moduleRects;
    Animation enableAnimation = new DecelerateAnimation(150, 1);
    private final List<String> searchTerms = new ArrayList<>();
    private String searchText;



    @Override
    public void onDrag(int mouseX, int mouseY) {
        boolean focusedConfigGui = Fall.INSTANCE.getSideGui().isFocused();
        int fakeMouseX = focusedConfigGui ? 0 : mouseX, fakeMouseY = focusedConfigGui ? 0 : mouseY;

        drag.onDraw(fakeMouseX, fakeMouseY);
        Fall.INSTANCE.getSideGui().onDrag(mouseX, mouseY);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {

            if (Fall.INSTANCE.getSearchBar().isFocused()) {
                Fall.INSTANCE.getSearchBar().getSearchField().setText("");
                Fall.INSTANCE.getSearchBar().getSearchField().setFocused(false);
                return;
            }

            if (Fall.INSTANCE.getSideGui().isFocused()) {
                Fall.INSTANCE.getSideGui().setFocused(false);
                return;
            }

            openingAnimation.setDirection(Direction.BACKWARDS);
        }
        modulePanel.keyTyped(typedChar, keyCode);
        Fall.INSTANCE.getSideGui().keyTyped(typedChar, keyCode);
        Fall.INSTANCE.getSearchBar().keyTyped(typedChar, keyCode);
    }


    @Override
    public void initGui() {
        openingAnimation.setDirection(Direction.FORWARDS);
        rectWidth = 500;
        rectHeight = 350;
        if (moduleRects != null) {
            moduleRects.forEach((cat, list) -> list.forEach(NewModuleRect::initGui));
        }
        modulePanel.initGui();
        Fall.INSTANCE.getSideGui().initGui();
    }
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        drag.onRelease(state);
        modulePanel.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private final List<NewModuleRect> searchResults = new ArrayList<>();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {


        if (ModuleCollection.reloadModules || moduleRects == null) {
            if (moduleRects == null) {
                moduleRects = new HashMap<>();
            } else moduleRects.clear();
            for (Category category : Category.values()) {
                ArrayList<NewModuleRect> modules = new ArrayList<>();
                for (Module module : Fall.INSTANCE.getModuleCollection().getModulesInCategory(category)) {
                    modules.add(new NewModuleRect(module));
                }

                moduleRects.put(category, modules);
            }
            moduleRects.forEach((cat, list) -> list.forEach(NewModuleRect::initGui));
            ModuleCollection.reloadModules = false;
            return;
        }
        typing = modulePanel.typing || (Fall.INSTANCE.getSideGui().isFocused() && Fall.INSTANCE.getSideGui().isTyping()) || Fall.INSTANCE.getSearchBar().isTyping();

        InventoryMove.updateStates();

        boolean focusedConfigGui = Fall.INSTANCE.getSideGui().isFocused();
        int fakeMouseX = focusedConfigGui ? 0 : mouseX, fakeMouseY = focusedConfigGui ? 0 : mouseY;

        float x = drag.getX(), y = drag.getY();

        if (!openingAnimation.isDone()) {
            x -= width + rectWidth / 2f;
            x += (width + rectWidth / 2f) * openingAnimation.getOutput().floatValue();
        } else if (openingAnimation.getDirection().equals(Direction.BACKWARDS)) {
            mc.displayGuiScreen(null);
            return;
        }

        rectWidth = 550;
        rectHeight = 300;
        float finalX = x;
        Blur.blur(() -> RoundedUtil.drawRound(finalX, y, rectWidth, rectHeight, 5, new Color(0, 0, 0, 100)), 4, 3);
        Blur.bloom(() -> RoundedUtil.drawRound(finalX, y, rectWidth, rectHeight, 5, new Color(0, 0, 0, 100)), 4, 1);
        RoundedUtil.drawRound(x, y, rectWidth, rectHeight, 5, new Color(0, 0, 0, 100));
        RoundedUtil.drawRound(x,y, 90, rectHeight, 5, new Color(0,0,0, 150));
        FluidBoldFont20.drawString("  Fall Client", x + 12, y + 20, Color.BLUE.brighter());
        FluidBoldFont20.drawString("  Fall Client", x + 13, y + 20, -1);
        RoundedUtil.drawRound(x, y + rectHeight - 40, 90, 40, 4,new Color(0,0,0, 180));
        RenderUtil.drawHead(mc.thePlayer.getLocationSkin(), x + 4, y + rectHeight - 36, 30, 30, 4,1);
        FluidFont14.drawString("Name: " + mc.thePlayer.getName(), x + 36, y + rectHeight - 24, -1);

        float bannerHeight = 75 / 2f;


        float minus = (bannerHeight + 3) + 33;
        ClickGUIMod clickGUIMod = Fall.INSTANCE.getModuleCollection().getModule(ClickGUIMod.class);
        float catHeight = ((rectHeight - minus) / (Category.values().length));
        float seperation = 0;


        for (Category category : Category.values()) {
            float catY = y + 30 + seperation;
            boolean hovering = HoveringUtil.isHovering(x, catY + 14, 90, 15, fakeMouseX, fakeMouseY);
            if (hovering){
                RoundedUtil.drawRound(x + 10, catY + 17, rubikFont.size(20).getStringWidth(category.name) + 23, rubikFont.size(20).getHeight() + 5, 4, new Color(0,0,0, 200));
            }
            if ((clickGUIMod.getActiveCategory() == category)){
                RoundedUtil.drawRound(x + 10, catY + 17, rubikFont.size(20).getStringWidth(category.name) + 23, rubikFont.size(20).getHeight() + 5, 4, new Color(0,0,0, 200));
            }
            switch (category.name) {
                case "Combat": {
                    fluxICON20.drawString("G", x + 15, catY + 21, Color.BLUE.brighter().brighter());
                }
                break;

                case "Movement":
                case "Render":
                case "Exploit":
                case "Scripts":
                case "Misc":
                case "Player":
                {
                    iconFont20.drawString(category.icon, x + 15, catY + 21, Color.BLUE.brighter().brighter());
                }
                break;
            }
            rubikFont.size(20).drawString(category.name, x + 28, catY + 20, -1);
            RenderUtil.resetColor();
            seperation += (catHeight - 10) + 4;
        }
        modulePanel.currentCat = clickGUIMod.getActiveCategory();
        modulePanel.moduleRects = getModuleRects(clickGUIMod.getActiveCategory());
        modulePanel.x = x;
        modulePanel.y = y;
        modulePanel.rectHeight = rectHeight;
        modulePanel.rectWidth = rectWidth;

        StencilUtil.initStencilToWrite();
        Gui.drawRect2(x, y, rectWidth, rectHeight, -1);
        StencilUtil.readStencilBuffer(1);
        modulePanel.drawScreen(fakeMouseX, fakeMouseY);
        StencilUtil.uninitStencilBuffer();

        modulePanel.drawTooltips(fakeMouseX, fakeMouseY);

    }
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!Fall.INSTANCE.getSideGui().isFocused()) {
            drag.onClick(mouseX, mouseY, mouseButton, HoveringUtil.isHovering(drag.getX(), drag.getY() - 35, rectWidth, 40, mouseX, mouseY));
            float bannerWidth = 180 / 2f;
            float bannerHeight = 75 / 2f;

            ClickGUIMod clickGUIMod = Fall.INSTANCE.getModuleCollection().getModule(ClickGUIMod.class);

            int separation = 0;
            float minus = (bannerHeight + 3) + 33;
            float catHeight = ((rectHeight - minus) / (Category.values().length));
            for (Category category : Category.values()) {
                float catY = drag.getY() + 30 + separation;
                boolean hovering = HoveringUtil.isHovering(drag.getX(), catY + 14, 90, 15, mouseX, mouseY);
                if (hovering) {
                    clickGUIMod.setActiveCategory(category);
                }
                separation += (catHeight - 10) + 4;
            }

            modulePanel.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }
    public List<NewModuleRect> getModuleRects(Category category) {
        if (!Fall.INSTANCE.getSearchBar().isFocused()) {
            return moduleRects.get(category);
        }

        String search = Fall.INSTANCE.getSearchBar().getSearchField().getText();

        if (search.equals(searchText)) {
            return searchResults;
        } else {
            searchText = search;
        }

        List<NewModuleRect> moduleRects1 = moduleRects.values().stream().flatMap(Collection::stream).collect(Collectors.toList());

        searchResults.clear();
        moduleRects1.forEach(moduleRect -> {
            searchTerms.clear();
            Module module = moduleRect.module;

            searchTerms.add(module.getName());
            searchTerms.add(module.getCategory().name);
            if (!module.getAuthor().isEmpty()) {
                searchTerms.add(module.getAuthor());
            }
            for (Setting setting : module.getSettingsList()) {
                searchTerms.add(setting.name);
            }

            moduleRect.setSearchScore(FuzzySearch.extractOne(search, searchTerms).getScore());
        });

        searchResults.addAll(moduleRects1.stream().filter(moduleRect -> moduleRect.getSearchScore() > 60)
                .sorted(Comparator.comparingInt(NewModuleRect::getSearchScore).reversed()).collect(Collectors.toList()));

        return searchResults;
    }




}
