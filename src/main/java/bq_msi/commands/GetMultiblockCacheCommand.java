package bq_msi.commands;

import bq_msi.tasks.TaskMultiblock;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class GetMultiblockCacheCommand extends CommandBase{
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
		if(sender.getCommandSenderEntity() == null || !(sender.getCommandSenderEntity() instanceof EntityPlayerMP) || sender.getEntityWorld().isRemote) return;
				
		String message = TaskMultiblock.getHashmapContents();
		TextComponentString text = new TextComponentString(message);
		text.getStyle().setColor(TextFormatting.BLUE);
		sender.sendMessage(text);
	}	
	
	@Override
	public String getName() {
		return "getMultiblockCache";
	}
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "command.getMultiblockCache.usage";
	}
}
