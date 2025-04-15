package io.github.eggohito.neo_apoli.power.internal;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public class MultiplePower extends Power {

	public static final Identifier ID = NeoApoli.id("multiple");

	//	TODO: This set of filters should be controllable via config
	public static final Set<Pattern> SUB_POWER_KEY_FILTERS = Util.make(new ObjectOpenHashSet<>(), filters -> {

		Properties.CODEC.keys(JavaOps.INSTANCE)
			.map(Object::toString)
			.distinct()
			.map(Pattern::compile)
			.forEach(filters::add);

		filters.add(Pattern.compile(Power.TYPE_KEY));
		filters.add(Pattern.compile("^\\$"));

	});

	private static final Codec<String> SUB_POWER_NAME_CODEC = PrimitiveCodec.STRING.validate(MultiplePower::validateSubPowerName);

	private static final Codec<Map<String, Power>> SUB_POWERS_CODEC = CodecUtil.filteredUnboundedMap(SUB_POWER_NAME_CODEC, Power.CODEC, MultiplePower::isKeyIgnored).validate(
		subPowers -> {

			for (Map.Entry<String, Power> subPower : subPowers.entrySet()) {

				if (subPower.getValue() instanceof MultiplePower) {
					return DataResult.error(() -> "The sub-power \"" + subPower.getKey() + "\" is using the \"" + NeoApoliRegistries.POWER_TYPE.getId(PowerTypes.MULTIPLE) + "\" power type, which is not allowed!");
				}

			}

			return DataResult.success(subPowers);

		}
	);

	public static final MapCodec<MultiplePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonFields(instance).and(
		MapCodec.assumeMapUnsafe(SUB_POWERS_CODEC).forGetter(MultiplePower::getSubPowers)
	).apply(instance, MultiplePower::new));

	public static final PacketCodec<RegistryByteBuf, MultiplePower> PACKET_CODEC = createCommonPacketCodec(
		(buf, multiplePower) -> {

			Map<String, Power> subPowers = multiplePower.getSubPowers();
			buf.writeVarInt(subPowers.size());

			subPowers.forEach((name, subPower) -> {
				buf.writeString(name);
				Power.PACKET_CODEC.encode(buf, subPower);
			});

		},
		(buf, metadata) -> {

			Map<String, Power> subPowers = new Object2ObjectOpenHashMap<>();
			int size = buf.readVarInt();

			for (int i = 0; i < size; i++) {

				String name = buf.readString();
				Power subPower = Power.PACKET_CODEC.decode(buf);

				subPowers.put(name, subPower);

			}

			return new MultiplePower(metadata, subPowers);

		}
	);

	private final Map<String, Power> subPowers;

	public MultiplePower(Properties properties, Map<String, Power> subPowers) {
		super(properties);
		this.subPowers = subPowers;
	}

	@Override
	public Type<? extends Power> getType() {
		return PowerTypes.MULTIPLE;
	}

	public Map<String, Power> getSubPowers() {
		return subPowers;
	}

	@ApiStatus.Internal
	public static void preProcessSubPowers(Identifier id, PowerManager.PackData packData, String directoryPath, RegistryOps<JsonElement> registryOps) {

		if (packData.element() instanceof JsonObject jsonObject) {

			Optional<Type<?>> powerType = PowerTypes.CODEC
				.parse(registryOps, jsonObject.get(TYPE_KEY))
				.result();

			if (powerType.isPresent() && powerType.get() == PowerTypes.MULTIPLE) {
				jsonObject.entrySet().removeIf(entry -> !isKeyIgnored(entry.getKey()) && !PowerManager.isResourceConditionFulfilled(id, entry.getValue(), directoryPath, registryOps));
			}

		}

	}

	public static DataResult<String> validateSubPowerName(String name) {

		if (name.isEmpty()) {
			return DataResult.error(() -> "Empty sub-power names are not allowed!");
		}

		else if (Identifier.isPathValid(name)) {
			return DataResult.success(name);
		}

		else {
			DataResult<String> result = DataResult.error(() -> "Non [a-z0-9/._-] character in sub-power name: " + name);
			return result.setPartial(name);
		}

	}

	public static boolean isKeyIgnored(String key) {
		return SUB_POWER_KEY_FILTERS
			.stream()
			.anyMatch(pattern -> pattern.matcher(key).find());
	}

}
