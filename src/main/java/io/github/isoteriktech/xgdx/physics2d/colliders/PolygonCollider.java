package io.github.isoteriktech.xgdx.physics2d.colliders;

import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import io.github.isoteriktech.xgdx.physics2d.PhysicsMaterial2d;

/**
 * A collider that generates a convexed polygon collision boundary. Useful for oddly shaped game objects.
 *
 * @author isoteriksoftware
 */
public class PolygonCollider extends Collider {
    private float[] vertices;

    /**
     * Creates a new instance given the vertices of the polygon. It is assumed the vertices are in x,y order and define a convex polygon. It is
     * assumed that the exterior is the right of each edge.
     * @param vertices the vertices of the polygon
     * @throws IllegalArgumentException if the vertices array is null or empty
     */
    public PolygonCollider(float[] vertices) throws IllegalArgumentException {
        if (vertices == null || vertices.length == 0)
            throw new IllegalArgumentException("Vertices are required!");

        this.vertices = vertices;
    }

    @Override
    public PolygonCollider setMaterial(PhysicsMaterial2d material) {
        super.setMaterial(material);
        return this;
    }

    @Override
    public FixtureDef __getFixtureDef() {
        shape = new PolygonShape();
        ((PolygonShape)shape).set(vertices);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;

        return fdef;
    }
}