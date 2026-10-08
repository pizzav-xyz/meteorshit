package com.example.addon.social.blacklistedpeople;

import com.mojang.util.UndashedUuid;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class BlacklistedPeople extends System<BlacklistedPeople> implements Iterable<BlacklistedPerson> {
    private final List<BlacklistedPerson> blacklistedPeople = new ArrayList<>();

    public BlacklistedPeople() {
        super("blacklistedpeople");
    }

    public static BlacklistedPeople get() {
        return Systems.get(BlacklistedPeople.class);
    }

    public boolean add(BlacklistedPerson blacklistedPerson) {
        if (blacklistedPerson.name.isEmpty() || blacklistedPerson.name.contains(" ")) return false;

        if (!blacklistedPeople.contains(blacklistedPerson)) {
            blacklistedPeople.add(blacklistedPerson);
            save();

            return true;
        }

        return false;
    }

    public boolean remove(BlacklistedPerson blacklistedPerson) {
        if (blacklistedPeople.remove(blacklistedPerson)) {
            save();
            return true;
        }

        return false;
    }

    public BlacklistedPerson get(String name) {
        for (BlacklistedPerson blacklistedPerson : blacklistedPeople) {
            if (blacklistedPerson.name.equals(name)) {
                return blacklistedPerson;
            }
        }

        return null;
    }

    public BlacklistedPerson get(Player player) {
        return get(player.getName().getString());
    }

    public BlacklistedPerson get(PlayerInfo player) {
        return get(player.getProfile().name());
    }

    public boolean isBlacklisted(Player player) {
        return player != null && get(player) != null;
    }

    public boolean isBlacklisted(PlayerInfo player) {
        return get(player) != null;
    }

    public boolean shouldIgnore(Player player) {
        return isBlacklisted(player);
    }

    public int count() {
        return blacklistedPeople.size();
    }

    public boolean isEmpty() {
        return blacklistedPeople.isEmpty();
    }

    @Override
    public @NonNull Iterator<BlacklistedPerson> iterator() {
        return blacklistedPeople.iterator();
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.put("blacklistedpeople", NbtUtils.listToTag(blacklistedPeople));

        return tag;
    }

    @Override
    public BlacklistedPeople fromTag(CompoundTag tag) {
        blacklistedPeople.clear();

        for (Tag itemTag : tag.getListOrEmpty("blacklistedpeople")) {
            CompoundTag blacklistedPersonTag = (CompoundTag) itemTag;
            if (!blacklistedPersonTag.contains("name")) continue;

            String name = blacklistedPersonTag.getStringOr("name", "");
            if (get(name) != null) continue;

            String uuid = blacklistedPersonTag.getStringOr("id", "");
            BlacklistedPerson blacklistedPerson = !uuid.isBlank()
                ? new BlacklistedPerson(name, UndashedUuid.fromStringLenient(uuid))
                : new BlacklistedPerson(name);

            blacklistedPeople.add(blacklistedPerson);
        }

        Collections.sort(blacklistedPeople);

        MeteorExecutor.execute(() -> blacklistedPeople.forEach(BlacklistedPerson::updateInfo));

        return this;
    }
}