package me.senseiwells.arucas.extensions.discordapi;

import me.senseiwells.arucas.api.wrappers.ArucasConstructor;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.ArucasWrapper;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.FunctionContext;
import me.senseiwells.arucas.values.MapValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.WrapperClassValue;
import me.senseiwells.arucas.values.functions.FunctionValue;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ArucasWrapper(name = "DiscordBot")
public class DiscordBotWrapper implements IArucasWrappedClass, EventListener {
	private Map<String, List<FunctionContext>> commandMap;
	private Map<String, List<FunctionContext>> eventMap;
	private JDA jda;

	@ArucasConstructor
	public void construct(Context context, StringValue token) throws LoginException {
		this.commandMap = new HashMap<>();
		this.eventMap = new HashMap<>();
		this.jda = JDABuilder.createDefault(token.value).addEventListeners(this).build();
		context.getThreadHandler().addShutdownEvent(() -> this.jda.shutdown());
	}

	@ArucasFunction
	public void setActivity(Context context, StringValue activityAsString, StringValue message) {
		Activity activity = switch (activityAsString.value) {
			case "playing" -> Activity.playing(message.value);
			case "watching" -> Activity.watching(message.value);
			case "listening" -> Activity.listening(message.value);
			case "competing" -> Activity.competing(message.value);
			default -> throw new RuntimeException("'%s' is an invalid activity".formatted(activityAsString.value));
		};
		this.jda.getPresence().setActivity(activity);
	}

	@ArucasFunction
	public Value<?> getActivity(Context context) {
		Activity activity = this.jda.getPresence().getActivity();
		if (activity == null) {
			return NullValue.NULL;
		}
		return StringValue.of(activity.getType().name() + ": " + activity.getName());
	}

	@ArucasFunction
	public void setStatus(Context context, StringValue status) {
		OnlineStatus onlineStatus = OnlineStatus.fromKey(status.value);
		if (onlineStatus == OnlineStatus.UNKNOWN) {
			throw new RuntimeException("'%s' is an invalid status".formatted(status.value));
		}
		this.jda.getPresence().setStatus(onlineStatus);
	}

	@ArucasFunction
	public StringValue getStatus(Context context) {
		return StringValue.of(this.jda.getPresence().getStatus().getKey());
	}

	@ArucasFunction
	public StringValue getUserId(Context context) {
		return DiscordUtils.getId(this.jda.getSelfUser());
	}

	@ArucasFunction
	public void registerEvent(Context context, StringValue eventName, FunctionValue functionValue) {
		List<FunctionContext> events = this.eventMap.getOrDefault(eventName.value, new ArrayList<>());
		events.add(new FunctionContext(context, functionValue));
		this.eventMap.putIfAbsent(eventName.value, events);
	}

	@ArucasFunction
	public void addCommand(Context context, MapValue commandMap) throws CodeError {
		SlashCommandData commandData = DiscordUtils.parseMapAsCommand(context, this.commandMap, commandMap.value);
		this.jda.upsertCommand(commandData).queue();
	}

	@ArucasFunction
	public void stop(Context context) {
		this.jda.shutdown();
	}

	@ArucasFunction
	private WrapperClassValue getChannel(Context context, StringValue channelId) throws CodeError {
		MessageChannel messageChannel = this.jda.getChannelById(MessageChannel.class, channelId.value);
		if (messageChannel == null) {
			throw new RuntimeException("Channel with id '%s' couldn't be found".formatted(channelId.value));
		}
		return DiscordChannelWrapper.createNewChannelWrapper(messageChannel, context);
	}

	@ArucasFunction
	private WrapperClassValue getServer(Context context, StringValue serverId) throws CodeError {
		Guild guild = this.jda.getGuildById(serverId.value);
		if (guild == null) {
			throw new RuntimeException("Server with id '%s' couldn't be found".formatted(serverId.value));
		}
		return DiscordServerWrapper.createNewChannelWrapper(guild, context);
	}

	@Override
	public void onEvent(@NotNull GenericEvent event) {
		if (event instanceof GenericCommandInteractionEvent commandEvent) {
			List<FunctionContext> commands = this.commandMap.get(commandEvent.getName());
			int parameterSize = commandEvent.getOptions().size();
			if (commands == null || commands.size() < parameterSize) {
				return;
			}
			FunctionContext functionContext = commands.get(parameterSize);
			if (functionContext == null) {
				commandEvent.reply("Invalid number of parameters").queue();
				return;
			}
			Context context = functionContext.context();
			context.getThreadHandler().runAsyncFunctionInContext(context.createBranch(), branchContext -> {
				functionContext.functionValue().call(branchContext, DiscordUtils.getParameters(context, commandEvent));
			});
			return;
		}
		String eventName = event.getClass().getSimpleName();
		List<FunctionContext> events = this.eventMap.get(eventName);
		if (events == null) {
			return;
		}
		events.forEach(functionContext -> {
			Context context = functionContext.context();
			context.getThreadHandler().runAsyncFunctionInContext(context.createBranch(), branchContext -> {
				List<Value<?>> parameters = new ArrayList<>(1);
				WrapperClassValue wrapperValue = DiscordEventWrapper.createNewEventWrapper(event, branchContext);
				parameters.add(wrapperValue);
				functionContext.functionValue().call(branchContext, parameters);
			});
		});
	}
}
