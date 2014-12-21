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

package appeng.block.grindstone;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.implementations.tiles.ICrankable;
import appeng.block.AEBaseBlock;
import appeng.client.render.BaseBlockRender;
import appeng.client.render.blocks.RenderBlockCrank;
import appeng.core.features.AEFeature;
import appeng.core.stats.Stats;
import appeng.tile.AEBaseTile;
import appeng.tile.grindstone.TileCrank;

public class BlockCrank extends AEBaseBlock
{

	public BlockCrank() {
		super( BlockCrank.class, Material.wood );
		setFeature( EnumSet.of( AEFeature.GrindStone ) );
		setTileEntity( TileCrank.class );
		setLightOpacity( 0 );
		isFullSize = isOpaque = false;
	}

	@Override
	public boolean onActivated(World w, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if ( player instanceof FakePlayer || player == null )
		{
			dropCrank(w, x, y, z);
			return true;
		}

		AEBaseTile tile = getTileEntity( w, x, y, z );
		if ( tile instanceof TileCrank )
		{
			if ( ((TileCrank) tile).power() )
			{
				Stats.TurnedCranks.addToPlayer( player, 1 );
			}
		}

		return true;
	}

	@Override
	public Class<? extends BaseBlockRender> getRenderer()
	{
		return RenderBlockCrank.class;
	}

	private boolean isCrankable( World world, int x, int y, int z, ForgeDirection offset )
	{
		TileEntity te = world.getTileEntity( x + offset.offsetX, y + offset.offsetY, z + offset.offsetZ );

		return te instanceof ICrankable && ( ( ICrankable ) te ).canCrankAttach( offset.getOpposite() );
	}

	private ForgeDirection findCrankable( World world, int x, int y, int z )
	{
		for ( ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS )
			if ( isCrankable( world, x, y, z, dir ) )
			{
				return dir;
			}
		return ForgeDirection.UNKNOWN;
	}

	@Override
	public boolean canPlaceBlockAt( World world, int x, int y, int z )
	{
		return findCrankable( world , x, y, z ) != ForgeDirection.UNKNOWN;
	}

	@Override
	public boolean isValidOrientation( World world, int x, int y, int z, ForgeDirection forward, ForgeDirection up )
	{
		TileEntity te = world.getTileEntity( x, y, z );
		return !(te instanceof TileCrank) || isCrankable( world, x, y, z, up.getOpposite() );
	}

	private void dropCrank( World world, int x, int y, int z )
	{
		world.func_147480_a( x, y, z, true ); // w.destroyBlock( x, y, z, true );
		world.markBlockForUpdate( x, y, z );
	}

	@Override
	public void onBlockPlacedBy( World world, int x, int y, int z, EntityLivingBase placer, ItemStack itemStack )
	{
		AEBaseTile tile = getTileEntity( world, x, y, z );
		if ( tile != null )
		{
			ForgeDirection mnt = findCrankable( world, x, y, z );
			ForgeDirection forward = ForgeDirection.UP;
			if ( mnt == ForgeDirection.UP || mnt == ForgeDirection.DOWN )
			{
				forward = ForgeDirection.SOUTH;
			}
			tile.setOrientation( forward, mnt.getOpposite() );
		}
		else
		{
			dropCrank( world, x, y, z );
		}
	}

	@Override
	public void onNeighborBlockChange (World world, int x, int y, int z, Block block )
	{
		AEBaseTile tile = getTileEntity( world, x, y, z );
		if ( tile != null )
		{
			if ( !isCrankable( world, x, y, z, tile.getUp().getOpposite() ) )
			{
				dropCrank( world, x, y, z );
			}
		}
		else
		{
			dropCrank( world, x, y, z );
		}
	}

}
