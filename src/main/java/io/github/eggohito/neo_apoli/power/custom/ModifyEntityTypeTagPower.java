package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.mixin.access.TagEntryAccessor;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizeEntityTypeTagCacheS2CPacket;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.DependencyTracker;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ModifyEntityTypeTagPower extends Power {

	private static final Map<Identifier, List<Identifier>> TAG_CACHE = new ConcurrentHashMap<>();
	private static final String TAG_PATH = RegistryKeys.getPath(RegistryKeys.ENTITY_TYPE);

	public static final MapCodec<ModifyEntityTypeTagPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(NeoApoliCodecs.UNPREFIXED_ENTITY_TYPE_TAG.fieldOf("tag").forGetter(ModifyEntityTypeTagPower::getTag))
		.apply(instance, ModifyEntityTypeTagPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyEntityTypeTagPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) ->
			NeoApoliPacketCodecs.ENTITY_TYPE_TAG.encode(buf, power.getTag()),
		(buf, properties, condition) -> new ModifyEntityTypeTagPower(properties, condition,
			NeoApoliPacketCodecs.ENTITY_TYPE_TAG.decode(buf)
		)
	);

	private final TagKey<EntityType<?>> tag;

	public ModifyEntityTypeTagPower(Properties properties, EntityCondition activeCondition, TagKey<EntityType<?>> tag) {
		super(properties, activeCondition);
		this.tag = tag;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_ENTITY_TYPE_TAG;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	@ApiStatus.Internal
	public static <T> void setCache(String directory, TagEntry.ValueGetter<T> getter, DependencyTracker<Identifier, TagGroupLoader.TagDependencies> dependencyTracker) {

		if (Objects.equals(TAG_PATH, directory)) {
			dependencyTracker.traverse((id, dependencies) -> dependencies.entries()
				.stream()
				.map(TagGroupLoader.TrackedEntry::entry)
				.filter(tagEntry -> tagEntry.resolve(getter, value -> {}))
				.map(TagEntryAccessor.class::cast)
				.filter(TagEntryAccessor::isTag)
				.forEach(entry -> TAG_CACHE
					.computeIfAbsent(id, k -> new ObjectArrayList<>())
					.add(entry.getId())));
		}

	}

	@ApiStatus.Internal
	public static void resetCache(MinecraftServer ignoredServer, LifecycledResourceManager ignoredManager) {
		TAG_CACHE.clear();
	}

	@ApiStatus.Internal
	public static void sendCache(ServerPlayerEntity player, boolean ignoredJoined) {
		ServerPlayNetworking.send(player, new SynchronizeEntityTypeTagCacheS2CPacket(TAG_CACHE));
	}

	@Environment(EnvType.CLIENT)
	@ApiStatus.Internal
	public static void receiveCache(SynchronizeEntityTypeTagCacheS2CPacket payload, ClientPlayNetworking.Context ignoredContext) {
		TAG_CACHE.clear();
		TAG_CACHE.putAll(payload.tags());
	}

	public static class Impl extends Power.Impl<ModifyEntityTypeTagPower> {

		protected Impl(@NotNull Entity holder, @NotNull ModifyEntityTypeTagPower power) {
			super(holder, power);
		}

		public boolean doesApply(TagKey<EntityType<?>> tag) {

			if (Objects.equals(power.getTag(), tag)) {
				return true;
			}

			else {

				List<Identifier> nestedTagIds = TAG_CACHE.getOrDefault(tag.id(), new ObjectArrayList<>());

				for (var nestedTagId : nestedTagIds) {

					TagKey<EntityType<?>> nestedTag = TagKey.of(RegistryKeys.ENTITY_TYPE, nestedTagId);

					if (this.doesApply(nestedTag)) {
						return true;
					}

				}

				return false;

			}

		}

	}

	public static Context createContext(@NotNull Entity entity) {
		return PowerTypes.MODIFY_ENTITY_TYPE_TAG.contextBuilder()
			.add(ContextParameters.ENTITY, entity)
			.add(ContextParameters.ENTITY_POS, entity.getPos())
			.build(entity.getWorld());
	}

	public static boolean doesApply(Context context, TagKey<EntityType<?>> tag) {
		return PowersComponent.hasPowerImpl(context.nullable(ContextParameters.ENTITY), Impl.class, impl -> impl.isActive(context) && impl.doesApply(tag));
	}

	public static boolean doesApply(Context context, RegistryEntryList<EntityType<?>> tagsEntryList) {
		return tagsEntryList.getTagKey()
			.map(tag -> doesApply(context, tag))
			.orElse(false);
	}

}
