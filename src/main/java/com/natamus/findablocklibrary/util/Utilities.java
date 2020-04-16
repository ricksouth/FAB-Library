package com.natamus.findablocklibrary.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Utilities {
	private static Map<World, List<BlockPos>> worldcampfires = new HashMap<World, List<BlockPos>>();
	private static Map<World, Map<Date, BlockPos>> timeoutpositions = new HashMap<World, Map<Date, BlockPos>>();
	
	public static BlockPos getRequestedBlockAroundEntitySpawn(Block requestedblock, Integer radius, Double radiusmodifier, World world, Entity entity) {
		List<Entity> passengers = entity.getPassengers();
		BlockPos epos = entity.getPosition();
		
		List<BlockPos> currentcampfires;
		BlockPos removetodatescampfire = null;
		
		if (worldcampfires.containsKey(world)) {
			currentcampfires = worldcampfires.get(world);
			
			List<BlockPos> cftoremove = new ArrayList<BlockPos>(); 
			for (BlockPos campfire : currentcampfires) {
				if (!world.getChunkProvider().chunkExists(campfire.getX() >> 4, campfire.getZ() >> 4)) {
					cftoremove.add(campfire);
					continue;
				}
				if (!world.getBlockState(campfire).getBlock().equals(Blocks.CAMPFIRE)) {
					cftoremove.add(campfire);
					continue;
				}
				
				if (campfire.withinDistance(epos, radius*radiusmodifier)) {
					if (passengers.size() > 0) {
						for (Entity passenger : passengers) {
							passenger.remove();
						}
					}
					System.out.println("Campfire saved!");
					removetodatescampfire = campfire.toImmutable();
					return campfire.toImmutable();
				}
			}
			
			if (cftoremove.size() > 0) {
				for (BlockPos tr : cftoremove) {
					currentcampfires.remove(tr);
				}
			}
		}
		else {
			currentcampfires = new ArrayList<BlockPos>();
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
					if (removetodatescampfire != null) {
						if (toepos.withinDistance(removetodatescampfire, 64)) {
							totoremove.add(todate);
							System.out.println("TOTO removed campfire.");
						}
					}
					long ms = (now.getTime()-todate.getTime())/1000;
					System.out.println(ms);
					if (ms > 20000) {
						totoremove.add(todate);
						System.out.println(todate.toString() + " expired!!");
						continue;
					}
					if (toepos.withinDistance(epos, radius*radiusmodifier)) {
						System.out.println("Timeout shows entity nearby. time and resources saved!");
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
		
		System.out.println(currentcampfires);
		System.out.println(timeouts);
		
		int r = radius;
		for (int x = -r; x < r; x++) {
			for (int y = -r; y < r; y++) {
				for (int z = -r; z < r; z++) {
					BlockPos cpos = epos.east(x).north(y).up(z);
					BlockState state = world.getBlockState(cpos);
					if (state.getBlock().equals(Blocks.CAMPFIRE)) {
						currentcampfires.add(cpos.toImmutable());
						worldcampfires.put(world, currentcampfires);
						
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
}