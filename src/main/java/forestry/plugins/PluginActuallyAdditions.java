package forestry.plugins;

import javax.annotation.Nullable;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.core.ForestryAPI;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.core.fluids.Fluids;
import forestry.modules.ForestryModuleUids;

@SuppressWarnings("unused")
@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.ACT_ADD, name = "Actually Additions", author = "Ellpeck", url = "http://ellpeck.de/actadd", unlocalizedDescription = "for.module.actuallyadditions.description")
public class PluginActuallyAdditions extends CompatPlugin {

	private static final String ACT_ADD = "actuallyadditions";

	public PluginActuallyAdditions() {
		super("Actually Additions", ACT_ADD);
	}

	@Override
	public void registerRecipes() {
		Item canolaSeed = getItem("item_canola_seed");
		Item flaxSeed = getItem("item_flax_seed");
		Item riceSeed = getItem("item_rice_seed");
		Item coffeeSeed = getItem("item_coffee_seed");

		//add seed squeezing
		int amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		FluidStack seedFluid = Fluids.SEED_OIL.getFluid(amount);
		if (seedFluid != null) {
			for (Item seed : Arrays.asList(canolaSeed, flaxSeed, riceSeed, coffeeSeed)) {
				if (seed != null) {
					RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(seed), seedFluid);
				}
			}
		}

		Fluid canolaOil = getFluid("canolaoil");
		if (canolaOil != null) {
			//add canola squeezing to canola oil
			Item misc = getItem("item_misc");
			if (misc != null) {
				ItemStack canola = new ItemStack(misc, 1, 13);
				RecipeManagers.squeezerManager.addRecipe(15, canola, new FluidStack(canolaOil, 80));
			}

			//add canola oil fermenting in still
			Fluid oil = getFluid("refinedcanolaoil");
			if (oil != null) {
				RecipeManagers.stillManager.addRecipe(200, new FluidStack(canolaOil, 5), new FluidStack(oil, 5));
			}
		}
	}
}
