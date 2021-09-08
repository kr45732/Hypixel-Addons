/*
 * Hypixel Addons - A quality of life mod for Hypixel
 * Copyright (c) 2021 kr45732
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.kr45732.hypixeladdons.mixins;

import com.kr45732.hypixeladdons.features.MysteryBoxOverlay;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer extends GuiScreen {

	@Shadow
	private Slot theSlot;

	private static final String TARGET_GETSTACK = "Lnet/minecraft/inventory/Slot;getStack()Lnet/minecraft/item/ItemStack;";

	@Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = TARGET_GETSTACK))
	public ItemStack drawScreen_getStack(Slot slot) {
		if (theSlot != null && theSlot == slot && theSlot.getStack() != null) {
			ItemStack newStack = MysteryBoxOverlay.INSTANCE.overrideStack(theSlot.inventory, theSlot.getSlotIndex(), theSlot.getStack());
			if (newStack != null) {
				return newStack;
			}
		}
		return slot.getStack();
	}

	@Redirect(method = "drawSlot", at = @At(value = "INVOKE", target = TARGET_GETSTACK))
	public ItemStack drawSlot_getStack(Slot slot) {
		ItemStack stack = slot.getStack();

		if (stack != null) {
			ItemStack newStack = MysteryBoxOverlay.INSTANCE.overrideStack(slot.inventory, slot.getSlotIndex(), stack);
			if (newStack != null) {
				stack = newStack;
			}
		}
		return stack;
	}

	@Inject(method = "handleMouseClick", at = @At(value = "HEAD"), cancellable = true)
	public void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType, CallbackInfo ci) {
		GuiContainer $this = (GuiContainer) (Object) this;

		if (slotIn != null && slotIn.getStack() != null) {
			if (MysteryBoxOverlay.INSTANCE.onStackClick($this.inventorySlots.windowId, slotId, clickedButton, clickType)) {
				ci.cancel();
			}
		}
	}
}
