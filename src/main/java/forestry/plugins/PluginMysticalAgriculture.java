package forestry.plugins;

import com.google.common.collect.ImmutableList;
import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.core.utils.Log;
import forestry.modules.ForestryModuleUids;
import net.minecraft.util.IStringSerializable;

@SuppressWarnings("unused")
@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.MAGICAL_AGRICULTURE, name = "Mystical Agriculture", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.mysticalagriculture.description")
public class PluginMysticalAgriculture extends CompatPlugin {
	private static final String MAGICAL_AGRICULTURE = "mysticalagriculture";

	public PluginMysticalAgriculture() {
		super("Mystical Agriculture", MAGICAL_AGRICULTURE);
	}

	private ImmutableList<String> getCropNames() {
		try {
			Class<?> typeEnum = Class.forName("com.blakebr0.mysticalagriculture.lib.CropType$Type");
			if (typeEnum.getEnumConstants() == null) {
				return ImmutableList.of();
			}
			ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();
			for (Object obj : typeEnum.getEnumConstants()) {
				if (obj instanceof IStringSerializable) {
					builder.add(((IStringSerializable) obj).getName());
				}
			}
			return builder.build();
		} catch (ClassNotFoundException e) {
			Log.error("Failed to find the class ('com.blakebr0.mysticalagriculture.lib.CropType$Type') that contains the crop types of 'Mystical Agriculture'.");
			return ImmutableList.of();
		}
	}
}
