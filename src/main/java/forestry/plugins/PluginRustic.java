package forestry.plugins;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.item.ItemStack;

import forestry.api.core.ForestryAPI;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.modules.ForestryModuleUids;

@SuppressWarnings("unused")
@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.RUSTIC, name = "rustic", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.rustic.description")
public class PluginRustic extends CompatPlugin {

	public PluginRustic() {
		super("Rustic", "rustic");
	}

	@Override
	public void registerRecipes() {
		ImmutableList<String> fruits = ImmutableList.of(
			"tomato",
			"chili_pepper"
		);
		ImmutableList<String> crops = ImmutableList.of(
			"tomato",
			"chili"
		);
		PropertyInteger cropAge = PropertyInteger.create("age", 0, 3);
		int seedAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		int juiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple");
		ItemStack grapes = getItemStack("grapes");
		ItemStack grapeSeeds = getItemStack("grape_stem");
		Block grapeLeaves = getBlock("grape_leaves");
		if (grapeSeeds != null) {
			RecipeManagers.squeezerManager.addRecipe(10, grapeSeeds, Fluids.SEED_OIL.getFluid(seedAmount));
		}
		if (grapes != null) {
			RecipeManagers.squeezerManager.addRecipe(10, grapes, Fluids.JUICE.getFluid(juiceAmount / 12));
		}

		ItemStack sapling = getItemStack("sapling");
		ItemStack ironBerries = getItemStack("ironberries");
		ItemStack olives = getItemStack("olives");

		for (int i = 0; i < fruits.size(); i++) {
			String fruitName = fruits.get(i);
			ItemStack seeds = getItemStack(fruitName + "_seeds");
			Block block = getBlock(crops.get(i) + "_crop");
			ItemStack fruit = getItemStack(fruitName);
			if (seeds != null) {
				RecipeManagers.squeezerManager.addRecipe(10, seeds, Fluids.SEED_OIL.getFluid(seedAmount));
			}
			if (fruit != null) {
				RecipeManagers.squeezerManager.addRecipe(10, fruit, Fluids.JUICE.getFluid(juiceAmount / 25));
			}
		}
	}
}
