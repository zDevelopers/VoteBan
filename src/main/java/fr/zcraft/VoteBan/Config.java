package fr.zcraft.VoteBan;

import fr.zcraft.quartzlib.components.configuration.Configuration;
import fr.zcraft.quartzlib.components.configuration.ConfigurationItem;
import fr.zcraft.quartzlib.components.configuration.ConfigurationList;
import fr.zcraft.quartzlib.components.configuration.ConfigurationSection;

import java.util.Locale;

import static fr.zcraft.quartzlib.components.configuration.ConfigurationItem.item;
import static fr.zcraft.quartzlib.components.configuration.ConfigurationItem.list;
import static fr.zcraft.quartzlib.components.configuration.ConfigurationItem.section;


/**
 * Configuration.
 */
public class Config extends Configuration
{
    static public final ConfigurationItem<Locale> LOCALE = item("locale", Locale.class);
    
    static public final VotesSection VOTES = section("votes", VotesSection.class);
    static public class VotesSection extends ConfigurationSection
    {
        public final ConfigurationItem<Integer> DELAY = item("delay", 40);
        public final ConfigurationItem<Integer> COOLDOWN = item("cooldown", 120);
        public final ConfigurationItem<Integer> MINIMAL_VOTES = item("minimal_votes", 3);
        public final ConfigurationItem<Integer> POSITIVE_PERCENTAGE_REQUIRED = item("positive_percentage_required", 50);
    }
    
    static public final ConfigurationList<String> BAN_COMMANDS = list("ban_commands", String.class);
}
