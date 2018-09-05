package emanondev.quests.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.quest.Quest;
import emanondev.quests.utils.YmlLoadable;

public class Category extends YmlLoadable {
	public static final String PATH_COMMAND = "commandtoopen";
	public static final String PATH_ITEM = "itemtoopen";
	public static final String PATH_PERMISSION = "permission";
	public static final String PATH_ROWS = "rows_amount";
	public static final String PATH_QUESTS = "quests";
	
	private final HashMap<Integer,String> quests = new HashMap<Integer,String>();
	//private String title;
	/**
	 * Always low case
	 */
	private String command;
	private ItemStack item;
	/**
	 * Always low case
	 */
	private String permission;
	/**
	 * Values Range = 9 - 81
	 */
	private int size; //multiplo di 9 size/9 = righe
	
	private final static int MAX_ROWS = 9;
	private final static int MIN_ROWS = 1;
	private final static int DEFAULT_ROWS = 6;
	
	public Category(ConfigSection m) {
		super(m);
		command = loadCommandName().toLowerCase();
		item = loadItem();
		permission = loadPermission().toLowerCase();
		size = loadSize();
	}

	private int loadSize() {
		int rows = getSection().getInt(PATH_ROWS, DEFAULT_ROWS);
		if (rows < MIN_ROWS || rows > MAX_ROWS)
			rows = DEFAULT_ROWS;
		return rows*9;
	}

	private String loadPermission() {
		String perm = getSection().getString(PATH_PERMISSION,null);
		if (perm !=null && (perm.isEmpty()||perm.contains(" ") ))
				throw new IllegalArgumentException("Invalid Permission '"+perm+"'");
		return perm;
	}

	private ItemStack loadItem() {
		ItemStack item = getSection().getItemStack(PATH_ITEM,null);
		if (item.getType() == Material.AIR)
			return null;
		return item;
	}

	private String loadCommandName() {
		String cmd = getSection().getString(PATH_COMMAND,null);
		return cmd;
	}

	public boolean hasPermission(Player p) {
		if (permission == null)
			return true;
		return p.hasPermission(permission);
	}
	
	
	@Override
	protected boolean shouldAutogenDisplayName() {
		return false;
	}

	@Override
	protected List<String> getWorldsListDefault() {
		return new ArrayList<String>();
	}

	@Override
	protected boolean shouldWorldsAutogen() {
		return false;
	}

	@Override
	protected boolean getUseWorldsAsBlacklistDefault() {
		return true;
	}
	
	public boolean setRows(int rows) {
		if (rows < MIN_ROWS)
			rows = MIN_ROWS;
		if (rows > MAX_ROWS)
			rows = MAX_ROWS;
		if (rows == size/9)
			return false;
		getSection().set(PATH_ROWS, rows);
		this.size = rows*9;
		setDirty(true);
		return true;
	}
	public boolean setCommand(String command) {
		if (command == null && this.command == null)
			return false;
		if (command !=null && command.equalsIgnoreCase(this.command) )
			return false;
		getSection().set(PATH_COMMAND,command);
		this.command = command;
		setDirty(true);
		return true;
	}
	public boolean setItem(ItemStack item) {
		if (item != null && item.getType() == Material.AIR)
			item = null;
		if (item == null && this.item == null)
			return false;
		if (item !=null && item.equals(this.item) )
			return false;
		getSection().set(PATH_ITEM,item);
		this.item = item;
		setDirty(true);
		return true;
	}
	public boolean setPermission(String perm) {
		if (perm != null && (perm.isEmpty() || perm.contains(" ")))
			return false;
		if (perm == null && this.permission == null)
			return false;
		if (perm !=null && perm.equalsIgnoreCase(this.permission) )
			return false;
		getSection().set(PATH_PERMISSION,perm);
		this.permission = perm;
		setDirty(true);
		return true;
	}
	
	public boolean removeSlot(int id) {
		if (id < 0 || id >= size)
			return false;
		if (!quests.containsKey(id))
			return false;
		getSection().set(PATH_QUESTS+"."+id,null);
		quests.remove(id);
		setDirty(true);
		return true;
	}
	public boolean addSlot(int id, Quest q) {
		if (id < 0 || id >= size)
			return false;
		if (q ==null)
			return false;
		quests.put(id, q.getNameID());
		setDirty(true);
		return true;
	}
	
	/*public PermissionButtonFactory extends TextEditorButtonFactory {
		
	}*/

}
