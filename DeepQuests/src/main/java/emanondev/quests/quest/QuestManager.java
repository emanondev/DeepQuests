package emanondev.quests.quest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.Quests;
import emanondev.quests.YMLConfig;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomMultiPageGui;
import emanondev.quests.gui.SubExplorerFactory;
import emanondev.quests.utils.Savable;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.YmlLoadable;

public class QuestManager implements Savable {
	
	private final YMLConfig data = new YMLConfig(Quests.getInstance(),"quests");
	private static final HashMap<String,Quest> quests = new HashMap<String,Quest>();
	
	public QuestManager() {
		reload();
	}
	public void save() {
		data.save();
		setDirty(false);
	}
	public void reload() {
		quests.clear();
		data.reload();
		Set<String> s = data.getValues(false).keySet();
		s.forEach((key)->{
			boolean dirty = false;
			try {
				Quest quest = new Quest((MemorySection) data.get(key),this);
				quests.put(quest.getNameID(),quest);
				if (quest.isDirty())
					dirty = true;
			}catch (Exception e) {
				e.printStackTrace();
				Quests.getInstance().getLoggerManager().getLogger("errors")
					.log("Error while loading Quests on file quests.yml '"
							+key+"' could not be read as valid quest"
							,ExceptionUtils.getStackTrace(e));
			}
			for (Quest quest : quests.values()){
				if (isDirty())
					break;
				if (quest.isDirty())
					setDirty(true);
			}
				
			if (dirty)
				data.save();
		});
	}
	
	
	public Quest getQuestByNameID(String key) {
		return quests.get(key);
	}
	public Collection<Quest> getQuests() {
		return Collections.unmodifiableCollection(quests.values());
	}

	private boolean dirty = false;
	
	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void setDirty(boolean value) {
		if (dirty == value)
			return;
		dirty = value;
		if (dirty == true) {
			Bukkit.getScheduler().runTaskLater(Quests.getInstance(), new Runnable() {
				@Override
				public void run() {
					QuestManager man = Quests.getInstance().getQuestManager();
					if (!man.isDirty())
						return;
					man.data.save();
					man.setDirty(false);
				}
			}, 20);
		}
		else {
			for (Quest quest : quests.values()) {
				quest.setDirty(false);
			}
		}
		
	}
	
	public boolean addQuest(String id,String displayName) {
		if (id == null || id.isEmpty() || 
				id.contains(" ")||id.contains(".")||id.contains(":"))
			return false;
		if (displayName == null)
			displayName = id.replace("_"," ");
		id = id.toLowerCase();
		if (quests.containsKey(id))
			return false;
		data.set(id+"."+YmlLoadable.PATH_DISPLAY_NAME,displayName);
		Quest q = new Quest((MemorySection) data.get(id),this);
		quests.put(q.getNameID(), q);
		save();
		reload();
		Quests.getInstance().getPlayerManager().reload();
		return true;
	}

	public boolean deleteQuest(Quest quest) {
		if (quest == null || !quests.containsKey(quest.getNameID()) )
			return false;
		data.set(quest.getNameID(),null);
		quests.remove(quest.getNameID());
		save();
		reload();
		Quests.getInstance().getPlayerManager().reload();
		return true;
	}
	public void openEditorGui(Player clicker, CustomGui prevGui) {
		clicker.openInventory(new QuestsEditorGui(clicker,prevGui).getInventory());
	}
	public void openEditorGui(Player clicker) {
		openEditorGui(clicker,null);
	}
	private class QuestsEditorGui extends CustomMultiPageGui<CustomButton> {
		public QuestsEditorGui(Player p, CustomGui previusHolder) {
			super(p,previusHolder, 6,1);
			this.setFromEndCloseButtonPosition(8);
			this.addButton(new SubExplorerFactory<Quest>(Quest.class,getQuests(),
					"&8Quests List").getCustomButton(this));			
			this.addButton(new AddQuestGuiItem(this));
			this.setTitle(null, StringUtils.fixColorsAndHolders("&8Quests Manager Editor"));
			reloadInventory();
		}
		
		private class AddQuestGuiItem extends CustomButton {

			public AddQuestGuiItem(CustomGui parent) {
				super(parent);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lAdd Quest"));
				ArrayList<String> lore = new ArrayList<String>();
				lore.add(StringUtils.fixColorsAndHolders("&6Click to add a new Quest"));
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			private ItemStack item = new ItemStack(Material.GLOWSTONE);
			@Override
			public ItemStack getItem() {
				return item;
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				String key = null;
				boolean found = false;
				int i = 1;
				do {
					key = "quest";
					if (i<10)
						key = key+"000"+i;
					else if (i<100)
						key = key+"00"+i;
					else if (i<1000)
						key = key+"0"+i;
					else
						key = key+i;
					if (!quests.containsKey(key))
						found = true;
					i++;
				}while (i<10000 && found == false);
				if (found == false) {
					//TODO
					return;
				}
				if (!addQuest(key,"New Quest")) {
					//TODO
					return;
				}
				clicker.performCommand("questadmin quest "+key+" editor");
			}
			
		}
		
	}
	
	
}
