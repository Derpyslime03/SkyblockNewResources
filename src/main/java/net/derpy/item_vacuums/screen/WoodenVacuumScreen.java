package net.derpy.item_vacuums.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.derpy.item_vacuums.ItemVacuumsMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class WoodenVacuumScreen extends AbstractContainerScreen<WoodenVacuumMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ItemVacuumsMod.MOD_ID, "textures/gui/vacuum_gui.png");

    public WoodenVacuumScreen(WoodenVacuumMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(TEXTURE, x + 19, y + 37, 176, 0, 31, menu.getScaledProgress());
        }
    }

    @Override
    protected void init() {
        super.init();
    }
}
