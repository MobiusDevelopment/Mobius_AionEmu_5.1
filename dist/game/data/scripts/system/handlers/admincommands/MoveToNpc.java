/*
 * This file is part of the Aion-Emu project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package system.handlers.admincommands;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author MrPoke, lord_rex and ginho1
 */
public class MoveToNpc extends AdminCommand
{
	
	public MoveToNpc()
	{
		super("movetonpc");
	}
	
	@Override
	public void execute(Player player, String... params)
	{
		int npcId = 0;
		String message = "";
		try
		{
			npcId = Integer.valueOf(params[0]);
		}
		catch (final ArrayIndexOutOfBoundsException e)
		{
			onFail(player, e.getMessage());
		}
		catch (final NumberFormatException e)
		{
			String npcName = "";
			
			for (final String param : params)
			{
				npcName += param + " ";
			}
			npcName = npcName.substring(0, npcName.length() - 1);
			
			for (final NpcTemplate template : DataManager.NPC_DATA.getNpcData().valueCollection())
			{
				if (template.getName().equalsIgnoreCase(npcName))
				{
					if (npcId == 0)
					{
						npcId = template.getTemplateId();
					}
					else
					{
						if (message.equals(""))
						{
							message += "Found others (" + npcName + "): \n";
						}
						message += "Id: " + template.getTemplateId() + "\n";
					}
				}
			}
			if (npcId == 0)
			{
				PacketSendUtility.sendMessage(player, "NPC " + npcName + " cannot be found");
			}
		}
		
		if (npcId > 0)
		{
			message = "Teleporting to Npc: " + npcId + "\n" + message;
			PacketSendUtility.sendMessage(player, message);
			TeleportService2.teleportToNpc(player, npcId);
		}
	}
	
	@Override
	public void onFail(Player player, String message)
	{
		PacketSendUtility.sendMessage(player, "syntax //movetonpc <npc_id|npc name>");
	}
}