package dev.Fall.ui.notifications;

import dev.Fall.utils.font.FontUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@Getter
@AllArgsConstructor
public enum NotificationType {
    SUCCESS(new Color(68, 227, 158), FontUtil.CHECKMARK),
    DISABLE(new Color(255, 77, 77), FontUtil.XMARK),
    INFO(new Color(0x2AFAF3), FontUtil.INFO),
    WARNING(new Color(255, 176, 58), FontUtil.WARNING);
    private final Color color;
    private final String icon;
}