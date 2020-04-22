package bq_msi.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_msi.client.gui.PanelTaskRCLocate;
import bq_msi.core.*;
import bq_msi.tasks.factory.FactoryTaskRCLocate;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.io.*;
import java.lang.Math;

public class TaskRCLocate implements ITaskTickable
{
	private final Set<UUID> completeUsers = new TreeSet<>();
	
	public String structure = "";
	public String name = "New Structure";
	public int dim = 0;
	public boolean hideInfo = false;
	
	@Override
	public ResourceLocation getFactoryID()
	{
		return FactoryTaskRCLocate.INSTANCE.getRegistryName();
	}
	
	@Override
	public String getUnlocalisedName()
	{
		return BQMSI.MODID + ".task.rc_locate";
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
        } else
        {
            completeUsers.remove(uuid);
        }
	}
	
	@Override
	public void tickTask(@Nonnull ParticipantInfo pInfo, DBEntry<IQuest> quest)
	{
		if(pInfo.PLAYER.ticksExisted%100 == 0) {
			internalDetect(pInfo, quest);
		}
	}
	
	@Override
	public void detect(@Nonnull ParticipantInfo pInfo, DBEntry<IQuest> quest)
	{
		internalDetect(pInfo, quest);
	}
	
	public boolean DataReadCompare(String regionName, String structure, int dim, int xPos, int zPos) {
		File fileName = new File(DimensionManager.getCurrentSaveRootDirectory() + File.separator + "data" + File.separator + "QuestStructure" + File.separator + dim + File.separator + regionName + ".txt");
		
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			
			while((line = bufferedReader.readLine()) != null) {
				if(line.contains(structure)) {
					String[] tokens = line.split(",");
					int minX = Integer.parseInt(tokens[1]);
					int maxX = Integer.parseInt(tokens[2]);
					int minZ = Integer.parseInt(tokens[3]);
					int maxZ = Integer.parseInt(tokens[4]);
					
					
					if((minX < xPos) && (xPos < maxX) && (minZ < zPos) && (zPos < maxZ)) {
						bufferedReader.close();
						return true; }
				}
			}
			bufferedReader.close();
			return false;
		}
		catch(IOException ex) {
			if(ex instanceof FileNotFoundException) {
				System.out.println("BQ_RLC tried to access non-existant file" + ex);
				return false; }
			else {
				System.out.println(ex);
				return false; }
		}
	}
	
	public String convertPosToRegion(int xPos, int zPos)
	{	
		int regionX = Math.round(xPos/512);
		int regionZ = Math.round(zPos/512);
		String regionName = ("r." + Integer.toString(regionX) + "." + Integer.toString(regionZ));
		return regionName;
	}
	
	private void internalDetect(@Nonnull ParticipantInfo pInfo, DBEntry<IQuest> quest)
    {
		if(!pInfo.PLAYER.isEntityAlive() || !(pInfo.PLAYER instanceof EntityPlayerMP)) return;
		
		EntityPlayerMP playerMP = (EntityPlayerMP)pInfo.PLAYER;
		
		BlockPos pos = playerMP.getPosition();
		int xPos = pos.getX();
		int zPos = pos.getZ();
		String regionName = convertPosToRegion(xPos, zPos);
		
		if(playerMP.dimension == dim)
		{
		    if(!StringUtils.isNullOrEmpty(structure) && DataReadCompare(regionName, structure, dim, xPos, zPos))
            {
		            pInfo.ALL_UUIDS.forEach((uuid) -> {
		                if(!isComplete(uuid)) setComplete(uuid);
		            });
		            pInfo.markDirtyParty(Collections.singletonList(quest.getID()));
            } 
		}
		

    }
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("dimension", dim);
		nbt.setString("structure", structure);
		nbt.setString("name", name);
		nbt.setBoolean("hideInfo", hideInfo);
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		dim = nbt.getInteger("dimension");
		structure = nbt.getString("structure");
		name = nbt.getString("name");
		hideInfo = nbt.getBoolean("hideInfo");
	}
	
	@Override
	public NBTTagCompound writeProgressToNBT(NBTTagCompound nbt, @Nullable List<UUID> users)
	{
		NBTTagList jArray = new NBTTagList();
		
		completeUsers.forEach((uuid) -> {
		    if(users == null || users.contains(uuid)) jArray.appendTag(new NBTTagString(uuid.toString()));
		});
		
		nbt.setTag("completeUsers", jArray);
		
		return nbt;
	}
 
	@Override
	public void readProgressFromNBT(NBTTagCompound nbt, boolean merge)
	{
		if(!merge) completeUsers.clear();
		NBTTagList cList = nbt.getTagList("completeUsers", 8);
		for(int i = 0; i < cList.tagCount(); i++)
		{
			try
			{
				completeUsers.add(UUID.fromString(cList.getStringTagAt(i)));
			} catch(Exception e)
			{
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest)
	{
	    return new PanelTaskRCLocate(rect, this);
	}
 
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest)
	{
		return null;
	}
}