package mod.hisui.personalspace.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mod.hisui.personalspace.PersonalSpace;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Config {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();

    public boolean enabled = true;
    public double minimum_distance = 2;
    public double maximum_distance = 3;
    public int minimum_opacity_percent = 0;

    // Reading and saving

    /**
     * Loads config file.
     *
     * @param file file to load the config file from.
     * @return MyModConfig object
     */
    public static Config loadConfigFile(File file) {
        Config config = null;

        if (file.exists()) {
            // An existing config is present, we should use its values
            try (BufferedReader fileReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
            )) {
                // Parses the config file and puts the values into config object
                config = gson.fromJson(fileReader, Config.class);
                if(config.minimum_distance > config.maximum_distance) {
                    PersonalSpace.LOGGER.warn("[Personal Space] Error in config: minimum_distance is greater than maximum_distance! Using default settings");
                    config = null;
                }
            } catch (IOException e) {
                PersonalSpace.LOGGER.error("[Personal Space] Error occurred when trying to load config: ", e);
            }
        }
        // gson.fromJson() can return null if file is empty
        if (config == null) {
            config = new Config();
        }

        // Saves the file in order to write new fields if they were added
        config.saveConfigFile(file);
        return config;
    }

    /**
     * Saves the config to the given file.
     *
     * @param file file to save config to
     */
    public void saveConfigFile(File file) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            PersonalSpace.LOGGER.error("[Personal Space] Error occurred while trying to save config:", e);
        }
    }
}
