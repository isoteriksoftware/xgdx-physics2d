package io.github.isoteriktech.xgdx.physics2d.test;

import com.badlogic.gdx.graphics.Color;
import io.github.isoteriktech.xgdx.GameObject;
import io.github.isoteriktech.xgdx.Scene;
import io.github.isoteriktech.xgdx.physics2d.PhysicsManager2d;
import io.github.isoteriktech.xgdx.physics2d.PhysicsMaterial2d;
import io.github.isoteriktech.xgdx.physics2d.RigidBody2d;
import io.github.isoteriktech.xgdx.physics2d.colliders.BoxCollider;
import io.github.isoteriktech.xgdx.physics2d.colliders.CircleCollider;
import io.github.isoteriktech.xgdx.physics2d.utils.Box2dUtil;

public class MultipleColliderTest extends Scene {
    public MultipleColliderTest() {
        setBackgroundColor(Color.BLACK);

        PhysicsManager2d physicsManager2d = PhysicsManager2d.setup(this);
        Box2dUtil.createBoundaryBox(physicsManager2d.getPhysicsWorld(), gameWorldUnits.getWorldWidth(),
                gameWorldUnits.getWorldHeight(), .2f);

        physicsManager2d.setRenderPhysicsDebugLines(true);

        GameObject car = GameObject.newInstance();
        float width = 3, height = 1;
        car.transform.setPosition(3, gameWorldUnits.getWorldHeight() - height);
        car.transform.setSize(width, height);

        PhysicsMaterial2d tireMaterial = new PhysicsMaterial2d(.1f, .3f, .3f);

        car.addComponent(new RigidBody2d(RigidBody2d.DynamicBody, physicsManager2d));
        car.addComponent(new BoxCollider());
        car.addComponent(new CircleCollider(.5f, -width/2, -height/2).setMaterial(tireMaterial));
        car.addComponent(new CircleCollider(.5f, width/2, -height/2).setMaterial(tireMaterial));

        addGameObject(car);
    }
}
