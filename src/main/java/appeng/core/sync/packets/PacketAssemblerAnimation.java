/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.core.sync.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import appeng.api.storage.data.IAEItemStack;
import appeng.client.EffectType;
import appeng.core.CommonHelper;
import appeng.core.sync.AppEngPacket;
import appeng.core.sync.network.INetworkInfo;
import appeng.util.item.AEItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketAssemblerAnimation extends AppEngPacket
{

	final public int x, y, z;
	final public byte rate;
	final public IAEItemStack is;

	// automatic.
	public PacketAssemblerAnimation(ByteBuf stream) throws IOException {
		x = stream.readInt();
		y = stream.readInt();
		z = stream.readInt();
		rate = stream.readByte();
		is = AEItemStack.loadItemStackFromPacket( stream );
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void clientPacketData(INetworkInfo network, AppEngPacket packet, EntityPlayer player)
	{
		double d0 = 0.5d;// + ((double) (Platform.getRandomFloat() - 0.5F) * 0.26D);
		double d1 = 0.5d;// + ((double) (Platform.getRandomFloat() - 0.5F) * 0.26D);
		double d2 = 0.5d;// + ((double) (Platform.getRandomFloat() - 0.5F) * 0.26D);

		CommonHelper.proxy.spawnEffect( EffectType.Assembler, player.getEntityWorld(), x + d0, y + d1, z + d2, this );
	}

	// api
	public PacketAssemblerAnimation(int x, int y, int z, byte rate, IAEItemStack is) throws IOException {

		ByteBuf data = Unpooled.buffer();

		data.writeInt( getPacketID() );
		data.writeInt( this.x = x );
		data.writeInt( this.y = y );
		data.writeInt( this.z = z );
		data.writeByte( this.rate = rate );
		is.writeToPacket( data );
		this.is = is;

		configureWrite( data );
	}
}
