package fr.zcraft.VoteBan.commands.votebans;

import fr.zcraft.quartzlib.components.commands.CommandInfo;


@CommandInfo (name = "yes", usageParameters = "[player]")
public final class VoteBansYesCommand extends VoteBansVoteCommand
{
    public VoteBansYesCommand()
    {
        setChoice(true);
    }
}
