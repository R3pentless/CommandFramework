package pl.inh.commandframework;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {

    private final Map<String, Map<UUID, Long>> cooldowns = new ConcurrentHashMap<>();

    public boolean isOnCooldown(String key, UUID uuid) {
        Map<UUID, Long> commandCooldowns = cooldowns.get(key);
        if (commandCooldowns == null) return false;

        Long expiry = commandCooldowns.get(uuid);
        if (expiry == null) return false;

        if (System.currentTimeMillis() < expiry) {
            return true;
        }

        commandCooldowns.remove(uuid);
        return false;
    }

    public long getRemainingCooldown(String key, UUID uuid) {
        Map<UUID, Long> commandCooldowns = cooldowns.get(key);
        if (commandCooldowns == null) return 0;

        Long expiry = commandCooldowns.get(uuid);
        if (expiry == null) return 0;

        long remaining = expiry - System.currentTimeMillis();
        return remaining > 0 ? remaining / 1000 : 0;
    }

    public void setCooldown(String key, UUID uuid, int seconds) {
        cooldowns.computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                .put(uuid, System.currentTimeMillis() + (seconds * 1000L));
    }

    public void removeCooldown(String key, UUID uuid) {
        Map<UUID, Long> commandCooldowns = cooldowns.get(key);
        if (commandCooldowns != null) {
            commandCooldowns.remove(uuid);
        }
    }

    public void cleanup() {
        long now = System.currentTimeMillis();
        cooldowns.values().forEach(map ->
                map.entrySet().removeIf(entry -> entry.getValue() < now)
        );
    }
}