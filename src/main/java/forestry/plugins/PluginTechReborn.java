package forestry.plugins;

import com.google.common.collect.ImmutableMap;
import forestry.api.core.ForestryAPI;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.items.EnumPropolis;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.ModuleCore;
import forestry.core.blocks.BlockBogEarth;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.items.EnumElectronTube;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.Log;
import forestry.core.utils.ModUtil;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry.ItemStackHolder;
import net.minecraftforge.registries.IForgeRegistry;
import techreborn.api.ISubItemRetriever;
import techreborn.api.TechRebornAPI;

import javax.annotation.Nullable;

@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.TECH_REBORN, name = "TechReborn", author = "temp1011", url = Constants.URL, unlocalizedDescription = "for.module.techreborn.description")
public class PluginTechReborn extends CompatPlugin {

	public static final String MOD_ID = "techreborn";

	@ItemStackHolder("techreborn:rubber_log")
	@Nullable
	public static ItemStack RUBBER_WOOD = null;
	@ItemStackHolder("techreborn:rubber_sapling")
	@Nullable
	public static final ItemStack RUBBER_SAPLING = null;

	public static ItemStack sap;
	public static ItemStack rubber;

	public PluginTechReborn() {
		super("TechReborn", MOD_ID);
	}

	@Override
	@Optional.Method(modid = MOD_ID)
	public void doInit() {
		ISubItemRetriever getItem = TechRebornAPI.subItemRetriever;
		sap = getItem.getPartByName("rubberSap");
		rubber = getItem.getPartByName("rubber");

	}

	@Override
	@Optional.Method(modid = MOD_ID)
	public void registerRecipes() {
		RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY,
			Fluids.GLASS.getFluid(500),
			ModuleCore.items.tubes.get(EnumElectronTube.RUBBER, 4),
			new Object[]{" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "itemRubber"});

		if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			ItemRegistryApiculture beeItems = ModuleApiculture.getItems();
			if (!ModUtil.isModLoaded(PluginIC2.MOD_ID)) {
				RecipeManagers.centrifugeManager.addRecipe(20, beeItems.propolis.get(EnumPropolis.NORMAL, 1), ImmutableMap.of(sap, 1.0f));
			} else {
				Log.info("Using ic2 Propolis recipe rather than Tech Reborn");
			}
		}

		int bogEarthOutputCan = ForestryAPI.activeMode.getIntegerSetting("recipe.output.bogearth.can");
		if (bogEarthOutputCan > 0) {
			ItemStack waterCell = TechRebornAPI.subItemRetriever.getCellByName("water");
			ItemStack bogEarthCan = ModuleCore.getBlocks().bogEarth.get(BlockBogEarth.SoilType.BOG_EARTH, bogEarthOutputCan);
			RecipeUtil.addRecipe("techreborn_bog_earth_can", bogEarthCan, "#Y#", "YXY", "#Y#", '#', Blocks.DIRT, 'X', waterCell, 'Y', "sand");
		}
	}

	public static boolean rubberItemsSuccess() {
		IForgeRegistry<Item> registry = ForgeRegistries.ITEMS;
		return registry.containsKey(new ResourceLocation("techreborn", "rubber_wood"))
				&& registry.containsKey(new ResourceLocation("techreborn", "part"));
	}
}