package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Texture;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.PlatformSupport;
import com.watabou.utils.RectF;

/** Shared toolkit palette, rounded nine-patch surfaces and physical safe area. */
public final class ToolkitTheme {
    public static final int BACKGROUND = 0xFF151D27;
    public static final int TEXT = 0xE5EDF4;
    public static final int ACCENT = 0xA4D8CA;
    public static final int DIVIDER = 0xFF3A4858;

    private ToolkitTheme() {}

    public static RectF safeInsets() {
        RectF safe = Game.platform.getSafeInsets(PlatformSupport.INSET_ALL);
        if (DeviceCompat.isiOS()) {
            // The game platform deliberately ignores some iOS cutouts and the home indicator.
            // Toolkit controls must instead respect the complete native safe area.
            safe.left = Math.max(safe.left, Gdx.graphics.getSafeInsetLeft());
            safe.top = Math.max(safe.top, Gdx.graphics.getSafeInsetTop());
            safe.right = Math.max(safe.right, Gdx.graphics.getSafeInsetRight());
            safe.bottom = Math.max(safe.bottom, Gdx.graphics.getSafeInsetBottom());
        }
        return safe;
    }

    private static boolean inside(float x, float y, int w, int h, float radius, float inset) {
        if (x < inset || y < inset || x >= w - inset || y >= h - inset) return false;
        float r = Math.max(0, radius - inset);
        float cx = Math.max(inset + r, Math.min(w - inset - r, x));
        float cy = Math.max(inset + r, Math.min(h - inset - r, y));
        float dx = x - cx, dy = y - cy;
        return dx * dx + dy * dy <= r * r;
    }

    private static int shade(int color, float factor) {
        int r = Math.min(255, Math.round(((color >>> 16) & 255) * factor));
        int g = Math.min(255, Math.round(((color >>> 8) & 255) * factor));
        int b = Math.min(255, Math.round((color & 255) * factor));
        return (color & 0xFF000000) | (r << 16) | (g << 8) | b;
    }

    /** Small embossed navigation chevrons, cached and restored with other textures. */
    public static Image navigationIcon(int direction) {
        String key = "toolkit-nav-v2:" + direction;
        boolean exists = TextureCache.contains(key);
        SmartTexture tex = TextureCache.create(key, 11, 11);
        if (!exists) {
            Pixmap pix = tex.bitmap;
            pix.setBlending(Pixmap.Blending.None);
            for (int y = 0; y < 11; y++) {
                for (int x = 0; x < 11; x++) {
                    int u = direction == 0 ? 10 - y : direction < 0 ? x : 10 - x;
                    int v = direction == 0 ? x : y;
                    int distance = Math.abs(u - (2 + Math.abs(v - 5)));
                    int color = v < 1 || v > 9 ? 0 : distance <= 0
                            ? 0xFFE9F4F7 : distance == 1 ? 0xFF9DAFBE : distance == 2 ? 0xFF15202B : 0;
                    pix.drawPixel(x, y, (color << 8) | (color >>> 24));
                }
            }
            tex.filter(Texture.NEAREST, Texture.NEAREST);
        }
        return new Image(tex);
    }

    public static NinePatch patch(String name, int w, int h, int left, int top,
                                  int right, int bottom, int radius, int fill, int border) {
        String key = "toolkit-theme-v2:" + name;
        boolean exists = TextureCache.contains(key);
        SmartTexture texture = TextureCache.create(key, w, h);
        if (!exists) {
            Pixmap pixels = texture.bitmap;
            pixels.setBlending(Pixmap.Blending.None);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    float px = x + 0.5f, py = y + 0.5f;
                    boolean panel = name.equals("window") || name.equals("silver") || name.equals("tabset");
                    float vertical = y / (float)Math.max(1, h - 1);
                    int argb = 0;
                    if (inside(px, py, w, h, radius, 0)) {
                        if (!inside(px, py, w, h, radius, 1)) {
                            // Raised bevel: lit upper rim, shaded lower rim.
                            argb = shade(border, y < h / 2f ? 1.32f : 0.63f);
                        } else if (panel) {
                            argb = y < 2 ? shade(fill, 1.22f) : y >= h - 2 ? shade(fill, 0.74f) : fill;
                        } else {
                            argb = shade(fill, 1.24f - 0.43f * vertical);
                            if (y == 1) argb = shade(fill, 1.47f);
                            if (y == h - 2) argb = shade(fill, 0.71f);
                        }
                    }
                    pixels.drawPixel(x, y, (argb << 8) | (argb >>> 24));
                }
            }
            texture.filter(Texture.NEAREST, Texture.NEAREST);
        }
        return new NinePatch(texture, 0, 0, w, h, left, top, right, bottom);
    }
}
