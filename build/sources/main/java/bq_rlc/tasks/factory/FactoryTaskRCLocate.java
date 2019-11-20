package bq_rlc.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import bq_rlc.core.BQRLC;
import bq_rlc.tasks.TaskRCLocate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskRCLocate implements IFactoryData<ITask, NBTTagCompound>
{
	public static final FactoryTaskRCLocate INSTANCE = new FactoryTaskRCLocate();
	
	@Override
	public ResourceLocation getRegistryName()
	{
		return new ResourceLocation(BQRLC.MODID + ":location");
	}

	@Override
	public TaskRCLocate createNew()
	{
		return new TaskRCLocate();
	}

	@Override
	public TaskRCLocate loadFromData(NBTTagCompound json)
	{
		TaskRCLocate task = new TaskRCLocate();
		task.readFromNBT(json);
		return task;
	}
	
}