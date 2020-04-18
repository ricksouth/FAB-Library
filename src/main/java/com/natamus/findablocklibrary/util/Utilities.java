package com.natamus.findablocklibrary.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Utilities {
	private static Map<Block, Map<World, List<BlockPos>>> getMapFromBlock = new HashMap<Block, Map<World, List<BlockPos>>>();
	private static Map<World, Map<Date, BlockPos>> timeoutpositions = new HashMap<World, Map<Date, BlockPos>>();
	
	public static BlockPos getRequestedBlockAroundEntitySpawn(Block requestedblock, Integer radius, Double radiusmodifier, World world, Entity entity) {
		Map<World,List<BlockPos>> worldblocks = getMap(requestedblock);
		
		List<Entity> passengers = entity.getPassengers();
		BlockPos epos = entity.getPosition();
		
		List<BlockPos> currentblocks;
		BlockPos removeblockpos = null;
		
		if (worldblocks.containsKey(world)) {
			currentblocks = worldblocks.get(world);
			
			List<BlockPos> cbtoremove = new ArrayList<BlockPos>(); 
			for (BlockPos cblock : currentblocks) {
				if (!world.getChunkProvider().chunkExists(cblock.getX() >> 4, cblock.getZ() >> 4)) {
					cbtoremove.add(cblock);
					continue;
				}
				if (!world.getBlockState(cblock).getBlock().equals(requestedblock)) {
					cbtoremove.add(cblock);
					continue;
				}
				
				if (cblock.withinDistance(epos, radius*radiusmodifier)) {
					if (passengers.size() > 0) {
						for (Entity passenger : passengers) {
							passenger.remove();
						}
					}
					removeblockpos = cblock.toImmutable();
					return cblock.toImmutable();
				}
			}
			
			if (cbtoremove.size() > 0) {
				for (BlockPos tr : cbtoremove) {
					currentblocks.remove(tr);
				}
			}
		}
		else {
			currentblocks = new ArrayList<BlockPos>();
		}
		
		// Timeout function which prevents too many of the loop through blocks.
		Map<Date, BlockPos> timeouts;
		if (timeoutpositions.containsKey(world)) {
			timeouts = timeoutpositions.get(world);
			
			List<Date> totoremove = new ArrayList<Date>(); 
			if (timeouts.size() > 0) {
				Date now = new Date();
				for (Date todate : timeouts.keySet()) {
					BlockPos toepos = timeouts.get(todate);
					if (removeblockpos != null) {
						if (toepos.withinDistance(removeblockpos, 64)) {
							totoremove.add(todate);
						}
					}
					long ms = (now.getTime()-todate.getTime())/1000;
					if (ms > 20000) {
						totoremove.add(todate);
						continue;
					}
					if (toepos.withinDistance(epos, radius*radiusmodifier)) {
						return null;
					}
				}
			}
			
			if (totoremove.size() > 0) {
				for (Date tr : totoremove) {
					timeouts.remove(tr);
				}
			}
		}
		else {
			timeouts = new HashMap<Date, BlockPos>();
		}
		
		int r = radius;
		for (int x = -r; x < r; x++) {
			for (int y = -r; y < r; y++) {
				for (int z = -r; z < r; z++) {
					BlockPos cpos = epos.east(x).north(y).up(z);
					BlockState state = world.getBlockState(cpos);
					if (state.getBlock().equals(requestedblock)) {
						currentblocks.add(cpos.toImmutable());
						worldblocks.put(world, currentblocks);
						getMapFromBlock.put(requestedblock, worldblocks);
						
						if (passengers.size() > 0) {
							for (Entity passenger : passengers) {
								passenger.remove();
							}
						}
						return cpos.toImmutable();
					}
				}
			}
		}
		
		timeouts.put(new Date(), epos.toImmutable());
		timeoutpositions.put(world, timeouts);
		return null;
	}
	
	public static BlockPos updatePlacedBlock(Block requestedblock, BlockPos bpos, World world) {
		BlockState state = world.getBlockState(bpos);
		if (state.getBlock().equals(requestedblock)) {
			Map<World, List<BlockPos>> worldblocks = getMap(requestedblock);
			
			List<BlockPos> currentblocks;
			if (worldblocks.containsKey(world)) {
				currentblocks = worldblocks.get(world);
			}
			else {
				currentblocks = new ArrayList<BlockPos>();
			}
			
			if (!currentblocks.contains(bpos)) {
				currentblocks.add(bpos);
				worldblocks.put(world, currentblocks);
				getMapFromBlock.put(requestedblock, worldblocks);
			}
			return bpos;
		}
		
		return null;
	}
	
	// Internal util functions
	private static Map<World,List<BlockPos>> getMap(Block requestedblock) {
		Map<World,List<BlockPos>> worldblocks;
		if (getMapFromBlock.containsKey(requestedblock)) {
			worldblocks = getMapFromBlock.get(requestedblock);
		}
		else {
			worldblocks = new HashMap<World, List<BlockPos>>();
		}
		return worldblocks;
	}
}