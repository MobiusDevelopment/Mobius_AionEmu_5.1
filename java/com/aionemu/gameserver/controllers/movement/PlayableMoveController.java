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
package com.aionemu.gameserver.controllers.movement;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.taskmanager.tasks.PlayerMoveTaskManager;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.aionemu.gameserver.world.World;

/**
 * @author ATracer base class for summon & player move controller
 */
public abstract class PlayableMoveController<T extends Creature>extends CreatureMoveController<T>
{
	
	private boolean sendMovePacket = true;
	private int movementHeading = -1;
	
	public float vehicleX;
	public float vehicleY;
	public float vehicleZ;
	public int vehicleSpeed;
	
	public float vectorX;
	public float vectorY;
	public float vectorZ;
	public byte glideFlag;
	public int unk1;
	public int unk2;
	
	public PlayableMoveController(T owner)
	{
		super(owner);
	}
	
	@Override
	public void startMovingToDestination()
	{
		updateLastMove();
		if (owner.canPerformMove())
		{
			if (isControlled() && started.compareAndSet(false, true))
			{
				movementMask = MovementMask.NPC_STARTMOVE;
				sendForcedMovePacket();
				PlayerMoveTaskManager.getInstance().addPlayer(owner);
			}
		}
	}
	
	private final boolean isControlled()
	{
		return owner.getEffectController().isUnderFear();
	}
	
	private void sendForcedMovePacket()
	{
		PacketSendUtility.broadcastPacketAndReceive(owner, new SM_MOVE(owner));
		sendMovePacket = false;
	}
	
	@Override
	public void moveToDestination()
	{
		if (!owner.canPerformMove())
		{
			if (started.compareAndSet(true, false))
			{
				setAndSendStopMove(owner);
			}
			updateLastMove();
			return;
		}
		
		if (sendMovePacket && isControlled())
		{
			sendForcedMovePacket();
		}
		
		final float x = owner.getX();
		final float y = owner.getY();
		final float z = owner.getZ();
		
		final float currentSpeed = StatFunctions.getMovementModifier(owner, StatEnum.SPEED, owner.getGameStats().getMovementSpeedFloat());
		float futureDistPassed = (currentSpeed * (System.currentTimeMillis() - lastMoveUpdate)) / 1000f;
		final float dist = (float) MathUtil.getDistance(x, y, z, targetDestX, targetDestY, targetDestZ);
		
		if (dist == 0)
		{
			return;
		}
		
		if (futureDistPassed > dist)
		{
			futureDistPassed = dist;
		}
		
		final float distFraction = futureDistPassed / dist;
		final float newX = ((targetDestX - x) * distFraction) + x;
		final float newY = ((targetDestY - y) * distFraction) + y;
		final float newZ = ((targetDestZ - z) * distFraction) + z;
		
		/*
		 * if ((movementMask & MovementMask.MOUSE) == 0) { targetDestX = newX + vectorX; targetDestY = newY + vectorY; targetDestZ = newZ + vectorZ; }
		 */
		
		World.getInstance().updatePosition(owner, newX, newY, newZ, heading, false);
		updateLastMove();
	}
	
	@Override
	public void abortMove()
	{
		started.set(false);
		PlayerMoveTaskManager.getInstance().removePlayer(owner);
		targetDestX = owner.getX();
		targetDestY = owner.getY();
		targetDestZ = owner.getZ();
		setAndSendStopMove(owner);
	}
	
	@Override
	public void setNewDirection(float x, float y, float z)
	{
		if ((targetDestX != x) || (targetDestY != y) || (targetDestZ != z))
		{
			sendMovePacket = true;
		}
		targetDestX = x;
		targetDestY = y;
		targetDestZ = z;
		
		final float h = MathUtil.calculateAngleFrom(owner.getX(), owner.getY(), targetDestX, targetDestY);
		if (h != 0)
		{
			int value = (int) (((heading * 3) - h) / 45);
			if (value < 0)
			{
				value += 8;
			}
			if (movementHeading != value)
			{
				movementHeading = value;
			}
		}
	}
	
	public int getMovementHeading()
	{
		if (!isInMove())
		{
			return -1;
		}
		return movementHeading;
	}
	
}