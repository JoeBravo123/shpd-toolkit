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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.SeedFinder;
import com.shatteredpixel.shatteredpixeldungeon.SeedFinder.SeedfinderLogResult;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Clipboard;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.AvailableUpdateData;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.Updates;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndCatalog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSeedfinderLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSeedfinderSeedinput;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSettings;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndVictoryCongrats;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.ColorMath;
import com.watabou.utils.DeviceCompat;


public class TitleScene extends PixelScene {

	@Override
	public void create() {

		super.create();

		Music.INSTANCE.playTracks(
				new String[]{Assets.Music.THEME_1, Assets.Music.THEME_2},
				new float[]{1, 1},
				false);

		uiCamera.visible = false;

		int w = Camera.main.width;
		int h = Camera.main.height;

		Archs archs = new Archs();
		archs.setSize( w, h );
		add( archs );

		Image title = BannerSprites.get( landscape() ? BannerSprites.Type.TITLE_LAND : BannerSprites.Type.TITLE_PORT);
		add( title );

		float topRegion = Math.max(title.height - 6, h*0.39f);

		title.x = (w - title.width()) / 2f;
		title.y = 2 + (topRegion - title.height()) / 2f;

		align(title);

		//placeTorch(title.x + 22, title.y + 46);
		//placeTorch(title.x + title.width - 22, title.y + 46);

		Image signs = new Image(BannerSprites.get( landscape() ? BannerSprites.Type.TITLE_GLOW_LAND : BannerSprites.Type.TITLE_GLOW_PORT)){
			private float time = 0;
			@Override
			public void update() {
				super.update();
				am = Math.max(0f, (float)Math.sin( time += Game.elapsed ));
				if (time >= 1.5f*Math.PI) time = 0;
			}
			@Override
			public void draw() {
				Blending.setLightMode();
				super.draw();
				Blending.setNormalMode();
			}
		};
		signs.x = title.x + (title.width() - signs.width())/2f;
		signs.y = title.y;
		add( signs );

		final Chrome.Type GREY_TR = Chrome.Type.GREY_BUTTON_TR;

		StyledButton btnScout = new StyledButton(Chrome.Type.RED_BUTTON, Messages.get(TitleScene.class, "scout_seed_button")) {
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.scene()
						.addToFront(new WndSeedfinderSeedinput(Messages.get(TitleScene.class, "scout_custom_seed_title"),
								Messages.get(TitleScene.class, "scout_info_text"),
								SPDSettings.seedinputText(),
								20,
								false,
								Messages.get(TitleScene.class, "scout_button_yes"),
								Messages.get(TitleScene.class, "scout_button_no")) {
							@Override
							public void onSelect(boolean positive, String text) {
								if (positive && text != null && !text.isEmpty()) {
									SPDSettings.seedinputText(text);

									text = DungeonSeed.formatText(text);
									long seed = DungeonSeed.convertFromText(text);

									SeedfinderLogResult result = new SeedFinder().logSeedItemsSeededRun(seed);

									ShatteredPixelDungeon.scene().addToFront(
											new WndSeedfinderLog(Icons.get(Icons.BACKPACK),
													Messages.get(TitleScene.class, "scout_result_title", DungeonSeed.convertToCode(Dungeon.seed)),
													result));
								} else {
									SPDSettings.seedinputText("");
								}
							}

							// @Override
							// public void onTextChange(String text) {
							// 	SPDSettings.seedinputText(text);
							// }
						});
			}
		};
		btnScout.icon(Icons.get(Icons.SEED));
		btnScout.textColor(Window.TITLE_COLOR);
		add(btnScout);

		StyledButton btnSeedfinder = new StyledButton(GREY_TR, Messages.get(this, "seedfinder_button")){
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.scene()
						.addToFront(new WndSeedfinderSeedinput(Messages.get(TitleScene.class, "seedfinder_title"),
								Messages.get(TitleScene.class, "seedfinder_info_text"),
								SPDSettings.seeditemsText(),
								100,
								true,
								Messages.get(TitleScene.class, "seedfinder_button_yes"),
								Messages.get(TitleScene.class, "seedfinder_button_no")) {
							@Override
							public void onSelect(boolean positive, String seeditems_userInput) {
								if (positive) {
									SPDSettings.seeditemsText(seeditems_userInput);

									final Thread[] searchThread = new Thread[1];
									final WndOptions[] progressWnd = new WndOptions[1];

									// run the seedfinder in a new thread
									searchThread[0] = new Thread(() -> {
										final String foundSeed = new SeedFinder().find_seed(seeditems_userInput);

										// process results in the rendering thread
										Gdx.app.postRunnable(() -> {
											if (progressWnd[0].parent == null) {
												// Window was removed, so search was cancelled.
												return;
											}
											progressWnd[0].hide();

											if (foundSeed == null || foundSeed.startsWith("error")) {
												ShatteredPixelDungeon.scene().addToFront(new com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage(
														foundSeed != null ? foundSeed.substring("error:".length()).trim() : Messages.get(TitleScene.class, "seedfinder_not_found")));
												return;
											}

											// copy seed to clipboard on success
											Clipboard clipboard = Gdx.app.getClipboard();
											clipboard.setContents(foundSeed);

											long seed = DungeonSeed.convertFromText(foundSeed);

											SeedfinderLogResult result = new SeedFinder().logSeedItemsSeededRun(seed);

											ShatteredPixelDungeon.scene().addToFront(
													new WndSeedfinderLog(Icons.get(Icons.BACKPACK),
															Messages.get(TitleScene.class, "seedfinder_result_title", DungeonSeed.convertToCode(Dungeon.seed)),
															result));
										});
									});

									progressWnd[0] = new WndOptions(
											Icons.get(Icons.MAGNIFY),
											Messages.get(TitleScene.class, "seedfinder_searching_title"),
											Messages.get(TitleScene.class, "seedfinder_searching_text"),
											Messages.get(TitleScene.class, "seedfinder_searching_cancel")
									) {
										@Override
										protected void onSelect(int index) {
											if (index == 0) {
												searchThread[0].interrupt();
												hide();
											}
										}

										@Override
										public void onBackPressed() {
											// stiky like the other seedfinder windows to prevent accidental cancellation

											// searchThread[0].interrupt();
											// super.onBackPressed();
										}
									};

									ShatteredPixelDungeon.scene().addToFront(progressWnd[0]);
									searchThread[0].start();

								} else {
									SPDSettings.seeditemsText("");
								}
							}

							// @Override
							// public void onTextChange(String text) {
							// 	SPDSettings.seeditemsText(text);
							// }
						});
			}
		};
		btnSeedfinder.icon(Icons.get(Icons.MAGNIFY));
		add(btnSeedfinder);

		StyledButton btnScoutDaily = new StyledButton(GREY_TR, Messages.get(this, "scout_daily")) {
			@Override
			protected void onClick() {
				SeedfinderLogResult result = new SeedFinder().logSeedItemsDailyRunRun(0);

				String date = Dungeon.customSeedText;

				ShatteredPixelDungeon.scene().addToFront(
						new WndSeedfinderLog(Icons.get(Icons.BACKPACK),
								Messages.get(TitleScene.class, "daily_result_title", date),
								result));
			}
		};
		btnScoutDaily.icon(Icons.get(Icons.CALENDAR));
		add(btnScoutDaily);
		Dungeon.daily = Dungeon.dailyReplay = false;

		StyledButton btnCatalog = new StyledButton(GREY_TR, Messages.get(this, "item_catalog")){
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.scene().addToFront(new WndCatalog());
			}
		};
		btnCatalog.icon(new ItemSprite(ItemSpriteSheet.POTION_AZURE));
		add(btnCatalog);

		StyledButton btnSource = new StyledButton(GREY_TR, Messages.get(this, "source")) {
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.platform.openURI("https://github.com/Elektrochecker/shpd-toolkit");
			}
		};
		btnSource.icon(Icons.get(Icons.NEWS));
		add(btnSource);

		StyledButton btnChanges = new ChangesButton(GREY_TR, Messages.get(this, "changes"));
		btnChanges.icon(Icons.get(Icons.CHANGES));
		add(btnChanges);

		StyledButton btnSettings = new SettingsButton(GREY_TR, Messages.get(this, "settings"));
		add(btnSettings);

		StyledButton btnAbout = new StyledButton(GREY_TR, Messages.get(this, "about")){
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.switchScene( AboutScene.class );
			}
		};
		btnAbout.icon(Icons.get(Icons.INFO));
		add(btnAbout);

		// Primary tools first, secondary tools arranged in a consistent grid.
		final int gap = 3;
		final int rows = landscape() ? 3 : 5;
		float buttonAreaWidth = Math.min(w - 12, landscape() ? 240 : 170);
		float left = (w - buttonAreaWidth) / 2f;
		float half = (buttonAreaWidth - gap) / 2f;
		float buttonHeight = Math.min(24, (h - topRegion - 12 - rows * gap) / rows);
		float y = topRegion + gap;
		if (landscape()) {
			btnScout.setRect(left, y, half, buttonHeight);
			btnSeedfinder.setRect(left + half + gap, y, half, buttonHeight);
			y += buttonHeight + gap;
			float third = (buttonAreaWidth - gap * 2) / 3f;
			StyledButton[] secondary = {btnScoutDaily, btnCatalog, btnSettings, btnSource, btnChanges, btnAbout};
			for (int i = 0; i < secondary.length; i++) {
				secondary[i].setRect(left + (i % 3) * (third + gap),
						y + (i / 3) * (buttonHeight + gap), third, buttonHeight);
			}
		} else {
			btnScout.setRect(left, y, buttonAreaWidth, buttonHeight);
			y += buttonHeight + gap;
			btnSeedfinder.setRect(left, y, buttonAreaWidth, buttonHeight);
			y += buttonHeight + gap;
			StyledButton[] secondary = {btnScoutDaily, btnCatalog, btnSettings, btnChanges, btnSource, btnAbout};
			for (int i = 0; i < secondary.length; i++) {
				secondary[i].setRect(left + (i % 2) * (half + gap),
						y + (i / 2) * (buttonHeight + gap), half, buttonHeight);
			}
		}
		for (StyledButton btn : new StyledButton[]{btnScout, btnSeedfinder, btnScoutDaily, btnCatalog,
				btnSettings, btnChanges, btnSource, btnAbout}) align(btn);

		BitmapText version = new BitmapText( "v" + Game.version, pixelFont);
		version.measure();
		version.hardlight( 0xB6B7A7 );
		version.x = w - version.width() - 4;
		version.y = h - version.height() - 2;
		add( version );

		if (DeviceCompat.isDesktop()) {
			ExitButton btnExit = new ExitButton();
			btnExit.setPos( w - btnExit.width(), 0 );
			add( btnExit );
		}

		Badges.loadGlobal();
		if (Badges.isUnlocked(Badges.Badge.VICTORY) && !SPDSettings.victoryNagged()) {
			SPDSettings.victoryNagged(true);
			add(new WndVictoryCongrats());
		}

		fadeIn();
	}


	private static class ChangesButton extends StyledButton {

		public ChangesButton( Chrome.Type type, String label ){
			super(type, label);
			if (SPDSettings.updates()) Updates.checkForUpdate();
		}

		boolean updateShown = false;

		@Override
		public void update() {
			super.update();

			if (!updateShown && Updates.updateAvailable()){
				updateShown = true;
				text(Messages.get(TitleScene.class, "update"));
			}

			if (updateShown){
				textColor(ColorMath.interpolate( 0xFFFFFF, Window.SHPX_COLOR, 0.5f + (float)Math.sin(Game.timeTotal*5)/2f));
			}
		}

		@Override
		protected void onClick() {
			if (Updates.updateAvailable()){
				AvailableUpdateData update = Updates.updateData();

				ShatteredPixelDungeon.scene().addToFront( new WndOptions(
						Icons.get(Icons.CHANGES),
						update.versionName == null ? Messages.get(this,"title") : Messages.get(this,"versioned_title", update.versionName),
						update.desc == null ? Messages.get(this,"desc") : update.desc,
						Messages.get(this,"update"),
						Messages.get(this,"changes")
				) {
					@Override
					protected void onSelect(int index) {
						if (index == 0) {
							Updates.launchUpdate(Updates.updateData());
						} else if (index == 1){
							ChangesScene.changesSelected = 0;
							ShatteredPixelDungeon.switchNoFade( ChangesScene.class );
						}
					}
				});

			} else {
				ChangesScene.changesSelected = 0;
				ShatteredPixelDungeon.switchNoFade( ChangesScene.class );
			}
		}

	}

	private static class SettingsButton extends StyledButton {

		public SettingsButton( Chrome.Type type, String label ){
			super(type, label);
			if (Messages.lang().status() == Languages.Status.X_UNFINISH){
				icon(Icons.get(Icons.LANGS));
				icon.hardlight(1.5f, 0, 0);
			} else {
				icon(Icons.get(Icons.PREFS));
			}
		}

		@Override
		public void update() {
			super.update();

			if (Messages.lang().status() == Languages.Status.X_UNFINISH){
				textColor(ColorMath.interpolate( 0xFFFFFF, CharSprite.NEGATIVE, 0.5f + (float)Math.sin(Game.timeTotal*5)/2f));
			}
		}

		@Override
		protected void onClick() {
			if (Messages.lang().status() == Languages.Status.X_UNFINISH){
				WndSettings.last_index = 5;
			}
			ShatteredPixelDungeon.scene().add(new WndSettings());
		}
	}
}
