/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.items;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IModelManager;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Translator;

public class ItemCrated extends ItemForestry {

	private final ItemStack contained;
	private final boolean usesOreDict;

	public ItemCrated(ItemStack contained, boolean usesOreDict) {
		this.contained = contained;
		this.usesOreDict = usesOreDict;
	}

	public boolean usesOreDict() {
		return usesOreDict;
	}

	public ItemStack getContained() {
		return contained;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (!worldIn.isRemote) {
			if (contained == null || itemStackIn.stackSize == 0) {
				return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
			}

			itemStackIn.stackSize--;

			ItemStack dropStack = contained.copy();
			dropStack.stackSize = 9;
			ItemStackUtil.dropItemStackAsEntity(dropStack, worldIn, playerIn.posX, playerIn.posY, playerIn.posZ, 40);
		}
		return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		if (contained == null) {
			return Translator.translateToLocal("item.for.crate.name");
		} else {
			String containedName = Proxies.common.getDisplayName(contained);
			return Translator.translateToLocalFormatted("for.item.crated.grammar", containedName);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		if (contained == null) {
			manager.registerItemModel(item, 0);
			manager.registerItemModel(item, 1, "crate-filled");
		} else {
			String itemName = ItemStackUtil.getItemNameFromRegistry(item).getResourcePath();
			ModelResourceLocation modelLocation = new ModelResourceLocation("forestry:crate-filled", itemName);
			ModelLoader.setCustomModelResourceLocation(item, 0, modelLocation);
			ModelBakery.registerItemVariants(item, modelLocation);
		}
	}

	// TODO: crate rendering
//	@Override
//	public int getColorFromItemstack(ItemStack stack, int renderPass) {
//		if (getContained() == null || renderPass == 100) {
//			return super.getColorFromItemstack(stack, renderPass);
//		}
//		return getContained().getItem().getColorFromItemstack(getContained(), renderPass);
//	}

}
