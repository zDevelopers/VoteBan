/*
 * Copyright or © or Copr. AmauryCarrade (2015)
 * 
 * http://amaury.carrade.eu
 * 
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */
package fr.zcraft.VoteBan.votes;

import fr.zcraft.VoteBan.Config;
import fr.zcraft.VoteBan.Permissions;
import fr.zcraft.VoteBan.VoteBan;
import fr.zcraft.VoteBan.commands.votebans.VoteBansNoCommand;
import fr.zcraft.VoteBan.commands.votebans.VoteBansYesCommand;
import fr.zcraft.quartzlib.components.i18n.I;
import fr.zcraft.quartzlib.components.rawtext.RawText;
import fr.zcraft.quartzlib.tools.runners.RunTask;
import fr.zcraft.quartzlib.tools.text.RawMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class Vote
{
    private final UUID target;
    private final UUID launcher;

    private final String reason;

    private final Set<UUID> votesYes = new HashSet<>();
    private final Set<UUID> votesNo  = new HashSet<>();

    private float secondsLeft = 0.0f;
    private BossBar bar = null;
    private BukkitTask task = null;

    public Vote(final Player target, final Player launcher, final String reason)
    {
        this.target = target.getUniqueId();
        this.launcher = launcher.getUniqueId();

        this.reason = reason;

        votesYes.add(this.launcher);
    }

    public Player getTargetPlayer()
    {
        return Bukkit.getPlayer(target);
    }

    public Player getLauncherPlayer()
    {
        return Bukkit.getPlayer(launcher);
    }

    public UUID getTargetUUID()
    {
        return target;
    }

    public UUID getLauncherUUID()
    {
        return launcher;
    }

    public String getReason()
    {
        return reason;
    }

    public int getVotersCount()
    {
        return votesYes.size() + votesNo.size();
    }

    public double getYesPercentage()
    {
        return (float) votesYes.size() / (float) getVotersCount();
    }

    private void separator()
    {
        Bukkit.broadcastMessage(ChatColor.GRAY + "⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅ ⋅");
    }

    public void start()
    {
        final Player targetPlayer = getTargetPlayer();


        /* ** Broadcasts the vote ** */

        Bukkit.broadcastMessage("");
        separator();

        Bukkit.broadcastMessage(I.t("{gold}{bold}{0} started a vote to ban {1}", getLauncherPlayer().getName(), targetPlayer.getName()));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(I.t("{yellow}Reason: {gold}{0}", reason));
        Bukkit.broadcastMessage("");

        RawText invite = new RawText("")
                .then("[ " + I.t("I agree") + " ]")
                    .style(ChatColor.BOLD, ChatColor.GREEN)
                    /// Tooltip of the buttons in the chat. Followed by the command name: “You can also type /voteyes” as example.
                    .hover(new RawText(I.t("You can also type ")).color(ChatColor.WHITE).then("/voteyes").color(ChatColor.GOLD).build())
                    .command(VoteBansYesCommand.class, targetPlayer.getName())

                .then("     ")
                    .color(ChatColor.GRAY)

                .then("[ " + I.t("I disagree") + " ]")
                    .style(ChatColor.BOLD, ChatColor.RED)
                    .hover(new RawText(I.t("You can also type ")).color(ChatColor.WHITE).then("/voteno").color(ChatColor.GOLD).build())
                    .command(VoteBansNoCommand.class, targetPlayer.getName())

                .build();

        for (final Player player : Bukkit.getOnlinePlayers())
        {
            if (Permissions.VOTE.grantedTo(player) && !player.equals(targetPlayer))
                RawMessage.send(player, invite);

            else
                player.sendMessage(I.t("{gray}(You are not allowed to vote.)"));
        }

        separator();

        /* ** Broadcasts the vote of the sender (clearer for players) & the sound ** */

        RunTask.later(() -> broadcastVote(getLauncherPlayer(), true), 10);
        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1f, 1f));


        /* ** Schedules the end ** */

        secondsLeft = (float) Config.VOTES.DELAY.get();
        bar = Bukkit.createBossBar(I.t("{red}{bold}{0} wants to ban {1} {gray}-{yellow} {2}", getLauncherPlayer().getName(), targetPlayer.getName(), reason), BarColor.RED, BarStyle.SOLID);

        task = RunTask.timer(this::tick, 1L, 1L);
    }

    public boolean vote(Player player, boolean vote)
    {
        if (votesYes.contains(player.getUniqueId()) || votesNo.contains(player.getUniqueId()))
            return false;

        if (vote)
        {
            votesYes.add(player.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_BAMBOO_FALL, 1f, .1f));
        }
        else
        {
            votesNo.add(player.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_BAMBOO_FALL, 1f, 2f));
        }

        broadcastVote(player, vote);
        return true;
    }

    private void tick()
    {
        if (secondsLeft <= 0)
        {
            task.cancel();
            task = null;

            bar.removeAll();
            bar = null;

            end();
            return;
        }

        final List<Player> barPlayers = bar.getPlayers();

        Bukkit.getOnlinePlayers().stream().filter(player -> !barPlayers.contains(player)).forEach(bar::addPlayer);
        bar.setProgress(secondsLeft / (float) Config.VOTES.DELAY.get());

        secondsLeft -= .05f;
    }

    private void end()
    {
        separator();

        final boolean ban;

        if (getVotersCount() < Config.VOTES.MINIMAL_VOTES.get())
        {
            ban = false;

            Bukkit.broadcastMessage(I.t("{ce}There were not enough votes to take an action."));
        }
        else if ((int) Math.floor(getYesPercentage() * 100) < Config.VOTES.POSITIVE_PERCENTAGE_REQUIRED.get())
        {
            ban = false;

            Bukkit.broadcastMessage(I.t("{ce}At least {0}% of the votes must be positive to ban {1}.", Config.VOTES.POSITIVE_PERCENTAGE_REQUIRED.get(), getTargetPlayer().getName()));
        }
        else
        {
            ban = true;

            Bukkit.broadcastMessage(I.t("{cs}{bold}The people have spoken. {cs}{0} will be banned.", getTargetPlayer().getName()));
        }

        separator();

        if (ban)
        {
            String targetName = getTargetPlayer().getName();
            String launcherName = getLauncherPlayer().getName();

            for (String commandLine : Config.BAN_COMMANDS)
            {
                if (commandLine.startsWith("/"))
                    commandLine = commandLine.substring(1);

                commandLine = commandLine
                        .replace("{targetName}", targetName)
                        .replace("{targetUUID}", target.toString())
                        .replace("{launcherName}", launcherName)
                        .replace("{launcherUUID}", launcher.toString())
                        .replace("{reason}", reason)
                        .replace("{votesForBan}", String.valueOf(votesYes.size()))
                        .replace("{votesAgainstBan}", String.valueOf(votesNo.size()))
                        .replace("{votes}", String.valueOf(getVotersCount()));

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandLine);
            }

            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_WOLOLO, 1f, 1.2f));
        }
        else
        {
            VoteBan.get().immunize(target);
            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_CAT_AMBIENT, 1f, .6f));
        }

        VoteBan.get().unregisterVote(this);
    }

    private void broadcastVote(final Player voter, final boolean vote)
    {
        if (vote)
        {
            Bukkit.broadcastMessage(I.t("{darkgreen}\u271A  {green}{0} wants {1} to be banned", voter.getName(), getTargetPlayer().getName()));
        }
        else
        {
            Bukkit.broadcastMessage(I.t("{darkred}\u2716  {red}{0} is against the ban of {1}", voter.getName(), getTargetPlayer().getName()));
        }
    }
}
