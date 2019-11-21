package bq_rlc.core;

import org.apache.logging.log4j.Logger;
import bq_rlc.core.proxies.CommonProxy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = BQRLC.MODID, version = BQRLC.VERSION, name = BQRLC.NAME)
public class BQRLC
{
    public static final String MODID = "bq_rlc";
    public static final String VERSION = "BQ_RLC_VER";
    public static final String NAME = "RLCraftExpansion";
    public static final String PROXY = "bq_rlc.core.proxies";
    public static final String CHANNEL = "BQRLC";
	
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
        ModContainer modContainer = Loader.instance().getIndexedModList().get("bq_rlc");
        if(modContainer != null && modContainer.getMod() instanceof BQRLC)
        {
            BQRLC modInstance = (BQRLC)modContainer.getMod();
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
