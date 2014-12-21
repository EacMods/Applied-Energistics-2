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

package appeng.core.features.registries;

import java.util.HashSet;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import appeng.api.features.IWorldGen;

public class WorldGenRegistry implements IWorldGen
{

	private static class TypeSet
	{

		final HashSet<Class<? extends WorldProvider>> badProviders = new HashSet<Class<? extends WorldProvider>>();
		final HashSet<Integer> badDimensions = new HashSet<Integer>();
		final HashSet<Integer> enabledDimensions = new HashSet<Integer>();

	}

	final TypeSet[] types;

	static final public WorldGenRegistry instance = new WorldGenRegistry();

	private WorldGenRegistry() {

		types = new TypeSet[WorldGenType.values().length];

		for (WorldGenType type : WorldGenType.values())
		{
			types[type.ordinal()] = new TypeSet();
		}

	}

	@Override
	public boolean isWorldGenEnabled(WorldGenType type, World w)
	{
		if ( type == null )
			throw new IllegalArgumentException( "Bad Type Passed" );

		if ( w == null )
			throw new IllegalArgumentException( "Bad Provider Passed" );

		boolean	isBadProvider = types[type.ordinal()].badProviders.contains( w.provider.getClass() );
		boolean isBadDimension = types[type.ordinal()].badDimensions.contains( w.provider.dimensionId );
		boolean isGoodDimension = types[type.ordinal()].enabledDimensions.contains( w.provider.dimensionId );

		if ( isBadProvider || isBadDimension )
		{
			return false;
		}

		if ( !isGoodDimension && type == WorldGenType.Meteorites)
		{
			return false;
		}

		return true;
	}

	@Override
	public void disableWorldGenForProviderID(WorldGenType type, Class<? extends WorldProvider> provider)
	{
		if ( type == null )
			throw new IllegalArgumentException( "Bad Type Passed" );

		if ( provider == null )
			throw new IllegalArgumentException( "Bad Provider Passed" );

		types[type.ordinal()].badProviders.add( provider );
	}

	@Override
	public void disableWorldGenForDimension(WorldGenType type, int dimensionID)
	{
		if ( type == null )
		{
			throw new IllegalArgumentException( "Bad Type Passed" );
		}

		types[type.ordinal()].badDimensions.add( dimensionID );
	}

	@Override
	public void enableWorldGenForDimension(WorldGenType type, int dimensionID)
	{
		if ( type == null )
			throw new IllegalArgumentException( "Bad Type Passed" );

		types[type.ordinal()].enabledDimensions.add( dimensionID );
	}

}
