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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Ranastic
 */
public class SM_A_STATION_MOVE extends AionServerPacket
{
	private final int currentServerId;
	private final int newServerId;
	private final int mapId;
	
	public SM_A_STATION_MOVE(int currentServer, int newServerId, int mapId)
	{
		currentServerId = currentServer;
		this.newServerId = newServerId;
		this.mapId = mapId;
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(newServerId);
		writeD(currentServerId);
		writeC(0);
		writeD(mapId);
	}
}