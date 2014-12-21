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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import appeng.api.networking.IGridHost;
import appeng.api.util.DimensionalCoord;
import appeng.me.cluster.IAECluster;
import appeng.tile.spatial.TileSpatialPylon;

public class SpatialPylonCluster implements IAECluster
{

	public enum Axis
	{
		X, Y, Z, UNFORMED
	}

	final public DimensionalCoord min;
	final public DimensionalCoord max;
	public boolean isDestroyed = false;

	public Axis currentAxis = Axis.UNFORMED;

	final List<TileSpatialPylon> line = new ArrayList<TileSpatialPylon>();
	public boolean isValid;
	public boolean hasPower;
	public boolean hasChannel;

	public SpatialPylonCluster(DimensionalCoord _min, DimensionalCoord _max) {
		min = _min.copy();
		max = _max.copy();

		if ( min.x != max.x )
			currentAxis = Axis.X;
		else if ( min.y != max.y )
			currentAxis = Axis.Y;
		else if ( min.z != max.z )
			currentAxis = Axis.Z;
		else
			currentAxis = Axis.UNFORMED;
	}

	@Override
	public void updateStatus(boolean updateGrid)
	{
		for (TileSpatialPylon r : line)
		{
			r.recalculateDisplay();
		}
	}

	@Override
	public void destroy()
	{

		if ( isDestroyed )
			return;
		isDestroyed = true;

		for (TileSpatialPylon r : line)
		{
			r.updateStatus( null );
		}

	}

	public int tileCount()
	{
		return line.size();
	}

	@Override
	public Iterator<IGridHost> getTiles()
	{
		return (Iterator) line.iterator();
	}

}
