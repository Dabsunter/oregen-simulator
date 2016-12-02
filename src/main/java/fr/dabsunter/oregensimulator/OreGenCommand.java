package fr.dabsunter.oregensimulator;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by David on 27/11/2016.
 */
public class OreGenCommand implements CommandExecutor {

	private final OreGenPlugin plugin;

	public OreGenCommand(OreGenPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can use this command.");
			return true;
		}
		if (args.length == 0)
			return false;

		Player player = (Player)sender;
		switch (args[0].toLowerCase()) {
			case "help":
				helpCommand(player, label, args);
				return true;
			case "set":
				setCommand(player, label, args);
				return true;
			case "generate":
				generateCommand(player, label, args);
				return true;
			case "restore" :
				restoreCommand(player, label, args);
				return true;
			case "info":
				infoCommand(player, label, args);
				return true;
			default:
				return false;
		}
	}

	private void helpCommand(Player player, String label, String[] args) {
		String[] message;
		if (args.length == 1) {
			message = new String[]{
					" --- Help on \"/" + label + " commands ---",
					"help - Ask for help on a command's usage",
					"set - Update settings",
					"generate - Generate ores with current settings",
					"restore - Restore the destroyed chunks",
					"info - Display current settings"
			};
		} else {
			String header = " --- Help on \"/" + label + " " + args[1] + "\" command ---";
			switch (args[1].toLowerCase()) {
				case "help":
					message = new String[]{
							header,
							"/" + label + " help <command>",
							"Ask for help on the <command>'s usage"
					};
					break;
				case "set":
					message = new String[]{
							header,
							"/" + label + " set <flag> <anotherflag>...",
							"Update settings with provided flags. Flags are :",
							"chunk - Update the affected chunk to your's",
							"ore=[Block ID] - Update the block used as ore in generation",
							"size=[integer] - Update the average size of viens",
							"veins=[integer] - Update the number of viens per chunk",
							"layerMax=[integer] - Update the maximal layer where ore will spawn",
							"layerMin=[integer] - Update the minimal layer where ore will spawn",
							"lapisGen=true/false - Define the usage of lapis dispatching"
					};
					break;
				case "generate":
					message = new String[]{
							header,
							"/" + label + " generate [flag] [anotherflag]...",
							"Generate ores with current settings, updated with flags if any. Flags are :",
							"chunk - Update the affected chunk to your's",
							"ore=[Block ID] - Update the block used as ore in generation",
							"size=[integer] - Update the average size of viens",
							"veins=[integer] - Update the number of viens per chunk",
							"layerMax=[integer] - Update the maximal layer where ore will spawn",
							"layerMin=[integer] - Update the minimal layer where ore will spawn",
							"lapisGen=true/false - Define the usage of lapis dispatching"
					};
					break;
				case "restore":
					message = new String[]{
							header,
							"/" + label + " restore /all/<chunk X> <chunk Z>",
							"Restore the destroyed (for experiments :3) chunk as it was before.",
							"(no args) - Restore the chunk you are currently in",
							"all - Restore all destroyed chunks",
							"<chunk X> <chunk Z> - Restore the specified chunk (if it was saved by the plugin)"
					};
					break;
				case "info":
					message = new String[]{
							header,
							"/" + label + " info",
							"Display current settings."
					};
					break;
				default:
					message = new String[]{
							header,
							"/" + label + " " + args[1] + " <...",
							"Wait... this command does not exist x)"
					};
			}
		}
		player.sendMessage(message);
	}

	private void setCommand(Player player, String label, String[] args) {
		if (args.length < 2) {
			helpCommand(player, label, new String[]{"help", args[0]});
			return;
		}

		OreDecorator decorator;
		if (OreDecorator.DECORATORS.containsKey(player))
			decorator = OreDecorator.DECORATORS.get(player);
		else
			decorator = OreDecorator.createDecorator(player);

		for (int i = 1; i < args.length; i++) {
			String arg = args[i];
			try {
				if (arg.equals("chunk")) {
					decorator.setChunk(player.getLocation().getChunk());
				} else if (arg.startsWith("ore=")) {
					arg = arg.substring(4);
					decorator.setOreBlock(Material.getMaterial(Integer.parseInt(arg)));
				} else if (arg.startsWith("size=")) {
					arg = arg.substring(5);
					decorator.setVeinSize(Integer.parseInt(arg));
				} else if (arg.startsWith("veins=")) {
					arg = arg.substring(6);
					decorator.setVeins(Integer.parseInt(arg));
				} else if (arg.startsWith("layerMax=")) {
					arg = arg.substring(9);
					decorator.setLayerMax(Integer.parseInt(arg));
				} else if (arg.startsWith("layerMin=")) {
					arg = arg.substring(9);
					decorator.setLayerMin(Integer.parseInt(arg));
				} else if (arg.startsWith("lapisGen=")) {
					arg = arg.substring(9);
					decorator.setUsingLapisGen(Boolean.parseBoolean(arg));
				} else {
					throw new IllegalArgumentException("Unknown flag : " + arg);
				}
			} catch (IllegalArgumentException ex) {
				sendErrorMessage(player, ex.getMessage());
			}
		}
		player.sendMessage("Values has been updated.");
		infoCommand(player, label, args);
	}

	private void generateCommand(Player player, String label, String[] args) {
		if (args.length > 1)
			setCommand(player, label, args);

		OreDecorator decorator;
		if (OreDecorator.DECORATORS.containsKey(player))
			decorator = OreDecorator.DECORATORS.get(player);
		else
			decorator = OreDecorator.createDecorator(player);

		player.sendMessage("Generating... (maybe cause lag)");
		plugin.getChunkManager().clear(decorator.getChunk());
		decorator.generate();
		player.sendMessage("Done.");
	}

	private void restoreCommand(Player player, String label, String[] args) {
		if (args.length == 2 && args[1].equalsIgnoreCase("all")) {
			player.sendMessage("Restoring all saved chunks...");
			plugin.getChunkManager().restoreAll();
			player.sendMessage("Done.");
			return;
		}

		try {
			Chunk chunk;
			if (args.length == 3) {
				int x = Integer.parseInt(args[1]);
				int z = Integer.parseInt(args[2]);
				chunk = player.getWorld().getChunkAt(x, z);
			} else if (args.length == 1) {
				chunk = player.getLocation().getChunk();
			} else {
				helpCommand(player, label, new String[]{"help", args[0]});
				return;
			}

			plugin.getChunkManager().restore(chunk);
			player.sendMessage("Restored chunk X:" + chunk.getX() + "/Z:" + chunk.getZ());
			return;
		} catch (IllegalArgumentException ex) {
			sendErrorMessage(player, ex.getMessage());
		}
	}

	private void infoCommand(Player player, String label, String [] args) {
		OreDecorator d;
		if (OreDecorator.DECORATORS.containsKey(player))
			d = OreDecorator.DECORATORS.get(player);
		else
			d = OreDecorator.createDecorator(player);
		Chunk chunk = d.getChunk();
		player.sendMessage(new String[]{
				" --- Generation Infos (on chunk X:" + chunk.getX() + "/Z:" + chunk.getZ() + ") ---",
				"Ore block : " + d.getOreBlock() + "   Vein size : " + d.getVeinSize() + "   Max layer : " + d.getLayerMax(),
				"Using lapis dispatch : " + d.isUsingLapisGen() + "   Vein per chunk : " + d.getVeins() + "   Min layer : " + d.getLayerMin()
		});
	}

	private static void sendErrorMessage(Player player, String message) {
		player.sendMessage(ChatColor.RED + "Error: " + message);
	}

}
