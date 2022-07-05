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
package forestry.arboriculture.blocks;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.MinecraftForge;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.genetics.AlleleRegisterEvent;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.arboriculture.items.ItemBlockDecorativeLeaves;
import forestry.arboriculture.items.ItemBlockLeaves;
import forestry.arboriculture.items.ItemBlockWood;
import forestry.arboriculture.items.ItemBlockWoodDoor;
import forestry.arboriculture.items.ItemBlockWoodSlab;
import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;
import forestry.core.utils.OreDictUtil;

public class BlockRegistryArboriculture extends BlockRegistry {
	public final List<BlockArbLog> logs;
	public final List<BlockArbPlanks> planks;
	public final List<BlockArbSlab> slabs;
	public final List<BlockArbSlab> slabsDouble;
	public final List<BlockArbFence> fences;
	public final List<BlockForestryFenceGate> fenceGates;
	public final List<BlockForestryStairs> stairs;
	public final List<BlockArbDoor> doors;

	public final BlockSapling saplingGE;
	public final BlockForestryLeaves leaves;
	public final List<BlockDefaultLeaves> leavesDefault;
	public final Map<String, IBlockState> speciesToLeavesDefault;
	public final List<BlockDefaultLeavesFruit> leavesDefaultFruit;
	public final Map<String, IBlockState> speciesToLeavesDefaultFruit;
	public final List<BlockDecorativeLeaves> leavesDecorative;
	private final Map<String, ItemStack> speciesToLeavesDecorative;
	public final Map<String, BlockFruitPod> podsMap;

	public final BlockArboriculture treeChest;

	public BlockRegistryArboriculture() {
		// Wood blocks
		logs = BlockArbLog.create();
		for (BlockArbLog block : logs) {
			registerBlock(block, new ItemBlockWood<>(block), "logs." + block.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.LOG_WOOD, block);
		}

		planks = BlockArbPlanks.create();
		for (BlockArbPlanks block : planks) {
			registerBlock(block, new ItemBlockWood<>(block), "planks." + block.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.PLANK_WOOD, block);
		}

		slabs = BlockArbSlab.create(false);
		slabsDouble = BlockArbSlab.create(true);
		for (int i = 0; i < slabs.size(); i++) {
			BlockArbSlab slab = slabs.get(i);
			BlockArbSlab slabDouble = slabsDouble.get(i);
			registerBlock(slab, new ItemBlockWoodSlab(slab, slab, slabDouble), "slabs." + slab.getBlockNumber());
			registerBlock(slabDouble, new ItemBlockWoodSlab(slabDouble, slab, slabDouble), "slabs.double." + slabDouble.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.SLAB_WOOD, slab);
		}

		fences = BlockArbFence.create();
		for (BlockArbFence block : fences) {
			registerBlock(block, new ItemBlockWood<>(block), "fences." + block.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.FENCE_WOOD, block);
		}

		fenceGates = new ArrayList<>();
		for (EnumForestryWoodType woodType : EnumForestryWoodType.VALUES) {
			BlockForestryFenceGate fenceGate = new BlockForestryFenceGate<>(woodType);
			registerBlock(fenceGate, new ItemBlockWood<>(fenceGate), "fence.gates." + woodType);
			registerOreDictWildcard(OreDictUtil.FENCE_GATE_WOOD, fenceGate);
			fenceGates.add(fenceGate);
		}

		stairs = new ArrayList<>();
		for (BlockArbPlanks plank : planks) {
			for (IBlockState blockState : plank.getBlockState().getValidStates()) {
				int meta = plank.getMetaFromState(blockState);
				EnumForestryWoodType woodType = plank.getWoodType(meta);

				BlockForestryStairs stair = new BlockForestryStairs<>(blockState, woodType);
				registerBlock(stair, new ItemBlockWood<>(stair), "stairs." + woodType);
				registerOreDictWildcard(OreDictUtil.STAIR_WOOD, stair);
				stairs.add(stair);
			}
		}

		doors = new ArrayList<>();
		for (EnumForestryWoodType woodType : EnumForestryWoodType.VALUES) {
			BlockArbDoor door = new BlockArbDoor(woodType);
			registerBlock(door, new ItemBlockWoodDoor(door), "doors." + woodType);
			registerOreDictWildcard(OreDictUtil.DOOR_WOOD, door);
			doors.add(door);
		}

		// Saplings
		TreeDefinition.preInit();
		saplingGE = new BlockSapling();
		registerBlock(saplingGE, new ItemBlockForestry<>(saplingGE), "sapling_ge");
		registerOreDictWildcard(OreDictUtil.TREE_SAPLING, saplingGE);

		// Leaves
		leaves = new BlockForestryLeaves();
		registerBlock(leaves, new ItemBlockLeaves(leaves), "leaves");
		registerOreDictWildcard(OreDictUtil.TREE_LEAVES, leaves);

		leavesDefault = BlockDefaultLeaves.create();
		speciesToLeavesDefault = new HashMap<>();
		for (BlockDefaultLeaves leaves : leavesDefault) {
			registerBlock(leaves, new ItemBlockLeaves(leaves), "leaves.default." + leaves.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.TREE_LEAVES, leaves);

			PropertyTreeType treeType = leaves.getVariant();
			for (TreeDefinition treeDefinition : treeType.getAllowedValues()) {
				Preconditions.checkNotNull(treeDefinition);
				String speciesUid = treeDefinition.getUID();
				IBlockState blockState = leaves.getDefaultState().withProperty(treeType, treeDefinition);
				speciesToLeavesDefault.put(speciesUid, blockState);
			}
		}

		leavesDefaultFruit = BlockDefaultLeavesFruit.create();
		speciesToLeavesDefaultFruit = new HashMap<>();
		for (BlockDefaultLeavesFruit leaves : leavesDefaultFruit) {
			registerBlock(leaves, new ItemBlockLeaves(leaves), "leaves.default.fruit." + leaves.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.TREE_LEAVES, leaves);

			PropertyTreeTypeFruit treeType = leaves.getVariant();
			for (PropertyTreeTypeFruit.LeafVariant variant : treeType.getAllowedValues()) {
				Preconditions.checkNotNull(variant);
				String speciesUid = variant.definition.getUID();
				IBlockState blockState = leaves.getDefaultState().withProperty(treeType, variant);
				speciesToLeavesDefaultFruit.put(speciesUid, blockState);
			}
		}

		leavesDecorative = BlockDecorativeLeaves.create();
		speciesToLeavesDecorative = new HashMap<>();
		for (BlockDecorativeLeaves leaves : leavesDecorative) {
			registerBlock(leaves, new ItemBlockDecorativeLeaves(leaves), "leaves.decorative." + leaves.getBlockNumber());
			registerOreDictWildcard(OreDictUtil.TREE_LEAVES, leaves);

			for (IBlockState state : leaves.getBlockState().getValidStates()) {
				TreeDefinition treeDefinition = state.getValue(leaves.getVariant());
				String speciesUid = treeDefinition.getUID();
				int meta = leaves.getMetaFromState(state);
				speciesToLeavesDecorative.put(speciesUid, new ItemStack(leaves, 1, meta));
			}
		}

		// Pods
		AlleleFruits.registerAlleles();
		MinecraftForge.EVENT_BUS.post(new AlleleRegisterEvent<>(IAlleleFruit.class));
		podsMap = new HashMap<>();
		for (BlockFruitPod pod : BlockFruitPod.create()) {
			IAlleleFruit fruit = pod.getFruit();
			registerBlock(pod, "pods." + fruit.getModelName());
			podsMap.put(fruit.getUID(), pod);
		}

		// Machines
		treeChest = new BlockArboriculture(BlockTypeArboricultureTesr.ARB_CHEST);
		registerBlock(treeChest, new ItemBlockForestry<>(treeChest), "tree_chest");
	}

	public ItemStack getDecorativeLeaves(String speciesUid) {
		ItemStack itemStack = speciesToLeavesDecorative.get(speciesUid);
		if (itemStack == null) {
			return ItemStack.EMPTY;
		}
		return itemStack.copy();
	}

	public IBlockState getDefaultLeaves(String speciesUid) {
		return speciesToLeavesDefault.get(speciesUid);
	}

	public IBlockState getDefaultLeavesFruit(String speciesUid) {
		return speciesToLeavesDefaultFruit.get(speciesUid);
	}

	@Nullable
	public BlockFruitPod getFruitPod(IAlleleFruit fruit) {
		return podsMap.get(fruit.getUID());
	}
}
