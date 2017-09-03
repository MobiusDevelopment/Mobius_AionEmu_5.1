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
package com.aionemu.gameserver.model.rift;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.rift.RiftTemplate;

/**
 * @author Source
 */
public class RiftLocation
{
	
	private boolean opened;
	protected RiftTemplate template;
	private final List<VisibleObject> spawned = new ArrayList<>();
	
	public RiftLocation()
	{
	}
	
	public RiftLocation(RiftTemplate template)
	{
		this.template = template;
	}
	
	public int getId()
	{
		return template.getId();
	}
	
	public int getWorldId()
	{
		return template.getWorldId();
	}
	
	public boolean isOpened()
	{
		return opened;
	}
	
	public void setOpened(boolean state)
	{
		opened = state;
	}
	
	public List<VisibleObject> getSpawned()
	{
		return spawned;
	}
	
}