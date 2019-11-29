package net.islandearth.schematics.extended;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public enum ExtraTags {
	
	ANVILS(Arrays.asList(
			Material.ANVIL, 
			Material.CHIPPED_ANVIL, 
			Material.DAMAGED_ANVIL)),
	SWORDS(Arrays.asList(
			Material.WOODEN_SWORD,
			Material.STONE_SWORD,
			Material.IRON_SWORD,
			Material.DIAMOND_SWORD,
			Material.GOLDEN_SWORD)),
	AXES(Arrays.asList(
			Material.WOODEN_AXE,
			Material.STONE_AXE,
			Material.IRON_AXE,
			Material.DIAMOND_AXE,
			Material.GOLDEN_AXE)),
	FENCE_GATES(Arrays.asList(Material.ACACIA_FENCE_GATE,
				Material.BIRCH_FENCE_GATE,
				Material.DARK_OAK_FENCE_GATE,
				Material.JUNGLE_FENCE_GATE,
				Material.OAK_FENCE_GATE,
				Material.SPRUCE_FENCE_GATE));

	private List<Material> materials;
	
	ExtraTags(List<Material> materials) {
		this.materials = materials;
	}

	public List<Material> getMaterials() {
		return materials;
	}
}
