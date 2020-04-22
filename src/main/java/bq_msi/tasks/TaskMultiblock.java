package bq_msi.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.ItemComparison;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_msi.MultiblockInventory;
import bq_msi.NbtBlockType;
import bq_msi.client.gui.PanelTaskMultiblock;
import bq_msi.core.*;
import bq_msi.tasks.factory.FactoryTaskMultiblock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.io.*;
import java.lang.Math;

public class TaskMultiblock implements ITask
{
	private final Set<UUID> completeUsers = new TreeSet<>(); 
	private final HashMap<UUID, Integer> userProgress = new HashMap<>();
	
	public static HashMap<String, MultiblockInventory> multiblockHash = new HashMap<>();

	public BigItemStack targetItem = new BigItemStack(Items.AIR);
	public final NbtBlockType targetBlock = new NbtBlockType(Blocks.AIR);
	public boolean render = false;
	public String fileName = "";
	public int length = 0;
	public int width = 0;
	public int height = 0;
	public boolean wildcardOptimization = false;
	public String name = "New Multiblock Name";
	
	public static String clearHashmap() {
		if(multiblockHash.size() == 0) return "No cached files.";
		String returnable = "Cleared cached files:";
		for (String key : multiblockHash.keySet()) returnable = returnable + " " + key;
		multiblockHash.clear();
		return returnable;
	}
	
	public static String getHashmapContents() {
		if(multiblockHash.size() == 0) return "No cached files.";
		String returnable = "Cached files:";
		for (String key : multiblockHash.keySet()) returnable = returnable + " " + key;
		return returnable;
	}
	
    @Override
    public ResourceLocation getFactoryID()
    {
        return FactoryTaskMultiblock.INSTANCE.getRegistryName();
    }
    
    @Override
    public String getUnlocalisedName()
    {
        return BQMSI.MODID + ".task.multiblock";
    }
    
	@Override
	public boolean isComplete(UUID uuid)
	{
		return completeUsers.contains(uuid);
	}
	
	@Override
	public void setComplete(UUID uuid)
	{
		completeUsers.add(uuid);
	}

	@Override
	public void resetUser(@Nullable UUID uuid)
	{
	    if(uuid == null)
        {
            completeUsers.clear();
            userProgress.clear();
        } else
        {
            completeUsers.remove(uuid);
            userProgress.remove(uuid);
        }
	}
	
    @Override
    public void detect(ParticipantInfo pInfo, DBEntry<IQuest> quest)
    {
        final List<Tuple<UUID, Integer>> progress = getBulkProgress(pInfo.ALL_UUIDS);
        
        progress.forEach((value) -> {
            if(value.getSecond() >= 1) setComplete(value.getFirst());
        });
        
		pInfo.markDirtyParty(Collections.singletonList(quest.getID()));
    }
    
    @Nullable
    public MultiblockInventory fileData(String fileName, int length, int width, int height)
    {
		File file = new File(Loader.instance().getConfigDir() + File.separator + "betterquesting" + File.separator + "resources" + File.separator + fileName);
		
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String[][][] fileArray = new String[length][width][height];
			int[] keyCoords = new int[3];
			String line;
			String separator = ".";
			String keyBlock = "keyBlock";
			int heightCount = 0;
			int lengthCount = 0;
			
			while((line = bufferedReader.readLine()) != null) {
				if(line.trim().contentEquals(separator.trim()))	{
					heightCount++;
					lengthCount = 0;
				}
				else if(line.trim().contentEquals("")) {}
				else {
					String[] blocks = line.split(",");
					for(int i=0; i<blocks.length; i++ ) {
						if(blocks[i].trim().contentEquals("")) {}
						else {
							fileArray[lengthCount][i][heightCount] = blocks[i].trim();
							//System.out.println(blocks[i]);
							if(blocks[i].trim().contentEquals(keyBlock)) {
								//System.out.println("Key found, " + lengthCount + "," + i + "," + heightCount);
								fileArray[lengthCount][i][heightCount] = "wildcard";
								keyCoords[0] = lengthCount;
								keyCoords[1] = i;
								keyCoords[2] = heightCount;
							}
						}
					}
					lengthCount++;
				}		
			}
			bufferedReader.close();
			//System.out.println("Returning Inventory");
			return new MultiblockInventory(fileArray, keyCoords);
		}
		catch(IOException ex) {
			if(ex instanceof FileNotFoundException) {
				System.out.println("TaskMultiblock tried to access non-existant file" + ex);
				return null; }
			else {
				System.out.println(ex);
				return null; }
		}
    }
	
	public boolean matchData(World world, IBlockState state, BlockPos pos, int length, int width, int height, boolean wildcardOptimization, MultiblockInventory inventory){
		String[][][] fileArray = inventory.getFileArray();
		int[] keyCoords = inventory.getKeyCoords();
		int keyLength = keyCoords[0];
		int keyWidth = keyCoords[1];
		int keyHeight = keyCoords[2];
		boolean rot0 = true;
		boolean rot1 = true;
		boolean rot2 = true;
		boolean rot3 = true;
			
		if(wildcardOptimization) {
			for(int h=0; h<height; h++) {
				for(int w=0; w<width; w++) {
					for(int l=0; l<length; l++) {
						if(fileArray[l][w][h].contentEquals("wildcard")) {}
						else {
							for(int rot=0; rot<4; rot++) {
								if(rot==0 && rot0) {
									int x = (-keyLength + l + pos.getX());
									int y = (-keyHeight + h + pos.getY());
									int z = (keyWidth + (-w) + pos.getZ());
									//System.out.println("fileArray: " + fileArray[l][w][h]);
									//System.out.println("block: " + world.getBlockState(new BlockPos(x, y, z)).getBlock().getRegistryName().toString());
									String targetBlock = world.getBlockState(new BlockPos(x, y, z)).getBlock().getRegistryName().toString();
									String fileBlock = fileArray[l][w][h];
									if(!(fileBlock.contentEquals(targetBlock) || fileBlock.contentEquals("wildcard"))) rot0=false;
								}
								if(rot==1 && rot1) {
									int x = (keyLength + (-l) + pos.getX());
									int y = (-keyHeight + h + pos.getY());
									int z = (-keyWidth + w + pos.getZ());
									//System.out.println("fileArray: " + fileArray[l][w][h]);
									//System.out.println("block: " + world.getBlockState(new BlockPos(x, y, z)).getBlock().getRegistryName().toString());
									String targetBlock = world.getBlockState(new BlockPos(x, y, z)).getBlock().getRegistryName().toString();
									String fileBlock = fileArray[l][w][h];
									if(!(fileBlock.contentEquals(targetBlock) || fileBlock.contentEquals("wildcard"))) rot1=false;
								}
								if(rot==2 && rot2) {
									int x = (-keyWidth + w + pos.getX());
									int y = (-keyHeight + h + pos.getY());								
									int z = (-keyLength + l + pos.getZ());
									//System.out.println("fileArray: " + fileArray[l][w][h]);
									//System.out.println("block: " + world.getBlockState(new BlockPos(x, y, z)).getBlock().getRegistryName().toString());
									String targetBlock = world.getBlockState(new BlockPos(x, y, z)).getBlock().getRegistryName().toString();
									String fileBlock = fileArray[l][w][h];
									if(!(fileBlock.contentEquals(targetBlock) || fileBlock.contentEquals("wildcard"))) rot2=false;
								}
								if(rot==3 && rot3) {
									int x = (keyWidth + (-w) + pos.getX());
									int y = (-keyHeight + h + pos.getY());							
									int z = (keyLength + (-l) + pos.getZ());
									//System.out.println("fileArray: " + fileArray[l][w][h]);
									//System.out.println("block: " + world.getBlockState(new BlockPos(x, y, z)).getBlock().getRegistryName().toString());
									String targetBlock = world.getBlockState(new BlockPos(x, y, z)).getBlock().getRegistryName().toString();
									String fileBlock = fileArray[l][w][h];
									if(!(fileBlock.contentEquals(targetBlock) || fileBlock.contentEquals("wildcard"))) rot3=false;
								}
								if(!(rot0) && !(rot1) && !(rot2) && !(rot3)) return false;
							}
						}
					}
				} //System.out.println("Finish width");
			} //System.out.println("Finish height");
			//System.out.println("Returning true wildcard optimized");
			return true;
		}
		else {
			for(int h=0; h<height; h++) {
				for(int w=0; w<width; w++) {
					for(int l=0; l<length; l++) {
						for(int rot=0; rot<4; rot++) {
							if(rot==0 && rot0) {
								int x = (-keyLength + l + pos.getX());
								int y = (-keyHeight + h + pos.getY());
								int z = (keyWidth + (-w) + pos.getZ());
								//System.out.println("fileArray: " + fileArray[l][w][h]);
								//System.out.println("block: " + world.getBlockState(new BlockPos(x, y, z)).getBlock().getRegistryName().toString());
								String targetBlock = world.getBlockState(new BlockPos(x, y, z)).getBlock().getRegistryName().toString();
								String fileBlock = fileArray[l][w][h];
								if(!(fileBlock.contentEquals(targetBlock) || fileBlock.contentEquals("wildcard"))) rot0=false;
							}
							if(rot==1 && rot1) {
								int x = (keyLength + (-l) + pos.getX());
								int y = (-keyHeight + h + pos.getY());
								int z = (-keyWidth + w + pos.getZ());
								//System.out.println("fileArray: " + fileArray[l][w][h]);
								//System.out.println("block: " + world.getBlockState(new BlockPos(x, y, z)).getBlock().getRegistryName().toString());
								String targetBlock = world.getBlockState(new BlockPos(x, y, z)).getBlock().getRegistryName().toString();
								String fileBlock = fileArray[l][w][h];
								if(!(fileBlock.contentEquals(targetBlock) || fileBlock.contentEquals("wildcard"))) rot1=false;
							}
							if(rot==2 && rot2) {
								int x = (-keyWidth + w + pos.getX());
								int y = (-keyHeight + h + pos.getY());								
								int z = (-keyLength + l + pos.getZ());
								//System.out.println("fileArray: " + fileArray[l][w][h]);
								//System.out.println("block: " + world.getBlockState(new BlockPos(x, y, z)).getBlock().getRegistryName().toString());
								String targetBlock = world.getBlockState(new BlockPos(x, y, z)).getBlock().getRegistryName().toString();
								String fileBlock = fileArray[l][w][h];
								if(!(fileBlock.contentEquals(targetBlock) || fileBlock.contentEquals("wildcard"))) rot2=false;
							}
							if(rot==3 && rot3) {
								int x = (keyWidth + (-w) + pos.getX());
								int y = (-keyHeight + h + pos.getY());							
								int z = (keyLength + (-l) + pos.getZ());
								//System.out.println("fileArray: " + fileArray[l][w][h]);
								//System.out.println("block: " + world.getBlockState(new BlockPos(x, y, z)).getBlock().getRegistryName().toString());
								String targetBlock = world.getBlockState(new BlockPos(x, y, z)).getBlock().getRegistryName().toString();
								String fileBlock = fileArray[l][w][h];
								if(!(fileBlock.contentEquals(targetBlock) || fileBlock.contentEquals("wildcard"))) rot3=false;
							}
							if(!(rot0) && !(rot1) && !(rot2) && !(rot3)) return false;
						}
					}
				} //System.out.println("Finish width");
			} //System.out.println("Finish height");
			//System.out.println("Returning true wildcard un-optimized");
			return true;
		}
	}
    
    public void onInteract(ParticipantInfo pInfo, DBEntry<IQuest> quest, EnumHand hand, ItemStack item, IBlockState state, BlockPos pos)
    {
    	if(!pInfo.PLAYER.isEntityAlive() || !(pInfo.PLAYER instanceof EntityPlayerMP)) return;
    	World world = pInfo.PLAYER.world;
    	
        if((targetBlock.b != Blocks.AIR) && (targetBlock.b == state.getBlock()) && (ItemComparison.StackMatch(targetItem.getBaseStack(), item, false, false)))
        {
            if(state.getBlock() == Blocks.AIR) return;
            if(!(hand == EnumHand.MAIN_HAND)) return;
            
            //long overallTimerStart = System.currentTimeMillis();
            //long cacheTimerStart = System.currentTimeMillis();
            
            if(multiblockHash.get(fileName) == null) {
            	MultiblockInventory invenToCache = fileData(fileName, length, width, height);
            	if(invenToCache == null) {
            		System.out.println("Multiblock task attempted to cache broken file " + fileName);
            		return;
            	}
            	System.out.println("Multiblock " + fileName + " has been cached!");
            	multiblockHash.put(fileName, invenToCache);
            }
            
            //long cacheTimerStop = System.currentTimeMillis();
            //long cacheTimeElapsed = cacheTimerStop - cacheTimerStart;
            //System.out.println("Cache time elapsed (ms): " + cacheTimeElapsed);
            
            if(matchData(world, state, pos, length, width, height, wildcardOptimization, multiblockHash.get(fileName))) {
            	
            	//long overallTimerStop = System.currentTimeMillis();
            	//long overallTimeElapsed = overallTimerStop - overallTimerStart;
            	//System.out.println("Overall time elapsed (ms): " + overallTimeElapsed);
            	
            	final List<Tuple<UUID, Integer>> progress = getBulkProgress(pInfo.ALL_UUIDS);
	            
	            progress.forEach((value) -> {
	                if(isComplete(value.getFirst())) return;
	                int np = Math.min(1, value.getSecond() + 1);
	                setUserProgress(value.getFirst(), np);
	                if(np >= 1) setComplete(value.getFirst());
	            });
	            
	    		pInfo.markDirtyParty(Collections.singletonList(quest.getID()));
            }
            //else {
            //	long overallTimerStop = System.currentTimeMillis();
            //	long overallTimeElapsed = overallTimerStop - overallTimerStart;
            //	System.out.println("Overall time elapsed (ms): " + overallTimeElapsed);
            //}
        }
    }
    
    @Override
    public synchronized NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setTag("item", targetItem.writeToNBT(new NBTTagCompound()));
        nbt.setTag("block", targetBlock.writeToNBT(new NBTTagCompound()));
        nbt.setString("fileName", fileName);
        nbt.setInteger("length", length);
        nbt.setInteger("width", width);
        nbt.setInteger("height", height);
        nbt.setBoolean("wildcardOptimization", wildcardOptimization);
        nbt.setString("name", name);
        return nbt;
    }
    
    @Override
    public synchronized void readFromNBT(NBTTagCompound nbt)
    {
        targetItem = new BigItemStack(nbt.getCompoundTag("item"));
        targetBlock.readFromNBT(nbt.getCompoundTag("block"));
        fileName = nbt.getString("fileName");
        length = nbt.getInteger("length");
        width = nbt.getInteger("width");
        height = nbt.getInteger("height");
        wildcardOptimization = nbt.getBoolean("wildcardOptimization");
        name = nbt.getString("name");
    }
    
    @Override
	public NBTTagCompound writeProgressToNBT(NBTTagCompound nbt, @Nullable List<UUID> users)
	{
		NBTTagList jArray = new NBTTagList();
		NBTTagList progArray = new NBTTagList();
		
		if(users != null)
        {
            users.forEach((uuid) -> {
                if(completeUsers.contains(uuid)) jArray.appendTag(new NBTTagString(uuid.toString()));
                
                Integer data = userProgress.get(uuid);
                if(data != null)
                {
                    NBTTagCompound pJson = new NBTTagCompound();
                    pJson.setString("uuid", uuid.toString());
                    pJson.setInteger("value", data);
                    progArray.appendTag(pJson);
                }
            });
        } else
        {
            completeUsers.forEach((uuid) -> jArray.appendTag(new NBTTagString(uuid.toString())));
            
            userProgress.forEach((uuid, data) -> {
                NBTTagCompound pJson = new NBTTagCompound();
			    pJson.setString("uuid", uuid.toString());
                pJson.setInteger("value", data);
                progArray.appendTag(pJson);
            });
        }
		
		nbt.setTag("completeUsers", jArray);
		nbt.setTag("userProgress", progArray);
		
		return nbt;
	}
    
	@Override
	public void readProgressFromNBT(NBTTagCompound nbt, boolean merge)
	{
		if(!merge)
        {
            completeUsers.clear();
            userProgress.clear();
        }
		
		NBTTagList cList = nbt.getTagList("completeUsers", 8);
		for(int i = 0; i < cList.tagCount(); i++)
		{
			try
			{
				completeUsers.add(UUID.fromString(cList.getStringTagAt(i)));
			} catch(Exception e) {}
		}
		
		NBTTagList pList = nbt.getTagList("userProgress", 10);
		for(int n = 0; n < pList.tagCount(); n++)
		{
			try
			{
                NBTTagCompound pTag = pList.getCompoundTagAt(n);
                UUID uuid = UUID.fromString(pTag.getString("uuid"));
                userProgress.put(uuid, pTag.getInteger("value"));
			} catch(Exception e) {}
		}
	}
	
	private void setUserProgress(UUID uuid, Integer progress)
	{
		userProgress.put(uuid, progress);
	}
	
	public int getUsersProgress(UUID uuid)
	{
        Integer n = userProgress.get(uuid);
        return n == null? 0 : n;
	}
	
	private List<Tuple<UUID, Integer>> getBulkProgress(@Nonnull List<UUID> uuids)
    {
        if(uuids.size() <= 0) return Collections.emptyList();
        List<Tuple<UUID, Integer>> list = new ArrayList<>();
        uuids.forEach((key) -> list.add(new Tuple<>(key, getUsersProgress(key))));
        return list;
    }
    
    @Override
	@SideOnly(Side.CLIENT)
    public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest)
    {
        return new PanelTaskMultiblock(rect, this);
    }
    
    @Override
    @Nullable
	@SideOnly(Side.CLIENT)
    public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest)
    {
        return null;
    }
}