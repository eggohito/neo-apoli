package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.mixin.access.TagEntryAccessor;
import io.github.eggohito.neo_apoli.network.packet.s2c.SynchronizeEntityTypeTagCacheS2CPacket;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.DependencySorter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ModifyEntityTypeTagPower extends Power {

	public static final MapCodec<ModifyEntityTypeTagPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(CodecUtil.hashedTag(Registries.ENTITY_TYPE).fieldOf("tag").forGetter(ModifyEntityTypeTagPower::getTag))
		.apply(instance, ModifyEntityTypeTagPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyEntityTypeTagPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		TagKey.streamCodec(Registries.ENTITY_TYPE), ModifyEntityTypeTagPower::getTag,
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
		return new io.github.eggohito.neo_apoli.power.custom.ModifyEntityTypeTagPower.Instance(holder, this);
	}

	@Override
	public void validate(ProblemReporter reporter) {
		super.validate(reporter);
		RegistryUtil.validateTag(reporter.forChild(".tag"), this.getTag());
	}

	@ApiStatus.Internal
	public static <T> void setCache(TagEntry.Lookup<T> getter, DependencySorter<ResourceLocation, TagLoader.SortingEntry> dependencyTracker) {
		dependencyTracker.orderByDependencies((id, dependencies) -> dependencies.entries()
			.stream()
			.map(TagLoader.EntryWithSource::entry)
			.filter(tagEntry -> tagEntry.build(getter, value -> {}))
			.map(TagEntryAccessor.class::cast)
			.filter(TagEntryAccessor::isTag)
			.map(entry -> TagKey.create(Registries.ENTITY_TYPE, entry.getId()))
			.forEach(nestedTag -> NESTED_TAGS_CACHE
				.computeIfAbsent(TagKey.create(Registries.ENTITY_TYPE, id), k -> new ObjectOpenHashSet<>())
				.add(nestedTag)));
	}

	@ApiStatus.Internal
	public static void resetCache(MinecraftServer ignoredServer, CloseableResourceManager ignoredManager) {
		NESTED_TAGS_CACHE.clear();
	}

	@ApiStatus.Internal
	public static void sendCache(ServerPlayer player, boolean ignoredJoined) {
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
			.add(NeoApoliContextKeys.THIS_ENTITY, entity)
			.add(NeoApoliContextKeys.THIS_POS, entity.position())
			.build(entity.level());
	}

	public static boolean doesApply(Context context, TagKey<EntityType<?>> tag) {

		Entity entity = context.nullable(NeoApoliContextKeys.THIS_ENTITY);
		List<io.github.eggohito.neo_apoli.power.custom.ModifyEntityTypeTagPower.Instance> instances = PowersComponent.getInstances(entity, io.github.eggohito.neo_apoli.power.custom.ModifyEntityTypeTagPower.Instance.class);

		for (var instance : instances) {

			ProblemReporter reporter = instance.createReporter();
			Context instanceContext = ContextImpl.of(context, builder -> builder.withReporter(reporter));

			try {

				if (instanceContext.markActive(instance) && instance.isActive(instanceContext) && instance.doesApply(tag)) {
					return true;
				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

		return false;

	}

	public static boolean doesApply(Context context, HolderSet<EntityType<?>> tagsEntryList) {
		return tagsEntryList.unwrapKey()
			.map(tag -> doesApply(context, tag))
			.orElse(false);
	}

}
