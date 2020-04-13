package bq_rlc.handlers;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraft.entity.player.EntityPlayer;
import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_rlc.tasks.*;
import java.util.List;

public class EventHandler
{
	@SubscribeEvent
    public void onEntityLiving(LivingUpdateEvent event)
    {
        if(!(event.getEntityLiving() instanceof EntityPlayer) || event.getEntityLiving().world.isRemote || event.getEntityLiving().ticksExisted%20 != 0 || QuestingAPI.getAPI(ApiReference.SETTINGS).getProperty(NativeProps.EDIT_MODE)) return;
        
        EntityPlayer player = (EntityPlayer)event.getEntityLiving();
        ParticipantInfo pInfo = new ParticipantInfo(player);
        
		List<DBEntry<IQuest>> actQuest = QuestingAPI.getAPI(ApiReference.QUEST_DB).bulkLookup(pInfo.getSharedQuests());
		
		//System.out.println("Living update running");
		
		for(DBEntry<IQuest> entry : actQuest)
		{
		    for(DBEntry<ITask> task : entry.getValue().getTasks().getEntries())
            {
                if(task.getValue() instanceof ITaskTickable)
                {
                	//System.out.println("Starting tick");
                    ((ITaskTickable)task.getValue()).tickTask(pInfo, entry);
                } //else if(task.getValue() instanceof TaskTrigger)
                //{
                //   ((TaskTrigger)task.getValue()).checkSetup(player, entry);
                //}
            }
		}
    }
}