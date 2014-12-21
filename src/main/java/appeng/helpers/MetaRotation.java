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

package appeng.helpers;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.util.IOrientable;

public class MetaRotation implements IOrientable
{

	final IBlockAccess w;
	final int x;
	final int y;
	final int z;

	public MetaRotation(IBlockAccess world, int x, int y, int z) {
		w = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void setOrientation(ForgeDirection Forward, ForgeDirection Up)
	{
		if ( w instanceof World )
			((World) w).setBlockMetadataWithNotify( x, y, z, Up.ordinal(), 1 + 2 );
		else
			throw new RuntimeException( w.getClass().getName() + " received, expected World" );
	}

	@Override
	public ForgeDirection getUp()
	{
		return ForgeDirection.getOrientation( w.getBlockMetadata( x, y, z ) );
	}

	@Override
	public ForgeDirection getForward()
	{
		if ( getUp().offsetY == 0 )
			return ForgeDirection.UP;
		return ForgeDirection.SOUTH;
	}

	@Override
	public boolean canBeRotated()
	{
		return true;
	}
}
