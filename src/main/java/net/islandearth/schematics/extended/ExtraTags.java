package net.islandearth.schematics.extended;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public enum ExtraTags {

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
