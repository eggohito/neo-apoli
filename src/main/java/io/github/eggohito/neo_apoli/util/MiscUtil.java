package io.github.eggohito.neo_apoli.util;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.text.Text;

public class MiscUtil {

	public static final DynamicCommandExceptionType PASSTHROUGH_COMMAND_EXCEPTION_TYPE = new DynamicCommandExceptionType(o -> Text.literal(o.toString()));

}
