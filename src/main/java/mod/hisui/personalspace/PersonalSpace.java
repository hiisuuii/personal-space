package mod.hisui.personalspace;

import mod.hisui.personalspace.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class PersonalSpace implements ClientModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("personal-space");

	private static final File CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("personal_space.json").toFile();
	public static Config CONFIG = Config.loadConfigFile(CONFIG_PATH);
	public static double MIN_DISTANCE = Math.pow(CONFIG.minimum_distance, 2);
	public static double MAX_DISTANCE = Math.pow(CONFIG.maximum_distance, 2);
	public static int MIN_OPACITY = (CONFIG.minimum_opacity_percent * 255) / 100;
	public static int MAX_OPACITY = 255;
	public static boolean ENABLED = CONFIG.enabled;

	public static KeyBinding TOGGLE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.personalspace.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, KeyBinding.MULTIPLAYER_CATEGORY)
	);

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (TOGGLE_KEY.wasPressed()) {
				CONFIG.enabled = !CONFIG.enabled;
				ENABLED = CONFIG.enabled;
				CONFIG.saveConfigFile(CONFIG_PATH);
				if(client.player != null) client.player.sendMessage(Text.literal("%s player hiding.".formatted(ENABLED ? "Enabled" : "Disabled")).formatted(ENABLED ? Formatting.GREEN : Formatting.RED),true);
			}
		});
	}
}