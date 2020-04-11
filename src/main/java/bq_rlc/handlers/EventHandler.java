package bq_rlc.handlers;

import java.io.BufferedWriter;
import java.io.IOException;

import ivorius.reccomplex.events.StructureGenerationEventLite;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import java.io.*;

public class EventHandler
{
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onStructureGenerationLitePost(StructureGenerationEventLite.Post event)
	{
		World world = event.getWorld();
		if(world.isRemote) return; 
		
		StructureBoundingBox boundingBox = event.getBoundingBox();
		String structureName = event.getStructureName();	
		String regionName = convertBoxToRegion(world, boundingBox);
		
		int minX = boundingBox.minX;
		int minY = boundingBox.minY;
		int maxX = boundingBox.maxX;
		int maxY = boundingBox.maxY;
		
		DataWrite(regionName, structureName, minX, maxX, minY, maxY);
		//int dimension = world.provider.getDimension();
	}
	
	public String convertBoxToRegion(World world, StructureBoundingBox boundingBox)
	{	
		int regionX = Math.round(boundingBox.minX + boundingBox.maxX)/512;
		int regionZ = Math.round(boundingBox.minZ + boundingBox.maxZ)/512;
		String regionName = ("r." + Integer.toString(regionX) + "." + Integer.toString(regionZ));
		return regionName;
	}
	
	public static void DataWrite(String regionName, String structureName, int minX, int maxX, int minY, int maxY) {
		File fileName = new File(DimensionManager.getCurrentSaveRootDirectory() + "/data/QuestStructure/" + regionName + ".txt");
		fileName.getParentFile().mkdirs();

		try {
			FileWriter fileWriter = new FileWriter(fileName,true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			
			bufferedWriter.write(structureName + "," + minX + "," + maxX + "," + minY + "," + maxY);
			bufferedWriter.close();
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
}