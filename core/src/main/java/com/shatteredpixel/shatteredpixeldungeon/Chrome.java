/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon;

import com.watabou.noosa.NinePatch;
import com.shatteredpixel.shatteredpixeldungeon.ui.ToolkitTheme;

public class Chrome {

	public enum  Type {
		TOAST,
		TOAST_TR,
		TOAST_TR_HEAVY,
		TOAST_WHITE,
		WINDOW,
		WINDOW_SILVER,
		RED_BUTTON,
		PRIMARY_BUTTON,
		GREY_BUTTON,
		GREY_BUTTON_TR,
		TAG,
		GEM,
		SCROLL,
		TAB_SET,
		TAB_SELECTED,
		TAB_UNSELECTED,
		BLANK
	}
	
	public static NinePatch get( Type type ) {
		String Asset = Assets.Interfaces.CHROME;
		switch (type) {
		case WINDOW:
			return ToolkitTheme.patch("window", 20, 20, 6, 6, 6, 6, 4, 0xFF202833, 0xFF3A4858);
		case WINDOW_SILVER:
			return ToolkitTheme.patch("silver", 22, 22, 7, 7, 7, 7, 4, 0xFF25313E, 0xFF566779);
		case TOAST:
			return ToolkitTheme.patch("toast", 9, 9, 4, 4, 4, 4, 3, 0xFF273340, 0xFF405166);
		case TOAST_TR:
		case GREY_BUTTON_TR:
			return ToolkitTheme.patch("quiet", 9, 9, 4, 4, 4, 4, 3, 0xFF2C3948, 0xFF41536A);
		case TOAST_TR_HEAVY:
			return ToolkitTheme.patch("heavy", 9, 9, 4, 4, 4, 4, 3, 0xFF202833, 0xFF405166);
		case TOAST_WHITE:
			return new NinePatch( Asset, 29, 0, 9, 9, 4 );
		case RED_BUTTON:
			return ToolkitTheme.patch("action", 6, 6, 2, 2, 2, 2, 2, 0xFF35465A, 0xFF4A6075);
		case PRIMARY_BUTTON:
			return ToolkitTheme.patch("primary", 10, 10, 3, 3, 3, 3, 3, 0xFF30685F, 0xFF4A8278);
		case GREY_BUTTON:
			return ToolkitTheme.patch("secondary", 8, 8, 3, 3, 3, 3, 3, 0xFF38485B, 0xFF506276);
		case TAG:
			return new NinePatch( Asset, 22, 18, 16, 14, 3 );
		case GEM:
			return new NinePatch( Asset, 0, 32, 32, 32, 13 );
		case SCROLL:
			return new NinePatch( Asset, 32, 32, 32, 32, 5, 11, 5, 11 );
		case TAB_SET:
			return ToolkitTheme.patch("tabset", 20, 20, 6, 6, 6, 6, 4, 0xFF202833, 0xFF3A4858);
		case TAB_SELECTED:
			return ToolkitTheme.patch("tabactive", 8, 13, 3, 7, 3, 5, 3, 0xFF28685F, 0xFF408C80);
		case TAB_UNSELECTED:
			return ToolkitTheme.patch("tabinactive", 8, 13, 3, 7, 3, 5, 3, 0xFF2D3948, 0xFF435367);
		case BLANK:
			return new NinePatch( Asset, 45, 0, 1, 1, 0, 0, 0, 0 );
		default:
			return null;
		}
	}
}
