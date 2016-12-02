package fr.dabsunter.oregensimulator;

import org.bukkit.Chunk;
import org.bukkit.Material;

import java.util.HashMap;

/**
 * Created by David on 27/11/2016.
 */
public class ChunkManager {

	private final HashMap<Chunk, ChunkState> states = new HashMap<>();

	public void clear(Chunk chunk) {
		if (!states.containsKey(chunk)) {
			ChunkState state = new ChunkState(chunk);
			states.put(chunk, state);
			state.save();
		}
		int maxHeight = chunk.getWorld().getMaxHeight();
		for (int x = 0; x < 16; x++)
			for (int y = 0; y < maxHeight; y++)
				for (int z = 0; z < 16; z++)
					chunk.getBlock(x, y, z).setType(Material.AIR, false);
	}

	public void restore(Chunk chunk) {
		if (!states.containsKey(chunk))
			throw new IllegalArgumentException("Chunk were never saved");
		states.remove(chunk).restore();
	}

	public void restoreAll() {
		for (Chunk chunk : states.keySet())
			states.remove(chunk).restore();
	}

}
