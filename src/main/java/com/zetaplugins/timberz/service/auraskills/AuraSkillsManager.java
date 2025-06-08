package com.zetaplugins.timberz.service.auraskills;

import com.zetaplugins.timberz.TimberZ;
import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class AuraSkillsManager {
    private final TimberZ plugin;
    private final AuraSkillsApi auraSkillsApi = AuraSkillsApi.get();

    public AuraSkillsManager(TimberZ plugin) {
        this.plugin = plugin;
    }

    public void giveAuraSkillsXP(Player player, Set<Block> blocksBroken) {
        if (auraSkillsApi == null) {
            plugin.getLogger().warning("AuraSkills API is not available. Cannot give XP.");
            return;
        }

        SkillsUser skillsUser = auraSkillsApi.getUser(player.getUniqueId());

        Map<String, Double> xpSources = Skills.FORAGING.getSources().stream()
                .filter(source -> source.getType().toString().equals("BLOCK"))
                .collect(Collectors.toMap(XpSource::name, XpSource::getXp));

        double totalXP = 0;
        for (var block : blocksBroken) {
            String blockType = block.getType().toString();
            if (xpSources.containsKey(blockType)) {
                totalXP += xpSources.get(blockType);
            }
        }

        double auraSkillsXPMultiplier = plugin.getConfig().getDouble("auraSkillsXPMultiplier", 1.0);

        skillsUser.addSkillXp(Skills.FORAGING, totalXP * auraSkillsXPMultiplier);
    }
}
