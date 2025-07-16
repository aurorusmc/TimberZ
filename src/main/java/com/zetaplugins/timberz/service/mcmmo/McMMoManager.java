package com.zetaplugins.timberz.service.mcmmo;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.zetaplugins.timberz.TimberZ;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Set;

public class McMMoManager {

    private final TimberZ plugin;

    public McMMoManager(TimberZ plugin) {
        this.plugin = plugin;
    }

    public void giveMcMMoXP(Player player, Set<Block> blocksBroken) {


        //noinspection deprecation
        McMMOPlayer mcMMOPlayer = new McMMOPlayer(player, new PlayerProfile(player.getName(), player.getUniqueId()));

            for(Block block : blocksBroken) {
                mcMMOPlayer.addXp(PrimarySkillType.WOODCUTTING, (float) plugin.getConfig().getDouble("mcMMoXP"));
            }
    }
}
