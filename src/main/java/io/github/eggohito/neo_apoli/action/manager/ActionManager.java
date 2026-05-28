package io.github.eggohito.neo_apoli.action.manager;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@ApiStatus.NonExtendable
public class ActionManager {

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

	public record ClientboundActionsUpdatePacket(Map<ResourceLocation, Action> actions) implements CustomPacketPayload {

		private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, Action>> ACTIONS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC, Action.STREAM_CODEC);

		public static final Type<ClientboundActionsUpdatePacket> TYPE = new Type<>(NeoApoli.id("clientbound/update_actions"));
		public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundActionsUpdatePacket> CODEC = ACTIONS_CODEC.map(ClientboundActionsUpdatePacket::new, ClientboundActionsUpdatePacket::actions);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle() {
			ActionManager.actions = ImmutableMap.copyOf(actions());
		}

	}

	public record ClientboundTagsUpdatePacket(Map<ResourceLocation, List<Action>> tags) implements CustomPacketPayload {

		private static final StreamCodec<ByteBuf, Action> ENTRY_CODEC = ResourceLocation.STREAM_CODEC.map(ActionManager::get, ActionManager::getId);
		private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, List<Action>>> TAGS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC, ENTRY_CODEC.apply(ByteBufCodecs.list()));

		public static final Type<ClientboundTagsUpdatePacket> TYPE = new Type<>(NeoApoli.id("clientbound/update_action_tags"));
		public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTagsUpdatePacket> CODEC = TAGS_CODEC.map(ClientboundTagsUpdatePacket::new, ClientboundTagsUpdatePacket::tags);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle() {
			ActionManager.tags = ImmutableMap.copyOf(tags());
		}

	}

}
