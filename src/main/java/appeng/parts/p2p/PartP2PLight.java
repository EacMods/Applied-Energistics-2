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

package appeng.parts.p2p;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import appeng.api.config.TunnelType;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.core.settings.TickRates;
import appeng.me.GridAccessException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PartP2PLight extends PartP2PTunnel<PartP2PLight> implements IGridTickable
{

	public PartP2PLight(ItemStack is) {
		super( is );
	}

	@Override
	public TunnelType getTunnelType()
	{
		return TunnelType.LIGHT;
	}

	int lastValue = 0;
	float opacity = -1;

	public void setLightLevel(int out)
	{
		lastValue = out;
		getHost().markForUpdate();
	}

	@Override
	public int getLightLevel()
	{
		if ( output && isPowered() )
			return blockLight( lastValue );

		return 0;
	}

	private int blockLight(int emit)
	{
		if ( opacity < 0 )
		{
			TileEntity te = this.getTile();
			opacity = 255 - te.getWorldObj().getBlockLightOpacity( te.xCoord + side.offsetX, te.yCoord + side.offsetY, te.zCoord + side.offsetZ );
		}

		return (int) (emit * (opacity / 255.0f));
	}

	@Override
	public void chanRender(MENetworkChannelsChanged c)
	{
		onTunnelNetworkChange();
		super.chanRender( c );
	}

	@Override
	public void powerRender(MENetworkPowerStatusChange c)
	{
		onTunnelNetworkChange();
		super.powerRender( c );
	}

	@Override
	public void onTunnelNetworkChange()
	{
		if ( output )
		{
			PartP2PLight src = getInput();
			if ( src != null && src.proxy.isActive() )
				setLightLevel( src.lastValue );
			else
				getHost().markForUpdate();
		}
		else
			doWork();
	}

	@Override
	public void onTunnelConfigChange()
	{
		onTunnelNetworkChange();
	}

	@Override
	public void onNeighborChanged()
	{
		opacity = -1;

		doWork();

		if ( output )
			getHost().markForUpdate();
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT( tag );
		tag.setFloat( "opacity", opacity );
		tag.setInteger( "lastValue", lastValue );
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT( tag );
		if ( tag.hasKey( "opacity" ) )
			opacity = tag.getFloat( "opacity" );
		lastValue = tag.getInteger( "lastValue" );
	}

	@Override
	public boolean readFromStream(ByteBuf data) throws IOException
	{
		super.readFromStream( data );
		lastValue = data.readInt();
		output = lastValue > 0;
		return false;
	}

	@Override
	public void writeToStream(ByteBuf data) throws IOException
	{
		super.writeToStream( data );
		data.writeInt( output ? lastValue : 0 );
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getTypeTexture()
	{
		return Blocks.quartz_block.getBlockTextureFromSide( 0 );
	}

	@Override
	public TickingRequest getTickingRequest(IGridNode node)
	{
		return new TickingRequest( TickRates.LightTunnel.min, TickRates.LightTunnel.max, false, false );
	}

	private boolean doWork()
	{
		if ( output )
			return false;

		TileEntity te = getTile();
		World w = te.getWorldObj();

		int newLevel = w.getBlockLightValue( te.xCoord + side.offsetX, te.yCoord + side.offsetY, te.zCoord + side.offsetZ );

		if ( lastValue != newLevel && proxy.isActive() )
		{
			lastValue = newLevel;
			try
			{
				for (PartP2PLight out : getOutputs())
					out.setLightLevel( lastValue );
			}
			catch (GridAccessException e)
			{
				// :P
			}
			return true;
		}
		return false;
	}

	public float getPowerDrainPerTick()
	{
		return 0.5f;
	}

	@Override
	public TickRateModulation tickingRequest(IGridNode node, int TicksSinceLastCall)
	{
		return doWork() ? TickRateModulation.FASTER : TickRateModulation.SLOWER;
	}
}
