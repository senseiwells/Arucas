package me.senseiwells.arucas.extensions.discordapi;

import me.senseiwells.arucas.api.wrappers.ArucasClass;
import me.senseiwells.arucas.api.wrappers.ArucasConstructor;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
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

/**
 * DiscordBot class wrapper for Arucas. <br>
 * Fully Documented.
 * @author senseiwells
 */
@ArucasClass(name = "DiscordBot")
public class DiscordBotWrapper implements IArucasWrappedClass, EventListener {
	private Map<String, List<FunctionContext>> commandMap;
	private Map<String, List<FunctionContext>> eventMap;
	private JDA jda;

	/**
	 * Name: <code>new DiscordBot(token)</code> <br>
	 * Description: This creates a new DiscordBot instance <br>
	 * Parameter - String: the token of the bot <br>
	 * Example: <code>new DiscordBot("token");</code>
	 */
	@ArucasConstructor
	public void construct(Context context, StringValue token) throws LoginException {
		this.commandMap = new HashMap<>();
		this.eventMap = new HashMap<>();
		this.jda = JDABuilder.createDefault(token.value).addEventListeners(this).build();
		context.getThreadHandler().addShutdownEvent(() -> this.jda.shutdown());
	}

	/**
	 * Name: <code>&lt;DiscordBot>.setActivity(activity, message)</code> <br>
	 * Description: This sets the activity of the bot <br>
	 * Parameters - String, String: the activity you want the bot to have, the message you want to display <br>
	 * Throws - Error: <code>"... is an invalid activity"</code> if the activity is not valid <br>
	 * Example: <code>bot.setActivity("PLAYING", "Arucas");</code>
	 */
	@ArucasFunction
	public void setActivity(Context context, StringValue activityAsString, StringValue message) {
		Activity activity = switch (activityAsString.value.toLowerCase()) {
			case "playing" -> Activity.playing(message.value);
			case "watching" -> Activity.watching(message.value);
			case "listening" -> Activity.listening(message.value);
			case "competing" -> Activity.competing(message.value);
			default -> throw new RuntimeException("'%s' is an invalid activity".formatted(activityAsString.value));
		};
		this.jda.getPresence().setActivity(activity);
	}

	/**
	 * Name: <code>&lt;DiscordBot>.getActivity()</code> <br>
	 * Description: This gets the activity of the bot <br>
	 * Returns - String/Null: the activity of the bot, null if no activity <br>
	 * Example: <code>bot.getActivity();</code>
	 */
	@ArucasFunction
	public Value<?> getActivity(Context context) {
		Activity activity = this.jda.getPresence().getActivity();
		if (activity == null) {
			return NullValue.NULL;
		}
		return StringValue.of(activity.getType().name() + ": " + activity.getName());
	}

	/**
	 * Name: <code>&lt;DiscordBot>.setStatus(status)</code> <br>
	 * Description: This sets the status of the bot <br>
	 * Parameters - String: the status you want the bot to have <br>
	 * Throws - Error: <code>"... is an invalid status"</code> if the status is not valid <br>
	 * Example: <code>bot.setStatus("ONLINE");</code>
	 */
	@ArucasFunction
	public void setStatus(Context context, StringValue status) {
		OnlineStatus onlineStatus = OnlineStatus.fromKey(status.value);
		if (onlineStatus == OnlineStatus.UNKNOWN) {
			throw new RuntimeException("'%s' is an invalid status".formatted(status.value));
		}
		this.jda.getPresence().setStatus(onlineStatus);
	}

	/**
	 * Name: <code>&lt;DiscordBot>.getStatus()</code> <br>
	 * Description: This gets the status of the bot <br>
	 * Returns - String: the status of the bot <br>
	 * Example: <code>bot.getStatus();</code>
	 */
	@ArucasFunction
	public StringValue getStatus(Context context) {
		return StringValue.of(this.jda.getPresence().getStatus().getKey());
	}

	/**
	 * Name: <code>&lt;DiscordBot>.getUserId()</code> <br>
	 * Description: This gets the user id of the bot <br>
	 * Returns - String: the user id of the bot <br>
	 * Example: <code>bot.getUserId();</code>
	 */
	@ArucasFunction
	public StringValue getUserId(Context context) {
		return DiscordUtils.getId(this.jda.getSelfUser());
	}

	/**
	 * Name: <code>&lt;DiscordBot>.registerEvent(eventName, function)</code> <br>
	 * Description: This registers a function to be called when an event is triggered <br>
	 * Parameters - String, Function: the name of the event, the function to be called <br>
	 * Example: <code>bot.registerEvent("MessageReceivedEvent", function(event) { });</code>
	 */
	@ArucasFunction
	public void registerEvent(Context context, StringValue eventName, FunctionValue functionValue) {
		List<FunctionContext> events = this.eventMap.getOrDefault(eventName.value, new ArrayList<>());
		events.add(new FunctionContext(context, functionValue));
		this.eventMap.putIfAbsent(eventName.value, events);
	}

	/**
	 * Name: <code>&lt;DiscordBot>.addCommand(commandMap)</code> <br>
	 * Description: This adds a slash command to the bot <br>
	 * Parameter - Map: the command map <br>
	 * Throws - Error: "..." if the command map is invalid <br>
	 * Example: <code>bot.addCommand({"name": "command", "description": "", "command": fun(event) { }});</code>
	 */
	@ArucasFunction
	public void addCommand(Context context, MapValue commandMap) throws CodeError {
		SlashCommandData commandData = DiscordUtils.parseMapAsCommand(context, this.commandMap, commandMap.value);
		this.jda.upsertCommand(commandData).complete();
	}

	/**
	 * Name: <code>&lt;DiscordBot>.removeCommand(commandName)</code> <br>
	 * Description: This removes a slash command from the bot <br>
	 * Parameters - String: the name of the command <br>
	 * Example: <code>bot.removeCommand("command");</code>
	 */
	@ArucasFunction
	public void removeCommand(Context context, StringValue commandName) {
		this.commandMap.remove(commandName.value);
		this.jda.deleteCommandById(commandName.value).complete();
	}

	/**
	 * Name: <code>&lt;DiscordBot>.stop()</code> <br>
	 * Description: This stops the bot <br>
	 * Example: <code>bot.stop();</code>
	 */
	@ArucasFunction
	public void stop(Context context) {
		this.jda.shutdown();
	}

	/**
	 * Name: <code>&lt;DiscordBot>.getChannel(channelId)</code> <br>
	 * Description: This gets a channel by its id <br>
	 * Parameter - String: the id of the channel <br>
	 * Returns - DiscordChannel: the channel <br>
	 * Throws - Error: "Channel with id ... couldn't be found" if the channel is not found <br>
	 * Example: <code>bot.getChannel("12345678901234567890123456789012");</code>
	 */
	@ArucasFunction
	private WrapperClassValue getChannel(Context context, StringValue channelId) throws CodeError {
		MessageChannel messageChannel = this.jda.getChannelById(MessageChannel.class, channelId.value);
		if (messageChannel == null) {
			throw new RuntimeException("Channel with id '%s' couldn't be found".formatted(channelId.value));
		}
		return DiscordChannelWrapper.newDiscordChannel(messageChannel, context);
	}

	/**
	 * Name: <code>&lt;DiscordBot>.getServer(serverId)</code> <br>
	 * Description: This gets a server by its id <br>
	 * Parameter - String: the id of the server <br>
	 * Returns - DiscordServer: the server <br>
	 * Throws - Error: "Server with id ... couldn't be found" if the server is not found <br>
	 * Example: <code>bot.getServer("12345678901234567890123456789012");</code>
	 */
	@ArucasFunction
	private WrapperClassValue getServer(Context context, StringValue serverId) throws CodeError {
		Guild guild = this.jda.getGuildById(serverId.value);
		if (guild == null) {
			throw new RuntimeException("Server with id '%s' couldn't be found".formatted(serverId.value));
		}
		return DiscordServerWrapper.newDiscordServer(guild, context);
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
				commandEvent.reply("Invalid number of parameters").complete();
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
				WrapperClassValue wrapperValue = DiscordEventWrapper.newDiscordEvent(event, branchContext);
				parameters.add(wrapperValue);
				functionContext.functionValue().call(branchContext, parameters);
			});
		});
	}
}
