package com.example.addon.social.scarypeople;

import com.mojang.util.UndashedUuid;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.render.PlayerHeadTexture;
import meteordevelopment.meteorclient.utils.render.PlayerHeadUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

import org.jspecify.annotations.Nullable;
import java.util.Objects;
import java.util.UUID;

public class ScaryPerson implements ISerializable<ScaryPerson>, Comparable<ScaryPerson> {
    public volatile String name;
    private volatile @Nullable UUID id;
    private volatile @Nullable PlayerHeadTexture headTexture;
    private volatile boolean updating;

    public ScaryPerson(String name, @Nullable UUID id) {
        this.name = name;
        this.id = id;
        this.headTexture = null;
    }

    public ScaryPerson(Player player) {
        this(player.getName().getString(), player.getUUID());
    }

    public ScaryPerson(String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    public PlayerHeadTexture getHead() {
        return headTexture != null ? headTexture : PlayerHeadUtils.STEVE_HEAD;
    }

    public void updateInfo() {
        updating = true;
        APIResponse res = Http.get("https://api.mojang.com/users/profiles/minecraft/" + name).sendJson(APIResponse.class);
        if (res == null || res.name == null || res.id == null) return;
        name = res.name;
        id = UndashedUuid.fromStringLenient(res.id);
        byte[] head = PlayerHeadUtils.fetchHead(id);
        if (head != null) headTexture = new PlayerHeadTexture(head, true);
        updating = false;
    }

    public boolean headTextureNeedsUpdate() {
        return !this.updating && headTexture == null;
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.putString("name", name);
        if (id != null) tag.putString("id", UndashedUuid.toString(id));

        return tag;
    }

    @Override
    public ScaryPerson fromTag(CompoundTag tag) {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScaryPerson scaryPerson = (ScaryPerson) o;
        return Objects.equals(name, scaryPerson.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(@NonNull ScaryPerson scaryPerson) {
        return name.compareTo(scaryPerson.name);
    }

    private static class APIResponse {
        String name, id;
    }
}