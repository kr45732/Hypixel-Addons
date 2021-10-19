/*
 * Hypixel Addons - A customizable quality of life mod for Hypixel
 * Copyright (c) 2021 kr45732
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


package com.kr45732.hypixeladdons.mixins;

import com.kr45732.hypixeladdons.utils.chat.C;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    protected void renderScoreboard(ScoreObjective s, ScaledResolution score, CallbackInfo ci) {
        if (ConfigUtils.enableCustomSidebar) {
            ci.cancel();
            renderCustomSidebar(s, score);
        }
    }

    private void renderCustomSidebar(ScoreObjective sidebar, ScaledResolution scaledResolution) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null || mc.thePlayer == null || sidebar == null) {
            return;
        }

        Scoreboard scoreboard = sidebar.getScoreboard();
        List<Score> scores = new ArrayList<>();
        int sidebarWidth = mc.fontRendererObj.getStringWidth(sidebar.getDisplayName());
        for (Score score : scoreboard.getSortedScores(sidebar)) {
            String name = score.getPlayerName();
            if (scores.size() < 15 && name != null && !name.startsWith("#")) {
                sidebarWidth =
                        Math.max(
                                sidebarWidth,
                                mc.fontRendererObj.getStringWidth(
                                        ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(name), name) +
                                                (!ConfigUtils.hideSidebarRedNumbers ? ": " + C.RED + score.getScorePoints() : "")
                                )
                        );
                scores.add(score);
            }
        }

        int sidebarHeight = scores.size() * mc.fontRendererObj.FONT_HEIGHT;
        int sidebarX = scaledResolution.getScaledWidth() - sidebarWidth - 2 + ConfigUtils.sidebarXOffset;
        int sidebarY = scaledResolution.getScaledHeight() / 2 + sidebarHeight / 3 + ConfigUtils.sidebarYOffset;
        double xScale = (sidebarX + sidebarWidth) * (ConfigUtils.sidebarScale - 1.0);
        double yScale = (sidebarY - sidebarHeight / 2.0) * (ConfigUtils.sidebarScale - 1.0);

        GlStateManager.translate(-xScale, -yScale, 0.0);
        GlStateManager.scale(ConfigUtils.sidebarScale, ConfigUtils.sidebarScale, 1.0);

        for (int i = 0; i < scores.size(); i++) {
            Score currentScore = scores.get(i);
            String scorePoints = !ConfigUtils.hideSidebarRedNumbers ? C.RED + "" + currentScore.getScorePoints() : "";
            int scoreX = sidebarX + sidebarWidth + 1;
            int scoreY = sidebarY - (i + 1) * mc.fontRendererObj.FONT_HEIGHT;

            GuiScreen.drawRect(sidebarX - 2, scoreY, scoreX, scoreY + mc.fontRendererObj.FONT_HEIGHT, getBackgroundColor(false));
            mc.fontRendererObj.drawString(
                    ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(currentScore.getPlayerName()), currentScore.getPlayerName()),
                    sidebarX,
                    scoreY,
                    553648127
            );
            mc.fontRendererObj.drawString(scorePoints, scoreX - mc.fontRendererObj.getStringWidth(scorePoints), scoreY, 553648127);

            if (i + 1 == scores.size()) {
                GuiScreen.drawRect(sidebarX - 2, scoreY - mc.fontRendererObj.FONT_HEIGHT - 1, scoreX, scoreY - 1, getBackgroundColor(true));
                GuiScreen.drawRect(sidebarX - 2, scoreY - 1, scoreX, scoreY, getBackgroundColor(false));
                mc.fontRendererObj.drawString(
                        sidebar.getDisplayName(),
                        sidebarX + (sidebarWidth - mc.fontRendererObj.getStringWidth(sidebar.getDisplayName())) / 2,
                        scoreY - mc.fontRendererObj.FONT_HEIGHT,
                        553648127
                );
            }
        }

        GlStateManager.scale(1.0 / ConfigUtils.sidebarScale, 1.0 / ConfigUtils.sidebarScale, 1.0);
        GlStateManager.translate(xScale, yScale, 0.0);
    }

    private int getBackgroundColor(boolean darker) {
        int rgb = ConfigUtils.sidebarBackgroundColor;
        if (ConfigUtils.sidebarChromaBackground) {
            long time = 10000L / ConfigUtils.sidebarChromaSpeed;
            rgb = Color.HSBtoRGB((float) (System.currentTimeMillis() % time) / (float) time, 0.8F, 0.8F);
        }

        return darker
                ? fromRGBA(rgb, Math.min(255, (ConfigUtils.getSidebarAlphaScaled()) + 10))
                : fromRGBA(rgb, ConfigUtils.getSidebarAlphaScaled());
    }

    private int fromRGBA(int rgb, int alpha) {
        int r = rgb >> 16 & 255;
        int g = rgb >> 8 & 255;
        int b = rgb & 255;
        return alpha << 24 | r << 16 | g << 8 | b;
    }
}
