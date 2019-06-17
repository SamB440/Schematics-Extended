package net.islandearth.schematics.extended;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

import lombok.Getter;

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
	FENCES(Arrays.asList(Material.ACACIA_FENCE,
				Material.BIRCH_FENCE,
				Material.DARK_OAK_FENCE,
				Material.JUNGLE_FENCE,
				Material.NETHER_BRICK_FENCE,
				Material.OAK_FENCE,
				Material.SPRUCE_FENCE)),
	FENCE_GATES(Arrays.asList(Material.ACACIA_FENCE_GATE,
				Material.BIRCH_FENCE_GATE,
				Material.DARK_OAK_FENCE_GATE,
				Material.JUNGLE_FENCE_GATE,
				Material.OAK_FENCE_GATE,
				Material.SPRUCE_FENCE_GATE));
	
	@Getter
	private List<Material> materials;
	
	private ExtraTags(List<Material> materials) {
		this.materials = materials;
	}
}
