package emanondev.quests;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.LoggerManager.Logger;
import emanondev.quests.quest.Quest;
import emanondev.quests.utils.MemoryUtils;
import emanondev.quests.utils.StringUtils;

public class ConfigManager {
	private static YMLConfig config = Quests.getInstance().getConfig();
	private static ItemStack readItem(String path,String defaultItem) {
		ItemStack item = null;
		try {
			item = MemoryUtils.getGuiItem(config.getString(path, defaultItem));
		}catch (Exception e) {
			Logger l = Quests.getInstance().getLoggerManager().getLogger("error");
			l.log("Error on config file at '"+path+"' "+e.getMessage());
			l.log(ExceptionUtils.getStackTrace(e));
		}
		if (item == null || item.getType()==Material.AIR)
			item = MemoryUtils.getGuiItem(defaultItem);
		return item;
	}
	private static final String PAGE_HOLDER = "{page}";
	private static ItemStack getItemByVersion(String path,String pre113,String post113) {
		if (MemoryUtils.isPre113)
			return readItem(path,pre113);
		else 
			return readItem(path,post113);
	}
	
	private String questsMenuTitle = StringUtils.fixColorsAndHolders(
			config.getString("gui.quests-menu.title", "&9Quests Menu"));
	public String getQuestsMenuTitle(Player p) {
		return StringUtils.convertText(p, questsMenuTitle);
	}
	private ItemStack questsMenuPageItem = 
			readItem("gui.quests-menu.page.item","NAME_TAG");
	private String questsMenuPageTitle = StringUtils.fixColorsAndHolders(
			config.getString("gui.quests-menu.page.title","&9&lPage <&5&l{page}&9&l>"));
	private List<String> questsMenuPageLore = StringUtils.fixColorsAndHolders(
			MemoryUtils.getStringList(config,"gui.quests-menu.page.lore"));
			
	public ItemStack getQuestsMenuPageItem(Player p,int page) {
		ItemStack item = new ItemStack(questsMenuPageItem);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.convertText(p, questsMenuPageTitle, PAGE_HOLDER, ""+page));
		meta.setLore(StringUtils.convertList(p, questsMenuPageLore, PAGE_HOLDER, ""+page));
		item.setItemMeta(meta);
		return item;
	}
	

	private ItemStack questsMenuPreviusPageItem = 
			readItem("gui.quests-menu.previus-page.item","TRIPWIRE_HOOK");
	private String questsMenuPreviusPageTitle = StringUtils.fixColorsAndHolders(
			config.getString("gui.quests-menu.previus-page.title","&9&l<<<<<<<"));
	private List<String> questsMenuPreviusPageLore = StringUtils.fixColorsAndHolders(
			MemoryUtils.getStringList(config,"gui.quests-menu.previus-page.lore"));
			
	public ItemStack getQuestsMenuPreviusPageItem(Player p) {
		ItemStack item = new ItemStack(questsMenuPreviusPageItem);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.convertText(p, questsMenuPreviusPageTitle));
		meta.setLore(StringUtils.convertList(p, questsMenuPreviusPageLore));
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack questsMenuNextPageItem = 
			readItem("gui.quests-menu.next-page.item","TRIPWIRE_HOOK");
	private String questsMenuNextPageTitle = StringUtils.fixColorsAndHolders(
			config.getString("gui.quests-menu.next-page.title","&9&l>>>>>>>"));
	private List<String> questsMenuNextPageLore = StringUtils.fixColorsAndHolders(
			MemoryUtils.getStringList(config,"gui.quests-menu.next-page.lore"));
			
	public ItemStack getQuestsMenuNextPageItem(Player p) {
		ItemStack item = new ItemStack(questsMenuNextPageItem);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.convertText(p, questsMenuNextPageTitle));
		meta.setLore(StringUtils.convertList(p, questsMenuNextPageLore));
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack questsMenuBackItem = getItemByVersion("gui.quests-menu.back.item",
			"WOOD_DOOR","OAK_DOOR");
	private String questsMenuBackTitle = StringUtils.fixColorsAndHolders(
			config.getString("gui.quests-menu.back.title","&c&lGo Back"));
	private List<String> questsMenuBackLore = StringUtils.fixColorsAndHolders(
			MemoryUtils.getStringList(config,"gui.quests-menu.back.lore"));
			
	public ItemStack getQuestsMenuBackItem(Player p) {
		ItemStack item = new ItemStack(questsMenuBackItem);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.convertText(p, questsMenuBackTitle));
		meta.setLore(StringUtils.convertList(p, questsMenuBackLore));
		item.setItemMeta(meta);
		return item;
	}
	private ItemStack questsMenuCloseItem = getItemByVersion("gui.quests-menu.close.item",
			"IRON_DOOR","IRON_DOOR");
	private String questsMenuCloseTitle = StringUtils.fixColorsAndHolders(
			config.getString("gui.quests-menu.close.title","&c&lClose Gui"));
	private List<String> questsMenuCloseLore = StringUtils.fixColorsAndHolders(
			MemoryUtils.getStringList(config,"gui.quests-menu.close.lore"));
			
	public ItemStack getQuestsMenuCloseItem(Player p) {
		ItemStack item = new ItemStack(questsMenuCloseItem);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.convertText(p, questsMenuCloseTitle));
		meta.setLore(StringUtils.convertList(p, questsMenuCloseLore));
		item.setItemMeta(meta);
		return item;
	}

	private String missionsMenuTitle = StringUtils.fixColorsAndHolders(
			config.getString("gui.missions-menu.title", "&9Quest: {quest-name}"));
	public String getMissionsMenuTitle(Player p,Quest q) {
		return StringUtils.convertText(p, missionsMenuTitle, 
				H.QUEST_NAME, q.getDisplayName());
	}
	private ItemStack missionsMenuPageItem = 
			readItem("gui.missions-menu.page.item","NAME_TAG");
	private String missionsMenuPageTitle = StringUtils.fixColorsAndHolders(
			config.getString("gui.missions-menu.page.title","&9&lPage <&5&l{page}&9&l>"));
	private List<String> missionsMenuPageLore = StringUtils.fixColorsAndHolders(
			MemoryUtils.getStringList(config,"gui.missions-menu.page.lore"));
			
	public ItemStack getMissionsMenuPageItem(Player p,int page,Quest quest) {
		ItemStack item = new ItemStack(missionsMenuPageItem);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.convertText(p, missionsMenuPageTitle, PAGE_HOLDER, ""+page));
		meta.setLore(StringUtils.convertList(p, missionsMenuPageLore, PAGE_HOLDER, ""+page));
		item.setItemMeta(meta);
		return item;
	}
	

	private ItemStack missionsMenuPreviusPageItem = 
			readItem("gui.missions-menu.previus-page.item","TRIPWIRE_HOOK");
	private String missionsMenuPreviusPageTitle = StringUtils.fixColorsAndHolders(
			config.getString("gui.missions-menu.previus-page.title","&9&l<<<<<<<"));
	private List<String> missionsMenuPreviusPageLore = StringUtils.fixColorsAndHolders(
			MemoryUtils.getStringList(config,"gui.missions-menu.previus-page.lore"));
			
	public ItemStack getMissionsMenuPreviusPageItem(Player p) {
		ItemStack item = new ItemStack(missionsMenuPreviusPageItem);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.convertText(p, missionsMenuPreviusPageTitle));
		meta.setLore(StringUtils.convertList(p, missionsMenuPreviusPageLore));
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack missionsMenuNextPageItem = 
			readItem("gui.missions-menu.next-page.item","TRIPWIRE_HOOK");
	private String missionsMenuNextPageTitle = StringUtils.fixColorsAndHolders(
			config.getString("gui.missions-menu.next-page.title","&9&l>>>>>>>"));
	private List<String> missionsMenuNextPageLore = StringUtils.fixColorsAndHolders(
			MemoryUtils.getStringList(config,"gui.missions-menu.next-page.lore"));
			
	public ItemStack getMissionsMenuNextPageItem(Player p) {
		ItemStack item = new ItemStack(missionsMenuNextPageItem);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.convertText(p, missionsMenuNextPageTitle));
		meta.setLore(StringUtils.convertList(p, missionsMenuNextPageLore));
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack missionsMenuBackItem = getItemByVersion("gui.missions-menu.back.item",
			"WOOD_DOOR","OAK_DOOR");
	private String missionsMenuBackTitle = StringUtils.fixColorsAndHolders(
			config.getString("gui.missions-menu.back.title","&c&lGo Back"));
	private List<String> missionsMenuBackLore = StringUtils.fixColorsAndHolders(
			MemoryUtils.getStringList(config,"gui.missions-menu.back.lore"));
			
	public ItemStack getMissionsMenuBackItem(Player p) {
		ItemStack item = new ItemStack(missionsMenuBackItem);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.convertText(p, missionsMenuBackTitle));
		meta.setLore(StringUtils.convertList(p, missionsMenuBackLore));
		item.setItemMeta(meta);
		return item;
	}
	private ItemStack missionsMenuCloseItem = getItemByVersion("gui.missions-menu.close.item",
			"IRON_DOOR","IRON_DOOR");
	private String missionsMenuCloseTitle = StringUtils.fixColorsAndHolders(
			config.getString("gui.missions-menu.close.title","&c&lClose Gui"));
	private List<String> missionsMenuCloseLore = StringUtils.fixColorsAndHolders(
			MemoryUtils.getStringList(config,"gui.missions-menu.close.lore"));
			
	public ItemStack getMissionsMenuCloseItem(Player p) {
		ItemStack item = new ItemStack(missionsMenuCloseItem);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.convertText(p, missionsMenuCloseTitle));
		meta.setLore(StringUtils.convertList(p, missionsMenuCloseLore));
		item.setItemMeta(meta);
		return item;
	}
}
