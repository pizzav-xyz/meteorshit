package com.example.addon.social.gui;

import com.example.addon.social.SocialColorUtils;
import com.example.addon.social.alttracker.AltAccount;
import com.example.addon.social.alttracker.AltTracker;
import com.example.addon.social.blacklistedpeople.BlacklistedPeople;
import com.example.addon.social.blacklistedpeople.BlacklistedPerson;
import com.example.addon.social.modules.SocialColorsModule;
import com.example.addon.social.scarypeople.ScaryPeople;
import com.example.addon.social.scarypeople.ScaryPerson;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.screens.Screen;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class SocialTab extends Tab {
    public SocialTab() {
        super("Social");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new SocialScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof SocialScreen;
    }

    private static class SocialScreen extends WindowTabScreen {
        public SocialScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);
        }

        @Override
        public void initWidgets() {
            // Top row: Friends (left) + Scary People (right)
            WHorizontalList topRow = add(theme.horizontalList()).expandX().widget();

            WVerticalList friendsSection = topRow.add(theme.verticalList()).expandX().widget();
            friendsSection.add(theme.label("Friends")).expandX().center();
            friendsSection.add(theme.horizontalSeparator()).expandX();

            WTable friendsTable = friendsSection.add(theme.table()).expandX().minWidth(200).widget();
            initFriendsTable(friendsTable);

            friendsSection.add(theme.horizontalSeparator()).expandX();

            WHorizontalList friendsInputList = friendsSection.add(theme.horizontalList()).expandX().widget();
            WTextBox friendNameW = friendsInputList.add(theme.textBox("", (text, c) -> c != ' ')).expandX().widget();
            friendNameW.setFocused(true);

            WPlus friendAdd = friendsInputList.add(theme.plus()).widget();
            friendAdd.action = () -> {
                String name = friendNameW.get().trim();
                Friend friend = new Friend(name);

                if (Friends.get().add(friend)) {
                    friendNameW.set("");
                    reload();

                    MeteorExecutor.execute(() -> {
                        friend.updateInfo();
                        reload();
                    });
                }
            };

            topRow.add(theme.verticalSeparator()).centerY();

            WVerticalList scarySection = topRow.add(theme.verticalList()).expandX().widget();
            scarySection.add(theme.label("Scary People")).expandX().center();
            scarySection.add(theme.horizontalSeparator()).expandX();

            WTable scaryTable = scarySection.add(theme.table()).expandX().minWidth(200).widget();
            initScaryTable(scaryTable);

            scarySection.add(theme.horizontalSeparator()).expandX();

            WHorizontalList scaryInputList = scarySection.add(theme.horizontalList()).expandX().widget();
            WTextBox scaryNameW = scaryInputList.add(theme.textBox("", (text, c) -> c != ' ')).expandX().widget();

            WPlus scaryAdd = scaryInputList.add(theme.plus()).widget();
            scaryAdd.action = () -> {
                String name = scaryNameW.get().trim();
                ScaryPerson scaryPerson = new ScaryPerson(name);

                if (ScaryPeople.get().add(scaryPerson)) {
                    scaryNameW.set("");
                    reload();

                    MeteorExecutor.execute(() -> {
                        scaryPerson.updateInfo();
                        reload();
                    });
                }
            };

            // Bottom row: Blacklisted People (left) + Alt Groups (right)
            WHorizontalList bottomRow = add(theme.horizontalList()).expandX().widget();

            WVerticalList blacklistedSection = bottomRow.add(theme.verticalList()).expandX().widget();
            blacklistedSection.add(theme.label("Blacklisted People")).expandX().center();
            blacklistedSection.add(theme.horizontalSeparator()).expandX();

            WTable blacklistedTable = blacklistedSection.add(theme.table()).expandX().minWidth(200).widget();
            initBlacklistedTable(blacklistedTable);

            blacklistedSection.add(theme.horizontalSeparator()).expandX();

            WHorizontalList blacklistedInputList = blacklistedSection.add(theme.horizontalList()).expandX().widget();
            WTextBox blacklistedNameW = blacklistedInputList.add(theme.textBox("", (text, c) -> c != ' ')).expandX().widget();

            WPlus blacklistedAdd = blacklistedInputList.add(theme.plus()).widget();
            blacklistedAdd.action = () -> {
                String name = blacklistedNameW.get().trim();
                BlacklistedPerson blacklistedPerson = new BlacklistedPerson(name);

                if (BlacklistedPeople.get().add(blacklistedPerson)) {
                    blacklistedNameW.set("");
                    reload();

                    MeteorExecutor.execute(() -> {
                        blacklistedPerson.updateInfo();
                        reload();
                    });
                }
            };

            bottomRow.add(theme.verticalSeparator()).centerY();

            WVerticalList altsSection = bottomRow.add(theme.verticalList()).expandX().widget();
            altsSection.add(theme.label("Alt Groups")).expandX().center();
            altsSection.add(theme.horizontalSeparator()).expandX();

            WTable altsTable = altsSection.add(theme.table()).expandX().minWidth(200).widget();
            initAltsTable(altsTable);

            altsSection.add(theme.horizontalSeparator()).expandX();

            WHorizontalList altsInputList = altsSection.add(theme.horizontalList()).expandX().widget();
            WTextBox mainW = altsInputList.add(theme.textBox("", (text, c) -> c != ' ')).expandX().widget();
            WTextBox altW = altsInputList.add(theme.textBox("", (text, c) -> c != ' ')).expandX().widget();

            WPlus altsAdd = altsInputList.add(theme.plus()).widget();
            altsAdd.action = () -> {
                String main = mainW.get().trim();
                String alt = altW.get().trim();

                if (!main.isEmpty() && !alt.isEmpty() && AltTracker.get().linkAccounts(main, alt)) {
                    mainW.set("");
                    altW.set("");
                    reload();
                }
            };

            enterAction = friendAdd.action;
        }

        private Color getNameColor(String playerName) {
            SocialColorsModule colors = Modules.get() != null ? Modules.get().get(SocialColorsModule.class) : null;
            if (colors == null || !colors.isActive()) return null;

            boolean isSelf = mc.player != null && playerName.equals(mc.player.getName().getString());
            boolean isScary = ScaryPeople.get().get(playerName) != null;
            boolean isBlacklisted = BlacklistedPeople.get().get(playerName) != null;
            boolean isAlt = AltTracker.get().isTracked(playerName);
            boolean isFriend = Friends.get().get(playerName) != null;

            SocialColorUtils.Status status = SocialColorUtils.resolveStatus(isSelf, isScary, isBlacklisted, isAlt, isFriend, false);
            if (status == SocialColorUtils.Status.Player || status == SocialColorUtils.Status.Team) return null;
            return SocialColorUtils.colorFor(status);
        }

        private void initFriendsTable(WTable table) {
            table.clear();
            if (Friends.get().isEmpty()) return;

            Friends.get().forEach(friend ->
                MeteorExecutor.execute(() -> {
                    if (friend.headTextureNeedsUpdate()) {
                        friend.updateInfo();
                        reload();
                    }
                })
            );

            for (Friend friend : Friends.get()) {
                table.add(theme.texture(32, 32, friend.getHead().needsRotate() ? 90 : 0, friend.getHead()));

                Color nameColor = getNameColor(friend.getName());

                if (nameColor != null) {
                    table.add(theme.label(friend.getName())).widget().color = nameColor;
                } else {
                    table.add(theme.label(friend.getName()));
                }

                WMinus remove = table.add(theme.minus()).expandCellX().right().widget();
                remove.action = () -> {
                    Friends.get().remove(friend);
                    reload();
                };

                table.row();
            }
        }

        private void initScaryTable(WTable table) {
            table.clear();
            if (ScaryPeople.get().isEmpty()) return;

            ScaryPeople.get().forEach(scaryPerson ->
                MeteorExecutor.execute(() -> {
                    if (scaryPerson.headTextureNeedsUpdate()) {
                        scaryPerson.updateInfo();
                        reload();
                    }
                })
            );

            for (ScaryPerson scaryPerson : ScaryPeople.get()) {
                table.add(theme.texture(32, 32, scaryPerson.getHead().needsRotate() ? 90 : 0, scaryPerson.getHead()));

                Color nameColor = getNameColor(scaryPerson.getName());

                if (nameColor != null) {
                    table.add(theme.label(scaryPerson.getName())).widget().color = nameColor;
                } else {
                    table.add(theme.label(scaryPerson.getName()));
                }

                WMinus remove = table.add(theme.minus()).expandCellX().right().widget();
                remove.action = () -> {
                    ScaryPeople.get().remove(scaryPerson);
                    reload();
                };

                table.row();
            }
        }

        private void initBlacklistedTable(WTable table) {
            table.clear();
            if (BlacklistedPeople.get().isEmpty()) return;

            BlacklistedPeople.get().forEach(blacklistedPerson ->
                MeteorExecutor.execute(() -> {
                    if (blacklistedPerson.headTextureNeedsUpdate()) {
                        blacklistedPerson.updateInfo();
                        reload();
                    }
                })
            );

            for (BlacklistedPerson blacklistedPerson : BlacklistedPeople.get()) {
                table.add(theme.texture(32, 32, blacklistedPerson.getHead().needsRotate() ? 90 : 0, blacklistedPerson.getHead()));

                Color nameColor = getNameColor(blacklistedPerson.getName());

                if (nameColor != null) {
                    table.add(theme.label(blacklistedPerson.getName())).widget().color = nameColor;
                } else {
                    table.add(theme.label(blacklistedPerson.getName()));
                }

                WMinus remove = table.add(theme.minus()).expandCellX().right().widget();
                remove.action = () -> {
                    BlacklistedPeople.get().remove(blacklistedPerson);
                    reload();
                };

                table.row();
            }
        }

        private void initAltsTable(WTable table) {
            table.clear();
            if (AltTracker.get().isEmpty()) return;

            AltTracker.get().forEach(group ->
                MeteorExecutor.execute(() -> {
                    if (group.headTextureNeedsUpdate()) {
                        group.updateInfo();
                        reload();
                    }
                })
            );

            for (AltAccount group : AltTracker.get()) {
                table.add(theme.texture(32, 32, group.getHead().needsRotate() ? 90 : 0, group.getHead()));

                String label = group.getMainAccount() + " (+" + group.getAltAccounts().size() + " alts)";
                Color nameColor = getNameColor(group.getMainAccount());

                if (nameColor != null) {
                    table.add(theme.label(label)).widget().color = nameColor;
                } else {
                    table.add(theme.label(label));
                }

                WMinus remove = table.add(theme.minus()).expandCellX().right().widget();
                remove.action = () -> {
                    AltTracker.get().remove(group);
                    reload();
                };

                table.row();
            }
        }

        @Override
        public boolean toClipboard() {
            return NbtUtils.toClipboard(Friends.get()) && NbtUtils.toClipboard(ScaryPeople.get()) && NbtUtils.toClipboard(BlacklistedPeople.get()) && NbtUtils.toClipboard(AltTracker.get());
        }

        @Override
        public boolean fromClipboard() {
            return NbtUtils.fromClipboard(Friends.get()) && NbtUtils.fromClipboard(ScaryPeople.get()) && NbtUtils.fromClipboard(BlacklistedPeople.get()) && NbtUtils.fromClipboard(AltTracker.get());
        }
    }
}
