package bq_msi.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import bq_msi.core.BQMSI;
import bq_msi.tasks.TaskRCLocate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskRCLocate implements IFactoryData<ITask, NBTTagCompound>
{
	public static final FactoryTaskRCLocate INSTANCE = new FactoryTaskRCLocate();
	
	@Override
	public ResourceLocation getRegistryName()
	{
		return new ResourceLocation(BQMSI.MODID + ":location");
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