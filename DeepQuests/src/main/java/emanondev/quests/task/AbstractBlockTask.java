package emanondev.quests.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.MemorySection;

import emanondev.quests.Language;
import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.utils.MemoryUtils;

public abstract class AbstractBlockTask extends Task{
	private final static String PATH_BLOCK = "block";
	private final static String PATH_BLOCK_AS_WHITELIST = "blocks-is-whitelist";
	private final String description;
	private final ArrayList<BlockType> blocks = new ArrayList<BlockType>();
	private final boolean isWhitelist;
	public AbstractBlockTask(MemorySection m, Mission parent,TaskType type) {
		super(m, parent,type);
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
		isWhitelist = m.getBoolean(PATH_BLOCK_AS_WHITELIST,true);
		StringBuilder desc = new StringBuilder("");
		if (!isWhitelist)
			desc.append(Language.getConjNot()+" ");
		for (int i = 0; i < blocks.size()-1;i++)
			if (blocks.get(i).data==null)
				desc.append(Language.getBlockName(blocks.get(i).material.toString())
						+" "+Language.getConjOr()+" ");
			else
				desc.append(Language.getBlockName(blocks.get(i).material.toString()+":"+blocks.get(i).data)
						+" "+Language.getConjOr()+" ");
		if (!blocks.isEmpty())
			if (blocks.get(blocks.size()-1).data==null)
				desc.append(Language.getBlockName(blocks.get(blocks.size()-1).material.toString()));
			else
				desc.append(Language.getBlockName(blocks.get(blocks.size()-1).material.toString()
						+":"+blocks.get(blocks.size()-1).data));
		description = desc.toString();
	}
	
	public boolean isValidBlock(Block block) {
		for (BlockType bType : blocks) {
			if (bType.isBlock(block))
				return isWhitelist;
		}
		return !isWhitelist;
	}

	@Override
	public String getDescription() {
		return description;
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
