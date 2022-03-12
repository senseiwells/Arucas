package me.senseiwells.arucas.extensions.discordapi;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.FunctionContext;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.FunctionValue;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.RoleAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordUtils {
	private static final StringValue
		NEXT = StringValue.of("next"),
		OPTION = StringValue.of("option"),
		REQUIRED = StringValue.of("required"),
		COMMAND = StringValue.of("command"),
		NAME = StringValue.of("name"),
		DESCRIPTION = StringValue.of("description");

	private static Map<String, Permission> permissionMap;

	public static StringValue getId(ISnowflake snowflake) {
		return StringValue.of(snowflake.getId());
	}

	public static MessageEmbed parseMapAsEmbed(Context context, MapValue mapValue) throws CodeError {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		ArucasMap map = mapValue.value;

		Value<?> value = map.get(context, StringValue.of("title"));
		if (value != null) {
			embedBuilder.setTitle(value.getAsString(context));
		}

		value = map.get(context, StringValue.of("description"));
		if (value instanceof ListValue listValue) {
			for (Value<?> descriptions : listValue.value) {
				embedBuilder.appendDescription(descriptions.getAsString(context));
			}
		}
		else if (value != null) {
			embedBuilder.setDescription(value.getAsString(context));
		}

		value = map.get(context, StringValue.of("colour"));
		if (value == null) {
			value = map.get(context, StringValue.of("color"));
		}
		if (value instanceof NumberValue numberValue) {
			embedBuilder.setColor(numberValue.value.intValue());
		}

		value = map.get(context, StringValue.of("fields"));
		if (value instanceof ListValue listValue) {
			final StringValue
				NAME = StringValue.of("name"),
				VALUE = StringValue.of("value"),
				INLINE = StringValue.of("inline");
			for (Value<?> field : listValue.value) {
				if (!(field instanceof MapValue)) {
					continue;
				}
				ArucasMap fieldObj = (ArucasMap) field.value;
				Value<?> nameValue = fieldObj.get(context, NAME);
				String name = nameValue == null ? null : nameValue.getAsString(context);
				Value<?> valueValue = fieldObj.get(context, VALUE);
				String fieldValue = valueValue == null ? null : valueValue.getAsString(context);
				Value<?> inlineValue = fieldObj.get(context, INLINE);
				boolean inline = inlineValue instanceof BooleanValue booleanValue ? booleanValue.value : false;
				embedBuilder.addField(name, fieldValue, inline);
			}
		}

		value = map.get(context, StringValue.of("image"));
		if (value != null) {
			embedBuilder.setImage(value.getAsString(context));
		}
		return embedBuilder.build();
	}

	public static void parseMapAsRole(Context context, RoleAction roleAction, MapValue mapValue) throws CodeError {
		ArucasMap map = mapValue.value;

		Value<?> value = map.get(context, StringValue.of("name"));
		if (value != null) {
			roleAction = roleAction.setName(value.getAsString(context));
		}

		value = map.get(context, StringValue.of("colour"));
		if (value == null) {
			value = map.get(context, StringValue.of("color"));
		}
		if (value instanceof NumberValue numberValue) {
			roleAction = roleAction.setColor(numberValue.value.intValue());
		}

		value = map.get(context, StringValue.of("hoisted"));
		if (value instanceof BooleanValue booleanValue && booleanValue.value) {
			roleAction = roleAction.setHoisted(true);
		}

		value = map.get(context, StringValue.of("mentionable"));
		if (value instanceof BooleanValue booleanValue && booleanValue.value) {
			roleAction = roleAction.setMentionable(true);
		}

		value = map.get(context, StringValue.of("permissions"));
		if (!(value instanceof ListValue listValue)) {
			roleAction.queue();
			return;
		}

		if (permissionMap == null) {
			permissionMap = new HashMap<>();

			for (Permission permission : Permission.values()) {
				permissionMap.put(permission.getName(), permission);
			}
		}

		for (Value<?> permission : listValue.value) {
			Permission listedPermission = permissionMap.get(permission.getAsString(context));
			if (listedPermission != null) {
				roleAction = roleAction.setPermissions(listedPermission);
			}
		}
		roleAction.queue();
	}

	public static SlashCommandData parseMapAsCommand(Context context, Map<String, List<FunctionContext>> commandMap, ArucasMap arucasMap) throws CodeError {
		Value<?> name = arucasMap.get(context, NAME);
		Value<?> description = arucasMap.get(context, DESCRIPTION);
		if (name == null || description == null) {
			throw new RuntimeException("Command must have name and a description");
		}
		String commandName = name.getAsString(context);
		SlashCommandData slashCommandData = Commands.slash(commandName, description.getAsString(context));
		List<FunctionContext> functions = new ArrayList<>();
		commandMap.put(commandName, functions);
		Value<?> command = arucasMap.get(context, COMMAND);
		if (command instanceof FunctionValue functionValue) {
			functions.add(0, new FunctionContext(context, functionValue));
		}
		Value<?> nextOption = arucasMap.get(context, NEXT);
		if (nextOption instanceof MapValue mapValue) {
			slashCommandData = commandOption(Commands.slash(name.getAsString(context), description.getAsString(context)), functions, context, mapValue.value, 1);
		}
		return slashCommandData;
	}

	private static SlashCommandData commandOption(SlashCommandData slashCommandData, List<FunctionContext> commandList, Context context, ArucasMap arucasMap, int depth) throws CodeError {
		if (depth > 25) {
			throw new RuntimeException("Slash command went too deep");
		}
		Value<?> option = arucasMap.get(context, OPTION);
		if (option == null) {
			throw new RuntimeException("Command must include option type");
		}
		OptionType optionType = switch (option.getAsString(context)) {
			case "String" -> OptionType.STRING;
			case "Integer" -> OptionType.INTEGER;
			case "Number" -> OptionType.NUMBER;
			case "Boolean" -> OptionType.BOOLEAN;
			case "User" -> OptionType.USER;
			case "Channel" -> OptionType.CHANNEL;
			case "Attachment" -> OptionType.ATTACHMENT;
			default -> throw new RuntimeException("Invalid option");
		};
		Value<?> name = arucasMap.get(context, NAME);
		Value<?> description = arucasMap.get(context, DESCRIPTION);
		if (name == null || description == null) {
			throw new RuntimeException("Command must have name and a description");
		}
		Value<?> required = arucasMap.get(context, REQUIRED);
		boolean req = required instanceof BooleanValue booleanValue && booleanValue.value;
		slashCommandData = slashCommandData.addOption(optionType, name.getAsString(context), description.getAsString(context), req);
		Value<?> command = arucasMap.get(context, COMMAND);
		if (command instanceof FunctionValue functionValue) {
			commandList.add(depth, new FunctionContext(context, functionValue));
		}
		Value<?> nextOption = arucasMap.get(context, NEXT);
		if (nextOption instanceof MapValue mapValue) {
			depth++;
			slashCommandData = commandOption(slashCommandData, commandList, context, mapValue.value, depth);
		}
		return slashCommandData;
	}

	public static List<Value<?>> getParameters(Context context, GenericCommandInteractionEvent commandEvent) throws CodeError {
		List<Value<?>> parameters = new ArrayList<>();
		parameters.add(DiscordEventWrapper.createNewEventWrapper(commandEvent, context));
		for (OptionMapping mapping : commandEvent.getOptions()) {
			parameters.add(parseMapping(context, mapping));
		}
		return parameters;
	}

	private static Value<?> parseMapping(Context context, OptionMapping mapping) throws CodeError {
		return switch (mapping.getType()) {
			case INTEGER, NUMBER -> NumberValue.of(mapping.getAsDouble());
			case BOOLEAN -> BooleanValue.of(mapping.getAsBoolean());
			case USER -> DiscordUserWrapper.createNewDefinition(mapping.getAsUser(), context);
			case CHANNEL -> DiscordChannelWrapper.createNewChannelWrapper(mapping.getAsMessageChannel(), context);
			case ATTACHMENT -> DiscordAttachmentWrapper.createNewDefinition(mapping.getAsAttachment(), context);
			default -> StringValue.of(mapping.getAsString());
		};
	}
}
