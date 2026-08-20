package de.itsgraphax.fgtDiscovery.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.itsgraphax.fgtDiscovery.FgtDiscovery;
import de.itsgraphax.fgtDiscovery.util.PlayerTransporter;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import jdk.jfr.Description;
import net.strokkur.commands.Aliases;
import net.strokkur.commands.Command;
import net.strokkur.commands.CustomSuggestion;
import net.strokkur.commands.Executes;
import net.strokkur.commands.paper.Executor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Command("join")
@Aliases("j")
@Description("Join another server")
public class Join {
    @Executes()
    void join(CommandSender ignoredSender, @Executor Player player, @JoinServerSuggestions String server) {
        PlayerTransporter.join(player, server.toLowerCase());
    }

    @CustomSuggestion
    public @interface JoinServerSuggestions {
    }

    @JoinServerSuggestions
    public static class JoinServerSuggestionsImpl implements SuggestionProvider<CommandSourceStack> {
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
            ConfigurationSection servers = FgtDiscovery.getInstance().getConfig()
                    .getConfigurationSection("servers");

            if (servers == null) throw new RuntimeException();

            Set<String> suggestions = servers.getKeys(false);

            suggestions.stream()
                    .filter(str -> str.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                    .forEach(builder::suggest);

            return builder.buildFuture();
        }
    }
}
