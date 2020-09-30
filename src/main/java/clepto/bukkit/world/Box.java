package clepto.bukkit.world;

import clepto.ListUtils;
import clepto.math.V3;
import lombok.Data;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.IBlockData;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Data
public class Box implements Area {

	private final Area container;
	private final Location min;
	private final Location max;
	private final String name;
	private final String tag;
	private final V3 dimensions;
	private final List<Label> labels;
	private Map<String, Object> meta;

	public Box(Area container, Location min, Location max, String name, String tag) {
		this.container = container;
		this.min = min;
		this.max = max;
		this.name = name;
		this.tag = tag;
		this.dimensions = V3.of(max.x - min.x, max.y - min.y, max.z - min.z);
		this.labels = new ArrayList<>();
		this.updateLabelsCache();
	}

	public Box expandVert() {
		this.min.setY(0);
		this.max.setY(255);
		this.updateLabelsCache();
		return this;
	}

	public void updateLabelsCache() {
		this.labels.clear();
		for (Label label : this.container.getLabels()) {
			if (this.contains(label)) this.labels.add(label);
		}
	}

	@Override
	public CraftWorld getWorld() {
		return container.getWorld();
	}

	@Override
	public boolean contains(Location location) {
		return min.x <= location.x && max.x >= location.x &&
				min.y <= location.y && max.y >= location.y &&
				min.z <= location.z && max.z >= location.z;
	}

	public Location getCenter() {
		return new Location(container.getWorld(),
				min.x + (max.x - min.x) / 2,
				min.y + (max.y - min.y) / 2,
				min.z + (max.z - min.z) / 2
		);
	}

	public V3 toRelativeVector(Location location) {
		return V3.of(
				location.getX() - min.x,
				location.getY() - min.y,
				location.getZ() - min.z
		);
	}

	public void forEachNMS(BiConsumer<BlockPosition, IBlockData> action) {
		for (int x = (int) min.getX(); x <= max.getX(); x++) {
			for (int y = (int) min.getY(); y <= max.getY(); y++) {
				for (int z = (int) min.getZ(); z <= max.getZ(); z++) {
					BlockPosition t = new BlockPosition(x, y, z);
					action.accept(t, this.getWorld().getHandle().getType(t));
				}
			}
		}
	}

	public void forEachBukkit(Consumer<Location> action) {
		for (int x = (int) min.getX(); x <= max.getX(); x++) {
			for (int y = (int) min.getY(); y <= max.getY(); y++) {
				for (int z = (int) min.getZ(); z <= max.getZ(); z++) {
					action.accept(new Location(this.getWorld(), x, y, z));
				}
			}
		}
	}

	public void forEach(Consumer<V3> action) {
		for (int x = (int) min.getX(); x <= max.getX(); x++) {
			for (int y = (int) min.getY(); y <= max.getY(); y++) {
				for (int z = (int) min.getZ(); z <= max.getZ(); z++) {
					action.accept(V3.of(x, y, z));
				}
			}
		}
	}

	public Location transpose(V3 absoluteOrigin, Orientation orientation, V3 relativeOrigin, int x, int y, int z) {

		x = (int) (x - min.getX() - relativeOrigin.getX());
		y = (int) (y - min.getY() - relativeOrigin.getY());
		z = (int) (z - min.getZ() - relativeOrigin.getZ());

		int tx = (orientation.isSwap() ? z : x) * orientation.getFactor();
		int ty = y;
		int tz = (orientation.isSwap() ? -x : z) * orientation.getFactor();

		tx += absoluteOrigin.getX();
		ty += absoluteOrigin.getY();
		tz += absoluteOrigin.getZ();

		return new Location(this.getWorld(), tx, ty, tz);
	}

	public <T extends Entity> List<T> getEntities(Class<T> type) {
		return ListUtils.filter(getWorld().getEntitiesByClass(type), entity -> this.contains(entity.getLocation()));
	}

	public void outset(int amount) {
		this.min.subtract(amount, amount, amount);
		this.max.add(amount, amount, amount);
		this.updateLabelsCache();
	}

	public void loadChunks() {
		for (int x = min.getChunk().getX(); x <= max.getChunk().getX(); x++) {
			for (int z = min.getChunk().getZ(); z <= max.getChunk().getZ(); z++) {
				getWorld().loadChunk(x, z);
			}
		}
	}

}
