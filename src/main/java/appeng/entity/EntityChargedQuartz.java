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

package appeng.entity;

import appeng.api.AEApi;
import appeng.client.EffectType;
import appeng.core.AEConfig;
import appeng.core.CommonHelper;
import appeng.core.features.AEFeature;
import appeng.util.Platform;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

final public class EntityChargedQuartz extends AEBaseEntityItem
{

	int delay = 0;
	int transformTime = 0;

	public EntityChargedQuartz(World w)
	{
		super( w );
	}

	public EntityChargedQuartz(World w, double x, double y, double z, ItemStack is)
	{
		super( w, x, y, z, is );
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if ( !AEConfig.instance.isFeatureEnabled( AEFeature.inWorldFluix ) )
			return;

		if ( Platform.isClient() && delay++ > 30 && AEConfig.instance.enableEffects )
		{
			CommonHelper.proxy.spawnEffect( EffectType.Lightning, worldObj, posX, posY, posZ, null );
			delay = 0;
		}

		int j = MathHelper.floor_double( this.posX );
		int i = MathHelper.floor_double( this.posY );
		int k = MathHelper.floor_double( this.posZ );

		Material mat = worldObj.getBlock( j, i, k ).getMaterial();
		if ( Platform.isServer() && mat.isLiquid() )
		{
			transformTime++;
			if ( transformTime > 60 )
			{
				if ( !transform() )
					transformTime = 0;
			}
		}
		else
			transformTime = 0;
	}

	public boolean transform()
	{
		ItemStack item = getEntityItem();
		if ( AEApi.instance().materials().materialCertusQuartzCrystalCharged.sameAsStack( item ) )
		{
			AxisAlignedBB region = AxisAlignedBB.getBoundingBox( posX - 1, posY - 1, posZ - 1, posX + 1, posY + 1, posZ + 1 );
			List<Entity> l = this.getCheckedEntitiesWithinAABBExcludingEntity( region );

			EntityItem redstone = null;
			EntityItem netherQuartz = null;

			for (Entity e : l)
			{
				if ( e instanceof EntityItem && !e.isDead )
				{
					ItemStack other = ((EntityItem) e).getEntityItem();
					if ( other != null && other.stackSize > 0 )
					{
						if ( Platform.isSameItem( other, new ItemStack( Items.redstone ) ) )
							redstone = (EntityItem) e;

						if ( Platform.isSameItem( other, new ItemStack( Items.quartz ) ) )
							netherQuartz = (EntityItem) e;
					}
				}
			}

			if ( redstone != null && netherQuartz != null )
			{
				getEntityItem().stackSize--;
				redstone.getEntityItem().stackSize--;
				netherQuartz.getEntityItem().stackSize--;

				if ( getEntityItem().stackSize <= 0 )
					setDead();

				if ( redstone.getEntityItem().stackSize <= 0 )
					redstone.setDead();

				if ( netherQuartz.getEntityItem().stackSize <= 0 )
					netherQuartz.setDead();

				ItemStack Output = AEApi.instance().materials().materialFluixCrystal.stack( 2 );
				worldObj.spawnEntityInWorld( new EntityItem( worldObj, posX, posY, posZ, Output ) );

				return true;
			}
		}
		return false;
	}
}
