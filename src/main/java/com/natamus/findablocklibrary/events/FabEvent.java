package com.natamus.findablocklibrary.events;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.natamus.findablocklibrary.config.ConfigHandler;
import com.natamus.findablocklibrary.util.Reference;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class FabEvent {
	private static Map<World, List<BlockPos>> worldcampfires = new HashMap<World, List<BlockPos>>();
	private static Map<World, Map<Date, BlockPos>> timeoutpositions = new HashMap<World, Map<Date, BlockPos>>();
	
	@SubscribeEvent
	public void onEntityJoin(LivingSpawnEvent.CheckSpawn e) {
		World world = e.getWorld().getWorld();
		if (world.isRemote) {
			return;
		}
		
		Entity entity = e.getEntity();
		
		if (entity.getTags().contains(Reference.MOD_ID + ".checked" )) {
			return;	
		}
		entity.addTag(Reference.MOD_ID + ".checked");
		
		if (ConfigHandler.GENERAL.preventMobSpawnerSpawns.get()) {
			AbstractSpawner msbl = e.getSpawner();
			if (msbl != null) {
				return;
			}
		}
		
		if (!entity.getType().getClassification().equals(EntityClassification.MONSTER)) {
			return;
		}
		
		List<Entity> passengers = entity.getPassengers();
		
		int r = ConfigHandler.GENERAL.preventHostilesRadius.get();
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
				
				if (campfire.withinDistance(epos, r*ConfigHandler.GENERAL.burnHostilesRadiusModifier.get())) {
					e.setResult(Result.DENY);
					
					if (passengers.size() > 0) {
						for (Entity passenger : passengers) {
							passenger.remove();
						}
					}
					System.out.println("Campfire saved!");
					removetodatescampfire = campfire.toImmutable();
					break;
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
					if (toepos.withinDistance(epos, r)) {
						System.out.println("Timeout saved!");
						return;
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
		
		for (int x = -r; x < r; x++) {
			for (int y = -r; y < r; y++) {
				for (int z = -r; z < r; z++) {
					BlockPos cpos = epos.east(x).north(y).up(z);
					BlockState state = world.getBlockState(cpos);
					if (state.getBlock().equals(Blocks.CAMPFIRE)) {
						currentcampfires.add(cpos.toImmutable());
						worldcampfires.put(world, currentcampfires);
						
						e.setResult(Result.DENY);
						if (passengers.size() > 0) {
							for (Entity passenger : passengers) {
								passenger.remove();
							}
						}
						return;
					}
				}
			}
		}
		
		timeouts.put(new Date(), epos.toImmutable());
		timeoutpositions.put(world, timeouts);
	}
}