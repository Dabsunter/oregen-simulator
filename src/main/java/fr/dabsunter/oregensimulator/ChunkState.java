package fr.dabsunter.oregensimulator;

import org.bukkit.Chunk;
import org.bukkit.block.BlockState;

/**
 * Created by David on 27/11/2016.
 */
public class ChunkState {

	private final Chunk chunk;
	private BlockState[] blockStates;

	public ChunkState(Chunk chunk) {
		this.chunk = chunk;
	}

	public Chunk getChunk() {
		return chunk;
	}

	public void save() {
		int maxHeight = chunk.getWorld().getMaxHeight();
		blockStates = new BlockState[256 * maxHeight];
		int i = 0;
		for (int x = 0; x < 16; x++)
			for (int y = 0; y < maxHeight; y++)
				for (int z = 0; z < 16; z++)
					blockStates[i++] = chunk.getBlock(x, y, z).getState();
	}

	public void restore() {
		if (blockStates == null)
			throw new IllegalStateException("Chunk were never saved");
		for (BlockState state : blockStates)
			state.update(true, false);
	}

}
