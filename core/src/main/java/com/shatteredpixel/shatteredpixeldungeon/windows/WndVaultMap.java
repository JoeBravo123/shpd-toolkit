package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.badlogic.gdx.graphics.Pixmap;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.SeedFinder.VaultMapPreview;
import com.shatteredpixel.shatteredpixeldungeon.SeedFinder.VaultMapRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ToolkitTheme;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

/** A read-only snapshot of the exact same generated vault as the item list. */
public class WndVaultMap extends Window {
    private static final int PIXELS = 4;
    private static final int[] ROOM_COLORS = {0xFF285C4C, 0xFF354B62, 0xFF795C32, 0xFF604674, 0xFF783E42};
    private final VaultMapPreview data;
    private final Object textureKey = new Object();
    private final RenderedTextBlock detail;
    private final ScrollPane detailScroll;
    private final ColorBlock[] selectionEdges = new ColorBlock[4];
    private final float mapX, mapY, tile;
    private final int minX, minY;
    private int chosen = -1;

    private static String msg(String key, Object... args) {
        return Messages.get(WndVaultMap.class, key, args);
    }

    public WndVaultMap(VaultMapPreview data, int w, int h) {
        super(w, h);
        this.data = data;
        RenderedTextBlock title = PixelScene.renderTextBlock(msg("map_title"), 9);
        title.hardlight(ToolkitTheme.ACCENT);
        title.maxWidth(w - 4);
        title.setPos(2, 2);
        add(title);

        RenderedTextBlock legend = PixelScene.renderTextBlock(msg("map_legend") + "\n" + msg("map_markers"), 6);
        legend.maxWidth(w - 4);
        legend.setPos(2, title.bottom() + 3);
        add(legend);
        float top = legend.bottom() + 4;
        float bottom = h - 22;
        boolean side = w > h;
        float mapAreaW = side ? w * 0.58f - 4 : w - 4;
        float mapAreaH = side ? bottom - top : Math.max(12, Math.min(w - 4, (bottom - top) * 0.67f));

        int l = data.width - 1, r = 0, t = data.height - 1, b = 0;
        for (VaultMapRoom room : data.rooms) {
            l = Math.min(l, room.left);
            r = Math.max(r, room.right);
            t = Math.min(t, room.top);
            b = Math.max(b, room.bottom);
        }
        minX = Math.max(0, l - 1);
        minY = Math.max(0, t - 1);
        int maxX = Math.min(data.width - 1, r + 1);
        int maxY = Math.min(data.height - 1, b + 1);
        int mapW = Math.max(1, maxX - minX + 1);
        int mapH = Math.max(1, maxY - minY + 1);
        tile = Math.min(mapAreaW / mapW, mapAreaH / mapH);
        mapX = 2 + (mapAreaW - mapW * tile) / 2;
        mapY = top + (mapAreaH - mapH * tile) / 2;

        SmartTexture texture = TextureCache.create(textureKey, mapW * PIXELS, mapH * PIXELS);
        Pixmap pixels = texture.bitmap;
        pixels.setBlending(Pixmap.Blending.None);
        int[] owner = new int[data.terrain.length];
        for (int i = 0; i < owner.length; i++) owner[i] = -1;
        for (int i = 0; i < data.rooms.size(); i++) {
            VaultMapRoom room = data.rooms.get(i);
            for (int y = Math.max(0, room.top + 1); y < Math.min(data.height, room.bottom); y++) {
                for (int x = Math.max(0, room.left + 1); x < Math.min(data.width, room.right); x++) {
                    owner[y * data.width + x] = i;
                }
            }
        }
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                int pos = y * data.width + x;
                int room = owner[pos];
                int base = room < 0 ? 0xFF27343F : ROOM_COLORS[data.rooms.get(room).kind];
                int color = terrainColor(data.terrain[pos], base, room >= 0);
                int px = (x - minX) * PIXELS, py = (y - minY) * PIXELS;
                pixels.setColor(rgba(color));
                pixels.fillRectangle(px, py, PIXELS, PIXELS);
                if (data.markers[pos] != 0) {
                    int marker = data.markers[pos] == 4 ? 0xFFFF7779 : data.markers[pos] == 3 ? 0xFFD5ACFF
                            : data.markers[pos] == 2 ? 0xFFFFD27B : 0xFFF3F7FC;
                    pixels.setColor(rgba(marker));
                    pixels.fillRectangle(px + 1, py + 1, 2, 2);
                }
            }
        }
        Image image = new Image(texture);
        image.scale.set(tile / PIXELS);
        image.x = mapX;
        image.y = mapY;
        add(image);
        for (int i = 0; i < data.rooms.size(); i++) {
            final int index = i;
            VaultMapRoom room = data.rooms.get(i);
            float x = mapX + (room.left - minX + 1) * tile;
            float y = mapY + (room.top - minY + 1) * tile;
            float rw = Math.max(tile, (room.right - room.left - 1) * tile);
            float rh = Math.max(tile, (room.bottom - room.top - 1) * tile);
            RenderedTextBlock number = PixelScene.renderTextBlock(Integer.toString(i + 1), 6);
            number.hardlight(0xFFFFFF);
            number.setPos(x + (rw - number.width()) / 2, y + (rh - number.height()) / 2);
            add(number);
            Button tap = new Button() {
                @Override
                protected void onClick() { select(index); }
            };
            tap.setRect(x, y, rw, rh);
            add(tap);
        }
        for (int i = 0; i < selectionEdges.length; i++) {
            selectionEdges[i] = new ColorBlock(1, 1, 0xFFFFE1A0);
            selectionEdges[i].visible = false;
            add(selectionEdges[i]);
        }

        float detailX = side ? mapAreaW + 6 : 2;
        float detailY = side ? top : top + mapAreaH + 3;
        float detailW = w - detailX - 2;
        Component content = new Component();
        detail = PixelScene.renderTextBlock(7);
        detail.hardlight(ToolkitTheme.TEXT);
        detail.setHightlighting(true, ToolkitTheme.ACCENT);
        content.add(detail);
        detailScroll = new ScrollPane(content);
        add(detailScroll);
        detailScroll.setRect(detailX, detailY, detailW, Math.max(1, bottom - detailY));

        StyledButton back = new StyledButton(Chrome.Type.GREY_BUTTON, msg("map_close"), 7) {
            @Override
            protected void onClick() { hide(); }
        };
        back.setRect(0, h - 19, w, 19);
        add(back);
        showIndex();
    }

    private static int rgba(int argb) {
        return (argb << 8) | (argb >>> 24);
    }

    private static int terrainColor(int terrain, int base, boolean inside) {
        switch (terrain) {
            case Terrain.WALL:
            case Terrain.WALL_DECO:
                return inside ? 0xFF53606C : 0xFF141C25;
            case Terrain.CHASM: return 0xFF0B1118;
            case Terrain.BOOKSHELF: return 0xFF9A7047;
            case Terrain.WATER: return 0xFF356A8E;
            case Terrain.DOOR:
            case Terrain.OPEN_DOOR: return 0xFFAAB2A0;
            case Terrain.LOCKED_DOOR: return 0xFFC08A58;
            case Terrain.TRAP:
            case Terrain.INACTIVE_TRAP: return 0xFF8B5365;
            case Terrain.PEDESTAL: return 0xFF7C8997;
            case Terrain.GRASS:
            case Terrain.HIGH_GRASS:
            case Terrain.FURROWED_GRASS: return 0xFF466C4C;
            default: return base;
        }
    }

    private void showIndex() {
        StringBuilder text = new StringBuilder(msg("map_hint")).append("\n\n");
        for (int i = 0; i < data.rooms.size(); i++) {
            VaultMapRoom room = data.rooms.get(i);
            text.append(i + 1).append(". ").append(room.name);
            if (!room.items.isEmpty()) text.append(" · ").append(room.items.get(0));
            text.append("\n");
        }
        if (!data.unassigned.isEmpty()) {
            text.append("\n").append(msg("map_other")).append(":\n");
            for (String item : data.unassigned) text.append("- ").append(item).append("\n");
        }
        setDetail(text.toString());
    }

    private void select(int index) {
        if (chosen == index) {
            chosen = -1;
            for (ColorBlock edge : selectionEdges) edge.visible = false;
            showIndex();
            return;
        }
        chosen = index;
        VaultMapRoom room = data.rooms.get(index);
        float x = mapX + (room.left - minX) * tile;
        float y = mapY + (room.top - minY) * tile;
        float w = (room.right - room.left + 1) * tile;
        float h = (room.bottom - room.top + 1) * tile;
        edge(0, x, y, w, 0.7f);
        edge(1, x, y + h - 0.7f, w, 0.7f);
        edge(2, x, y, 0.7f, h);
        edge(3, x + w - 0.7f, y, 0.7f, h);

        StringBuilder text = new StringBuilder("_").append(msg("map_room", index + 1, room.name)).append("_\n\n");
        if (room.bookcase) text.append(msg("map_bookcase")).append("\n\n");
        if (room.items.isEmpty()) text.append(msg("map_empty"));
        else for (String item : room.items) text.append("- ").append(item).append("\n");
        setDetail(text.toString());
    }

    private void edge(int i, float x, float y, float w, float h) {
        ColorBlock block = selectionEdges[i];
        block.x = x;
        block.y = y;
        block.size(w, h);
        block.visible = true;
    }

    private void setDetail(String text) {
        detail.text(text, Math.max(1, (int)detailScroll.width() - 2));
        detail.setPos(0, 0);
        detailScroll.content().setSize(detailScroll.width(), Math.max(1, (int)Math.ceil(detail.height())));
        detailScroll.setSize(detailScroll.width(), detailScroll.height());
        detailScroll.scrollTo(0, 0);
    }

    @Override
    public void destroy() {
        super.destroy();
        TextureCache.remove(textureKey);
    }
}
