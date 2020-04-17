package bq_msi.core;

import org.apache.logging.log4j.Logger;

import bq_msi.core.proxies.CommonProxy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = BQMSI.MODID, version = BQMSI.VERSION, name = BQMSI.NAME, dependencies = "after:betterquesting;after:reccomplex")
public class BQMSI
{
    public static final String MODID = "bq_msi";
    public static final String VERSION = "1.0.2";
    public static final String NAME = "BQ_Multiblock_Structure_Integration";
    public static final String PROXY = "bq_msi.core.proxies";
    public static final String CHANNEL = "BQMSI";
	
	@Instance(MODID)
	public static BQMSI instance;
	
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
        ModContainer modContainer = Loader.instance().getIndexedModList().get("bq_msi");
        if(modContainer != null && modContainer.getMod() instanceof BQMSI)
        {
            BQMSI modInstance = (BQMSI)modContainer.getMod();
            // DO THINGS...
        }
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if(Loader.isModLoaded("betterquesting"))
        {
            proxy.registerExpansion();
        }
    }
}
