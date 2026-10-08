package com.example.addon.social.scarypeople;

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

public class ScaryPeople extends System<ScaryPeople> implements Iterable<ScaryPerson> {
    private final List<ScaryPerson> scaryPeople = new ArrayList<>();

    public ScaryPeople() {
        super("scarypeople");
    }

    public static ScaryPeople get() {
        return Systems.get(ScaryPeople.class);
    }

    public boolean add(ScaryPerson scaryPerson) {
        if (scaryPerson.name.isEmpty() || scaryPerson.name.contains(" ")) return false;

        if (!scaryPeople.contains(scaryPerson)) {
            scaryPeople.add(scaryPerson);
            save();

            return true;
        }

        return false;
    }

    public boolean remove(ScaryPerson scaryPerson) {
        if (scaryPeople.remove(scaryPerson)) {
            save();
            return true;
        }

        return false;
    }

    public ScaryPerson get(String name) {
        for (ScaryPerson scaryPerson : scaryPeople) {
            if (scaryPerson.name.equals(name)) {
                return scaryPerson;
            }
        }

        return null;
    }

    public ScaryPerson get(Player player) {
        return get(player.getName().getString());
    }

    public ScaryPerson get(PlayerInfo player) {
        return get(player.getProfile().name());
    }

    public boolean isScary(Player player) {
        return player != null && get(player) != null;
    }

    public boolean isScary(PlayerInfo player) {
        return get(player) != null;
    }

    public boolean shouldAttack(Player player) {
        return isScary(player);
    }

    public int count() {
        return scaryPeople.size();
    }

    public boolean isEmpty() {
        return scaryPeople.isEmpty();
    }

    @Override
    public @NonNull Iterator<ScaryPerson> iterator() {
        return scaryPeople.iterator();
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.put("scarypeople", NbtUtils.listToTag(scaryPeople));

        return tag;
    }

    @Override
    public ScaryPeople fromTag(CompoundTag tag) {
        scaryPeople.clear();

        for (Tag itemTag : tag.getListOrEmpty("scarypeople")) {
            CompoundTag scaryPersonTag = (CompoundTag) itemTag;
            if (!scaryPersonTag.contains("name")) continue;

            String name = scaryPersonTag.getStringOr("name", "");
            if (get(name) != null) continue;

            String uuid = scaryPersonTag.getStringOr("id", "");
            ScaryPerson scaryPerson = !uuid.isBlank()
                ? new ScaryPerson(name, UndashedUuid.fromStringLenient(uuid))
                : new ScaryPerson(name);

            scaryPeople.add(scaryPerson);
        }

        Collections.sort(scaryPeople);

        MeteorExecutor.execute(() -> scaryPeople.forEach(ScaryPerson::updateInfo));

        return this;
    }
}