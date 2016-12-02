package fr.dabsunter.oregensimulator;

import org.bukkit.plugin.java.JavaPlugin;

public class OreGenPlugin extends JavaPlugin {

	private ChunkManager chunkManager;

	@Override
	public void onLoad() {
		saveDefaultConfig();
		chunkManager = new ChunkManager();
	}

	@Override
	public void onEnable() {
		getCommand("oregen").setExecutor(new OreGenCommand(this));
	}

	@Override
	public void onDisable() {
		if (getConfig().getBoolean("restore-on-stop", true)) 
			chunkManager.restoreAll();
	}

	public ChunkManager getChunkManager() {
		return chunkManager;
	}
}
