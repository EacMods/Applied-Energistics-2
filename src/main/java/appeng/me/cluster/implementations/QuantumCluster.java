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

package appeng.me.cluster.implementations;

import java.util.Iterator;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.WorldEvent;
import appeng.api.AEApi;
import appeng.api.events.LocatableEventAnnounce;
import appeng.api.events.LocatableEventAnnounce.LocatableEvent;
import appeng.api.exceptions.FailedConnection;
import appeng.api.features.ILocatable;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.WorldCoord;
import appeng.me.cache.helpers.ConnectionWrapper;
import appeng.me.cluster.IAECluster;
import appeng.tile.qnb.TileQuantumBridge;
import appeng.util.iterators.ChainedIterator;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class QuantumCluster implements ILocatable, IAECluster
{

	final public WorldCoord min;
	final public WorldCoord max;
	public boolean isDestroyed = false;
	public boolean updateStatus = true;

	boolean registered = false;
	private long thisSide;
	private long otherSide;

	ConnectionWrapper connection;

	public TileQuantumBridge Ring[];
	private TileQuantumBridge center;

	@Override
	public Iterator<IGridHost> getTiles()
	{
		return new ChainedIterator<IGridHost>( Ring[0], Ring[1], Ring[2], Ring[3], Ring[4], Ring[5], Ring[6], Ring[7], center );
	}

	public void setCenter(TileQuantumBridge c)
	{
		registered = true;
		MinecraftForge.EVENT_BUS.register( this );
		center = c;
	}

	public QuantumCluster(WorldCoord _min, WorldCoord _max) {
		min = _min;
		max = _max;
		Ring = new TileQuantumBridge[8];
	}

	public boolean canUseNode(long qe)
	{
		QuantumCluster qc = (QuantumCluster) AEApi.instance().registries().locatable().findLocatableBySerial( qe );
		if ( qc != null )
		{
			World theWorld = qc.getCenter().getWorldObj();
			if ( !qc.isDestroyed )
			{
				Chunk c = theWorld.getChunkFromBlockCoords( qc.center.xCoord, qc.center.zCoord );
				if ( c.isChunkLoaded )
				{
					int id = theWorld.provider.dimensionId;
					World cur = DimensionManager.getWorld( id );

					TileEntity te = theWorld.getTileEntity( qc.center.xCoord, qc.center.yCoord, qc.center.zCoord );
					return te != qc.center || theWorld != cur;
				}
			}
		}
		return true;
	}

	@SubscribeEvent
	public void onUnload(WorldEvent.Unload e)
	{
		if ( center.getWorldObj() == e.world )
		{
			updateStatus = false;
			destroy();
		}
	}

	@Override
	public void updateStatus(boolean updateGrid)
	{
		long qe;

		qe = center.getQEFrequency();

		if ( thisSide != qe && thisSide != -qe )
		{
			if ( qe != 0 )
			{
				if ( thisSide != 0 )
					MinecraftForge.EVENT_BUS.post( new LocatableEventAnnounce( this, LocatableEvent.Unregister ) );

				if ( canUseNode( -qe ) )
				{
					otherSide = qe;
					thisSide = -qe;
				}
				else if ( canUseNode( qe ) )
				{
					thisSide = qe;
					otherSide = -qe;
				}

				MinecraftForge.EVENT_BUS.post( new LocatableEventAnnounce( this, LocatableEvent.Register ) );
			}
			else
			{
				MinecraftForge.EVENT_BUS.post( new LocatableEventAnnounce( this, LocatableEvent.Unregister ) );

				otherSide = 0;
				thisSide = 0;
			}
		}

		Object myOtherSide = otherSide == 0 ? null : AEApi.instance().registries().locatable().findLocatableBySerial( otherSide );

		boolean shutdown = false;

		if ( myOtherSide instanceof QuantumCluster )
		{
			QuantumCluster sideA = this;
			QuantumCluster sideB = (QuantumCluster) myOtherSide;

			if ( sideA.isActive() && sideB.isActive() )
			{
				if ( connection != null && connection.connection != null )
				{
					IGridNode a = connection.connection.a();
					IGridNode b = connection.connection.b();
					IGridNode sa = sideA.getNode();
					IGridNode sb = sideB.getNode();
					if ( (a == sa || b == sa) && (a == sb || b == sb) )
						return;
				}

				try
				{
					if ( sideA.connection != null )
					{
						if ( sideA.connection.connection != null )
						{
							sideA.connection.connection.destroy();
							sideA.connection = new ConnectionWrapper( null );
						}
					}

					if ( sideB.connection != null )
					{
						if ( sideB.connection.connection != null )
						{
							sideB.connection.connection.destroy();
							sideB.connection = new ConnectionWrapper( null );
						}
					}

					sideA.connection = sideB.connection = new ConnectionWrapper( AEApi.instance().createGridConnection( sideA.getNode(), sideB.getNode() ) );
				}
				catch (FailedConnection e)
				{
					// :(
				}
			}
			else
				shutdown = true;
		}
		else
			shutdown = true;

		if ( shutdown && connection != null )
		{
			if ( connection.connection != null )
			{
				connection.connection.destroy();
				connection.connection = null;
				connection = new ConnectionWrapper( null );
			}
		}
	}

	@Override
	public void destroy()
	{
		if ( isDestroyed )
			return;
		isDestroyed = true;

		if ( registered )
		{
			MinecraftForge.EVENT_BUS.unregister( this );
			registered = false;
		}

		if ( getLocatableSerial() != 0 )
		{
			updateStatus( true );
			MinecraftForge.EVENT_BUS.post( new LocatableEventAnnounce( this, LocatableEvent.Unregister ) );
		}

		center.updateStatus( null, (byte) -1, updateStatus );

		for (TileQuantumBridge r : Ring)
		{
			r.updateStatus( null, (byte) -1, updateStatus );
		}

		center = null;
		Ring = new TileQuantumBridge[8];
	}

	public boolean isCorner(TileQuantumBridge tileQuantumBridge)
	{
		return Ring[0] == tileQuantumBridge || Ring[2] == tileQuantumBridge || Ring[4] == tileQuantumBridge || Ring[6] == tileQuantumBridge;
	}

	@Override
	public long getLocatableSerial()
	{
		return thisSide;
	}

	public TileQuantumBridge getCenter()
	{
		return center;
	}

	public boolean hasQES()
	{
		return getLocatableSerial() != 0;
	}

	private IGridNode getNode()
	{
		return center.getGridNode( ForgeDirection.UNKNOWN );
	}

	private boolean isActive()
	{
		if ( isDestroyed || !registered )
			return false;

		return center.isPowered() && hasQES();
	}

}
