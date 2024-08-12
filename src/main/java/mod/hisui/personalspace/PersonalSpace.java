package mod.hisui.personalspace;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import mod.hisui.personalspace.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class PersonalSpace implements ClientModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("personal-space");

	private static final File CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("personal_space.json").toFile();
	private static final Path IGNORELIST_PATH = FabricLoader.getInstance().getGameDir().resolve("data").resolve("personalspace_ignorelist.dat");
	public static Config CONFIG = Config.loadConfigFile(CONFIG_PATH);
	public static double MIN_DISTANCE = Math.pow(CONFIG.minimum_distance, 2);
	public static double MAX_DISTANCE = Math.pow(CONFIG.maximum_distance, 2);
	public static int MIN_OPACITY = (CONFIG.minimum_opacity_percent * 255) / 100;
	public static int MAX_OPACITY = 255;
	public static boolean ENABLED = CONFIG.enabled;

	// TODO MOVE THIS INTO ITS OWN SINGLETON
	public static AbstractClientPlayerEntity TEMP_OWNER = null;
	public static boolean RENDERING_CLOAK = false;

	public static LongList IGNORE_LIST = loadIgnoreList(IGNORELIST_PATH);

	public static KeyBinding TOGGLE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.personalspace.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "key.categories.personalspace")
	);
	public static KeyBinding ADD_REMOVE_IGNORE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.personalspace.ignore", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.personalspace")
	);

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while(ADD_REMOVE_IGNORE_KEY.wasPressed()) {
				Entity other = client.targetedEntity;
				if(other instanceof OtherClientPlayerEntity otherClientPlayer){
					GameProfile gameProfile = otherClientPlayer.getGameProfile();
					long hash = computeUserHash(otherClientPlayer);
					if(IGNORE_LIST.contains(hash)) {
						client.player.sendMessage(Text.translatable("personalspace.removed", gameProfile.getName()).formatted(Formatting.RED));
						IGNORE_LIST.rem(hash);
					} else {
						IGNORE_LIST.add(hash);
						client.player.sendMessage(Text.translatable("personalspace.added", gameProfile.getName()).formatted(Formatting.GREEN));
					}
					saveIgnoreList(IGNORELIST_PATH);
				}
			}
			while (TOGGLE_KEY.wasPressed()) {
				CONFIG.enabled = !CONFIG.enabled;
				ENABLED = CONFIG.enabled;
				CONFIG.saveConfigFile(CONFIG_PATH);
				if(client.player != null) client.player.sendMessage(Text.literal("%s player hiding.".formatted(ENABLED ? "Enabled" : "Disabled")).formatted(ENABLED ? Formatting.GREEN : Formatting.RED),true);
			}
		});
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> saveIgnoreList(IGNORELIST_PATH));
	}

	public static int getOpacityForDistance(OtherClientPlayerEntity ocpe) {
		double distance = MinecraftClient.getInstance().getEntityRenderDispatcher().camera.getPos().squaredDistanceTo(ocpe.getPos().offset(Direction.UP, 0.5));
		if(distance <= PersonalSpace.MAX_DISTANCE) {

			// Define the minimum and maximum value
			int minValue = PersonalSpace.MIN_OPACITY;
			int maxValue = PersonalSpace.MAX_OPACITY;

			// Ensure the distance is within the specified range
			if (distance < PersonalSpace.MIN_DISTANCE) {
				distance = PersonalSpace.MIN_DISTANCE;
			}

			// Perform linear interpolation
			return (int) Math.round(minValue + (distance - PersonalSpace.MIN_DISTANCE) * (maxValue - minValue) / (PersonalSpace.MAX_DISTANCE - PersonalSpace.MIN_DISTANCE));
		} else {
			return 0xFF;
		}
	}

	public static boolean isIgnored(OtherClientPlayerEntity ocpe){
		return IGNORE_LIST.contains(computeUserHash(ocpe));
	}

	public static long computeUserHash(OtherClientPlayerEntity ocpe){
		GameProfile gameProfile = ocpe.getGameProfile();
		return Hashing.murmur3_128().hashUnencodedChars(gameProfile.getId().toString() + gameProfile.getProperties().get("textures").stream().filter(property -> property.name().equals("textures")).toList().getFirst().value()).asLong();
	}

	public static LongList loadIgnoreList(Path path) {
		LongList longList = new LongArrayList();
		try {
			List<String> lines = Files.readAllLines(path);
			lines.forEach(line -> longList.add(Long.parseLong(line)));
        } catch (IOException e){
			LOGGER.error("[Personal Space] Error occurred while trying to load ignore-list:", e);
		}
		return longList;
	}

	public void saveIgnoreList(Path path) {
		try {
			Files.createDirectories(path.getParent());
			try(BufferedWriter writer = Files.newBufferedWriter(path,
					StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
				for(long val : IGNORE_LIST) {
					writer.write(Long.toString(val));
					writer.newLine();
				}
			}
		} catch (IOException e) {
			LOGGER.error("[Personal Space] Error occurred while trying to save ignore-list:", e);
		}
	}
}