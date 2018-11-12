package emanondev.quests.interfaces.storage.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import emanondev.quests.interfaces.storage.IConfig;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class YmlConfig extends YmlSection implements IConfig {

    private final YmlConstructor constructor = new YmlConstructor();
    private final YmlRepresenter representer = new YmlRepresenter();
    private final ThreadLocal<Yaml> yaml = ThreadLocal.withInitial(() -> {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setAllowUnicode(true);
        return new Yaml(constructor, representer, options);
    });

    private File file;

    public YmlConfig(final File file) throws IOException {
        this(file.exists() ? new FileInputStream(file) : null);
        this.file = file;
    }

    public YmlConfig(final InputStream input) throws IOException {
        if (input == null) {
            return;
        }

        try (final InputStream inputStream = input;
             final InputStreamReader reader = new InputStreamReader(inputStream)) {
            Map<String, Object> values = yaml.get().load(reader);

            if (values == null) {
                values = new LinkedHashMap<>();
            }

            loadIntoSections(values, this);
        }
    }

    @Override
    public void copyDefaults(IConfig configuration) throws IOException {
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        boolean changed = false;
        for (String key : configuration.getKeys()) {
            if (!exists(key)) {
                Object value = configuration.get(key);

                set(key, value);
                changed = true;
            }
        }
        if (changed) {
            save();
        }
    }

    @Override
    public void reload() throws IOException {
        if (file == null) {
            return;
        }
        self.clear();

        try (final FileInputStream input = new FileInputStream(file);
             final InputStreamReader reader = new InputStreamReader(input)) {
            Map<String, Object> values = yaml.get().load(reader);

            if (values == null) {
                values = new LinkedHashMap<>();
            }

            loadIntoSections(values, this);
        }
    }

    @Override
    public void save() throws IOException {
        try (FileWriter fileWriter = new FileWriter(file);
             BufferedWriter writer = new BufferedWriter(fileWriter)) {

            this.yaml.get().dump(self, writer);
        }
    }
}
