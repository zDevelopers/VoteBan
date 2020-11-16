package fr.zcraft.VoteBan.commands.votebans;

import fr.zcraft.quartzlib.components.commands.CommandInfo;


@CommandInfo (name = "no", usageParameters = "[player]")
public final class VoteBansNoCommand extends VoteBansVoteCommand
{
    public VoteBansNoCommand()
    {
        setChoice(false);
    }
}
