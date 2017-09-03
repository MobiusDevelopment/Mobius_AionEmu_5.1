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

import java.util.Set;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author lord_rex
 */
public class SM_RECIPE_LIST extends AionServerPacket
{
	
	private final Integer[] recipeIds;
	private final int count;
	
	public SM_RECIPE_LIST(Set<Integer> recipeIds)
	{
		this.recipeIds = recipeIds.toArray(new Integer[recipeIds.size()]);
		count = recipeIds.size();
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeH(count);
		for (final int id : recipeIds)
		{
			writeD(id);
			writeC(0);
		}
	}
}