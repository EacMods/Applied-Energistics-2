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

package appeng.integration.modules.helpers;

import appeng.integration.abstraction.helpers.BaseMJPerdition;
import net.minecraft.nbt.NBTTagCompound;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;

public class MJPerdition extends BaseMJPerdition
{

	final protected PowerHandler bcPowerHandler;

	public MJPerdition(IPowerReceptor te) {
		bcPowerHandler = new PowerHandler( te, Type.MACHINE );
	}

	@Override
	public void Tick()
	{
		bcPowerHandler.update();
	}

	@Override
	public void writeToNBT(NBTTagCompound data)
	{
		bcPowerHandler.writeToNBT( data, "bcPowerHandler" );
	}

	@Override
	public void readFromNBT(NBTTagCompound data)
	{
		bcPowerHandler.readFromNBT( data, "bcPowerHandler" );
	}

	@Override
	public PowerReceiver getPowerReceiver()
	{
		return bcPowerHandler.getPowerReceiver();
	}

	@Override
	public double useEnergy(double min, double max, boolean doUse)
	{
		return bcPowerHandler.useEnergy( min, max, doUse );
	}

	@Override
	public void addEnergy(float failed)
	{
		bcPowerHandler.addEnergy( failed );
	}

	@Override
	public void configure(int i, int j, float f, int k)
	{
		bcPowerHandler.configure( i, j, f, k );
	}

}