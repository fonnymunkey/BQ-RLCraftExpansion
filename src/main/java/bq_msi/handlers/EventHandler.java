package bq_msi.handlers;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_msi.tasks.*;

import java.util.List;

public class EventHandler
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRightClickBlock(RightClickBlock event)
    {
        if(event.getEntityPlayer() == null || event.getEntityLiving().world.isRemote || event.isCanceled()) return;
        
		EntityPlayer player = event.getEntityPlayer();
        ParticipantInfo pInfo = new ParticipantInfo(player);
        
		List<DBEntry<IQuest>> actQuest = QuestingAPI.getAPI(ApiReference.QUEST_DB).bulkLookup(pInfo.getSharedQuests());
		
		IBlockState state = player.world.getBlockState(event.getPos());
		
		for(DBEntry<IQuest> entry : actQuest)
		{
		    for(DBEntry<ITask> task : entry.getValue().getTasks().getEntries())
            {
                if(task.getValue() instanceof TaskMultiblock) ((TaskMultiblock)task.getValue()).onInteract(pInfo, entry, event.getHand(), event.getItemStack(), state, event.getPos());
            }
		}
    }
    
	@SubscribeEvent
    public void onEntityLiving(LivingUpdateEvent event)
    {
        if(!(event.getEntityLiving() instanceof EntityPlayer) || event.getEntityLiving().world.isRemote || event.getEntityLiving().ticksExisted%20 != 0 || QuestingAPI.getAPI(ApiReference.SETTINGS).getProperty(NativeProps.EDIT_MODE)) return;
        
        EntityPlayer player = (EntityPlayer)event.getEntityLiving();
        ParticipantInfo pInfo = new ParticipantInfo(player);
        
		List<DBEntry<IQuest>> actQuest = QuestingAPI.getAPI(ApiReference.QUEST_DB).bulkLookup(pInfo.getSharedQuests());
		
		for(DBEntry<IQuest> entry : actQuest)
		{
		    for(DBEntry<ITask> task : entry.getValue().getTasks().getEntries())
            {
                if(task.getValue() instanceof ITaskTickable)
                {
                    ((ITaskTickable)task.getValue()).tickTask(pInfo, entry);
                } 
            }
		}
    }
}