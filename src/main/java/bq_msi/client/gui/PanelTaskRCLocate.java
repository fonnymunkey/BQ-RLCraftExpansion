package bq_msi.client.gui;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.utils.QuestTranslation;
import bq_msi.tasks.TaskRCLocate;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;

public class PanelTaskRCLocate extends CanvasEmpty
{
    private final TaskRCLocate task;
    
    public PanelTaskRCLocate(IGuiRect rect, TaskRCLocate task)
    {
        super(rect);
        this.task = task;
    }
    
    @Override
    public void initPanel()
    {
        super.initPanel();
        
        String desc = QuestTranslation.translate(task.name);
        
        if(!task.hideInfo)
        {
            desc += " (" + getDimName(task.dim) + ")";
            
            if(!StringUtils.isNullOrEmpty(task.structure))
            {
                desc += "\n" + QuestTranslation.translate("bq_msi.gui.structure", task.structure);
            }
        }
        
        if(task.isComplete(QuestingAPI.getQuestingUUID(Minecraft.getMinecraft().player)))
        {
            desc += "\n" + TextFormatting.BOLD + TextFormatting.GREEN + QuestTranslation.translate("bq_msi.gui.found");
        } else
        {
            desc += "\n" + TextFormatting.BOLD + TextFormatting.RED + QuestTranslation.translate("bq_msi.gui.undiscovered");
        }
        
        this.addPanel(new PanelTextBox(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0), desc).setColor(PresetColor.TEXT_MAIN.getColor()));
    }
    
    private static String getDimName(int dim)
	{
	    try
        {
            return DimensionType.getById(dim).getName();
        } catch(Exception e)
        {
            return "?";
        }
	}
}