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

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This class is used to send to client packets generated by admins with command //fsc
 * @author Luno
 * @see com.aionemu.gameserver.utils.chathandlers.admincommands.SendFakeServerPacket
 */
public class SM_CUSTOM_PACKET extends AionServerPacket
{
	
	/** Enumeration of types of packet elements. */
	public static enum PacketElementType
	{
		D('d')
		{
			
			@Override
			public void write(SM_CUSTOM_PACKET packet, String value)
			{
				packet.writeD(Integer.decode(value));
			}
		},
		B('b')
		{
			
			@Override
			public void write(SM_CUSTOM_PACKET packet, String value)
			{
				packet.writeB(new byte[Integer.valueOf(value)]);
			}
		},
		BB('B')
		{
			
			@Override
			public void write(SM_CUSTOM_PACKET packet, String value)
			{
				packet.writeB(value);
			}
		},
		H('h')
		{
			
			@Override
			public void write(SM_CUSTOM_PACKET packet, String value)
			{
				packet.writeH(Integer.decode(value));
			}
		},
		C('c')
		{
			
			@Override
			public void write(SM_CUSTOM_PACKET packet, String value)
			{
				packet.writeC(Integer.decode(value));
			}
		},
		F('f')
		{
			
			@Override
			public void write(SM_CUSTOM_PACKET packet, String value)
			{
				packet.writeF(Float.valueOf(value));
			}
		},
		DF('e')
		{
			
			@Override
			public void write(SM_CUSTOM_PACKET packet, String value)
			{
				packet.writeDF(Double.valueOf(value));
			}
		},
		Q('q')
		{
			
			@Override
			public void write(SM_CUSTOM_PACKET packet, String value)
			{
				packet.writeQ(Long.decode(value));
			}
		},
		S('s')
		{
			
			@Override
			public void write(SM_CUSTOM_PACKET packet, String value)
			{
				packet.writeS(value);
			}
		};
		
		private final char code;
		
		private PacketElementType(char code)
		{
			this.code = code;
		}
		
		public static PacketElementType getByCode(char code)
		{
			for (final PacketElementType type : values())
			{
				if (type.code == code)
				{
					return type;
				}
			}
			return null;
		}
		
		/**
		 * Writes <tt>value</tt> to buffer according to the ElementType
		 * @param packet packet instance
		 * @param buf packet write buffer
		 * @param value element value
		 */
		public abstract void write(SM_CUSTOM_PACKET packet, String value);
	}
	
	public static class PacketElement
	{
		
		private final PacketElementType type;
		private final String value;
		
		public PacketElement(PacketElementType type, String value)
		{
			this.type = type;
			this.value = value;
		}
		
		/**
		 * Writes value stored in this PacketElement into buffer <tt>buf</tt>
		 * @param packet packet instance.
		 * @param buf packet write buffer.
		 */
		public void writeValue(SM_CUSTOM_PACKET packet)
		{
			type.write(packet, value);
		}
	}
	
	private final List<PacketElement> elements = new ArrayList<>();
	
	public SM_CUSTOM_PACKET(int opcode)
	{
		super();
		setOpcode(opcode);
	}
	
	/**
	 * Add an element to this packet.
	 * @param packetElement
	 */
	public void addElement(PacketElement packetElement)
	{
		elements.add(packetElement);
	}
	
	/**
	 * Add packet element.
	 * @param type
	 * @param value
	 */
	public void addElement(PacketElementType type, String value)
	{
		elements.add(new PacketElement(type, value));
	}
	
	/** {@inheritDoc} */
	@Override
	public void writeImpl(AionConnection con)
	{
		for (final PacketElement el : elements)
		{
			el.writeValue(this);
		}
	}
}