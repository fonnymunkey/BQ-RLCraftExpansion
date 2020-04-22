package bq_msi.core.proxies;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import betterquesting.api2.registry.IRegistry;
import bq_msi.handlers.*;
import bq_msi.tasks.factory.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

public class CommonProxy
{
	public boolean isClient()
	{
		return false;
	}
	
	public void registerHandlers()
	{
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		
		if(Loader.isModLoaded("reccomplex")) MinecraftForge.EVENT_BUS.register(new RecurrentHandler());
	}
	
	public void registerRenderers()
	{
	}
	
	public void registerExpansion()
	{
		IRegistry<IFactoryData<ITask, NBTTagCompound>, ITask> taskReg = QuestingAPI.getAPI(ApiReference.TASK_REG);
		
		taskReg.register(FactoryTaskMultiblock.INSTANCE);
		if(Loader.isModLoaded("reccomplex")) taskReg.register(FactoryTaskRCLocate.INSTANCE);
	}
}