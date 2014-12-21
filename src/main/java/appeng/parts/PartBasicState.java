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

package appeng.parts;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.GridFlags;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.parts.IPartRenderHelper;
import appeng.client.texture.CableBusTextures;
import appeng.me.GridAccessException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PartBasicState extends AEBasePart implements IPowerChannelState
{

	protected final int POWERED_FLAG = 1;
	protected final int CHANNEL_FLAG = 2;

	protected int clientFlags = 0; // sent as byte.

	@MENetworkEventSubscribe
	public void chanRender(MENetworkChannelsChanged c)
	{
		getHost().markForUpdate();
	}

	@MENetworkEventSubscribe
	public void powerRender(MENetworkPowerStatusChange c)
	{
		getHost().markForUpdate();
	}

	public void setColors(boolean hasChan, boolean hasPower)
	{
		if ( hasChan )
		{
			int l = 14;
			Tessellator.instance.setBrightness( l << 20 | l << 4 );
			Tessellator.instance.setColorOpaque_I( getColor().blackVariant );
		}
		else if ( hasPower )
		{
			int l = 9;
			Tessellator.instance.setBrightness( l << 20 | l << 4 );
			Tessellator.instance.setColorOpaque_I( getColor().whiteVariant );
		}
		else
		{
			Tessellator.instance.setBrightness( 0 );
			Tessellator.instance.setColorOpaque_I( 0x000000 );
		}
	}

	@SideOnly(Side.CLIENT)
	public void renderLights(int x, int y, int z, IPartRenderHelper rh, RenderBlocks renderer)
	{
		rh.normalRendering();
		setColors( (clientFlags & (POWERED_FLAG | CHANNEL_FLAG)) == (POWERED_FLAG | CHANNEL_FLAG), (clientFlags & POWERED_FLAG) == POWERED_FLAG );
		rh.renderFace( x, y, z, CableBusTextures.PartMonitorSidesStatusLights.getIcon(), ForgeDirection.EAST, renderer );
		rh.renderFace( x, y, z, CableBusTextures.PartMonitorSidesStatusLights.getIcon(), ForgeDirection.WEST, renderer );
		rh.renderFace( x, y, z, CableBusTextures.PartMonitorSidesStatusLights.getIcon(), ForgeDirection.UP, renderer );
		rh.renderFace( x, y, z, CableBusTextures.PartMonitorSidesStatusLights.getIcon(), ForgeDirection.DOWN, renderer );
	}

	@Override
	public void writeToStream(ByteBuf data) throws IOException
	{
		super.writeToStream( data );

		clientFlags = 0;

		try
		{
			if ( proxy.getEnergy().isNetworkPowered() )
				clientFlags |= POWERED_FLAG;

			if ( proxy.getNode().meetsChannelRequirements() )
				clientFlags |= CHANNEL_FLAG;

			clientFlags = populateFlags( clientFlags );
		}
		catch (GridAccessException e)
		{
			// meh
		}

		data.writeByte( (byte) clientFlags );
	}

	protected int populateFlags(int cf)
	{
		return cf;
	}

	@Override
	public boolean readFromStream(ByteBuf data) throws IOException
	{
		boolean eh = super.readFromStream( data );

		int old = clientFlags;
		clientFlags = data.readByte();

		return eh || old != clientFlags;
	}

	public PartBasicState(Class c, ItemStack is) {
		super( c, is );
		proxy.setFlags( GridFlags.REQUIRE_CHANNEL );
	}

	@Override
	public boolean isPowered()
	{
		return (clientFlags & POWERED_FLAG) == POWERED_FLAG;
	}

	@Override
	public boolean isActive()
	{
		return (clientFlags & CHANNEL_FLAG) == CHANNEL_FLAG;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getBreakingTexture()
	{
		return CableBusTextures.PartTransitionPlaneBack.getIcon();
	}
}
