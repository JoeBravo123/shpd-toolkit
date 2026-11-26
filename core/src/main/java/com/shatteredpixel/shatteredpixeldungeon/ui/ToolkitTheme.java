package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Texture;
import com.watabou.noosa.Game;
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

    public static NinePatch patch(String name, int w, int h, int left, int top,
                                  int right, int bottom, int radius, int fill, int border) {
        String key = "toolkit-theme-v1:" + name;
        boolean exists = TextureCache.contains(key);
        SmartTexture texture = TextureCache.create(key, w, h);
        if (!exists) {
            Pixmap pixels = texture.bitmap;
            pixels.setBlending(Pixmap.Blending.None);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    float px = x + 0.5f, py = y + 0.5f;
                    int argb = !inside(px, py, w, h, radius, 0) ? 0
                            : inside(px, py, w, h, radius, 1) ? fill : border;
                    pixels.drawPixel(x, y, (argb << 8) | (argb >>> 24));
                }
            }
            texture.filter(Texture.NEAREST, Texture.NEAREST);
        }
        return new NinePatch(texture, 0, 0, w, h, left, top, right, bottom);
    }
}
