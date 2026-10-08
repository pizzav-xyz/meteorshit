package com.example.addon.social.gui;

import com.example.addon.social.alttracker.AltAccount;
import com.example.addon.social.alttracker.AltTracker;
import com.example.addon.social.blacklistedpeople.BlacklistedPeople;
import com.example.addon.social.blacklistedpeople.BlacklistedPerson;
import com.example.addon.social.scarypeople.ScaryPeople;
import com.example.addon.social.scarypeople.ScaryPerson;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
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
        private WTable friendsTable;
        private WTable scaryTable;
        private WTable blacklistedTable;
        private WTable altsTable;

        public SocialScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);
        }

        @Override
        public void initWidgets() {
            add(theme.label("Friends")).expandX().center();
            add(theme.horizontalSeparator()).expandX();
            friendsTable = add(theme.table()).expandX().minWidth(400).widget();
            initFriendsTable();

            WHorizontalList friendsInput = add(theme.horizontalList()).expandX().widget();
            WTextBox friendNameW = friendsInput.add(theme.textBox("", (text, c) -> c != ' ')).expandX().widget();
            friendNameW.setFocused(true);
            WPlus friendAdd = friendsInput.add(theme.plus()).widget();
            friendAdd.action = () -> {
                String name = friendNameW.get().trim();
                Friend friend = new Friend(name);
                if (Friends.get().add(friend)) {
                    friendNameW.set("");
                    initFriendsTable();
                    friendNameW.setFocused(true);
                    MeteorExecutor.execute(() -> {
                        friend.updateInfo();
                        mc.execute(() -> {
                            initFriendsTable();
                            friendNameW.setFocused(true);
                        });
                    });
                }
            };

            add(theme.horizontalSeparator()).expandX();
            add(theme.label("Scary People")).expandX().center();
            add(theme.horizontalSeparator()).expandX();
            scaryTable = add(theme.table()).expandX().minWidth(400).widget();
            initScaryTable();

            WHorizontalList scaryInput = add(theme.horizontalList()).expandX().widget();
            WTextBox scaryNameW = scaryInput.add(theme.textBox("", (text, c) -> c != ' ')).expandX().widget();
            WPlus scaryAdd = scaryInput.add(theme.plus()).widget();
            scaryAdd.action = () -> {
                String name = scaryNameW.get().trim();
                ScaryPerson scaryPerson = new ScaryPerson(name);
                if (ScaryPeople.get().add(scaryPerson)) {
                    scaryNameW.set("");
                    initScaryTable();
                    MeteorExecutor.execute(() -> {
                        scaryPerson.updateInfo();
                        mc.execute(this::initScaryTable);
                    });
                }
            };

            add(theme.horizontalSeparator()).expandX();
            add(theme.label("Blacklisted People")).expandX().center();
            add(theme.horizontalSeparator()).expandX();
            blacklistedTable = add(theme.table()).expandX().minWidth(400).widget();
            initBlacklistedTable();

            WHorizontalList blacklistedInput = add(theme.horizontalList()).expandX().widget();
            WTextBox blacklistedNameW = blacklistedInput.add(theme.textBox("", (text, c) -> c != ' ')).expandX().widget();
            WPlus blacklistedAdd = blacklistedInput.add(theme.plus()).widget();
            blacklistedAdd.action = () -> {
                String name = blacklistedNameW.get().trim();
                BlacklistedPerson blacklistedPerson = new BlacklistedPerson(name);
                if (BlacklistedPeople.get().add(blacklistedPerson)) {
                    blacklistedNameW.set("");
                    initBlacklistedTable();
                    MeteorExecutor.execute(() -> {
                        blacklistedPerson.updateInfo();
                        mc.execute(this::initBlacklistedTable);
                    });
                }
            };

            add(theme.horizontalSeparator()).expandX();
            add(theme.label("Alt Groups")).expandX().center();
            add(theme.horizontalSeparator()).expandX();
            altsTable = add(theme.table()).expandX().minWidth(400).widget();
            initAltsTable();

            WHorizontalList altsInput = add(theme.horizontalList()).expandX().widget();
            WTextBox mainW = altsInput.add(theme.textBox("", (text, c) -> c != ' ')).expandX().widget();
            WTextBox altW = altsInput.add(theme.textBox("", (text, c) -> c != ' ')).expandX().widget();
            WPlus altsAdd = altsInput.add(theme.plus()).widget();
            altsAdd.action = () -> {
                String main = mainW.get().trim();
                String alt = altW.get().trim();
                if (!main.isEmpty() && !alt.isEmpty() && AltTracker.get().linkAccounts(main, alt)) {
                    mainW.set("");
                    altW.set("");
                    initAltsTable();
                }
            };

            enterAction = friendAdd.action;
        }

        private void initFriendsTable() {
            friendsTable.clear();
            if (Friends.get().isEmpty()) return;

            Friends.get().forEach(friend ->
                MeteorExecutor.execute(() -> {
                    if (friend.headTextureNeedsUpdate()) friend.updateInfo();
                })
            );

            for (Friend friend : Friends.get()) {
                friendsTable.add(theme.texture(32, 32, friend.getHead().needsRotate() ? 90 : 0, friend.getHead()));
                friendsTable.add(theme.label(friend.getName()));

                WMinus remove = friendsTable.add(theme.minus()).expandCellX().right().widget();
                remove.action = () -> {
                    Friends.get().remove(friend);
                    initFriendsTable();
                };

                friendsTable.row();
            }
        }

        private void initScaryTable() {
            scaryTable.clear();
            if (ScaryPeople.get().isEmpty()) return;

            ScaryPeople.get().forEach(scaryPerson ->
                MeteorExecutor.execute(() -> {
                    if (scaryPerson.headTextureNeedsUpdate()) scaryPerson.updateInfo();
                })
            );

            for (ScaryPerson scaryPerson : ScaryPeople.get()) {
                scaryTable.add(theme.texture(32, 32, scaryPerson.getHead().needsRotate() ? 90 : 0, scaryPerson.getHead()));
                scaryTable.add(theme.label(scaryPerson.getName()));

                WMinus remove = scaryTable.add(theme.minus()).expandCellX().right().widget();
                remove.action = () -> {
                    ScaryPeople.get().remove(scaryPerson);
                    initScaryTable();
                };

                scaryTable.row();
            }
        }

        private void initBlacklistedTable() {
            blacklistedTable.clear();
            if (BlacklistedPeople.get().isEmpty()) return;

            BlacklistedPeople.get().forEach(blacklistedPerson ->
                MeteorExecutor.execute(() -> {
                    if (blacklistedPerson.headTextureNeedsUpdate()) blacklistedPerson.updateInfo();
                })
            );

            for (BlacklistedPerson blacklistedPerson : BlacklistedPeople.get()) {
                blacklistedTable.add(theme.texture(32, 32, blacklistedPerson.getHead().needsRotate() ? 90 : 0, blacklistedPerson.getHead()));
                blacklistedTable.add(theme.label(blacklistedPerson.getName()));

                WMinus remove = blacklistedTable.add(theme.minus()).expandCellX().right().widget();
                remove.action = () -> {
                    BlacklistedPeople.get().remove(blacklistedPerson);
                    initBlacklistedTable();
                };

                blacklistedTable.row();
            }
        }

        private void initAltsTable() {
            altsTable.clear();
            if (AltTracker.get().isEmpty()) return;

            AltTracker.get().forEach(group ->
                MeteorExecutor.execute(() -> {
                    if (group.headTextureNeedsUpdate()) group.updateInfo();
                })
            );

            for (AltAccount group : AltTracker.get()) {
                altsTable.add(theme.texture(32, 32, group.getHead().needsRotate() ? 90 : 0, group.getHead()));
                altsTable.add(theme.label(group.getMainAccount() + " (+" + group.getAltAccounts().size() + " alts)"));

                WMinus remove = altsTable.add(theme.minus()).expandCellX().right().widget();
                remove.action = () -> {
                    AltTracker.get().remove(group);
                    initAltsTable();
                };

                altsTable.row();
            }
        }
    }
}
