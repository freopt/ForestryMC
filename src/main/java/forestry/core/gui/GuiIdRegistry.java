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
package forestry.core.gui;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import forestry.apiculture.entities.EntityMinecartApiary;
import forestry.apiculture.entities.EntityMinecartBeehouse;
import forestry.apiculture.items.ItemHabitatLocator;
import forestry.apiculture.items.ItemImprinter;
import forestry.apiculture.multiblock.TileAlveary;
import forestry.apiculture.multiblock.TileAlvearyHygroregulator;
import forestry.apiculture.multiblock.TileAlvearySieve;
import forestry.apiculture.tiles.TileApiary;
import forestry.apiculture.tiles.TileBeeHouse;
import forestry.book.items.ItemForesterBook;
import forestry.climatology.tiles.TileHabitatFormer;
import forestry.core.items.ItemAlyzer;
import forestry.core.items.ItemSolderingIron;
import forestry.core.tiles.TileAnalyzer;
import forestry.core.tiles.TileEscritoire;
import forestry.core.tiles.TileNaturalistChest;
import forestry.database.tiles.TileDatabase;
import forestry.energy.tiles.TileEngineBiogas;
import forestry.energy.tiles.TileEnginePeat;
import forestry.factory.tiles.TileBottler;
import forestry.factory.tiles.TileCarpenter;
import forestry.factory.tiles.TileCentrifuge;
import forestry.factory.tiles.TileFabricator;
import forestry.factory.tiles.TileFermenter;
import forestry.factory.tiles.TileMoistener;
import forestry.factory.tiles.TileRaintank;
import forestry.factory.tiles.TileSqueezer;
import forestry.factory.tiles.TileStill;
import forestry.sorting.tiles.TileGeneticFilter;
import forestry.worktable.tiles.TileWorktable;

public class GuiIdRegistry {
	private static final Map<Class<? extends IGuiHandlerForestry>, GuiId> classMap = new HashMap<>();
	private static final Map<Integer, GuiId> idMap = new HashMap<>();
	private static int nextId = 0;

	static {
		registerGuiHandlers(GuiType.Tile, Arrays.asList(
			//Apiculture Module
			TileAlveary.class,
			TileAlvearyHygroregulator.class,
			TileAlvearySieve.class,
			TileApiary.class,
			TileBeeHouse.class,

			//Core Module
			TileAnalyzer.class,
			TileEscritoire.class,
			TileNaturalistChest.class,

			//Worktable Module
			TileWorktable.class,

			//Database Module
			TileDatabase.class,

			//Factory Module
			TileBottler.class,
			TileCarpenter.class,
			TileCentrifuge.class,
			TileFabricator.class,
			TileFermenter.class,
			TileMoistener.class,
			TileSqueezer.class,
			TileStill.class,
			TileRaintank.class,

			//Farm Module

			//Climatology
			TileHabitatFormer.class,

			//Sorting Module
			TileGeneticFilter.class,

			//Cultivation Module

			//Power Module
			TileEngineBiogas.class,
			TileEnginePeat.class
		));

		registerGuiHandlers(GuiType.Item, Arrays.asList(
			ItemAlyzer.class,
			ItemHabitatLocator.class,
			ItemImprinter.class,
			ItemSolderingIron.class,
			ItemForesterBook.class
		));

		registerGuiHandlers(GuiType.Entity, Arrays.asList(
			EntityMinecartApiary.class,
			EntityMinecartBeehouse.class
		));
	}

	private static void registerGuiHandlers(GuiType guiType, List<Class<? extends IGuiHandlerForestry>> guiHandlerClasses) {
		for (Class<? extends IGuiHandlerForestry> tileGuiHandlerClass : guiHandlerClasses) {
			GuiId guiId = new GuiId(nextId++, guiType, tileGuiHandlerClass);
			classMap.put(tileGuiHandlerClass, guiId);
			idMap.put(guiId.getId(), guiId);
		}
	}

	public static GuiId getGuiIdForGuiHandler(IGuiHandlerForestry guiHandler) {
		Class<? extends IGuiHandlerForestry> guiHandlerClass = guiHandler.getClass();
		GuiId guiId = classMap.get(guiHandlerClass);
		if (guiId == null) {
			for (Map.Entry<Class<? extends IGuiHandlerForestry>, GuiId> classGuiIdEntry : classMap.entrySet()) {
				if (classGuiIdEntry.getKey().isAssignableFrom(guiHandlerClass)) {
					guiId = classGuiIdEntry.getValue();
					break;
				}
			}
		}
		if (guiId == null) {
			throw new IllegalStateException("No gui ID for gui handler: " + guiHandler);
		}
		return guiId;
	}

	@Nullable
	public static GuiId getGuiId(int id) {
		return idMap.get(id);
	}
}
