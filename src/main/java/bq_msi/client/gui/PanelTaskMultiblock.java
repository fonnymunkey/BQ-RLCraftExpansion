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
import bq_msi.tasks.TaskMultiblock;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

public class PanelTaskMultiblock extends CanvasEmpty
{
    private final TaskMultiblock task;
    
    public PanelTaskMultiblock(IGuiRect rect, TaskMultiblock task)
    {
        super(rect);
        this.task = task;
    }
    
    @Override
    public void initPanel()
    {
        super.initPanel();
        
        String desc = QuestTranslation.translate(task.name);
        
        if(task.isComplete(QuestingAPI.getQuestingUUID(Minecraft.getMinecraft().player)))
        {
            desc += "\n" + TextFormatting.BOLD + TextFormatting.GREEN + QuestTranslation.translate("bq_msi.gui.created");
        } else
        {
            desc += "\n" + TextFormatting.BOLD + TextFormatting.RED + QuestTranslation.translate("bq_msi.gui.noncreated");
        }
        
        this.addPanel(new PanelTextBox(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0), desc).setColor(PresetColor.TEXT_MAIN.getColor()));
    }
}