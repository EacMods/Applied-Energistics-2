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

package appeng.integration;

public enum IntegrationType
{
	IC2(IntegrationSide.BOTH, "Industrial Craft 2", "IC2"),

	RotaryCraft(IntegrationSide.BOTH, "Rotary Craft", "RotaryCraft"),

	RC(IntegrationSide.BOTH, "Railcraft", "Railcraft"),

	BC(IntegrationSide.BOTH, "BuildCraft", "BuildCraft|Silicon"),

	MJ6(IntegrationSide.BOTH, "BuildCraft6 Power", null),

	MJ5(IntegrationSide.BOTH, "BuildCraft5 Power", null),

	RF(IntegrationSide.BOTH, "RedstoneFlux Power - Tiles", null),

	RFItem(IntegrationSide.BOTH, "RedstoneFlux Power - Items", null),

	MFR(IntegrationSide.BOTH, "Mine Factory Reloaded", "MineFactoryReloaded"),

	DSU(IntegrationSide.BOTH, "Deep Storage Unit", null),

	FZ(IntegrationSide.BOTH, "Factorization", "factorization"),

	FMP(IntegrationSide.BOTH, "Forge MultiPart", "McMultipart"),

	RB(IntegrationSide.BOTH, "Rotatable Blocks", "RotatableBlocks"),

	CLApi(IntegrationSide.BOTH, "Colored Lights Core", "coloredlightscore"),

	Waila(IntegrationSide.CLIENT, "Waila", "Waila"),

	InvTweaks(IntegrationSide.CLIENT, "Inventory Tweaks", "inventorytweaks"),

	NEI(IntegrationSide.CLIENT, "Not Enough Items", "NotEnoughItems"),

	CraftGuide(IntegrationSide.CLIENT, "Craft Guide", "craftguide"),

	Mekanism(IntegrationSide.BOTH, "Mekanism", "Mekanism"),

	ImmibisMicroblocks(IntegrationSide.BOTH, "ImmibisMicroblocks", "ImmibisMicroblocks"),
	
	BetterStorage(IntegrationSide.BOTH, "BetterStorage", "betterstorage" );

	public final IntegrationSide side;
	public final String dspName;
	public final String modID;

	private IntegrationType(IntegrationSide side, String Name, String modid) {
		this.side = side;
		this.dspName = Name;
		this.modID = modid;
	}

}
