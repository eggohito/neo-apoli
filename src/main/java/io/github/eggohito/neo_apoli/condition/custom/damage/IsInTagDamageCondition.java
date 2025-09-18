package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.DamageCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

@EqualsAndHashCode
@Data
public final class IsInTagDamageCondition extends DamageCondition {

	public static final MapCodec<IsInTagDamageCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		TagKey.unprefixedCodec(RegistryKeys.DAMAGE_TYPE).fieldOf("tag").forGetter(IsInTagDamageCondition::tag)
	).apply(instance, IsInTagDamageCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsInTagDamageCondition> PACKET_CODEC = PacketCodec.tuple(
		TagKey.packetCodec(RegistryKeys.DAMAGE_TYPE), IsInTagDamageCondition::tag,
		IsInTagDamageCondition::new
	);

	private final TagKey<DamageType> tag;

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.IS_IN_TAG;
	}

	@Override
	protected boolean impl(Context context) {
		return context.required(ContextParameters.DAMAGE_SOURCE).isIn(this.tag());
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		RegistryUtil.validateTag(reporter.makeChild(".tag"), this.tag());
	}

}
