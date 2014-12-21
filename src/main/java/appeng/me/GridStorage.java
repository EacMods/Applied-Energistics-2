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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridStorage;
import appeng.core.AELog;
import appeng.core.WorldSettings;

public class GridStorage implements IGridStorage
{

	private WeakReference<IGrid> internalGrid = null;

	final long myID;
	final NBTTagCompound data;

	public boolean isDirty = false;
	private final WeakHashMap<GridStorage,Boolean> divided = new WeakHashMap<GridStorage,Boolean>();
	final GridStorageSearch mySearchEntry; // keep myself in the list until I'm
											// lost...

	/**
	 * for use with world settings
	 * 
	 * @param id ID of grid storage
	 * @param gss grid storage search
	 */
	public GridStorage(long id, GridStorageSearch gss) {
		myID = id;
		mySearchEntry = gss;
		data = new NBTTagCompound();
	}

	/**
	 * for use with world settings
	 * 
	 * @param input array of bytes string
	 * @param id ID of grid storage
	 * @param gss grid storage search
	 */
	public GridStorage(String input, long id, GridStorageSearch gss) {
		myID = id;
		mySearchEntry = gss;
		NBTTagCompound myTag = null;

		try
		{
			byte[] byteData = javax.xml.bind.DatatypeConverter.parseBase64Binary( input );
			myTag = CompressedStreamTools.readCompressed( new ByteArrayInputStream( byteData ) );
		}
		catch (Throwable t)
		{
			myTag = new NBTTagCompound();
		}

		data = myTag;
	}

	/**
	 * fake storage.
	 */
	public GridStorage() {
		myID = 0;
		mySearchEntry = null;
		data = new NBTTagCompound();
	}

	public String getValue()
	{
		isDirty = false;

		Grid currentGrid = (Grid) getGrid();
		if ( currentGrid != null )
		{
			currentGrid.saveState();
		}

		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			CompressedStreamTools.writeCompressed( data, out );
			return javax.xml.bind.DatatypeConverter.printBase64Binary( out.toByteArray() );
		}
		catch (IOException e)
		{
			AELog.error( e );
		}

		return "";
	}

	@Override
	public NBTTagCompound dataObject()
	{
		return data;
	}

	@Override
	public long getID()
	{
		return myID;
	}

	public void markDirty()
	{
		isDirty = true;
	}

	public IGrid getGrid()
	{
		return internalGrid == null ? null : internalGrid.get();
	}

	public void setGrid(Grid grid)
	{
		internalGrid = new WeakReference<IGrid>( grid );
	}

	public void addDivided(GridStorage gs)
	{
		divided.put( gs, true );
	}

	public boolean hasDivided(GridStorage myStorage)
	{
		return divided.containsKey( myStorage );
	}

	public void remove()
	{
		WorldSettings.getInstance().destroyGridStorage( getID() );
	}

}
