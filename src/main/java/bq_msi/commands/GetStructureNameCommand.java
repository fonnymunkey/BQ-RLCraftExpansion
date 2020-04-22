package bq_msi.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.DimensionManager;

public class GetStructureNameCommand extends CommandBase{
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
		if(sender.getCommandSenderEntity() == null || !(sender.getCommandSenderEntity() instanceof EntityPlayerMP) || sender.getEntityWorld().isRemote) return;
		
		EntityPlayerMP playerMP = (EntityPlayerMP)sender.getCommandSenderEntity();
		
		if(params.length == 3 && isStringInt(params[0]) && isStringInt(params[1]) && isStringInt(params[2])) {
			int dim = Integer.parseInt(params[0]);
			int x = Integer.parseInt(params[1]);
			int z = Integer.parseInt(params[2]);
			
			String message = DataRead(convertPosToRegion(x,z), dim, x, z);
			TextComponentString text = new TextComponentString(message);
			text.getStyle().setColor(TextFormatting.BLUE);
			sender.sendMessage(text);
		}
		else if(params.length == 0) {
			int dim = playerMP.dimension;
			int x = playerMP.getPosition().getX();
			int z = playerMP.getPosition().getZ();
			
			String message = DataRead(convertPosToRegion(x,z), dim, x, z);
			TextComponentString text = new TextComponentString(message);
			text.getStyle().setColor(TextFormatting.BLUE);
			sender.sendMessage(text);
		}
	}	
	
	@Override
	public String getName() {
		return "getStructureName";
	}
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "command.getStructureName.usage";
	}
	
	boolean isStringInt(String a) {
		try {
			Integer.parseInt(a);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public String convertPosToRegion(int xPos, int zPos)
	{	
		int regionX = Math.round(xPos/512);
		int regionZ = Math.round(zPos/512);
		String regionName = ("r." + Integer.toString(regionX) + "." + Integer.toString(regionZ));
		return regionName;
	}
	
	public String DataRead(String regionName, int dim, int xPos, int zPos) {
		File fileName = new File(DimensionManager.getCurrentSaveRootDirectory() + File.separator + "data" + File.separator + "QuestStructure" + File.separator + dim + File.separator + regionName + ".txt");
		
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			
			while((line = bufferedReader.readLine()) != null) {
				String[] tokens = line.split(",");
				int minX = Integer.parseInt(tokens[1]);
				int maxX = Integer.parseInt(tokens[2]);
				int minZ = Integer.parseInt(tokens[3]);
				int maxZ = Integer.parseInt(tokens[4]);
					
					
				if((minX < xPos) && (xPos < maxX) && (minZ < zPos) && (zPos < maxZ)) {
					bufferedReader.close();
					return "The Recurrent Complex structure at this position is " + tokens[0];
				}
			}
			bufferedReader.close();
			return "There is no Recurrent Complex structure here.";
		}
		catch(IOException ex) {
			if(ex instanceof FileNotFoundException) {
				System.out.println("BQ_RLC tried to access non-existant file" + ex);
				return "This region has not been created yet."; }
			else {
				System.out.println(ex);
				return "Error while processing data file."; }
		}
	}
}
