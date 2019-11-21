package bq_rlc.core.proxies;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import betterquesting.api2.registry.IRegistry;
import bq_rlc.tasks.factory.*;
import net.minecraft.nbt.NBTTagCompound;

public class CommonProxy
{
	public boolean isClient()
	{
		return false;
	}
	
	public void registerHandlers()
	{
	}
	
	public void registerRenderers()
	{
	}
	
	public void registerExpansion()
	{
		IRegistry<IFactoryData<ITask, NBTTagCompound>, ITask> taskReg = QuestingAPI.getAPI(ApiReference.TASK_REG);
		taskReg.register(FactoryTaskRCLocate.INSTANCE);
	}
}