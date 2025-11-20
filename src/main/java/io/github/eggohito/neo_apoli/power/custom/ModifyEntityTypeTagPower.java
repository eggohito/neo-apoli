package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.mixin.access.TagEntryAccessor;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizeEntityTypeTagCacheS2CPacket;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ModifyEntityTypeTagPower extends Power {

	public static final MapCodec<ModifyEntityTypeTagPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(TagKey.codec(RegistryKeys.ENTITY_TYPE).fieldOf("tag").forGetter(ModifyEntityTypeTagPower::getTag))
		.apply(instance, ModifyEntityTypeTagPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyEntityTypeTagPower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.PACKET_CODEC), Power::getActiveCondition,
		TagKey.packetCodec(RegistryKeys.ENTITY_TYPE), ModifyEntityTypeTagPower::getTag,
		ModifyEntityTypeTagPower::new
	);

	private static final Map<TagKey<EntityType<?>>, Set<TagKey<EntityType<?>>>> NESTED_TAGS_CACHE = new ConcurrentHashMap<>();
	private final TagKey<EntityType<?>> tag;

	public ModifyEntityTypeTagPower(Optional<Condition> activeCondition, TagKey<EntityType<?>> tag) {
		super(activeCondition);
		this.tag = tag;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_ENTITY_TYPE_TAG;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {
		super.validate(reporter);
		RegistryUtil.validateTag(reporter.makeChild(".tag"), this.getTag());
	}

	@ApiStatus.Internal
	public static <T> void setCache(TagEntry.ValueGetter<T> getter, DependencyTracker<Identifier, TagGroupLoader.TagDependencies> dependencyTracker) {
		dependencyTracker.traverse((id, dependencies) -> dependencies.entries()
			.stream()
			.map(TagGroupLoader.TrackedEntry::entry)
			.filter(tagEntry -> tagEntry.resolve(getter, value -> {}))
			.map(TagEntryAccessor.class::cast)
			.filter(TagEntryAccessor::isTag)
			.map(entry -> TagKey.of(RegistryKeys.ENTITY_TYPE, entry.getId()))
			.forEach(nestedTag -> NESTED_TAGS_CACHE
				.computeIfAbsent(TagKey.of(RegistryKeys.ENTITY_TYPE, id), k -> new ObjectOpenHashSet<>())
				.add(nestedTag)));
	}

	@ApiStatus.Internal
	public static void resetCache(MinecraftServer ignoredServer, LifecycledResourceManager ignoredManager) {
		NESTED_TAGS_CACHE.clear();
	}

	@ApiStatus.Internal
	public static void sendCache(ServerPlayerEntity player, boolean ignoredJoined) {
		ServerPlayNetworking.send(player, new SynchronizeEntityTypeTagCacheS2CPacket(NESTED_TAGS_CACHE));
	}

	@Environment(EnvType.CLIENT)
	@ApiStatus.Internal
	public static void receiveCache(SynchronizeEntityTypeTagCacheS2CPacket payload, ClientPlayNetworking.Context ignoredContext) {
		NESTED_TAGS_CACHE.clear();
		NESTED_TAGS_CACHE.putAll(payload.tags());
	}

	public static class Instance extends Power.Instance<ModifyEntityTypeTagPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyEntityTypeTagPower power) {
			super(holder, power);
		}

		public boolean doesApply(TagKey<EntityType<?>> tag) {

			if (Objects.equals(power.getTag(), tag)) {
				return true;
			}

			else {

				Set<TagKey<EntityType<?>>> nestedTags = NESTED_TAGS_CACHE.getOrDefault(tag, new ObjectOpenHashSet<>());

				for (var nestedTag: nestedTags) {

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
			.add(NeoApoliContextParameters.THIS_ENTITY, entity)
			.add(NeoApoliContextParameters.ENTITY_POS, entity.getPos())
			.build(entity.getWorld());
	}

	public static boolean doesApply(Context context, TagKey<EntityType<?>> tag) {

		Entity entity = context.nullable(NeoApoliContextParameters.THIS_ENTITY);
		List<Instance> instances = PowersComponent.getInstances(entity, Instance.class);

		for (var instance : instances) {

			try {

				if (context.markActive(instance) && instance.isActive(context) && instance.doesApply(tag)) {
					return true;
				}

			}

			finally {
				context.markInActive(instance);
			}

		}

		return false;

	}

	public static boolean doesApply(Context context, RegistryEntryList<EntityType<?>> tagsEntryList) {
		return tagsEntryList.getTagKey()
			.map(tag -> doesApply(context, tag))
			.orElse(false);
	}

}
