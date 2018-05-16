package emanondev.quests.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.MemorySection;

import emanondev.quests.Quests;
import emanondev.quests.utils.MemoryUtils;

public class BlocksTaskInfo {
	private final static String PATH_BLOCK = "block";
	private final static String PATH_BLOCK_AS_WHITELIST = "blocks-is-whitelist";
	private final ArrayList<BlockType> blocks = new ArrayList<BlockType>();
	private final boolean blockWhitelist;
	
	public BlocksTaskInfo(MemorySection m) {
		List<String> list = MemoryUtils.getStringList(m, PATH_BLOCK);
		if (list!=null)
			for (String value : list) {
				try {
					blocks.add(new BlockType(value));
				} catch (Exception e) {
					Quests.getLogger("errors").log("error on Path "+m.getCurrentPath()+"."+m.getName()
						+" "+e.getMessage());
					Quests.getLogger("errors").log(ExceptionUtils.getStackTrace(e));
				}
			}
		blockWhitelist = m.getBoolean(PATH_BLOCK_AS_WHITELIST,true);
	}
	
	public boolean isValidBlock(Block block) {
		for (BlockType bType : blocks) {
			if (bType.isBlock(block))
				return blockWhitelist;
		}
		return !blockWhitelist;
	}
	
	private class BlockType{
		private Material material;
		private Byte data;
		
		private BlockType(String s) {
			if (s==null)
				throw new NullPointerException();
			String[] list = s.split(":");
			if (list.length!=1 && list.length!=2)
				throw new IllegalArgumentException("not recognized BlockType value on BlockBreakTask: "+s);
			try {
				this.material = Material.valueOf(list[0].toUpperCase());
			} catch (Exception e) {
				throw new IllegalArgumentException("not recognized BlockType material value on BlockBreakTask: "+list[0]);
			}
			if (material == null)
				throw new IllegalArgumentException("not recognized BlockType material value on BlockBreakTask: "+list[0]);
			if (list.length==1)
				data = null;
			else {
				try {
					data = Byte.valueOf(list[1]);
				}catch (Exception e) {
					throw new IllegalArgumentException("not recognized BlockType byte value on BlockBreakTask: "+list[1]);
				}
			}
		}
		@SuppressWarnings("deprecation")
		private boolean isBlock(Block block) {
			if (block==null||block.getType()!=material)
				return false;
			if (data==null||data.equals(block.getData()))
				return true;
			return false;
		}
	}

}