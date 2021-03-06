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
package com.aionemu.loginserver.network.gs.clientpackets;

import com.aionemu.loginserver.controller.PremiumController;
import com.aionemu.loginserver.network.gs.GsClientPacket;

/**
 * @author KID
 */
public class CM_PREMIUM_CONTROL extends GsClientPacket
{
	private int accountId;
	private int requestId;
	private long requiredCost;
	private byte serverId;
	
	@Override
	protected void readImpl()
	{
		accountId = readD();
		requestId = readD();
		requiredCost = readQ();
		serverId = (byte) readC();
	}
	
	@Override
	protected void runImpl()
	{
		PremiumController.getController().requestBuy(accountId, requestId, requiredCost, serverId);
	}
}
