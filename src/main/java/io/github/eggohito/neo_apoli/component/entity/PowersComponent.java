package io.github.eggohito.neo_apoli.component.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.PowerIdentifier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public final class PowersComponent implements Component, AutoSyncedComponent, ClientTickingComponent, ServerTickingComponent {

	private static final int FULL_SYNC_ID = 0;
	private static final int GRANT_SYNC_ID = 1;
	private static final int REVOKE_SYNC_ID = 2;

	private final Map<PowerIdentifier, Power> powers;
	private final Map<PowerIdentifier, Set<Identifier>> sources;

	private final Entity holder;

	public PowersComponent(Entity holder) {
		this.powers = new ConcurrentHashMap<>();
		this.sources = new ConcurrentHashMap<>();
		this.holder = holder;
	}

	@Override
	public void writeToNbt(NbtCompound rootNbt, RegistryWrapper.WrapperLookup wrapperLookup) {

		RegistryOps<NbtElement> nbtOps = wrapperLookup.getOps(NbtOps.INSTANCE);
		NbtList powersNbt = new NbtList();

		this.powers.forEach((id, power) -> {

			Set<Identifier> sources = this.sources.getOrDefault(id, Collections.emptySet());
			NbtElement data = power.encodeData(nbtOps)
				.mapError(err -> "Error trying to encode data of power \"" + id + "\" to NBT of entity " + holder.getName().getString() + " (defaulting to empty NBT): " + err)
				.resultOrPartial(NeoApoli.LOGGER::warn)
				.orElseGet(NbtCompound::new);

			Entry<NbtElement> entry = new Entry<>(id, power.getType(), sources, new Dynamic<>(nbtOps, data));
			Entry.CODEC.encoder().encodeStart(nbtOps, entry)
				.ifSuccess(powersNbt::add)
				.ifError(error -> NeoApoli.LOGGER.warn("Error trying to encode power \"{}\" to NBT of entity {} (skipping): {}", id, holder.getName().getString(), error.message()));

		});

		rootNbt.put("powers", powersNbt);

	}

	@Override
	public void readFromNbt(NbtCompound rootNbt, RegistryWrapper.WrapperLookup wrapperLookup) {

		RegistryOps<NbtElement> nbtOps = wrapperLookup.getOps(NbtOps.INSTANCE);

		NbtList powersNbt = rootNbt.getList("powers", NbtElement.COMPOUND_TYPE);
		ListIterator<NbtElement> powersNbtIterator = powersNbt.listIterator();

		this.powers.clear();
		this.sources.clear();

		while (powersNbtIterator.hasNext()) {

			NbtElement powerNbt = powersNbtIterator.next();
			int index = powersNbtIterator.nextIndex();

			try {

				Entry<?> entry = Entry.CODEC.decoder()
					.parse(nbtOps, powerNbt)
					.getOrThrow();

				PowerIdentifier powerId = entry.powerId();
				DataResult<Power> powerResult = PowerManager.getAsResult(powerId)
					.flatMap(p -> Power.BASE_CODEC.encodeStart(nbtOps, p)
						.flatMap(nbtElement -> Power.BASE_CODEC.parse(nbtOps, nbtElement)));

				switch (powerResult) {
					case DataResult.Success<Power> success -> {

						Power power = success.value();
						Dynamic<NbtElement> data = entry.data().convert(nbtOps);

						try {

							if (Objects.equals(entry.powerType(), power.getType())) {
								power.decodeData(nbtOps, data.getValue());
							}

							else {
								NeoApoli.LOGGER.warn("Power type of power \"{}\" has changed. Its data won't be recovered and will be skipped.", powerId);
							}

						}

						catch (Exception e) {
							NeoApoli.LOGGER.warn("There was a problem decoding data of power \"{}\" from NBT (skipping): {}", powerId, e);
						}

						this.powers.put(powerId, power);
						this.sources.put(powerId, entry.sources());

					}
					case DataResult.Error<Power> error ->
						NeoApoli.LOGGER.warn("Error decoding power \"{}\" from powers component of entity {} (skipping): {}", powerId, holder.getName().getString(), error.message());
				}

			}

			catch (Exception e) {
				NeoApoli.LOGGER.warn("Error decoding power NBT element ({}) at cardinal_components.\"{}\".powers[{}] (skipping): {}", powerNbt, NeoApoliEntityComponents.POWERS.getId(), index, e);
			}

		}

	}

	@Override
	public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
		buf.writeVarInt(FULL_SYNC_ID);
		AutoSyncedComponent.super.writeSyncPacket(buf, recipient);
	}

	@Override
	public void applySyncPacket(RegistryByteBuf buf) {

		switch (buf.readVarInt()) {
			case FULL_SYNC_ID ->
				AutoSyncedComponent.super.applySyncPacket(buf);
			case GRANT_SYNC_ID ->
				Synchronizer.GRANT.receive(buf, this);
			case REVOKE_SYNC_ID ->
				Synchronizer.REVOKE.receive(buf, this);
			default ->
				NeoApoli.LOGGER.warn("Entity {} (UUID: {}) received powers component sync packet with unknown sync ID!", holder.getName().getString(), holder.getUuidAsString());
		}

	}

	@Override
	public void clientTick() {

	}

	@Override
	public void serverTick() {

	}

	public boolean grantPower(PowerIdentifier id, Identifier source) {
		return !holder.getWorld().isClient()
			&& grantPowerSideAgnostic(id, source);
	}

	private boolean grantPowerSideAgnostic(PowerIdentifier id, Identifier source) {
		return PowerManager.getAsResult(id)
			.ifError(error -> NeoApoli.LOGGER.warn("Error trying to grant {} from source '{}' to entity {} (skipping): {}", StringUtils.uncapitalize(id.toString()), source, (holder.getName().getString() + " (UUID: " + holder.getUuidAsString() + ")"), error.message()))
			.mapOrElse(power -> grantPower(id, power, source), error -> false);
	}

	private boolean grantPower(PowerIdentifier id, Power power, Identifier source) {

		List<Power> grantedPowers = new ObjectArrayList<>();
		boolean wasPreviouslyGranted = this.powers.containsKey(id);

		boolean granted = this.grantPower(id, power, source, grantedPowers::add);
		for (Power grantedPower : grantedPowers) {

			grantedPower.onAdded(holder);

			if (!wasPreviouslyGranted) {
				grantedPower.onGained(holder);
			}

		}

		return granted;

	}

	private boolean grantPower(PowerIdentifier id, Power power, Identifier source, Consumer<Power> grantedConsumer) {

		Set<Identifier> sources = this.sources.computeIfAbsent(id, k -> new ObjectOpenHashSet<>());
		if (sources.contains(source)) {
			return false;
		}

		RegistryOps<NbtElement> nbtOps = holder.getRegistryManager().getOps(NbtOps.INSTANCE);
		power = Power.BASE_CODEC.encodeStart(nbtOps, power)
			.flatMap(nbtElement -> Power.BASE_CODEC.parse(nbtOps, nbtElement))
			.getOrThrow();

		grantedConsumer.accept(power);

		this.powers.put(id, power);
		sources.add(source);

		if (power instanceof MultiplePower multiplePower) {
			multiplePower.getSubPowers().values()
				.stream()
				.map(subPower -> Pair.of(PowerManager.getId(subPower), subPower))
				.forEach(pair -> this.grantPower(pair.getFirst(), pair.getSecond(), source, grantedConsumer));
		}

		return true;

	}

	public boolean revokePower(PowerIdentifier id, Identifier source) {
		return !holder.getWorld().isClient()
			&& revokePowerSideAgnostic(id, source);
	}

	private boolean revokePowerSideAgnostic(PowerIdentifier id, Identifier source) {
		return PowerManager.getAsResult(id)
			.ifError(error -> NeoApoli.LOGGER.warn("Error trying to revoke {} from source '{}' on entity {} (skipping): {}", StringUtils.uncapitalize(id.toString()), source, (holder.getName().getString() + " (UUID: " + holder.getUuidAsString() + ")"), error.message()))
			.mapOrElse(power -> revokePower(id, power, source), error -> false);
	}

	private boolean revokePower(PowerIdentifier id, Power power, Identifier source) {

		List<PowerIdentifier> revokedPowers = new ObjectArrayList<>();
		boolean result = this.revokePower(id, power, source, revokedPowers::add);

		powers.keySet().removeIf(revokedPowers::contains);
		sources.keySet().removeIf(revokedPowers::contains);

		return result;

	}

	private boolean revokePower(PowerIdentifier id, Power power, Identifier source, Consumer<PowerIdentifier> revokedConsumer) {

		Set<Identifier> sources = this.sources.computeIfAbsent(id, k -> new ObjectOpenHashSet<>());
		if (!sources.contains(source)) {
			return false;
		}

		sources.remove(source);

		if (powers.containsKey(id)) {

			power = powers.get(id);
			revokedConsumer.accept(id);

			power.onRemoved(holder);

			if (sources.isEmpty()) {
				power.onLost(holder);
			}

		}

		if (power instanceof MultiplePower multiplePower) {
			multiplePower.getSubPowers().values()
				.stream()
				.map(subPower -> Pair.of(PowerManager.getId(subPower), subPower))
				.forEach(pair -> this.revokePower(pair.getFirst(), pair.getSecond(), source, revokedConsumer));
		}

		return true;

	}

	public record Entry<T>(PowerIdentifier powerId, PowerType<?> powerType, Set<Identifier> sources, Dynamic<T> data) {

		public static final MapCodec<Entry<?>> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			PowerIdentifier.CODEC.fieldOf("id").forGetter(Entry::powerId),
			NeoApoliRegistries.POWER_TYPE.getCodec().fieldOf("type").forGetter(Entry::powerType),
			NeoApoliCodecs.MUTABLE_NON_EMPTY_IDENTIFIER_SET.fieldOf("sources").forGetter(Entry::sources),
			Codec.PASSTHROUGH.fieldOf("data").forGetter(Entry::data)
		).apply(instance, Entry::new));

		public static final PacketCodec<RegistryByteBuf, Entry<?>> PACKET_CODEC = PacketCodec.tuple(
			PowerIdentifier.PACKET_CODEC, Entry::powerId,
			PacketCodecs.registryValue(NeoApoliRegistryKeys.POWER_TYPE), Entry::powerType,
			NeoApoliPacketCodecs.MUTABLE_IDENTIFIER_SET, Entry::sources,
			PacketCodecs.unlimitedCodec(Codec.PASSTHROUGH), Entry::data,
			Entry::new
		);

	}

	public static final class Synchronizer<T> {

		public static final Synchronizer<Map<Identifier, Collection<PowerIdentifier>>> GRANT = new Synchronizer<>(
			GRANT_SYNC_ID,
			(buf, map) -> buf.writeMap(
				map,
				PacketByteBuf::writeIdentifier,
				(valueBuf, ids) -> valueBuf.writeCollection(ids, PowerIdentifier.PACKET_CODEC)
			),
			buf -> buf.readMap(
				PacketByteBuf::readIdentifier,
				valueBuf -> valueBuf.readCollection(ObjectArrayList::new, PowerIdentifier.PACKET_CODEC)
			),
			(powersComponent, map) -> map.forEach((source, ids) ->
				ids.forEach(id -> powersComponent.grantPowerSideAgnostic(id, source))
			)
		);

		public static final Synchronizer<Map<Identifier, Collection<PowerIdentifier>>> REVOKE = new Synchronizer<>(
			REVOKE_SYNC_ID,
			GRANT.encoder,
			GRANT.decoder,
			(powersComponent, map) -> map.forEach((source, ids) ->
				ids.forEach(id -> powersComponent.revokePowerSideAgnostic(id, source))
			)
		);

		private final int id;
		private final BiConsumer<RegistryByteBuf, T> encoder;
		private final Function<RegistryByteBuf, T> decoder;
		private final BiConsumer<PowersComponent, T> processor;

		private Synchronizer(int id, BiConsumer<RegistryByteBuf, T> encoder, Function<RegistryByteBuf, T> decoder, BiConsumer<PowersComponent, T> processor) {
			this.id = id;
			this.encoder = encoder;
			this.decoder = decoder;
			this.processor = processor;
		}

		public void sync(Entity holder, T t) {
			NeoApoliEntityComponents.POWERS.sync(holder, (buf, recipient) -> this.send(buf, t));
		}

		public void send(RegistryByteBuf buf, T t) {
			buf.writeVarInt(id);
			encoder.accept(buf, t);
		}

		public void receive(RegistryByteBuf buf, PowersComponent powersComponent) {
			processor.accept(powersComponent, decoder.apply(buf));
		}

	}

}
