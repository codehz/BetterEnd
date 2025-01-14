package ru.betterend.registry;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.biome.InternalBiomeData;
import net.fabricmc.fabric.impl.biome.WeightedBiomePicker;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.biome.Biomes;
import ru.bclib.util.JsonFactory;
import ru.betterend.BetterEnd;
import ru.betterend.config.Configs;
import ru.betterend.integration.Integrations;
import ru.betterend.interfaces.IBiomeList;
import ru.betterend.world.biome.EndBiome;
import ru.betterend.world.biome.air.BiomeIceStarfield;
import ru.betterend.world.biome.cave.EmptyAuroraCaveBiome;
import ru.betterend.world.biome.cave.EmptyEndCaveBiome;
import ru.betterend.world.biome.cave.EmptySmaragdantCaveBiome;
import ru.betterend.world.biome.cave.EndCaveBiome;
import ru.betterend.world.biome.cave.JadeCaveBiome;
import ru.betterend.world.biome.cave.LushAuroraCaveBiome;
import ru.betterend.world.biome.cave.LushSmaragdantCaveBiome;
import ru.betterend.world.biome.land.AmberLandBiome;
import ru.betterend.world.biome.land.BlossomingSpiresBiome;
import ru.betterend.world.biome.land.ChorusForestBiome;
import ru.betterend.world.biome.land.CrystalMountainsBiome;
import ru.betterend.world.biome.land.DragonGraveyardsBiome;
import ru.betterend.world.biome.land.DryShrublandBiome;
import ru.betterend.world.biome.land.DustWastelandsBiome;
import ru.betterend.world.biome.land.FoggyMushroomlandBiome;
import ru.betterend.world.biome.land.GlowingGrasslandsBiome;
import ru.betterend.world.biome.land.LanternWoodsBiome;
import ru.betterend.world.biome.land.MegalakeBiome;
import ru.betterend.world.biome.land.MegalakeGroveBiome;
import ru.betterend.world.biome.land.NeonOasisBiome;
import ru.betterend.world.biome.land.PaintedMountainsBiome;
import ru.betterend.world.biome.land.ShadowForestBiome;
import ru.betterend.world.biome.land.SulphurSpringsBiome;
import ru.betterend.world.biome.land.UmbrellaJungleBiome;
import ru.betterend.world.generator.BiomeMap;
import ru.betterend.world.generator.BiomePicker;
import ru.betterend.world.generator.BiomeType;
import ru.betterend.world.generator.GeneratorOptions;

public class EndBiomes {
	private static final HashMap<ResourceLocation, EndBiome> ID_MAP = Maps.newHashMap();
	private static final HashMap<Biome, EndBiome> CLIENT = Maps.newHashMap();
	public static final Set<ResourceLocation> FABRIC_VOID = Sets.newHashSet();
	private static final Set<ResourceLocation> SUBBIOMES_UNMUTABLES = Sets.newHashSet();
	
	public static final BiomePicker LAND_BIOMES = new BiomePicker();
	public static final BiomePicker VOID_BIOMES = new BiomePicker();
	public static final BiomePicker CAVE_BIOMES = new BiomePicker();
	public static final List<EndBiome> SUBBIOMES = Lists.newArrayList();
	private static final JsonObject EMPTY_JSON = new JsonObject();
	
	private static Registry<Biome> biomeRegistry;
	private static BiomeMap caveBiomeMap;
	
	// Vanilla Land
	public static final EndBiome END = registerBiome(Biomes.THE_END, BiomeType.LAND, 1F);
	public static final EndBiome END_MIDLANDS = registerSubBiome(Biomes.END_MIDLANDS, END, 0.5F);
	public static final EndBiome END_HIGHLANDS = registerSubBiome(Biomes.END_HIGHLANDS, END, 0.5F);
	
	// Vanilla Void
	public static final EndBiome END_BARRENS = registerBiome(Biomes.END_BARRENS, BiomeType.VOID, 1F);
	public static final EndBiome SMALL_END_ISLANDS = registerBiome(Biomes.SMALL_END_ISLANDS, BiomeType.VOID, 1);
	
	// Better End Land
	public static final EndBiome FOGGY_MUSHROOMLAND = registerBiome(new FoggyMushroomlandBiome(), BiomeType.LAND);
	public static final EndBiome CHORUS_FOREST = registerBiome(new ChorusForestBiome(), BiomeType.LAND);
	public static final EndBiome DUST_WASTELANDS = registerBiome(new DustWastelandsBiome(), BiomeType.LAND);
	public static final EndBiome MEGALAKE = registerBiome(new MegalakeBiome(), BiomeType.LAND);
	public static final EndBiome MEGALAKE_GROVE = registerSubBiome(new MegalakeGroveBiome(), MEGALAKE);
	public static final EndBiome CRYSTAL_MOUNTAINS = registerBiome(new CrystalMountainsBiome(), BiomeType.LAND);
	public static final EndBiome PAINTED_MOUNTAINS = registerSubBiome(new PaintedMountainsBiome(), DUST_WASTELANDS);
	public static final EndBiome SHADOW_FOREST = registerBiome(new ShadowForestBiome(), BiomeType.LAND);
	public static final EndBiome AMBER_LAND = registerBiome(new AmberLandBiome(), BiomeType.LAND);
	public static final EndBiome BLOSSOMING_SPIRES = registerBiome(new BlossomingSpiresBiome(), BiomeType.LAND);
	public static final EndBiome SULPHUR_SPRINGS = registerBiome(new SulphurSpringsBiome(), BiomeType.LAND);
	public static final EndBiome UMBRELLA_JUNGLE = registerBiome(new UmbrellaJungleBiome(), BiomeType.LAND);
	public static final EndBiome GLOWING_GRASSLANDS = registerBiome(new GlowingGrasslandsBiome(), BiomeType.LAND);
	public static final EndBiome DRAGON_GRAVEYARDS = registerBiome(new DragonGraveyardsBiome(), BiomeType.LAND);
	public static final EndBiome DRY_SHRUBLAND = registerBiome(new DryShrublandBiome(), BiomeType.LAND);
	public static final EndBiome LANTERN_WOODS = registerBiome(new LanternWoodsBiome(), BiomeType.LAND);
	public static final EndBiome NEON_OASIS = registerSubBiome(new NeonOasisBiome(), DUST_WASTELANDS);
	
	// Better End Void
	public static final EndBiome ICE_STARFIELD = registerBiome(new BiomeIceStarfield(), BiomeType.VOID);
	
	// Better End Caves
	public static final EndCaveBiome EMPTY_END_CAVE = registerCaveBiome(new EmptyEndCaveBiome());
	public static final EndCaveBiome EMPTY_SMARAGDANT_CAVE = registerCaveBiome(new EmptySmaragdantCaveBiome());
	public static final EndCaveBiome LUSH_SMARAGDANT_CAVE = registerCaveBiome(new LushSmaragdantCaveBiome());
	public static final EndCaveBiome EMPTY_AURORA_CAVE = registerCaveBiome(new EmptyAuroraCaveBiome());
	public static final EndCaveBiome LUSH_AURORA_CAVE = registerCaveBiome(new LushAuroraCaveBiome());
	public static final EndCaveBiome JADE_CAVE = registerCaveBiome(new JadeCaveBiome());
	
	public static void register() {
		CAVE_BIOMES.rebuild();
	}
	
	public static void onWorldLoad(long seed) {
		if (caveBiomeMap == null || caveBiomeMap.getSeed() != seed) {
			caveBiomeMap = new BiomeMap(seed, GeneratorOptions.getBiomeSizeCaves(), CAVE_BIOMES);
		}
	}
	
	public static void mutateRegistry(Registry<Biome> biomeRegistry) {
		EndBiomes.biomeRegistry = biomeRegistry;
		
		LAND_BIOMES.clearMutables();
		VOID_BIOMES.clearMutables();
		CAVE_BIOMES.clearMutables();
		
		if (FABRIC_VOID.isEmpty()) {
			loadFabricAPIBiomes();
		}
		
		Map<String, JsonObject> configs = Maps.newHashMap();
		
		biomeRegistry.forEach((biome) -> {
			if (biome.getBiomeCategory() == BiomeCategory.THEEND) {
				ResourceLocation id = biomeRegistry.getKey(biome);
				if (!id.getNamespace().equals("ultra_amplified_dimension") && Configs.BIOME_CONFIG.getBoolean(id, "enabled", true)) {
					if (!LAND_BIOMES.containsImmutable(id) && !VOID_BIOMES.containsImmutable(id) && !SUBBIOMES_UNMUTABLES.contains(id)) {
						JsonObject config = configs.get(id.getNamespace());
						if (config == null) {
							config = loadJsonConfig(id.getNamespace());
							configs.put(id.getNamespace(), config);
						}
						float fog = 1F;
						float chance = 1F;
						boolean isVoid = FABRIC_VOID.contains(id);
						boolean hasCaves = true;
						JsonElement element = config.get(id.getPath());
						if (element != null && element.isJsonObject()) {
							fog = JsonFactory.getFloat(element.getAsJsonObject(), "fog_density", 1);
							chance = JsonFactory.getFloat(element.getAsJsonObject(), "generation_chance", 1);
							isVoid = JsonFactory.getString(element.getAsJsonObject(), "type", "land").equals("void");
							hasCaves = JsonFactory.getBoolean(element.getAsJsonObject(), "has_caves", true);
						}
						EndBiome endBiome = new EndBiome(id, biome, fog, chance, hasCaves);

						if (isVoid) {
							VOID_BIOMES.addBiomeMutable(endBiome);
						}
						else {
							LAND_BIOMES.addBiomeMutable(endBiome);
						}
						ID_MAP.put(id, endBiome);
					}
				}
			}
		});
		Integrations.addBiomes();
		Configs.BIOME_CONFIG.saveChanges();
		
		rebuildPicker(LAND_BIOMES, biomeRegistry);
		rebuildPicker(VOID_BIOMES, biomeRegistry);
		rebuildPicker(CAVE_BIOMES, biomeRegistry);
		
		SUBBIOMES.forEach((endBiome) -> {
			endBiome.updateActualBiomes(biomeRegistry);
		});
		
		CLIENT.clear();
	}
	
	private static void rebuildPicker(BiomePicker picker, Registry<Biome> biomeRegistry) {
		picker.rebuild();
		picker.getBiomes().forEach((endBiome) -> {
			endBiome.updateActualBiomes(biomeRegistry);
		});
	}
	
	private static void loadFabricAPIBiomes() {
		List<ResourceKey<Biome>> biomes = Lists.newArrayList();
		biomes.addAll(getBiomes(InternalBiomeData.getEndBiomesMap().get(Biomes.SMALL_END_ISLANDS)));
		biomes.addAll(getBiomes(InternalBiomeData.getEndBarrensMap().get(Biomes.END_BARRENS)));
		biomes.forEach((key) -> FABRIC_VOID.add(key.location()));
		
		if (BetterEnd.isDevEnvironment()) {
			System.out.println("==================================");
			System.out.println("Added void biomes from Fabric API:");
			FABRIC_VOID.forEach((id) -> {
				System.out.println(id);
			});
			System.out.println("==================================");
		}
	}
	
	private static List<ResourceKey<Biome>> getBiomes(WeightedBiomePicker picker) {
		IBiomeList biomeList = (IBiomeList) (Object) picker;
		return biomeList == null ? Collections.emptyList() : biomeList.getBiomes();
	}
	
	private static JsonObject loadJsonConfig(String namespace) {
		InputStream inputstream = EndBiomes.class.getResourceAsStream("/data/" + namespace + "/end_biome_properties.json");
		if (inputstream != null) {
			return JsonFactory.getJsonObject(inputstream);
		}
		else {
			return EMPTY_JSON;
		}
	}
	
	/**
	 * Initialize registry if it was not initialized in world generation (when using mods/datapacks, that overrides the End generation)
	 * @param server - {@link MinecraftServer}
	 */
	public static void initRegistry(MinecraftServer server) {
		if (biomeRegistry == null || biomeRegistry == BuiltinRegistries.BIOME) {
			biomeRegistry = server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
		}
	}
	
	/**
	 * Registers new {@link EndBiome} and adds it to picker, can be used to add existing mod biomes into the End.
	 * @param biome - {@link Biome} instance
	 * @param type - {@link BiomeType}
	 * @param genChance - generation chance [0.0F - Infinity]
	 * @return registered {@link EndBiome}
	 */
	public static EndBiome registerBiome(Biome biome, BiomeType type, float genChance) {
		return registerBiome(biome, type, 1, genChance);
	}
	
	/**
	 * Registers new {@link EndBiome} and adds it to picker, can be used to add existing mod biomes into the End.
	 * @param biome - {@link Biome} instance
	 * @param type - {@link BiomeType}
	 * @param fogDensity - density of fog (def: 1F) [0.0F - Infinity]
	 * @param genChance - generation chance [0.0F - Infinity]
	 * @return registered {@link EndBiome}
	 */
	public static EndBiome registerBiome(Biome biome, BiomeType type, float fogDensity, float genChance) {
		EndBiome endBiome = new EndBiome(BuiltinRegistries.BIOME.getKey(biome), biome, fogDensity, genChance, true);
		if (Configs.BIOME_CONFIG.getBoolean(endBiome.getID(), "enabled", true)) {
			addToPicker(endBiome, type);
		}
		return endBiome;
	}
	
	/**
	 * Registers new {@link EndBiome} from existed {@link Biome} and put as a sub-biome into selected parent.
	 * @param biome - {@link Biome} instance
	 * @param parent - {@link EndBiome} to be linked with
	 * @param genChance - generation chance [0.0F - Infinity]
	 * @return registered {@link EndBiome}
	 */
	public static EndBiome registerSubBiome(Biome biome, EndBiome parent, float genChance, boolean hasCaves) {
		return registerSubBiome(biome, parent, 1, genChance, hasCaves);
	}
	
	/**
	 * Registers new {@link EndBiome} from existed {@link Biome} and put as a sub-biome into selected parent.
	 * @param biome - {@link Biome} instance
	 * @param parent - {@link EndBiome} to be linked with
	 * @param fogDensity - density of fog (def: 1F) [0.0F - Infinity]
	 * @param genChance - generation chance [0.0F - Infinity]
	 * @return registered {@link EndBiome}
	 */
	public static EndBiome registerSubBiome(Biome biome, EndBiome parent, float fogDensity, float genChance, boolean hasCaves) {
		EndBiome endBiome = new EndBiome(BuiltinRegistries.BIOME.getKey(biome), biome, fogDensity, genChance, hasCaves);
		if (Configs.BIOME_CONFIG.getBoolean(endBiome.getID(), "enabled", true)) {
			parent.addSubBiome(endBiome);
			SUBBIOMES.add(endBiome);
			SUBBIOMES_UNMUTABLES.add(endBiome.getID());
			ID_MAP.put(endBiome.getID(), endBiome);
		}
		return endBiome;
	}
	
	/**
	 * Put existing {@link EndBiome} as a sub-biome into selected parent.
	 * @param biome - {@link EndBiome} instance
	 * @param parent - {@link EndBiome} to be linked with
	 * @return registered {@link EndBiome}
	 */
	public static EndBiome registerSubBiome(EndBiome biome, EndBiome parent) {
		if (Configs.BIOME_CONFIG.getBoolean(biome.getID(), "enabled", true)) {
			registerBiomeDirectly(biome);
			parent.addSubBiome(biome);
			SUBBIOMES.add(biome);
			SUBBIOMES_UNMUTABLES.add(biome.getID());
			ID_MAP.put(biome.getID(), biome);
			addLandBiomeToFabricApi(biome);
		}
		return biome;
	}
	
	/**
	 * Registers {@link EndBiome} and adds it into worldgen.
	 * @param biome - {@link EndBiome} instance
	 * @param type - {@link BiomeType}
	 * @return registered {@link EndBiome}
	 */
	public static EndBiome registerBiome(EndBiome biome, BiomeType type) {
		if (Configs.BIOME_CONFIG.getBoolean(biome.getID(), "enabled", true)) {
			registerBiomeDirectly(biome);
			addToPicker(biome, type);
			ID_MAP.put(biome.getID(), biome);
			if (type == BiomeType.LAND) {
				addLandBiomeToFabricApi(biome);
			}
			else {
				addVoidBiomeToFabricApi(biome);
			}
		}
		return biome;
	}
	
	/**
	 * Put integration sub-biome {@link EndBiome} into subbiomes list and registers it.
	 * @param biome - {@link EndBiome} instance
	 * @return registered {@link EndBiome}
	 */
	public static EndBiome registerSubBiomeIntegration(EndBiome biome) {
		if (Configs.BIOME_CONFIG.getBoolean(biome.getID(), "enabled", true)) {
			registerBiomeDirectly(biome);
			SUBBIOMES.add(biome);
			SUBBIOMES_UNMUTABLES.add(biome.getID());
			ID_MAP.put(biome.getID(), biome);
			addLandBiomeToFabricApi(biome);
		}
		return biome;
	}
	
	/**
	 * Link integration sub-biome with parent.
	 * @param biome - {@link EndBiome} instance
	 * @param parent - {@link ResourceLocation} parent id
	 */
	public static void addSubBiomeIntegration(EndBiome biome, ResourceLocation parent) {
		if (Configs.BIOME_CONFIG.getBoolean(biome.getID(), "enabled", true)) {
			EndBiome parentBiome = ID_MAP.get(parent);
			if (parentBiome != null && !parentBiome.containsSubBiome(biome)) {
				parentBiome.addSubBiome(biome);
			}
		}
	}
	
	public static EndBiome registerBiome(ResourceKey<Biome> key, BiomeType type, float genChance) {
		return registerBiome(BuiltinRegistries.BIOME.get(key), type, genChance);
	}
	
	public static EndBiome registerSubBiome(ResourceKey<Biome> key, EndBiome parent, float genChance) {
		return registerSubBiome(BuiltinRegistries.BIOME.get(key), parent, genChance, true);
	}
	
	private static void addToPicker(EndBiome biome, BiomeType type) {
		if (type == BiomeType.LAND) {
			LAND_BIOMES.addBiome(biome);
		}
		else {
			VOID_BIOMES.addBiome(biome);
		}
	}

	private static void registerBiomeDirectly(EndBiome biome) {
		Registry.register(BuiltinRegistries.BIOME, biome.getID(), biome.getBiome());
	}
	
	private static void addLandBiomeToFabricApi(EndBiome biome) {
		float weight = biome.getGenChanceImmutable();
		ResourceKey<Biome> key = BuiltinRegistries.BIOME.getResourceKey(biome.getBiome()).get();
		InternalBiomeData.addEndBiomeReplacement(Biomes.END_HIGHLANDS, key, weight);
		InternalBiomeData.addEndBiomeReplacement(Biomes.END_MIDLANDS, key, weight);
	}
	
	private static void addVoidBiomeToFabricApi(EndBiome biome) {
		float weight = biome.getGenChanceImmutable();
		ResourceKey<Biome> key = BuiltinRegistries.BIOME.getResourceKey(biome.getBiome()).get();
		InternalBiomeData.addEndBiomeReplacement(Biomes.SMALL_END_ISLANDS, key, weight);
	}
	
	public static EndBiome getFromBiome(Biome biome) {
		return ID_MAP.getOrDefault(biomeRegistry.getKey(biome), END);
	}
	
	@Environment(EnvType.CLIENT)
	public static EndBiome getRenderBiome(Biome biome) {
		EndBiome endBiome = CLIENT.get(biome);
		if (endBiome == null) {
			Minecraft minecraft = Minecraft.getInstance();
			ResourceLocation id = minecraft.level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biome);
			endBiome = id == null ? END : ID_MAP.getOrDefault(id, END);
			CLIENT.put(biome, endBiome);
		}
		return endBiome;
	}
	
	public static ResourceLocation getBiomeID(Biome biome) {
		ResourceLocation id = biomeRegistry.getKey(biome);
		return id == null ? END.getID() : id;
	}

	public static EndBiome getBiome(ResourceLocation biomeID) {
		return ID_MAP.getOrDefault(biomeID, END);
	}
	
	public static Biome getActualBiome(EndBiome biome) {
		Biome actual = biome.getActualBiome();
		if (actual == null) {
			biome.updateActualBiomes(biomeRegistry);
			actual = biome.getActualBiome();
		}
		return actual;
	}

	public static List<EndBiome> getModBiomes() {
		List<EndBiome> result = Lists.newArrayList();
		result.addAll(EndBiomes.LAND_BIOMES.getBiomes());
		result.addAll(EndBiomes.VOID_BIOMES.getBiomes());
		result.addAll(EndBiomes.CAVE_BIOMES.getBiomes());
		result.addAll(SUBBIOMES);
		return result;
	}
	
	public static EndCaveBiome registerCaveBiome(EndCaveBiome biome) {
		if (Configs.BIOME_CONFIG.getBoolean(biome.getID(), "enabled", true)) {
			registerBiomeDirectly(biome);
			CAVE_BIOMES.addBiome(biome);
			ID_MAP.put(biome.getID(), biome);
		}
		return biome;
	}
	
	public static EndCaveBiome getCaveBiome(int x, int z) {
		return (EndCaveBiome) caveBiomeMap.getBiome(x, z);
	}
	
	public static boolean hasBiome(ResourceLocation biomeID) {
		return ID_MAP.containsKey(biomeID);
	}
}
