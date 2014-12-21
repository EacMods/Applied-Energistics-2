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

package appeng.tile.misc;

import io.netty.buffer.ByteBuf;

import java.util.EnumSet;

import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.implementations.tiles.ICrystalGrowthAccelerator;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.util.AECableType;
import appeng.me.GridAccessException;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkTile;
import appeng.util.Platform;

public class TileQuartzGrowthAccelerator extends AENetworkTile implements IPowerChannelState, ICrystalGrowthAccelerator
{

	public boolean hasPower = false;

	@MENetworkEventSubscribe
	public void onPower(MENetworkPowerStatusChange ch)
	{
		markForUpdate();
	}

	@Override
	public AECableType getCableConnectionType(ForgeDirection dir)
	{
		return AECableType.COVERED;
	}

	@TileEvent(TileEventType.NETWORK_READ)
	public boolean readFromStream_TileQuartzGrowthAccelerator(ByteBuf data)
	{
		boolean hadPower = hasPower;
		hasPower = data.readBoolean();
		return hasPower != hadPower;
	}

	@TileEvent(TileEventType.NETWORK_WRITE)
	public void writeToStream_TileQuartzGrowthAccelerator(ByteBuf data)
	{
		try
		{
			data.writeBoolean( gridProxy.getEnergy().isNetworkPowered() );
		}
		catch (GridAccessException e)
		{
			data.writeBoolean( false );
		}
	}

	public TileQuartzGrowthAccelerator() {
		gridProxy.setValidSides( EnumSet.noneOf( ForgeDirection.class ) );
		gridProxy.setFlags();
		gridProxy.setIdlePowerUsage( 8 );
	}

	@Override
	public void setOrientation(ForgeDirection inForward, ForgeDirection inUp)
	{
		super.setOrientation( inForward, inUp );
		gridProxy.setValidSides( EnumSet.of( getUp(), getUp().getOpposite() ) );
	}

	@Override
	public boolean isPowered()
	{
		if ( Platform.isServer() )
		{
			try
			{
				return gridProxy.getEnergy().isNetworkPowered();
			}
			catch (GridAccessException e)
			{
				return false;
			}
		}

		return hasPower;
	}

	@Override
	public boolean isActive()
	{
		return isPowered();
	}

}
