/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http:www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.plugins;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.ForestryAPI;
import forestry.api.fuels.EngineBronzeFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.items.EnumPropolis;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.ModuleCore;
import forestry.core.blocks.BlockBogEarth;
import forestry.core.circuits.CircuitLayout;
import forestry.core.circuits.Circuits;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemRegistryCore;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.Log;
import forestry.core.utils.ModUtil;
import forestry.energy.ModuleEnergy;
import forestry.energy.blocks.BlockRegistryEnergy;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.INDUSTRIALCRAFT2, name = "IndustrialCraft2", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.ic2.description")
public class PluginIC2 extends BlankForestryModule {
	public static final String MOD_ID = "ic2";

	@Nullable
	private static ItemStack rubberSapling;
	@Nullable
	private static ItemStack rubber;
	@Nullable
	public static ItemStack rubberWood;
	@Nullable
	public static ItemStack resin;
	@Nullable
	public static ItemStack fertilizer;


	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(PluginIC2.MOD_ID);
	}

	@Override
	public String getFailMessage() {
		return "IndustrialCraft2 not found";
	}

	@Nonnull
	@Override
	public Set<ResourceLocation> getDependencyUids() {
		Set<ResourceLocation> dependencyUids = new HashSet<>();
		dependencyUids.add(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.CORE));
		dependencyUids.add(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.FACTORY));
		return dependencyUids;
	}

	@Override
	@Optional.Method(modid = PluginIC2.MOD_ID)
	public void preInit() {
		// rubber chain
		rubberWood = IC2Items.getItem("rubber_wood");
		resin = IC2Items.getItem("misc_resource", "resin");
		rubberSapling = IC2Items.getItem("sapling");
		rubber = IC2Items.getItem("crafting", "rubber");
		fertilizer = IC2Items.getItem("crop_res", "fertilizer");

		ICircuitLayout layoutEngineTin = new CircuitLayout("engine.tin", CircuitSocketType.ELECTRIC_ENGINE);
		ChipsetManager.circuitRegistry.registerLayout(layoutEngineTin);

	}

	@Override
	@Optional.Method(modid = PluginIC2.MOD_ID)
	public void doInit() {
		// Remove some items from the recycler
		if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			if (Recipes.recyclerBlacklist != null) {
				ItemRegistryApiculture beeItems = ModuleApiculture.getItems();
				Recipes.recyclerBlacklist.add(Recipes.inputFactory.forStack(new ItemStack(beeItems.beeQueenGE)));
				Recipes.recyclerBlacklist.add(Recipes.inputFactory.forStack(new ItemStack(beeItems.beePrincessGE)));
			} else {
				Log.error("IC2 Recipes.recyclerBlacklist not found.");
			}
		}
	}

	@Override
	public void postInit() {
		ItemStack rubberLeaves = IC2Items.getItem("leaves");
	}

	@Override
	@Optional.Method(modid = PluginIC2.MOD_ID)
	public void registerRecipes() {
		ItemRegistryCore coreItems = ModuleCore.getItems();
		FluidStack glass = Fluids.GLASS.getFluid(500);
		if (rubber != null && glass != null) {
			RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, glass, coreItems.tubes.get(EnumElectronTube.RUBBER, 4),
				new Object[]{" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "itemRubber"});
		}

		ItemStack plantBall = IC2Items.getItem("crafting", "plant_ball");
		if (plantBall != null) {
			RecipeUtil.addFermenterRecipes(plantBall, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat") * 9, Fluids.BIOMASS);
		}

		ItemStack bioChaff = IC2Items.getItem("crafting", "bio_chaff");
		if (bioChaff != null) {
			RecipeUtil.addFermenterRecipes(bioChaff, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat") * 9, Fluids.BIOMASS);
		}

		if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			ItemRegistryApiculture beeItems = ModuleApiculture.getItems();
			if (resin != null) {
				RecipeManagers.centrifugeManager.addRecipe(20, beeItems.propolis.get(EnumPropolis.NORMAL, 1), ImmutableMap.of(resin, 1.0f));
			} else {
				Log.info("Missing IC2 resin, skipping centrifuge recipe for propolis to resin.");
			}
		}

		if (rubberSapling != null) {
			RecipeUtil.addFermenterRecipes(rubberSapling, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling"), Fluids.BIOMASS);
		} else {
			Log.info("Missing IC2 rubber sapling, skipping fermenter recipe for converting rubber sapling to biomass.");
		}


		if (ModuleHelper.isEnabled(ForestryModuleUids.ENERGY)) {
			Fluid biogas = FluidRegistry.getFluid("ic2biogas");
			Fluid biomass = Fluids.BIOMASS.getFluid();
			if (biogas != null && biomass != null) {
				int burnDuration = Math.round(Constants.ENGINE_CYCLE_DURATION_BIOMASS * ForestryAPI.activeMode.getFloatSetting("fuel.biomass.biogas"));
				EngineBronzeFuel bronzeFuel = new EngineBronzeFuel(biomass, Constants.ENGINE_FUEL_VALUE_BIOMASS, burnDuration, 1);
				FuelManager.bronzeEngineFuel.put(biogas, bronzeFuel);
			}
		}

		ItemStack waterCell = IC2Items.getItem("fluid_cell", "water");
		if (waterCell != null) {
			int bogEarthOutputCan = ForestryAPI.activeMode.getIntegerSetting("recipe.output.bogearth.can");
			if (bogEarthOutputCan > 0) {
				ItemStack bogEarthCan = ModuleCore.getBlocks().bogEarth.get(BlockBogEarth.SoilType.BOG_EARTH, bogEarthOutputCan);
				RecipeUtil.addRecipe("ic2_bog_earth_can", bogEarthCan, "#Y#", "YXY", "#Y#", '#', Blocks.DIRT, 'X', waterCell, 'Y', "sand");
			}
		}

		ICircuitLayout layout = Preconditions.checkNotNull(ChipsetManager.circuitRegistry.getLayout("forestry.engine.tin"));

		// / Solder Manager
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.COPPER, 1), Circuits.energyElectricChoke1);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.TIN, 1), Circuits.energyElectricBoost1);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.BRONZE, 1), Circuits.energyElectricBoost2);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.IRON, 1), Circuits.energyElectricEfficiency1);

		BlockRegistryEnergy energyBlocks = ModuleEnergy.blocks;
	}
}
