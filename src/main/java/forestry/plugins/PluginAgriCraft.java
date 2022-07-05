package forestry.plugins;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.core.ForestryAPI;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = "AgriCraft", name = "AgriCraft", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.plugin.agricraft.description")
public class PluginAgriCraft extends CompatPlugin {

	public PluginAgriCraft() {
		super("AgriCraft", "agricraft");
	}

	@Override
	public void registerRecipes() {
		int seedAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		Item seedItem = getItem("agri_seed");
		if (seedItem != null) {
			FluidStack fluid = Fluids.SEED_OIL.getFluid(seedAmount);
			if (fluid != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(seedItem), fluid);
			}
		}
	}
}
