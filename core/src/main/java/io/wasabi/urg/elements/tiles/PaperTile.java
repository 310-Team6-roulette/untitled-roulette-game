package io.wasabi.urg.elements.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import io.wasabi.urg.Roulette;
import io.wasabi.urg.elements.game.Tile;

public class PaperTile extends TileType {
    private static final Texture PAPER_TEXTURE = new Texture(Gdx.files.internal("tiles/PaperTile.png"));

    private final Tile tile;
    private final Mesh mesh;

    public PaperTile(TileType originalType, Tile tile) {
        this.tile = tile;
        setColour(originalType.getColour());
        setNumber(originalType.getNumber());
        setBetMultiplier(originalType.getBetMultiplier());
        setPostMultiplierBonus(100f);
        tooltip.setDescriptionVisible(true);
        tooltip.setDescription("Gain an extra 100 chips when scored. This tile breaks after scoring");
        tooltip.addType("PAPER", com.badlogic.gdx.graphics.Color.BLACK, com.badlogic.gdx.graphics.Color.WHITE);

        mesh = new Mesh(false, 2000, 2000,
                new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
                new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
    }

    @Override
    protected void updateTooltipTitle() {
        tooltip.setTitle("PAPER");
    }

    @Override
    public void setRegion(float[] vertices, short[] indices) {
        super.setRegion(vertices, indices);
        mesh.setVertices(textureWrapVertices(vertices, new TextureRegion(PAPER_TEXTURE)));
        mesh.setIndices(indices);
    }

    @Override
    public void drawTextures() {
        // The paper texture supplies the complete tile appearance.
    }

    @Override
    public void drawOverlay() {
        PAPER_TEXTURE.bind();
        mesh.render(POLY_BATCH.getShader(), GL20.GL_TRIANGLES);
    }

    @Override
    public Texture getTexture() {
        return PAPER_TEXTURE;
    }

    @Override
    public void onLanded() {
        Roulette.getInstance().getRunState().removeTile(tile);
    }

    @Override
    public void dispose() {
        mesh.dispose();
        super.dispose();
    }
}