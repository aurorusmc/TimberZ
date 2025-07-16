package com.zetaplugins.timberz.service.mcmmo;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.zetaplugins.timberz.TimberZ;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Set;

public class McMMoManager {

    public McMMoManager(TimberZ plugin) {
    }

    public void giveMcMMoXP(Player player, Set<Block> blocksBroken) {

        McMMOPlayer mcMMOPlayer = new McMMOPlayer(player, new PlayerProfile(player.getName(), player.getUniqueId(), 0));

            for(Block block : blocksBroken) {
                ExperienceAPI.addXpFromBlockBySkill(block.getState(), mcMMOPlayer, PrimarySkillType.WOODCUTTING);

                if(ExperienceAPI.getXpNeededToLevel(ExperienceAPI.getLevel(player, PrimarySkillType.WOODCUTTING) + 1) <= ExperienceAPI.getXP(player, "WOODCUTTING"))
                    ExperienceAPI.addLevel(player, "WOODCUTTING", 1);

            }


    }
}
