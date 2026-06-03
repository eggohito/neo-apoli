package io.github.eggohito.neo_apoli.action.manager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.util.Reporter;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@ApiStatus.NonExtendable
public class ActionManager {

	private static final StreamCodec<ByteBuf, Map<ResourceLocation, Tag>> ACTIONS_STREAM_CODEC = ByteBufCodecs.map(Object2ObjectLinkedOpenHashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.TRUSTED_TAG);
	private static final StreamCodec<ByteBuf, Map<ResourceLocation, List<ResourceLocation>>> TAGS_STREAM_CODEC = ByteBufCodecs.map(Object2ObjectLinkedOpenHashMap::new, ResourceLocation.STREAM_CODEC, ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()));

	public static final ResourceLocation ID = NeoApoli.id("manager/action");

	protected static volatile ImmutableMap<ResourceLocation, Action> actions = ImmutableMap.of();
	protected static volatile ImmutableMap<ResourceLocation, List<Action>> tags = ImmutableMap.of();

	public ActionManager() {
		//  Disallow extending non-internal classes
		var ignored = (ServerActionManager) this;
	}

	public static DataResult<Action> getAsResult(ResourceLocation id) {
		var candidate = actions.get(id);
		return candidate != null
			? DataResult.success(candidate)
			: DataResult.error(() -> "Unknown action: \"" + id + "\"");
	}

	public static Action get(ResourceLocation id) {
		return getAsResult(id).getOrThrow();
	}

	public static DataResult<ResourceLocation> getIdAsResult(Action action) {

		for (var candidate : actions.entrySet()) {

			if (candidate.getValue() == action) {
				return DataResult.success(candidate.getKey());
			}

		}

		return DataResult.error(() -> "No ID found for " + action);

	}

	public static ResourceLocation getId(Action action) {
		return getIdAsResult(action).getOrThrow();
	}

	public static DataResult<List<Action>> getTag(ResourceLocation id) {
		var candidates = tags.get(id);
		return candidates != null
			? DataResult.success(candidates)
			: DataResult.error(() -> "Unknown action tag: \"" + id + "\"");
	}

	public static List<Action> getTagOrEmpty(ResourceLocation id) {
		return getTag(id)
			.result()
			.orElseGet(List::of);
	}

	public static Iterable<ResourceLocation> ids() {
		return actions.keySet();
	}

	public static Iterable<Action> actions() {
		return actions.values();
	}

	public static Iterable<ResourceLocation> tags() {
		return tags.keySet();
	}

	public static boolean contains(ResourceLocation id) {
		return getAsResult(id).isSuccess();
	}

	public static boolean containsId(Action action) {
		return getIdAsResult(action).isSuccess();
	}

	protected static void send(RegistryAccess registryAccess, Consumer<CustomPacketPayload> sender) {
		sender.accept(new ClientboundUpdateActionsPacket(packActions(registryAccess.createSerializationContext(NbtOps.INSTANCE))));
		sender.accept(new ClientboundUpdateActionTagsPacket(packTags()));
	}

	protected static Map<ResourceLocation, Tag> packActions(RegistryOps<Tag> ops) {

		Map<ResourceLocation, Tag> actions = new Object2ObjectLinkedOpenHashMap<>();
		for (var entry : ActionManager.actions.entrySet()) {

			ResourceLocation id = entry.getKey();
			Action action = entry.getValue();

			Action.CODEC.encodeStart(ops, action)
				.ifError(error -> NeoApoli.LOGGER.error("Couldn't encode action \"{}\" during the syncing process (skipping): {}", id, error))
				.ifSuccess(tag -> actions.put(id, tag));

		}

		return actions;

	}

	protected static ImmutableMap<ResourceLocation, Action> unpackActions(RegistryAccess registryAccess, Map<ResourceLocation, Tag> packed) {

		ImmutableMap.Builder<ResourceLocation, Action> builder = ImmutableMap.builder();
		RegistryOps<Tag> ops = registryAccess.createSerializationContext(NbtOps.INSTANCE);

		for (var entry : packed.entrySet()) {

			ResourceLocation id = entry.getKey();
			Tag tag = entry.getValue();

			Action.CODEC.parse(ops, tag)
				.ifError(error -> NeoApoli.LOGGER.error("Couldn't receive action \"{}\" from the server: {}", id, error.message()))
				.ifSuccess(action -> builder.put(id, action));

		}

		return builder.build();

	}

	protected static Map<ResourceLocation, List<ResourceLocation>> packTags() {

		Map<ResourceLocation, List<ResourceLocation>> tags = new Object2ObjectLinkedOpenHashMap<>();
		for (var tag : ActionManager.tags.entrySet()) {

			ResourceLocation tagId = tag.getKey();
			List<Action> tagEntries = tag.getValue();

			for (var tagEntry : tagEntries) {
				getIdAsResult(tagEntry).ifSuccess(id -> tags
					.computeIfAbsent(tagId, k -> new ObjectArrayList<>())
					.add(id));
			}

		}

		return tags;

	}

	protected static ImmutableMap<ResourceLocation, List<Action>> unpackTags(Map<ResourceLocation, List<ResourceLocation>> packed) {

		ImmutableMap.Builder<ResourceLocation, List<Action>> build = ImmutableMap.builder();
		for (var entry : packed.entrySet()) {

			ResourceLocation tagId = entry.getKey();
			List<ResourceLocation> actionIds = entry.getValue();

			ImmutableList.Builder<Action> actionsBuilder = ImmutableList.builder();
			Reporter reporter = new Reporter("{\"#" + tagId + "\"}");

			for (var actionId : actionIds) {
				ActionManager.getAsResult(actionId)
					.ifError(error -> reporter.forChild(".\"" + actionId + "\"").report(error.message()))
					.ifSuccess(actionsBuilder::add);
			}

			reporter.getErrorsFlattened().ifPresent(errors -> NeoApoli.LOGGER.error("Couldn't properly receive action tag \"{}\" due to errors {}", tagId, errors));
			build.put(tagId, actionsBuilder.build());

		}

		return build.build();

	}

	public record SynchronizeTask(RegistryAccess registryAccess) implements ConfigurationTask {

		public static final Type TYPE = new Type(NeoApoli.id("task/synchronize_actions").toString());

		@Override
		public void start(Consumer<Packet<?>> task) {
			send(registryAccess, packet -> task.accept(ServerConfigurationNetworking.createS2CPacket(packet)));
			task.accept(ServerConfigurationNetworking.createS2CPacket(ClientboundSyncInitiatedPacket.INSTANCE));
		}

		@Override
		public @NotNull Type type() {
			return TYPE;
		}

	}

	public enum ServerboundSyncAcknowledgedPacket implements CustomPacketPayload {

		INSTANCE;

		public static final Type<ServerboundSyncAcknowledgedPacket> TYPE = new Type<>(NeoApoli.id("serverbound/actions/sync_acknowledged"));
		public static final StreamCodec<ByteBuf, ServerboundSyncAcknowledgedPacket> CODEC = StreamCodec.unit(INSTANCE);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle(ServerConfigurationNetworking.Context context) {
			context.networkHandler().completeTask(SynchronizeTask.TYPE);
		}

	}

	public enum ClientboundSyncInitiatedPacket implements CustomPacketPayload {

		INSTANCE;

		public static final Type<ClientboundSyncInitiatedPacket> TYPE = new Type<>(NeoApoli.id("clientbound/actions/sync_initiated"));
		public static final StreamCodec<ByteBuf, ClientboundSyncInitiatedPacket> CODEC = StreamCodec.unit(INSTANCE);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle(PacketSender sender) {
			sender.sendPacket(ServerboundSyncAcknowledgedPacket.INSTANCE);
		}

	}

	public record ClientboundUpdateActionsPacket(Map<ResourceLocation, Tag> actions) implements CustomPacketPayload {

		public static final Type<ClientboundUpdateActionsPacket> TYPE = new Type<>(NeoApoli.id("clientbound/update_actions"));
		public static final StreamCodec<ByteBuf, ClientboundUpdateActionsPacket> CODEC = ACTIONS_STREAM_CODEC.map(ClientboundUpdateActionsPacket::new, ClientboundUpdateActionsPacket::actions);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle(RegistryAccess registryAccess) {
			ActionManager.actions = unpackActions(registryAccess, actions());
		}

	}

	public record ClientboundUpdateActionTagsPacket(Map<ResourceLocation, List<ResourceLocation>> tags) implements CustomPacketPayload {

		public static final Type<ClientboundUpdateActionTagsPacket> TYPE = new Type<>(NeoApoli.id("clientbound/update_action_tags"));
		public static final StreamCodec<ByteBuf, ClientboundUpdateActionTagsPacket> CODEC = TAGS_STREAM_CODEC.map(ClientboundUpdateActionTagsPacket::new, ClientboundUpdateActionTagsPacket::tags);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle() {
			ActionManager.tags = unpackTags(tags());
		}

	}

}
