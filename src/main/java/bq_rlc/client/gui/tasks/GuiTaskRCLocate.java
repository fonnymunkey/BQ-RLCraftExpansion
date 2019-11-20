package bq_rlc.client.gui.tasks;

import java.awt.Color;
import java.text.DecimalFormat;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import betterquesting.client.gui.GuiQuesting;
import betterquesting.client.gui.misc.GuiEmbedded;
import betterquesting.quests.QuestInstance;
import betterquesting.utils.RenderUtils;
import bq_rlc.tasks.TaskRCLocate;

public class GuiTaskRCLocate extends GuiEmbedded
{
	QuestInstance quest;
	TaskRCLocate task;
	
	public GuiTaskRCLocate(QuestInstance quest, TaskRCLocate task, GuiQuesting screen, int posX, int posY, int sizeX, int sizeY)
	{
		super(screen, posX, posY, sizeX, sizeY);
		this.task = task;
		this.quest = quest;
	}
}
