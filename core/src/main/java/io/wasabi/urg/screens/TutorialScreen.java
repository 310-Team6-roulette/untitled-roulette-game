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

/** Owns tutorial navigation, input, and rendering separately from the main menu. */
public final class TutorialScreen extends ScreenAdapter {
    private final Roulette game;
    private final Texture backgroundTexture;
    private final Texture patchTexture;
    private final TutorialRenderer renderer;
    private final TutorialPageNavigator pageNavigator = new TutorialPageNavigator();

    private final MainMenuButton backButton = new MainMenuButton(
        MainMenuButton.Action.BACK_TO_MENU,
        "BACK TO MENU",
        -542f,
        -371f,
        450f,
        72f
    );
    private final MainMenuButton previousButton = new MainMenuButton(
        MainMenuButton.Action.PREVIOUS_TUTORIAL_PAGE,
        "PREVIOUS",
        -40f,
        -371f,
        200f,
        72f
    );
    private final MainMenuButton nextButton = new MainMenuButton(
        MainMenuButton.Action.NEXT_TUTORIAL_PAGE,
        "NEXT",
        360f,
        -371f,
        200f,
        72f
    );
    private final TutorialInputHandler inputHandler;

    public TutorialScreen(Roulette game) {
        this.game = game;
        SpriteBatch spriteBatch = RendererManager.getInstance().getSpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("ui/MainMenu.png"));
        patchTexture = new Texture(Gdx.files.internal("ui/CorneredPatch.png"));
        NinePatch patch = new NinePatch(patchTexture, 10, 10, 10, 10);
        renderer = new TutorialRenderer(
            spriteBatch,
            backgroundTexture,
            patch,
            FontManager.getInstance().getFontByName("Terminus64PXBold"),
            FontManager.getInstance().getFontByName("Terminus32PX")
        );
        inputHandler = new TutorialInputHandler(
            game.getCamera(),
            backButton,
            previousButton,
            nextButton,
            this::handleButtonAction
        );
        updateNavigationButtons();
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

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.17f, 0.20f, 0.12f, 1f);
        renderer.drawPage(
            pageNavigator.getCurrentPage(),
            pageNavigator.getCurrentPageNumber(),
            pageNavigator.getPageCount(),
            backButton,
            inputHandler.getBackButtonState(),
            previousButton,
            inputHandler.getPreviousButtonState(),
            nextButton,
            inputHandler.getNextButtonState()
        );
    }

    private void handleButtonAction(MainMenuButton.Action action) {
        switch (action) {
            case BACK_TO_MENU:
                game.setScreen(game.getMainMenuScreen());
                break;
            case PREVIOUS_TUTORIAL_PAGE:
                pageNavigator.goToPreviousPage();
                updateNavigationButtons();
                break;
            case NEXT_TUTORIAL_PAGE:
                pageNavigator.goToNextPage();
                updateNavigationButtons();
                break;
            default:
                throw new IllegalStateException("Unsupported tutorial action: " + action);
        }
    }

    private void updateNavigationButtons() {
        inputHandler.setNavigationEnabled(
            pageNavigator.canGoToPreviousPage(),
            pageNavigator.canGoToNextPage()
        );
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        patchTexture.dispose();
    }
}
