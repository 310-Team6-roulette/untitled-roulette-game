package io.wasabi.urg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import io.wasabi.urg.Roulette;
import io.wasabi.urg.managers.FontManager;
import io.wasabi.urg.managers.RendererManager;

public final class MainMenuScreen extends ScreenAdapter {
    private static final float TITLE_SCALE = 1.25f;
    private static final float BUTTON_WIDTH = 390f;
    private static final float BUTTON_HEIGHT = 72f;

    // These anchors are in the shared 1600 x 900 world and align with MainMenu.png.
    private static final float CONTENT_CENTER_X = -255f;
    private static final float WOOD_CENTER_X = 575f;

    private final Roulette game;
    private final Texture backgroundTexture;
    private final Texture patchTexture;
    private final MainMenuButton[] menuButtons = {
        new MainMenuButton(MainMenuButton.Action.PLAY, "PLAY", -450f, 100f, BUTTON_WIDTH, BUTTON_HEIGHT),
        new MainMenuButton(MainMenuButton.Action.TUTORIAL, "TUTORIAL", -450f, 0f, BUTTON_WIDTH, BUTTON_HEIGHT),
        new MainMenuButton(MainMenuButton.Action.SETTINGS, "SETTINGS", -450f, -100f, BUTTON_WIDTH, BUTTON_HEIGHT),
        new MainMenuButton(MainMenuButton.Action.COLLECTIONS, "COLLECTIONS", -450f, -200f, BUTTON_WIDTH, BUTTON_HEIGHT),
        new MainMenuButton(MainMenuButton.Action.QUIT, "QUIT", -450f, -300f, BUTTON_WIDTH, BUTTON_HEIGHT)
    };

    private final MainMenuRenderer renderer;
    private final MainMenuInputHandler inputHandler;

    /** Loads resources used throughout the menu; shared renderers are owned by RendererManager. */
    public MainMenuScreen(final Roulette game) {
        this.game = game;

        RendererManager rendererManager = RendererManager.getInstance();
        SpriteBatch spriteBatch = rendererManager.getSpriteBatch();

        this.backgroundTexture = new Texture(Gdx.files.internal("ui/MainMenu.png"));
        this.patchTexture = new Texture(Gdx.files.internal("ui/CorneredPatch.png"));
        NinePatch patch = new NinePatch(patchTexture, 10, 10, 10, 10);

        this.renderer = new MainMenuRenderer(
            spriteBatch,
            backgroundTexture,
            patch,
            FontManager.getInstance().getFontByName("Terminus64PXBold"),
            FontManager.getInstance().getFontByName("Terminus32PX")
        );

        this.inputHandler = new MainMenuInputHandler(game, menuButtons, this::handleMenuAction);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputHandler);
    }

    @Override
    public void hide() {
        if (Gdx.input.getInputProcessor() == inputHandler) {
            Gdx.input.setInputProcessor(null);
        }
    }

    /** Renders the menu in layer order so the background stays behind all UI. */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.17f, 0.20f, 0.12f, 1f);

        renderer.drawBackground(game.getWorldWidth(), game.getWorldHeight());
        renderer.drawTitle(CONTENT_CENTER_X, TITLE_SCALE);
        renderer.drawMenuButtons(menuButtons, inputHandler);
        renderer.drawHighScore(
            WOOD_CENTER_X,
            235f,
            game.getHighScoreManager().getHighScoreAct(),
            game.getHighScoreManager().getHighScoreRound()
        );
    }

    /**
     * Centralises the menu action routing so each button is mapped to a single action type
     * rather than depending on display text. This is easier to maintain as new screens are added.
     */
    private void handleMenuAction(MainMenuButton.Action action) {
        switch (action) {
            case PLAY:
                handlePlayAction();
                break;
            case TUTORIAL:
                handleTutorialAction();
                break;
            case SETTINGS:
                handleSettingsAction();
                break;
            case COLLECTIONS:
                handleCollectionsAction();
                break;
            case QUIT:
                handleQuitAction();
                break;
            default:
                throw new IllegalStateException("Unsupported menu action: " + action);
        }
    }

    private void handlePlayAction() {
        int startingChips = 100;
        game.getRunState().reset(startingChips);
        game.setScreen(game.getGameScreen());
    }

    private void handleTutorialAction() {
        // Placeholder until the tutorial screen is implemented.
    }

    private void handleSettingsAction() {
        // Placeholder until the settings screen is implemented.
    }

    private void handleCollectionsAction() {
        // Placeholder until the collection screen is implemented.
    }

    private void handleQuitAction() {
        Gdx.app.exit();
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        patchTexture.dispose();
    }
}