package clepto.bukkit;

import clepto.ListUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.PacketPlayOutSetCooldown;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftSkull;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftMetaSkull;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.UUID;

public class B {

	public static final String marker = "\u00A77\u229E\u00A7f ";
	public static Plugin plugin;

	public static void bc(String msg) {
		System.out.println(msg);
		TextComponent textComponent = new TextComponent(msg);
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			onlinePlayer.sendMessage(textComponent);
		}
	}

	public static void sound(Location loc, Sound sound, float pitch) {
		loc.getWorld().playSound(loc, sound, SoundCategory.MASTER, 1, pitch);
	}

	public static Vector randomVector() {
		return new Vector(ListUtils.random.nextGaussian(), ListUtils.random.nextGaussian(), ListUtils.random.nextGaussian()).normalize();
	}

	public static int postpone(int ticks, Runnable task) {
		return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task, ticks);
	}

	public static int repeat(int ticks, Runnable task) {
		return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, task, ticks, ticks);
	}

	public static void events(Listener... listeners) {
		for (Listener listener : listeners) Bukkit.getPluginManager().registerEvents(listener, plugin);
	}

	public static float parseFloat(String str, float fallback) {
		try {
			return Float.parseFloat(str);
		} catch (NumberFormatException ex) {
			return fallback;
		}
	}

	public static GameProfile createDummyProfile(String encodedTexture) {
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		profile.getProperties().put("textures", new Property("textures", encodedTexture));
		return profile;
	}

	public static ItemStack createSkull(GameProfile profile) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		CraftMetaSkull meta = (CraftMetaSkull) item.getItemMeta();
		meta.profile = profile;
		item.setItemMeta(meta);
		return item;
	}

	public static CraftSkull placeSkull(Location location, GameProfile profile, BlockFace rotation) {
		Block block = location.getBlock();
		block.setType(Material.SKULL);

		CraftSkull state = (CraftSkull) block.getState();
		state.setSkullType(SkullType.PLAYER);
		state.setRawData((byte) 1);
		state.profile = profile;
		if (rotation != null)
			state.setRotation(rotation);
		state.update();
		return state;
	}



	public static Player nearby(Location location, Player exception, double max) {
		double minDistance = Double.MAX_VALUE;
		Player closest = null;
		max *= max;
		for (Player player : location.getWorld().getPlayers()) {
			if (player == exception) continue;
			Location loc = player.getLocation();
			double distance = loc.distanceSquared(location);
			if (distance > minDistance) continue;
			if (distance > max) continue;
			minDistance = distance;
			closest = player;
		}
		return closest;
	}

	public static void potion(Player p, PotionEffectType effect, int seconds, int amplifier) {
		p.removePotionEffect(effect);
		p.addPotionEffect(new PotionEffect(effect, seconds * 20, amplifier));
	}

	public static void particle(Particle particle, Location location, double dx, double dy, double dz, double speed, int amount) {
		location.getWorld().spawnParticle(particle, location, amount, dx, dy, dz, speed);
	}

	public static void blockdust(Material material, Location location, double dx, double dy, double dz, double speed, int amount) {
		location.getWorld().spawnParticle(Particle.BLOCK_DUST, location, amount, dx, dy, dz, speed, new MaterialData(material));
	}

	public static void reddust(int color, Location location) {

		int r = color << 16 & 255;
		int g = color << 8 & 255;
		int b = color & 255;

		location.getWorld().spawnParticle(Particle.REDSTONE, location, 0,
				Math.min(r / 255d, 0.01),
				Math.min(g / 255d, 0.01),
				Math.min(b / 255d, 0.01),
				1);
	}

	public static void itemCooldown(Player p, ItemStack itemStack, int ticks) {
		Item item = CraftItemStack.asNMSCopy(itemStack).item;
		PacketPlayOutSetCooldown packet = new PacketPlayOutSetCooldown(item, ticks);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}

//
//	private static final String[] ARMOR_MATERIALS = {"CHAINMAIL", "IRON", "GOLD", "DIAMOND"};
//	private static final String[] ARMOR_TYPES = {"BOOTS", "LEGGINGS", "CHESTPLATE", "HELMET"};
//	public static ItemStack armor(int slot, int color, String name, String... lore) {
//		String materialStr = color < 4 ? ARMOR_MATERIALS[color] : "LEATHER";
//		String typeStr = ARMOR_TYPES[slot];
//		Material material = Material.valueOf(materialStr + "_" + typeStr);
//
//		ItemStack item = new ItemStack(material);
//		ItemMeta meta = item.getItemMeta();
//		meta.setDisplayName(name);
//		meta.setLore(Arrays.asList(lore));
//		if (color >= 4) ((LeatherArmorMeta) meta).setColor(Color.fromRGB(color));
//		item.setItemMeta(meta);
//		return item;
//	}

	public static String fullwidth(String s) {
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++) chars[i] += 0xfee0;
		return new String(chars);
	}

	public static void destroy(Block block) {
		((CraftWorld) block.getWorld()).getHandle().setAir(new BlockPosition(block.getX(), block.getY(), block.getZ()), true);
	}

	public static void run(Runnable runnable) {
		Bukkit.getScheduler().runTask(plugin, runnable);
	}

	public static void async(Runnable runnable) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
	}

	public static void regConsumerCommand(ConsumerExecutor executor, String command, String... aliases) {
		Bukkit.getServer().getCommandMap().register(command, new Cmd(command, (sender, args) -> {
			executor.execute(sender, args);
			return null;
		}, aliases));
	}

	public static void regCommand(Executor executor, String command, String... aliases) {
		Bukkit.getServer().getCommandMap().register(command, new Cmd(command, executor, aliases));
	}

	private static class Cmd extends BukkitCommand {

		private final Executor executor;

		public Cmd(String cmd, Executor executor, String... aliases) {
			super(cmd);
			this.executor = executor;
			this.setAliases(Arrays.asList(aliases));
		}

		@Override
		public boolean execute(CommandSender commandSender, String s, String[] strings) {
			String msg = executor.execute(commandSender instanceof Player ? (Player) commandSender : null, strings);
			if (msg != null) commandSender.sendMessage(msg);
			return true;
		}

	}

	public interface Executor {
		String execute(Player sender, String[] args);
	}
	public interface ConsumerExecutor {
		void execute(Player sender, String[] args);
	}

	public static void thunder(Location location) {
		location.getWorld().strikeLightning(location);
	}

	public static BlockPosition nms(Location loc) {
		return new BlockPosition(loc.getX(), loc.getY(), loc.getZ());
	}


}
