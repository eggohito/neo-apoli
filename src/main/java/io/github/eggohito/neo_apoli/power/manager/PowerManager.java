package io.github.eggohito.neo_apoli.power.manager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

@ApiStatus.NonExtendable
public class PowerManager {

	public static final ResourceLocation ID = NeoApoli.id("manager/power");
	public static final TagEntry.Lookup<PowerHolder<?>> TAG_LOOKUP = new TagEntry.Lookup<>() {

		@Override
		public @Nullable PowerHolder<?> element(ResourceLocation id, boolean required) {
			return getAsResult(PowerIdentifier.of(id)).result().orElse(null);
		}

		@Override
		public @Nullable Collection<PowerHolder<?>> tag(ResourceLocation id) {
			return getTag(id).result().orElse(null);
		}

		@Override
		public String toString() {
			return "Power manager";
		}

	};

	protected static volatile ImmutableMap<PowerIdentifier, PowerHolder<?>> powers = ImmutableMap.of();
	protected static volatile ImmutableMap<ResourceLocation, List<PowerHolder<?>>> tags = ImmutableMap.of();

	public PowerManager() {
		//  Disallow extending non-internal classes
		var ignored = (ServerPowerManager) this;
	}

	public static DataResult<PowerHolder<?>> getAsResult(PowerIdentifier id) {
		var candidate = powers.get(id);
		return candidate != null
			? DataResult.success(candidate)
			: DataResult.error(() -> "Unknown " + id.asDisplayString(false));
	}

	public static PowerHolder<?> get(PowerIdentifier id) {
		return getAsResult(id).getOrThrow();
	}

	public static DataResult<PowerIdentifier> getIdAsResult(Power power) {

		for (var candidate : powers.values()) {

			if (candidate.value() == power) {
				return DataResult.success(candidate.id());
			}

		}

		return DataResult.error(() -> "No ID found for " + power);

	}

	public static PowerIdentifier getId(Power power) {
		return getIdAsResult(power).getOrThrow();
	}

	public static DataResult<List<PowerHolder<?>>> getTag(ResourceLocation id) {
		var candidates = tags.get(id);
		return candidates != null
			? DataResult.success(candidates)
			: DataResult.error(() -> "Unknown power tag: \"" + id + "\"");
	}

	public static List<PowerHolder<?>> getTagOrEmpty(ResourceLocation id) {
		return getTag(id)
			.result()
			.orElseGet(List::of);
	}

	public static Iterable<PowerIdentifier> ids() {
		return powers.keySet();
	}

	public static Iterable<PowerHolder<?>> powers() {
		return powers.values();
	}

	public static Iterable<ResourceLocation> tags() {
		return tags.keySet();
	}

	public static boolean contains(PowerIdentifier id) {
		return getAsResult(id).isSuccess();
	}

	public static boolean containsId(Power power) {
		return getIdAsResult(power).isSuccess();
	}

	protected static void handle(PowerHolder<?> powerHolder, BiConsumer<PowerIdentifier, PowerHolder<?>> handler) {

		handler.accept(powerHolder.id(), powerHolder);

		if (powerHolder.value() instanceof MultiplePower(ImmutableSet<PowerHolder<?>> subPowers)) {

			if (powerHolder.id().isSubPower()) {
				throw new IllegalStateException("Tried to register " + powerHolder.id().asDisplayString(false) + " with \"" + MultiplePower.ID + "\" power type, which is not allowed!");
			}

			else {
				subPowers.forEach(subPower -> handle(subPower, handler));
			}

		}

	}

	public record ClientboundUpdatePacket(Map<PowerIdentifier, PowerHolder<?>> powers, Map<ResourceLocation, List<PowerHolder<?>>> tags) implements CustomPacketPayload {

		public static final Type<ClientboundUpdatePacket> TYPE = new Type<>(ID.withPath(path -> "clientbound/" + path + "/update"));
		public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdatePacket> CODEC = StreamCodec.ofMember(ClientboundUpdatePacket::send, ClientboundUpdatePacket::receive);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		private static ClientboundUpdatePacket receive(RegistryFriendlyByteBuf buf) {

			Map<PowerIdentifier, PowerHolder<?>> powers = new Object2ObjectLinkedOpenHashMap<>();
			int powersCount = buf.readInt();

			for (int i = 0; i < powersCount; i++) {

				PowerIdentifier id = PowerIdentifier.STREAM_CODEC.decode(buf);

				try {
					handle(PowerHolder.STREAM_CODEC.decode(buf), powers::put);
				}

				catch (Exception e) {
					NeoApoli.LOGGER.error("Couldn't decode {} during the syncing process", id.asDisplayString(false), e);
					throw e;
				}

			}

			Map<ResourceLocation, List<PowerHolder<?>>> tags = new Object2ObjectLinkedOpenHashMap<>();
			int tagsCount = buf.readInt();

			for (int i = 0; i < tagsCount; i++) {

				ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);
				int count = buf.readInt();

				for (int j = 0; j < count; j++) {

					try {

						PowerIdentifier holderId = PowerIdentifier.STREAM_CODEC.decode(buf);
						PowerHolder<?> holder = Objects.requireNonNull(powers.get(holderId), "Unknown " + holderId.asDisplayString(false));

						tags
							.computeIfAbsent(id, k -> new ObjectArrayList<>())
							.add(holder);

					}

					catch (Exception e) {
						NeoApoli.LOGGER.error("Couldn't decode power tag \"{}\" during the syncing process", id, e);
						throw e;
					}

				}

			}

			return new ClientboundUpdatePacket(powers, tags);

		}

		private void send(RegistryFriendlyByteBuf buf) {

			Map<PowerIdentifier, PowerHolder<?>> filtered = new Object2ObjectLinkedOpenHashMap<>(powers());
			filtered.keySet().removeIf(PowerIdentifier::isSubPower);

			buf.writeInt(filtered.size());

			for (var powerEntry : filtered.entrySet()) {

				PowerIdentifier id = powerEntry.getKey();
				PowerIdentifier.STREAM_CODEC.encode(buf, id);

				try {
					PowerHolder.STREAM_CODEC.encode(buf, powerEntry.getValue());
				}

				catch (Exception e) {
					NeoApoli.LOGGER.error("Couldn't encode {} during the syncing process", id.asDisplayString(false), e);
					throw e;
				}

			}

			buf.writeInt(tags().size());

			for (var tagEntry : tags().entrySet()) {

				ResourceLocation id = tagEntry.getKey();
				ResourceLocation.STREAM_CODEC.encode(buf, id);

				List<PowerHolder<?>> holders = tagEntry.getValue();
				buf.writeInt(holders.size());

				for (var holder : holders) {
					PowerIdentifier.STREAM_CODEC.encode(buf, holder.id());
				}

			}
			
		}

	}

}
