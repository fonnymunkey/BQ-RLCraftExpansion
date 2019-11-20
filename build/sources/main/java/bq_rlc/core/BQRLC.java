package bq_rlc.core;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;
import betterquesting.core.BetterQuesting;
import betterquesting.network.PacketTypeRegistry;
import betterquesting.quests.tasks.TaskRegistry;
import bq_rlc.core.proxies.CommonProxy;
import bq_rlc.tasks.TaskRCLocate;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = BQRLC.MODID, version = BQRLC.VERSION, name = BQRLC.NAME, guiFactory = "bq_rlc.handlers.ConfigGuiFactory")
public class BQRLC
{
    public static final String MODID = "bq_rlc";
    public static final String VERSION = "BQ_RLC_VER";
    public static final String NAME = "RLCraftExpansion";
    public static final String PROXY = "bq_rlc.core.proxies";
    public static final String CHANNEL = "BQ_RLC";
	
	@Instance(MODID)
	public static BQRLC instance;
	
	@SidedProxy(clientSide = PROXY + ".ClientProxy", serverSide = PROXY + ".CommonProxy")
	public static CommonProxy proxy;
	public SimpleNetworkWrapper network;
	public static Logger logger;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	logger = event.getModLog();
    	network = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);
    	
    	proxy.registerHandlers();
    	
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.registerThemes();
    	
    	TaskRegistry.RegisterTask(TaskRCLocate.class, new ResourceLocation(MODID + ":rc_locate"));
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}
