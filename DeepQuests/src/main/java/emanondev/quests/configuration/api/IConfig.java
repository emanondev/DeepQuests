package emanondev.quests.configuration.api;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;

import emanondev.quests.configuration.api.json.JsonConfig;
import emanondev.quests.configuration.api.yaml.YmlConfig;

public interface IConfig extends ISection {

    /**
     * Creates a file from inputstream.
     * @param input The input stream to be used for file contents.
     * @param targetFile The file to be created.
     * @return The created file.
     */
    public static File createDefaultFile(InputStream input, File targetFile) {
        if (targetFile.exists()) {
            throw new RuntimeException("Failed to create an already existing file!");
        }
        if (targetFile.isDirectory()) {
            throw new RuntimeException("Failed to create a file from default!");
        }
        if (input == null) {
            throw new RuntimeException("Cannot create a default file from a null value!");
        }
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        try {
            Files.copy(input, Paths.get(targetFile.toURI()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetFile;
    }

    /**
     * Loads in a IConfig object from file.
     * @param file The file that has to be read.
     * @return A new IConfig instance for the given file.
     */
    public static IConfig loadJsonConfiguration(File file) {
        return loadConfiguration(FileStorageType.JSON, file);
    }

    /**
     * Loads in a IConfig object from InputStream.
     * @param input The stream that has to be read.
     * @return A new IConfig instance for the given stream.
     */
    public static IConfig loadJsonConfiguration(InputStream input) {
        return loadConfiguration(FileStorageType.JSON, input);
    }

    /**
     * Loads in a IConfig object from file.
     * @param file The file that has to be read.
     * @return A new IConfig instance for the given file.
     */
    public static IConfig loadYamlConfiguration(File file) {
        return loadConfiguration(FileStorageType.YAML, file);
    }

    /**
     * Loads in a IConfig object from InputStream.
     * @param input The stream that has to be read.
     * @return A new IConfig instance for the given stream.
     */
    public static IConfig loadYamlConfiguration(InputStream input) {
        return loadConfiguration(FileStorageType.YAML, input);
    }

    /**
     * Loads in a IConfig object from File.
     * @param clazz Whether any class implementing IConfig with a constructor from File.
     * @param file The file you want to load.
     * @param <T> The class implementing IConfig you want to get.
     * @return A new instance of T implementing IConfig, null if an error occured.
     */
    @SuppressWarnings("unchecked")
    public static <T extends IConfig> T loadConfiguration(Class<T> clazz, File file) {
        try {
            Constructor<?> constructor = ConfigUtils.getConstructor(clazz, File.class);
            return (T) constructor.newInstance(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads in a IConfig object from InputStream.
     * @param clazz Whether any class implementing IConfig with a constructor from InputStream.
     * @param stream The InputStream you want to load.
     * @param <T> The class implementing IConfig you want to get.
     * @return A new instance of T implementing IConfig, null if an error occured.
     */
    @SuppressWarnings("unchecked")
    public static <T extends IConfig> T loadConfiguration(Class<T> clazz, InputStream stream) {
        try {
            Constructor<?> constructor = ConfigUtils.getConstructor(clazz, InputStream.class);
            return (T) constructor.newInstance(stream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads in a IConfig object from File.
     *
     * @param type JSON or YAML type.
     * @param file The file you want to load.
     * @return A new IConfig instance, null if an error occured.
     */
    public static IConfig loadConfiguration(FileStorageType type, File file) {
        try {
            Constructor<?> constructor = ConfigUtils.getConstructor(getConfigurationClass(type), File.class);
            return (IConfig) constructor.newInstance(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads in a IConfig object from InputStream.
     *
     * @param type   JSON or YAML type.
     * @param stream The InputStream you want to load.
     * @return A new IConfig instance, null if an error occured.
     */
    public static IConfig loadConfiguration(FileStorageType type, InputStream stream) {
        try {
            Constructor<?> constructor = ConfigUtils.getConstructor(getConfigurationClass(type), InputStream.class);
            return (IConfig) constructor.newInstance(stream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the default (built in) class for Json and Yaml file management.
     *
     * @param type JSON or YAML type.
     * @return The default BungeeUtilisals management for these classes.
     */
    public static Class<?> getConfigurationClass(FileStorageType type) {
        switch (type) {
            default:
            case JSON:
                return JsonConfig.class;
            case YAML:
                return YmlConfig.class;
        }
    }

    /**
     * Copies keys and values from the given IConfig instance IF NOT found in the instance.
     * @param configuration The configuration you want to load defaults from.
     * @throws IOException If there is an error saving the file.
     */
    public void copyDefaults(IConfig configuration) throws IOException;

    /**
     * Reloads the IConfig from File.
     * @throws IOException Being thrown if the File is not found. For example if you reload a IConfig built with a stream.
     */
    public void reload() throws IOException;

    /**
     * Saves the IConfig to the File.
     * @throws IOException Being thrown if the File is not found. For example if you try to save a IConfig built with a stream.
     */
    public void save() throws IOException;
}
