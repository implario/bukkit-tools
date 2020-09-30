package clepto.bukkit.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Orientation {

	PX(false, 1),
	MX(true, 1),
	PY(false, -1),
	MY(true, -1);

	private final boolean swap;
	private final int factor;

}
