package dev.implario.bukkit.world;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.lang.Math.*;

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

        if (min.getWorld() != max.getWorld())
            throw new IllegalArgumentException("min and max points are from different worlds");

        this.container = container;
        double minX = min.getX();
        double minY = min.getY();
        double minZ = min.getZ();
        double maxX = max.getX();
        double maxY = max.getY();
        double maxZ = max.getZ();
        this.min = new Location(min.getWorld(), min(minX, maxX), min(minY, maxY), min(minZ, maxZ));
        this.max = new Location(max.getWorld(), max(minX, maxX), max(minY, maxY), max(minZ, maxZ));
        this.name = name;
        this.tag = tag;
        this.dimensions = V3.of(
                abs(minX - maxX) + 1,
                abs(minY - maxY) + 1,
                abs(minZ - maxZ) + 1
        );
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
    public World getWorld() {
        return container.getWorld();
    }

    @Override
    public boolean contains(Location location) {
        return min.getX() <= location.getX() && max.getX() >= location.getX() &&
                min.getY() <= location.getY() && max.getY() >= location.getY() &&
                min.getZ() <= location.getZ() && max.getZ() >= location.getZ();
    }

    public Location getCenter() {
        return new Location(container.getWorld(),
                min.getX() + (max.getX() - min.getX()) / 2,
                min.getY() + (max.getY() - min.getY()) / 2,
                min.getZ() + (max.getZ() - min.getZ()) / 2
        );
    }

    public V3 toRelativeVector(Location location) {
        return V3.of(
                location.getX() - min.getX(),
                location.getY() - min.getY(),
                location.getZ() - min.getZ()
        );
    }

//    public void forEachNMS(BiConsumer<BlockPosition, IBlockData> action) {
//        for (int x = (int) min.getX(); x <= max.getX(); x++) {
//            for (int y = (int) min.getY(); y <= max.getY(); y++) {
//                for (int z = (int) min.getZ(); z <= max.getZ(); z++) {
//                    BlockPosition t = new BlockPosition(x, y, z);
//                    action.accept(t, this.getWorld().getHandle().getType(t));
//                }
//            }
//        }
//    }

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
        Collection<T> allEntities = getWorld().getEntitiesByClass(type);
        List<T> list = new ArrayList<>();
        for (T entity : allEntities) {
            if (contains(entity.getLocation())) {
                list.add(entity);
            }
        }
        return list;
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