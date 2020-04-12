package bq_rlc.handlers;

import ivorius.reccomplex.events.StructureGenerationEventLite;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.World;
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
		String regionName = convertBoxToRegion(boundingBox);

		int minX = boundingBox.minX;
		int minZ = boundingBox.minZ;
		int maxX = boundingBox.maxX;
		int maxZ = boundingBox.maxZ;
		int dimension = world.provider.getDimension();
		
		//Todo: Whitelist or Blacklist?
		
		DataWrite(regionName, dimension, structureName, minX, maxX, minZ, maxZ);		
	}
	
	public String convertBoxToRegion(StructureBoundingBox boundingBox)
	{	
		int regionX = Math.round((boundingBox.minX + boundingBox.maxX)/1024);
		int regionZ = Math.round((boundingBox.minZ + boundingBox.maxZ)/1024);
		String regionName = ("r." + Integer.toString(regionX) + "." + Integer.toString(regionZ));
		return regionName;
	}
	
	public static void DataWrite(String regionName, int dimension, String structureName, int minX, int maxX, int minZ, int maxZ) {
		File fileName = new File(DimensionManager.getCurrentSaveRootDirectory() + File.separator + "data" + File.separator + "QuestStructure" + File.separator + dimension + File.separator + regionName + ".txt");
		fileName.getParentFile().mkdirs();

		try {
			FileWriter fileWriter = new FileWriter(fileName,true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			
			bufferedWriter.write(structureName + "," + minX + "," + maxX + "," + minZ + "," + maxZ);
			bufferedWriter.newLine();
			bufferedWriter.close();
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
}