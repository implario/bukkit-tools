package clepto.bukkit.groovy;

import groovy.lang.Script;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class ScriptInitializer extends JavaPlugin {

	@Override
	public void onEnable() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(getResource("scriptClasses")));
			while (true) {
				String line = reader.readLine();
				if (line == null || line.isEmpty()) break;
				Class<?> scriptClass = Class.forName(line);
				if (!Script.class.isAssignableFrom(scriptClass)) continue;
				Script script = (Script) scriptClass.newInstance();
				try {
					script.run();
				} catch (Throwable throwable) {
					Bukkit.getLogger().log(Level.SEVERE, "An error occurred while running script '" + scriptClass.getName() + "':", throwable);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
