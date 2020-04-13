package bq_rlc.core.proxies;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import betterquesting.api2.registry.IRegistry;
import bq_rlc.tasks.factory.*;
import bq_rlc.handlers.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
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
		
		if(Loader.isModLoaded("reccomplex")) taskReg.register(FactoryTaskRCLocate.INSTANCE);
	}
}