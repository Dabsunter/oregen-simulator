package fr.dabsunter.oregensimulator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by David on 27/11/2016.
 */
public class OreDecorator {

	public static final HashMap<Player, OreDecorator> DECORATORS = new HashMap<>();

	private Chunk chunk;
	private Random randGen = new Random();
	private Material oreBlock = Material.REDSTONE_ORE;
	private int veinSize = 7;
	private int veins = 8;
	private int layerMin = 0;
	private int layerMax = 16;
	private boolean usingLapisGen = false;

	public static OreDecorator createDecorator(Player player) {
		OreDecorator decorator = new OreDecorator(player.getLocation().getChunk());
		DECORATORS.put(player, decorator);
		return decorator;
	}

	private OreDecorator(Chunk chunk) {
		this.chunk = chunk;
	}

	public Chunk getChunk() {
		return chunk;
	}

	public void setChunk(Chunk chunk) {
		this.chunk = chunk;
	}

	public Material getOreBlock() {
		return oreBlock;
	}

	public void setOreBlock(Material oreBlock) {
		if (oreBlock == null || !oreBlock.isBlock())
			throw new IllegalArgumentException(oreBlock + " is not a Block");
		this.oreBlock = oreBlock;
	}

	public int getVeinSize() {
		return veinSize;
	}

	public void setVeinSize(int veinSize) {
		this.veinSize = veinSize;
	}

	public int getVeins() {
		return veins;
	}

	public void setVeins(int veins) {
		this.veins = veins;
	}

	public int getLayerMax() {
		return layerMax;
	}

	public void setLayerMax(int layerMax) {
		checkLayer(layerMax);
		if (layerMax <= layerMin)
			throw new IllegalArgumentException("Maximal layer cannot be smaller or equals to minimal layer");
		this.layerMax = layerMax;
	}

	public int getLayerMin() {
		return layerMin;
	}

	public void setLayerMin(int layerMin) {
		checkLayer(layerMin);
		if (layerMin >= layerMax)
			throw new IllegalArgumentException("Minimal layer cannot be greater or equals to maximal layer");
		this.layerMin = layerMin;
	}

	private void checkLayer(int layer) {
		if (layer < 0)
			throw new IllegalArgumentException("Minimal layer is 0");
		int max = chunk.getWorld().getMaxHeight();
		if (layer >= max)
			throw new IllegalArgumentException("Maximal layer is " + max);
	}

	public boolean isUsingLapisGen() {
		return usingLapisGen;
	}

	public void setUsingLapisGen(boolean usingLapisGen) {
		this.usingLapisGen = usingLapisGen;
	}

	public void generate() {
		for (int i = 0; i < veins; i++) {
			int x = randGen.nextInt(16);
			int y;
			if (usingLapisGen)
				y = randGen.nextInt(layerMax) + randGen.nextInt(layerMax) + (layerMin - layerMax);
			else
				y = randGen.nextInt(layerMax - layerMin) + layerMin;
			int z = randGen.nextInt(16);
			generateVein(x, y, z);
		}
	}

	/**
	 * Raw function imported from vanilla code.
	 * @param x
	 * @param y
	 * @param z
	 */
	private void generateVein(int x, int y, int z)
	{
		float var6 = randGen.nextFloat() * (float)Math.PI;
		// Use Chunk#getBlock() method, remove "+ 8" adjustation
		double var7 = ((float)(x /*+ 8*/) + Math.sin(var6) * (float)veinSize / 8.0F);
		double var9 = ((float)(x /*+ 8*/) - Math.sin(var6) * (float)veinSize / 8.0F);
		double var11 = ((float)(z /*+ 8*/) + Math.cos(var6) * (float)veinSize / 8.0F);
		double var13 = ((float)(z /*+ 8*/) - Math.cos(var6) * (float)veinSize / 8.0F);
		double var15 = (double)(y + randGen.nextInt(3) - 2);
		double var17 = (double)(y + randGen.nextInt(3) - 2);

		for (int var19 = 0; var19 <= veinSize; ++var19)
		{
			double var20 = var7 + (var9 - var7) * (double)var19 / (double)veinSize;
			double var22 = var15 + (var17 - var15) * (double)var19 / (double)veinSize;
			double var24 = var11 + (var13 - var11) * (double)var19 / (double)veinSize;
			double var26 = randGen.nextDouble() * (double)veinSize / 16.0D;
			double var28 = (Math.sin((float)var19 * (float)Math.PI / (float)veinSize) + 1.0F) * var26 + 1.0D;
			double var30 = (Math.sin((float)var19 * (float)Math.PI / (float)veinSize) + 1.0F) * var26 + 1.0D;
			int var32 = (int)Math.floor(var20 - var28 / 2.0D);
			int var33 = (int)Math.floor(var22 - var30 / 2.0D);
			int var34 = (int)Math.floor(var24 - var28 / 2.0D);
			int var35 = (int)Math.floor(var20 + var28 / 2.0D);
			int var36 = (int)Math.floor(var22 + var30 / 2.0D);
			int var37 = (int)Math.floor(var24 + var28 / 2.0D);

			for (int blockX = var32; blockX <= var35; ++blockX)
			{
				double var39 = ((double)blockX + 0.5D - var20) / (var28 / 2.0D);

				if (var39 * var39 < 1.0D)
				{
					for (int blockY = var33; blockY <= var36; ++blockY)
					{
						double var42 = ((double)blockY + 0.5D - var22) / (var30 / 2.0D);

						if (var39 * var39 + var42 * var42 < 1.0D)
						{
							for (int blockZ = var34; blockZ <= var37; ++blockZ)
							{
								double var45 = ((double)blockZ + 0.5D - var24) / (var28 / 2.0D);

								if (var39 * var39 + var42 * var42 + var45 * var45 < 1.0D)
								{
									// Do not check if it was a stone block, so use an average chance.
									if (isInChunk(blockX, blockY, blockZ) && randGen.nextFloat() < 0.621040813F)
										chunk.getBlock(blockX, blockY, blockZ).setType(oreBlock, false);
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean isInChunk(int x, int y, int z) {
		return x >= 0 && x < 16 && y >= 0 && y < chunk.getWorld().getMaxHeight() && z >= 0 && z < 16;
	}

}
