/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.SeedFinder.SeedfinderLogResult;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.PlatformSupport;
import com.watabou.utils.RectF;

public class WndSeedfinderLog extends Window {

    private static final int GAP = 2;
    private static final int BUTTON_HEIGHT = 19;
    private final SeedfinderLogResult result;
    private final int floorCount;
    private int selectedIndex;
    private boolean rooms;
    private final int textWidth;

    private final RenderedTextBlock floorLabel;
    private final RenderedTextBlock body;
    private final ScrollPane scroll;
    private final StyledButton previous;
    private final StyledButton floorButton;
    private final StyledButton next;
    private final StyledButton roomButton;

    public WndSeedfinderLog(Image icon, String title, SeedfinderLogResult result) {
        super();
        this.result = result;
        floorCount = result.main.length;
        selectedIndex = SPDSettings.seedfinderLastFloor(result.seed, floorCount) - 1;

        RectF insets = Game.platform.getSafeInsets(PlatformSupport.INSET_BLK);
        int availableWidth = (int)((Game.width - insets.left - insets.right) / PixelScene.defaultZoom);
        int availableHeight = (int)((Game.height - insets.top - insets.bottom) / PixelScene.defaultZoom);
        int w = Math.min(PixelScene.landscape() ? 260 : 180,
                availableWidth - chrome.marginHor() - 4);
        int h = availableHeight - chrome.marginVer() - 4;
        resize(w, h);
        textWidth = w - 8;

        IconTitle titlebar = new IconTitle(icon, title);
        titlebar.setRect(2, 1, w - 4, 0);
        add(titlebar);

        float headerY = titlebar.bottom() + 5;
        roomButton = new StyledButton(Chrome.Type.GREY_BUTTON_TR, message("rooms"), 7) {
            @Override
            protected void onClick() {
                rooms = !rooms;
                refresh();
            }
        };
        float toggleWidth = Math.max(36, Math.min(w / 2f, roomButton.reqWidth() + 14));
        roomButton.setRect(w - toggleWidth, headerY, toggleWidth, BUTTON_HEIGHT);
        add(roomButton);

        floorLabel = PixelScene.renderTextBlock(7);
        floorLabel.hardlight(TITLE_COLOR);
        add(floorLabel);

        Component content = new Component();
        body = PixelScene.renderTextBlock(SPDSettings.seedfinderFontSize());
        content.add(body);
        scroll = new ScrollPane(content);
        add(scroll);
        float bodyY = headerY + BUTTON_HEIGHT + 5;
        float navY = h - BUTTON_HEIGHT;
        scroll.setRect(3, bodyY, w - 6, Math.max(1, navY - bodyY - 4));
        separator(2, bodyY - 3, w - 4);
        separator(2, navY - 3, w - 4);

        previous = new StyledButton(Chrome.Type.GREY_BUTTON, "", 8) {
            @Override
            protected void onClick() {
                setFloor(selectedIndex - 1);
            }
        };
        floorButton = new StyledButton(Chrome.Type.RED_BUTTON, "", 7) {
            @Override
            protected void onClick() {
                ShatteredPixelDungeon.scene().addToFront(new FloorPicker());
            }
        };
        next = new StyledButton(Chrome.Type.GREY_BUTTON, "", 8) {
            @Override
            protected void onClick() {
                setFloor(selectedIndex + 1);
            }
        };
        StyledButton back = new StyledButton(Chrome.Type.GREY_BUTTON, message("back"), 7) {
            @Override
            protected void onClick() {
                hide();
            }
        };

        previous.icon(Icons.LEFTARROW.get());
        next.icon(Icons.RIGHTARROW.get());
        floorButton.textColor(TITLE_COLOR);
        float arrowWidth = 20;
        float backWidth = Math.max(32, back.reqWidth() + 6);
        float floorWidth = w - 2 * arrowWidth - backWidth - 3 * GAP;
        previous.setRect(0, navY, arrowWidth, BUTTON_HEIGHT);
        floorButton.setRect(previous.right() + GAP, navY, floorWidth, BUTTON_HEIGHT);
        next.setRect(floorButton.right() + GAP, navY, arrowWidth, BUTTON_HEIGHT);
        back.setRect(next.right() + GAP, navY, backWidth, BUTTON_HEIGHT);
        add(previous);
        add(floorButton);
        add(next);
        add(back);

        rememberFloor();
        refresh();
    }

    private void separator(float x, float y, float width) {
        ColorBlock line = new ColorBlock(1, 1, 0xFF727865);
        line.x = x;
        line.y = y;
        line.size(width, 1);
        add(line);
    }

    private static String message(String key, Object... args) {
        return Messages.get(WndSeedfinderLog.class, key, args);
    }

    private void rememberFloor() {
        SPDSettings.seedfinderRememberFloor(result.seed, selectedIndex + 1);
    }

    private void setFloor(int index) {
        int clamped = Math.max(0, Math.min(floorCount - 1, index));
        if (clamped != selectedIndex) {
            selectedIndex = clamped;
            rememberFloor();
            refresh();
        }
    }

    private void refresh() {
        floorLabel.text(message("current_floor", selectedIndex + 1), (int)roomButton.left() - GAP);
        floorLabel.setPos(0, roomButton.top() + (BUTTON_HEIGHT - floorLabel.height()) / 2);
        floorButton.text(message("floor_action", selectedIndex + 1, floorCount));
        roomButton.text(message(rooms ? "items" : "rooms"));
        previous.enable(selectedIndex > 0);
        next.enable(selectedIndex + 1 < floorCount);
        String details = (rooms ? result.rooms : result.main)[selectedIndex];
        if (details == null || details.trim().isEmpty()) details = message("empty");
        // Highlight section headings, not individual item names or search data.
        details = details.replaceAll("(?m)^([^\\n-][^\\n:：]{0,24}[:：]) *$", "_$1_");
        body.text(details, textWidth);
        body.setPos(0, 0);
        scroll.content().setSize(scroll.width(), Math.max(1, (int)Math.ceil(body.height())));
        scroll.setSize(scroll.width(), scroll.height());
        scroll.scrollTo(0, 0);
    }

    private class FloorPicker extends Window {
        FloorPicker() {
            super();
            int w = Math.min(145, WndSeedfinderLog.this.width);
            RenderedTextBlock heading = PixelScene.renderTextBlock(message("choose_floor"), 9);
            heading.hardlight(TITLE_COLOR);
            heading.maxWidth(w);
            heading.setPos((w - heading.width()) / 2, 0);
            add(heading);

            float startY = heading.bottom() + 5;
            float cellWidth = (w - 4 * GAP) / 5f;
            int rows = (floorCount + 4) / 5;
            for (int i = 0; i < floorCount; i++) {
                final int index = i;
                StyledButton button = new StyledButton(
                        i == selectedIndex ? Chrome.Type.RED_BUTTON : Chrome.Type.GREY_BUTTON,
                        Integer.toString(i + 1), 8) {
                    @Override
                    protected void onClick() {
                        hide();
                        setFloor(index);
                    }
                };
                if (i == selectedIndex) {
                    button.textColor(TITLE_COLOR);
                } else if ((i + 1) % 5 == 0) {
                    button.textColor(0xFFAA66);
                }
                button.setRect((i % 5) * (cellWidth + GAP),
                        startY + (i / 5) * (BUTTON_HEIGHT + GAP), cellWidth, BUTTON_HEIGHT);
                add(button);
            }
            float bottom = startY + rows * (BUTTON_HEIGHT + GAP);
            StyledButton cancel = new StyledButton(Chrome.Type.GREY_BUTTON, message("back"), 7) {
                @Override
                protected void onClick() { hide(); }
            };
            cancel.setRect(0, bottom, w, BUTTON_HEIGHT);
            add(cancel);
            resize(w, (int)Math.ceil(bottom + BUTTON_HEIGHT));
        }
    }
}
