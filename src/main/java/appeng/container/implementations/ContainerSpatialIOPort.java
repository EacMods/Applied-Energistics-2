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

package appeng.container.implementations;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.spatial.ISpatialCache;
import appeng.container.AEBaseContainer;
import appeng.container.guisync.GuiSync;
import appeng.container.slot.SlotOutput;
import appeng.container.slot.SlotRestrictedInput;
import appeng.tile.spatial.TileSpatialIOPort;
import appeng.util.Platform;

public class ContainerSpatialIOPort extends AEBaseContainer
{

	final TileSpatialIOPort spatialIOPort;

	IGrid network;

	@GuiSync(0)
	public long currentPower;
	@GuiSync(1)
	public long maxPower;
	@GuiSync(2)
	public long reqPower;
	@GuiSync(3)
	public long eff;

	int delay = 40;

	public ContainerSpatialIOPort(InventoryPlayer ip, TileSpatialIOPort spatialIOPort) {
		super( ip, spatialIOPort, null );
		this.spatialIOPort = spatialIOPort;

		if ( Platform.isServer() )
			network = spatialIOPort.getGridNode( ForgeDirection.UNKNOWN ).getGrid();

		addSlotToContainer( new SlotRestrictedInput( SlotRestrictedInput.PlacableItemType.SPATIAL_STORAGE_CELLS, spatialIOPort, 0, 52, 48, invPlayer ) );
		addSlotToContainer( new SlotOutput( spatialIOPort, 1, 113, 48, SlotRestrictedInput.PlacableItemType.SPATIAL_STORAGE_CELLS.IIcon ) );

		bindPlayerInventory( ip, 0, 197 - /* height of player inventory */82 );
	}

	@Override
	public void detectAndSendChanges()
	{
		verifyPermissions( SecurityPermissions.BUILD, false );

		if ( Platform.isServer() )
		{
			delay++;
			if ( delay > 15 && network != null )
			{
				delay = 0;

				IEnergyGrid eg = network.getCache( IEnergyGrid.class );
				ISpatialCache sc = network.getCache( ISpatialCache.class );
				if ( eg != null )
				{
					currentPower = (long) (100.0 * eg.getStoredPower());
					maxPower = (long) (100.0 * eg.getMaxStoredPower());
					reqPower = (long) (100.0 * sc.requiredPower());
					eff = (long) (100.0f * sc.currentEfficiency());
				}
			}
		}

		super.detectAndSendChanges();
	}
}
