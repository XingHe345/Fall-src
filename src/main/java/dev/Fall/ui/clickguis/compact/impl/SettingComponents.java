package dev.Fall.ui.clickguis.compact.impl;

import dev.Fall.module.Module;
import dev.Fall.module.settings.Setting;
import dev.Fall.module.settings.impl.*;
import dev.Fall.ui.GuiEvents;
import dev.Fall.ui.Screen;
import dev.Fall.utils.animations.Animation;
import dev.Fall.utils.animations.Direction;
import dev.Fall.utils.animations.impl.DecelerateAnimation;
import dev.Fall.utils.misc.HoveringUtil;
import dev.Fall.utils.misc.MathUtils;
import dev.Fall.utils.objects.TextField;
import dev.Fall.utils.render.ColorUtil;
import dev.Fall.utils.render.RenderUtil;
import dev.Fall.utils.render.RoundedUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SettingComponents implements Screen {

    private final Module module;
    private final HashMap<NumberSetting, Float> numberSettingMap = new HashMap<>();
    private final HashMap<StringSetting, TextField> textFieldMap = new HashMap<>();
    private final HashMap<ColorSetting, Animation> colorSettingMap = new HashMap<>();
    private final HashMap<ModeSetting, Animation> modeSettingMap = new HashMap<>();
    private final HashMap<MultipleBoolSetting, Animation> multiBoolMap = new HashMap<>();
    private final HashMap<Setting, Animation> booleanSettingHashMap;
    public float size;
    public Color actualColor;
    public float x, y, rectWidth;
    public Setting draggingNumber;
    private boolean hueFlag;
    private boolean saturationFlag;
    public boolean typing;
    Animation enableAnimation = new DecelerateAnimation(150, 1);
    public SettingComponents(Module module) {
        this.module = module;
        booleanSettingHashMap = new HashMap<>();
        for (Setting setting : module.getSettingsList()) {
            if (setting instanceof NumberSetting) {
                numberSettingMap.put((NumberSetting) setting, 0f);
            }
            if (setting instanceof StringSetting) {
                StringSetting stringSetting = (StringSetting) setting;
                TextField textField = new TextField(rubikFont14);
                textField.setText(stringSetting.getString());
                textField.setCursorPositionZero();
                textFieldMap.put(stringSetting, textField);
            }
            if (setting instanceof ColorSetting) {
                ColorSetting colorSetting = (ColorSetting) setting;
                Animation animation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
                colorSettingMap.put(colorSetting, animation);
            }
            if (setting instanceof ModeSetting) {
                ModeSetting modeSetting = (ModeSetting) setting;
                Animation animation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
                modeSettingMap.put(modeSetting, animation);
            }
            if (setting instanceof MultipleBoolSetting) {
                MultipleBoolSetting multipleBoolSetting = (MultipleBoolSetting) setting;
                Animation animation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
                multiBoolMap.put(multipleBoolSetting, animation);
            }

            if (setting instanceof BooleanSetting) booleanSettingHashMap.put(setting, new DecelerateAnimation(250, 1));
        }
    }

    @Override
    public void initGui() {
        for (Map.Entry<StringSetting, TextField> entry : textFieldMap.entrySet()) {
            entry.getValue().setText(entry.getKey().getString());
            entry.getValue().setCursorPositionZero();
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        for (Map.Entry<StringSetting, TextField> entry : textFieldMap.entrySet()) {
            entry.getValue().keyTyped(typedChar, keyCode);
        }
    }


    public void handle(int mouseX, int mouseY, int button, GuiEvents type) {
        typing = false;
        float settingHeight = 22;
        float count = 0;
        //Color accentColor = new Color(162, 255, 7);
        Color accentColor = new Color(8, 150, 250);
        Color disabledColor = new Color(0, 0, 0, 50);
        for (Setting setting : module.getSettingsList()) {
            if (setting.cannotBeShown()) continue;
            if (setting instanceof KeybindSetting) continue;
            float settingY = y + (count * settingHeight) + 5;
            float middleSettingY = (float) (MathUtils.roundToHalf(y + rubikFont.size(16).getMiddleOfBox(settingHeight) + (count * settingHeight)));

            if (setting instanceof NumberSetting) {
                NumberSetting numberSetting = (NumberSetting) setting;
                rubikFont.size(16).drawString(setting.name, x + 5, middleSettingY + 2, -1);

                String value = String.valueOf(MathUtils.round(numberSetting.getValue(), 2));

                value = value.contains(".") ? value.replaceAll("0*$", "").replaceAll("\\.$", "") : value;

                //TODO account for min value being a larger string width than max
                String maxValue = Double.toString(MathUtils.round(numberSetting.getMaxValue(), 2));
                float valueWidth = rubikFont14.getStringWidth(maxValue);
                //RoundedUtil.drawRound(x + rectWidth - (valueWidth + 7), settingY + 4, valueWidth + 4, 8, 1, disabledColor);

                rubikFont14.drawCenteredString(value, x + rectWidth - (valueWidth + 5) + valueWidth / 2f, settingY + 6, -1);
                GlStateManager.color(1, 1, 1, 1);

                float sliderWidth = 200;
                float sliderHeight = 1.5f;
                float sliderX = x + 5;
                float sliderY = settingY + settingHeight / 2f - sliderHeight / 2f + 4;
                float sliderRadius = 1;

                boolean hoveringSlider = HoveringUtil.isHovering(sliderX, sliderY - 2, sliderWidth, sliderHeight + 4, mouseX, mouseY);

                if (type == GuiEvents.RELEASE) {
                    draggingNumber = null;
                }

                if (type == GuiEvents.CLICK && hoveringSlider && button == 0) {
                    draggingNumber = numberSetting;
                }

                double currentValue = numberSetting.getValue();


                if (draggingNumber != null && draggingNumber == setting) {
                    float percent = Math.min(1, Math.max(0, (mouseX - sliderX) / sliderWidth));
                    double newValue = (percent * (numberSetting.getMaxValue()
                            - numberSetting.getMinValue())) + numberSetting.getMinValue();
                    numberSetting.setValue(newValue);
                }

                float sliderMath = (float) (((currentValue) - numberSetting.getMinValue())
                        / (numberSetting.getMaxValue() - numberSetting.getMinValue()));


                numberSettingMap.put(numberSetting, (float) RenderUtil.animate(sliderWidth * sliderMath, numberSettingMap.get(numberSetting), .1));


                RoundedUtil.drawRound(sliderX, sliderY, sliderWidth, sliderHeight, 1, disabledColor);
                RoundedUtil.drawRound(sliderX, sliderY, Math.max(3, numberSettingMap.get(numberSetting)), sliderHeight, 1, new Color(8, 150, 250));
                //  RenderUtil.renderRoundedRect(sliderX, sliderY, sliderWidth, sliderHeight, sliderRadius, new Color(42, 42, 42).getRGB());


                //   RenderUtil.renderRoundedRect(sliderX, sliderY, Math.max(3, numberSettingMap.get(numberSetting)), sliderHeight, sliderRadius, accentColor.getRGB());


                float whiteRectWidth = 1.5f;
                float whiteRectHeight = 6f;
                RenderUtil.resetColor();

                RenderUtil.drawGoodCircle(sliderX + Math.max(3, numberSettingMap.get(numberSetting)) - 1.5f,
                        sliderY + sliderHeight / 2f - whiteRectHeight / 2f + 3, 3, -1);

                // RenderUtil.drawGoodCircle(sliderX + Math.max(3, numberSettingMap.get(numberSetting)), sliderY + circleSize /2f - .5f, circleSize, accentColor.getRGB());

            }
            boolean isHoveringSetting = HoveringUtil.isHovering(x + 109 + 78, settingY - 1, 20, 10, mouseX, mouseY);
            if (setting instanceof BooleanSetting) {
                BooleanSetting booleanSetting = (BooleanSetting) setting;
                Animation animation = booleanSettingHashMap.get(setting);
                animation.setDirection(booleanSetting.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);

                if (type == GuiEvents.CLICK && isHoveringSetting && button == 0) {
                    booleanSetting.toggle();
                }

                rubikFont.size(16).drawString(setting.name, x + 5, middleSettingY + 2, -1);
                //  RenderUtil.renderRoundedRect(x + 95, settingY, 20, 10, 4,
                //color);

                GL11.glEnable(GL11.GL_BLEND);
                RoundedUtil.drawRound(x + 109 + 80, settingY, 18, 8, 4, ColorUtil.interpolateColorC(new Color(30, 31, 35), new Color(8, 150, 250), (float) animation.getOutput().floatValue()));

                GlStateManager.color(1, 1, 1);
                RenderUtil.drawGoodCircle(x + 113 + 80 + (10 * animation.getOutput().floatValue()), settingY + 4, 4f, -1);
                count -= .2f;
            }
            if (setting instanceof ColorSetting) {
                ColorSetting colorSetting = (ColorSetting) setting;

                Animation clickAnimation = colorSettingMap.get(colorSetting);


                rubikFont.size(16).drawString(setting.name, x + 5, middleSettingY, -1);

                float colorWidth = 20;

                boolean hovered = HoveringUtil.isHovering(x + rectWidth - (colorWidth + 5), middleSettingY - 1, colorWidth, 6, mouseX, mouseY);

                if (hovered && button == 1 && type == GuiEvents.CLICK) {
                    clickAnimation.changeDirection();
                }


                Gui.drawRect2(x + rectWidth - (colorWidth + 5), middleSettingY - 1, colorWidth, 6,
                        hovered ? ColorUtil.darker(colorSetting.getColor(), .7f).getRGB() : colorSetting.getColor().getRGB());


                if (clickAnimation.isDone() && clickAnimation.getDirection().equals(Direction.FORWARDS) || !clickAnimation.isDone()) {
                    if (colorSetting.isRainbow()) {
                        Color color = colorSetting.getColor();
                        int red = color.getRed(), green = color.getGreen(), blue = color.getBlue();
                        float[] hsb = Color.RGBtoHSB(red, green, blue, null);
                        colorSetting.setHue(hsb[0]);
                        colorSetting.setSaturation(hsb[1]);
                        colorSetting.setBrightness(hsb[2]);
                    }


                    float[] hsb = {(float) colorSetting.getHue(), (float) colorSetting.getSaturation(), (float) colorSetting.getBrightness()};

                    float gradientX = x + 5;
                    float gradientY = settingY + 20;
                    float gradientWidth = 115;
                    float gradientHeight = (float) (10 + (35 * clickAnimation.getOutput().floatValue()));

                    if (type == GuiEvents.CLICK) {
                        if (button == 0) {
                            if (HoveringUtil.isHovering(gradientX + gradientWidth + 5, gradientY, 5, gradientHeight, mouseX, mouseY)) {
                                draggingNumber = setting;
                                hueFlag = true;
                            }
                            if (HoveringUtil.isHovering(gradientX, gradientY, gradientWidth, gradientHeight, mouseX, mouseY)) {
                                draggingNumber = setting;
                                hueFlag = false;
                            }
                        }
                    }
                    if (type == GuiEvents.RELEASE) {
                        draggingNumber = null;
                    }

                    if (draggingNumber != null && draggingNumber.equals(setting)) {
                        if (hueFlag) {
                            colorSetting.setHue(Math.min(1, Math.max(0, (mouseY - gradientY) / gradientHeight)));
                        } else {
                            colorSetting.setBrightness(Math.min(1, Math.max(0, 1 - ((mouseY - gradientY) / gradientHeight))));
                            colorSetting.setSaturation(Math.min(1, Math.max(0, (mouseX - gradientX) / gradientWidth)));
                        }
                    }

                    int hsbZeroOneOne = ColorUtil.applyOpacity(Color.getHSBColor(hsb[0], 1, 1).getRGB(), (float) clickAnimation.getOutput().floatValue());

                    int hsbZeroZeroOne = ColorUtil.applyOpacity(Color.getHSBColor(hsb[0], 0, 1).getRGB(), (float) clickAnimation.getOutput().floatValue());

                    int hsbZeroOneZero = ColorUtil.applyOpacity(Color.getHSBColor(hsb[0], 1, 0).getRGB(), (float) clickAnimation.getOutput().floatValue());

                    int hue = ColorUtil.applyOpacity(Color.getHSBColor(hsb[0], 0, 0).getRGB(), (float) clickAnimation.getOutput().floatValue());

                    //Picker gradients
                    Gui.drawRect2(gradientX, gradientY, gradientWidth, gradientHeight, hsbZeroOneOne);

                    Gui.drawGradientRectSideways2(gradientX, gradientY, gradientWidth, gradientHeight,
                            hsbZeroZeroOne, ColorUtil.applyOpacity(Color.getHSBColor(hsb[0], 0, 1).getRGB(), 0));

                    Gui.drawGradientRect2(gradientX, gradientY, gradientWidth, gradientHeight,
                            ColorUtil.applyOpacity(Color.getHSBColor(hsb[0], 1, 0).getRGB(), 0), hsbZeroOneZero);


                    int rectColor = ColorUtil.applyOpacity(disabledColor.getRGB(), (float) clickAnimation.getOutput().floatValue());
                    int textColor = ColorUtil.applyOpacity(-1, (float) clickAnimation.getOutput().floatValue());
                    float colorInfoWidth = (float) rubikFont14.getStringWidth("R: 255") + 6;


                    Gui.drawRect2(gradientX, gradientY + gradientHeight + 4, colorInfoWidth, 8, rectColor);
                    Gui.drawRect2(gradientX + colorInfoWidth + 5, gradientY + gradientHeight + 4, colorInfoWidth, 8, rectColor);
                    Gui.drawRect2(gradientX + (colorInfoWidth * 2) + 10, gradientY + gradientHeight + 4, colorInfoWidth, 8, rectColor);

                    int redColor = new Color(255, 0, 0, (int) (255 * clickAnimation.getOutput().floatValue())).getRGB();
                    int greenColor = new Color(0, 255, 0, (int) (255 * clickAnimation.getOutput().floatValue())).getRGB();
                    int blueColor = new Color(0, 0, 255, (int) (255 * clickAnimation.getOutput().floatValue())).getRGB();
                    GlStateManager.color(1, 1, 1, 1);
                    rubikFont14.drawCenteredString("R§f: " + colorSetting.getColor().getRed(), gradientX + 2.5f + colorWidth / 2f, gradientY + gradientHeight + 6, redColor);
                    GlStateManager.color(1, 1, 1, 1);

                    rubikFont14.drawCenteredString("G§f: " + colorSetting.getColor().getGreen(), gradientX + colorInfoWidth + 5 + colorInfoWidth / 2f,
                            gradientY + gradientHeight + 6, greenColor);

                    GlStateManager.color(1, 1, 1, 1);

                    rubikFont14.drawCenteredString("B§f: " + colorSetting.getColor().getBlue(), gradientX + (colorInfoWidth * 2) + 12.5f + colorWidth / 2f,
                            gradientY + gradientHeight + 6, blueColor);


                    float rainbowX = gradientX + (colorInfoWidth * 2) + 10 + colorInfoWidth + 3;


                    rubikFont14.drawString("Rainbow: ", rainbowX, gradientY + gradientHeight + 6, textColor);

                    float clickAnim = clickAnimation.getOutput().floatValue();

                    RoundedUtil.drawRound(rainbowX + rubikFont14.getStringWidth("Rainbow: ") + 4, gradientY + gradientHeight + 5.5f,
                            6, 6, 2.5f, ColorUtil.applyOpacity(colorSetting.isRainbow() ? new Color(8, 150, 250) : disabledColor, clickAnim));

                    if (type == GuiEvents.CLICK && button == 0) {
                        if (HoveringUtil.isHovering(rainbowX + rubikFont14.getStringWidth("Rainbow: ") + 3,
                                gradientY + gradientHeight + 5, 7, 7, mouseX, mouseY)) {
                            colorSetting.setRainbow(!colorSetting.isRainbow());
                        }

                    }


                    float pickerY = gradientY + (gradientHeight * (1 - hsb[2]));
                    float pickerX = gradientX + (gradientWidth * hsb[1] - 1);
                    pickerY = Math.max(Math.min(gradientY + gradientHeight - 2, pickerY), gradientY);
                    pickerX = Math.max(Math.min(gradientX + gradientWidth - 2, pickerX), gradientX);

                    GlStateManager.color(1, 1, 1, 1);
                    GL11.glEnable(GL11.GL_BLEND);
                    RenderUtil.color(textColor);
                    mc.getTextureManager().bindTexture(new ResourceLocation("Fluid/colorpicker2.png"));
                    Gui.drawModalRectWithCustomSizedTexture(pickerX, pickerY, 0, 0, 4, 4, 4, 4);


                    GlStateManager.color(1, 1, 1, 1);
                    // Hue bar
                    RenderUtil.color(textColor);
                    mc.getTextureManager().bindTexture(new ResourceLocation("Fluid/hue2.png"));
                    Gui.drawModalRectWithCustomSizedTexture(gradientX + gradientWidth + 5, gradientY, 0, 0, 5, gradientHeight, 5, gradientHeight);

                    GlStateManager.color(1, 1, 1, 1);
                    Gui.drawRect2(gradientX + gradientWidth + 5, gradientY + (gradientHeight * hsb[0]), 5, 1, textColor);


                    if (colorSetting.isRainbow()) {
                        Gui.drawGradientRect2(gradientX + gradientWidth + 15, gradientY, 5, gradientHeight, textColor, Color.RED.getRGB());

                        Gui.drawRect2(gradientX + gradientWidth + 15, gradientY + (gradientHeight * hsb[1]), 5, 1, Color.BLACK.getRGB());

                        boolean hoveringSat = HoveringUtil.isHovering(gradientX + gradientWidth + 15, gradientY, 5, gradientHeight, mouseX, mouseY);

                        if (type == GuiEvents.CLICK && button == 0 && hoveringSat) {
                            saturationFlag = true;
                            draggingNumber = setting;
                        }

                        if (type == GuiEvents.RELEASE && saturationFlag) {
                            saturationFlag = false;
                            draggingNumber = null;
                        }

                        if (saturationFlag) {
                            colorSetting.getRainbow().setSaturation(Math.min(1, Math.max(0, (mouseY - gradientY) / gradientHeight)));
                        }


                    }


                }


                count += (4 * clickAnimation.getOutput().floatValue());
            }
            if (setting instanceof StringSetting) {
                StringSetting stringSetting = (StringSetting) setting;
                RenderUtil.resetColor();
                rubikFont.size(16).drawString(setting.name, x + 5, middleSettingY, -1);


                TextField textField = textFieldMap.get(stringSetting);

                float textFieldWidth = 60;
                float textFieldHeight = 10;

                textField.setBackgroundText("Type Here...");
                textField.setXPosition(x + rectWidth - (textFieldWidth + 5));
                textField.setYPosition(settingY + (settingHeight / 2) - (textFieldHeight / 2));
                textField.setWidth(textFieldWidth);
                textField.setHeight(textFieldHeight);
                textField.setDrawingBackground(false);

                if (type == GuiEvents.CLICK) textField.mouseClicked(mouseX, mouseY, button);

                textField.drawTextBox();
                typing = textField.isFocused();

                //Writes to StringSetting
                stringSetting.setString(textField.getText());

            }
            if (setting instanceof ModeSetting) {
                ModeSetting modeSetting = (ModeSetting) setting;
                rubikFont.size(16).drawString(setting.name, x + 5,middleSettingY + 3, -1);

                float modeRectWidth = (float) rubikFont14.getStringWidth(modeSetting.getMode()) + 4;

                float modeSize = 10;
                float realY = settingY + settingHeight / 2f - modeSize / 2f;
                boolean hovered = HoveringUtil.isHovering(x + rectWidth - (modeRectWidth + 5), realY, modeRectWidth, modeSize, mouseX, mouseY);


                Animation openAnimation = modeSettingMap.get(modeSetting);

                if (!openAnimation.isDone() || openAnimation.getDirection().equals(Direction.FORWARDS)) {
                    Color dropdownColor = ColorUtil.darker(disabledColor, .8f);
                    //RoundedUtil.drawRoundOutline(x + rectWidth - (modeRectWidth + 5), realY + modeSize, modeRectWidth,
                    //        ((modeSetting.modes.size() - 1) * modeSize) * openAnimation.getOutput().floatValue(), 3, 0.2f, dropdownColor, new Color(100, 100, 100));

                    float seperation = 0;
                    for (String mode : modeSetting.modes) {
                        if (mode.equals(modeSetting.getMode())) continue;

                        float modeY = (realY + 3.5f) + (6 * openAnimation.getOutput().floatValue()) + rubikFont14.getMiddleOfBox(modeSize) + seperation;

                        boolean hoveringMode = HoveringUtil.isHovering(x + rectWidth - (modeRectWidth + 5),
                                modeY - rubikFont14.getMiddleOfBox(modeSize), modeRectWidth, modeSize, mouseX, mouseY) && openAnimation.isDone();

                        if (hoveringMode && button == 0 && type == GuiEvents.CLICK) {
                            modeSetting.setCurrentMode(mode);
                            openAnimation.setDirection(Direction.BACKWARDS);
                            return;
                        }

                        GlStateManager.color(1, 1, 1, 1);
                        Gui.drawRect2(x + rectWidth - (modeRectWidth + 5) + 1, modeY - rubikFont14.getMiddleOfBox(modeSize), modeRectWidth - 2, modeSize,
                                ColorUtil.applyOpacity(hoveringMode ? accentColor : dropdownColor, openAnimation.getOutput().floatValue()).getRGB());

                        GlStateManager.color(1, 1, 1, 1);
                        rubikFont14.drawString(mode, x + rectWidth - (modeRectWidth + 3), modeY, ColorUtil.applyOpacity(-1, openAnimation.getOutput().floatValue()));


                        seperation += modeSize * openAnimation.getOutput().floatValue();
                    }


                }


                RoundedUtil.drawRoundOutline(x + rectWidth - (modeRectWidth + 5), realY, modeRectWidth, modeSize, 4, 0.1f, disabledColor, new Color(90, 90, 90));

                rubikFont14.drawString(modeSetting.getMode(), (x + rectWidth - (modeRectWidth + 5)) + 2, realY + rubikFont14.getMiddleOfBox(modeSize), -1);

                if (hovered && button == 1 && type == GuiEvents.CLICK) {
                    openAnimation.changeDirection();
                }




                count += ((2 + ((modeSetting.modes.size() - 1) * modeSize)) / settingHeight) * openAnimation.getOutput().floatValue();
            }
            if (setting instanceof MultipleBoolSetting) {
                MultipleBoolSetting multipleBoolSetting = (MultipleBoolSetting) setting;

                Animation openingAnimation = multiBoolMap.get(multipleBoolSetting);
                rubikFont.size(16).drawString(setting.name, x + 6, middleSettingY + 3, -1);

                float width = 65;
                float boolRectSize = 10;

                float realY = settingY + (settingHeight) / 2f - boolRectSize / 2f;

                Collection<BooleanSetting> booleanSettings = multipleBoolSetting.getBoolSettings();

                int settingsCount = booleanSettings.size();
                int enabledCount = (int) booleanSettings.stream().filter(BooleanSetting::isEnabled).count();

                if (!openingAnimation.isDone() || openingAnimation.getDirection().equals(Direction.FORWARDS)) {
                    Color dropdownColor = ColorUtil.darker(disabledColor, .9f);
                    RoundedUtil.drawRound(x + rectWidth - (width + 5), realY + boolRectSize, width,
                            (1 + (settingsCount * boolRectSize)) * openingAnimation.getOutput().floatValue(), 4, dropdownColor);


                    float seperation = 0;
                    for (BooleanSetting booleanSetting : booleanSettings) {
                        boolean enabled = booleanSetting.isEnabled();

                        float boolSettingY = (realY + 4) + (7 * openingAnimation.getOutput().floatValue()) + rubikFont14.getMiddleOfBox(boolRectSize) + seperation;

                        boolean hovered = HoveringUtil.isHovering(x + rectWidth - (width + 5),
                                boolSettingY - rubikFont14.getMiddleOfBox(boolRectSize), width, boolRectSize, mouseX, mouseY);

                        if (hovered && button == 0 && type == GuiEvents.CLICK) {
                            booleanSetting.toggle();
                        }

                        Color toggleColor = enabled ? accentColor : dropdownColor;
                        int hoverColor = hovered ? ColorUtil.darker(toggleColor, .8f).getRGB() : toggleColor.getRGB();
                        RenderUtil.resetColor();
                        Gui.drawRect2(x + rectWidth - (width + 5) + 1, boolSettingY - rubikFont14.getMiddleOfBox(boolRectSize),
                                width - 2, boolRectSize, ColorUtil.applyOpacity(hoverColor, (float) openingAnimation.getOutput().floatValue()));

                        RenderUtil.resetColor();
                        rubikFont14.drawString(booleanSetting.name, x + rectWidth - (width + 2), boolSettingY, ColorUtil.applyOpacity(-1, (float) openingAnimation.getOutput().floatValue()));

                        seperation += boolRectSize * openingAnimation.getOutput().floatValue();
                    }
                }

                RoundedUtil.drawRoundOutline(x + rectWidth - (width + 5), realY, width, boolRectSize, 4, 0.1f, disabledColor, new Color(90, 90, 90));

                rubikFont14.drawCenteredString(enabledCount + "/" + settingsCount + " enabled", (x + rectWidth - (width + 5)) + (width / 2f),
                        realY + rubikFont14.getMiddleOfBox(boolRectSize), -1);

                boolean hovering = HoveringUtil.isHovering(x + rectWidth - (width + 5), realY, width, boolRectSize, mouseX, mouseY);

                if (hovering && button == 1 && type == GuiEvents.CLICK) {
                    openingAnimation.changeDirection();
                }



                count += ((2 + (multipleBoolSetting.getBoolSettings().size() * boolRectSize)) / settingHeight) * openingAnimation.getOutput().floatValue();
            }

            count++;
        }
        size = count * settingHeight;
    }


    @Override
    public void drawScreen(int mouseX, int mouseY) {
        handle(mouseX, mouseY, -1, GuiEvents.DRAW);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        handle(mouseX, mouseY, button, GuiEvents.CLICK);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        handle(mouseX, mouseY, state, GuiEvents.RELEASE);
    }

}
