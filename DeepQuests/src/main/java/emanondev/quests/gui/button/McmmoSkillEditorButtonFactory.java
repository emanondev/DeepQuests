package emanondev.quests.gui.button;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.nossr50.datatypes.skills.SkillType;

public abstract class McmmoSkillEditorButtonFactory extends AbstractSelectorButtonFactory<SkillType> {

	public McmmoSkillEditorButtonFactory() {
		super("SkillType Editor");
	}

	protected ArrayList<String> getButtonDescription() {
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("&6&lSkillType editor button");
		desc.add("&6click to edit selected skilltype");
		if (getObject() == null)
			desc.add("&cNo skilltype has been selected yet");
		else
			desc.add("&7Current selected skilltype is &a"+getObject().getName());
		return desc;
	}
	
	private final static ItemStack DEFAULT_BUTTON = craftDefaultButton();
	
	private static ItemStack craftDefaultButton() {
		ItemStack item = new ItemStack(Material.IRON_SWORD);
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		return item;
	}
	@Override
	protected ItemStack getButtonItemStack() {
		return DEFAULT_BUTTON;
	}

	@Override
	protected ArrayList<String> getObjectButtonDescription(SkillType skillType) {
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("&6Click to select Mcmmo Skill "+skillType.getName());
		return desc;
	}

	@Override
	protected ItemStack getObjectItemStack(SkillType skillType) {
		return DEFAULT_BUTTON;
	}

	@Override
	public Collection<SkillType> getCollection() {
		return EnumSet.allOf(SkillType.class);
	}
}