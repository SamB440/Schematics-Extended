public class PlayerManagement {
	
	private JavaPlugin plugin;
	
	HashMap<UUID, Boolean> building = new HashMap<UUID, Boolean>();
	HashMap<UUID, String> current = new HashMap<UUID, String>();
	
	public PlayerManagement(JavaPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	public void setBuilding(UUID uuid, boolean val, String name)
	{
		if(!building.containsKey(uuid)) 
		{
			building.put(uuid, val);
		} else building.replace(uuid, val);
		
		if(!current.containsKey(uuid)) 
		{
			current.put(uuid, name);
		} else current.replace(uuid, name);
	}
	
	public boolean isBuilding(UUID uuid)
	{
		return building.get(uuid);
	}
	
	public Schematic getBuilding(UUID uuid)
	{
		return new Schematic(plugin, new File(plugin.getDataFolder() + "/schematics/" + current.get(uuid) + ".schematic"));
	}
}