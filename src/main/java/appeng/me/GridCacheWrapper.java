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

package appeng.me;

import appeng.api.networking.IGridCache;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridStorage;

public class GridCacheWrapper implements IGridCache
{

	final IGridCache myCache;
	final String name;

	public GridCacheWrapper(final IGridCache gc) {
		myCache = gc;
		name = myCache.getClass().getName();
	}

	@Override
	public void onUpdateTick()
	{
		myCache.onUpdateTick();
	}

	@Override
	public void removeNode(final IGridNode gridNode, final IGridHost machine)
	{
		myCache.removeNode( gridNode, machine );
	}

	@Override
	public void addNode(final IGridNode gridNode, final IGridHost machine)
	{
		myCache.addNode( gridNode, machine );
	}

	public String getName()
	{
		return name;
	}

	@Override
	public void onSplit(final IGridStorage storageB)
	{
		myCache.onSplit( storageB );
	}

	@Override
	public void onJoin(final IGridStorage storageB)
	{
		myCache.onJoin( storageB );
	}

	@Override
	public void populateGridStorage(final IGridStorage storage)
	{
		myCache.populateGridStorage( storage );
	}

}
