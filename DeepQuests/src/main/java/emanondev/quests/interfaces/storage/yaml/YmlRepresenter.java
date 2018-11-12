package emanondev.quests.interfaces.storage.yaml;

import com.google.common.collect.Maps;

import emanondev.quests.interfaces.storage.ConfigUtils;
import emanondev.quests.interfaces.storage.ConfigurationSerializable;
import emanondev.quests.interfaces.storage.ConfigurationSerialization;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;
import java.util.Map;

class YmlRepresenter extends Representer {

    YmlRepresenter() {
        this.multiRepresenters.put(YmlSection.class, new RepresentMap() {
            @Override
            public Node representData(Object object) {
                YmlSection section = (YmlSection) object;

                return super.representData(section.self);
            }
        });

        if (ConfigUtils.isBukkit()) {
            this.multiRepresenters.put(org.bukkit.configuration.serialization.ConfigurationSerializable.class, new RepresentMap() {
                @Override
                public Node representData(Object object) {
                    org.bukkit.configuration.serialization.ConfigurationSerializable serializable = (org.bukkit.configuration.serialization.ConfigurationSerializable) object;
                    Map<String, Object> values = Maps.newLinkedHashMap();
                    values.put("==", org.bukkit.configuration.serialization.ConfigurationSerialization.getAlias(serializable.getClass()));
                    values.putAll(serializable.serialize());
                    return super.representData(values);
                }
            });
        }
        this.multiRepresenters.put(ConfigurationSerializable.class, new RepresentMap() {
            @Override
            public Node representData(Object object) {
                ConfigurationSerializable serializable = (ConfigurationSerializable) object;
                Map<String, Object> values = Maps.newLinkedHashMap();
                values.put("==", ConfigurationSerialization.getAlias(serializable.getClass()));
                values.putAll(serializable.serialize());
                return super.representData(values);
            }
        });
    }
}
