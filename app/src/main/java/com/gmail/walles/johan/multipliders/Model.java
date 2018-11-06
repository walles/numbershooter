package com.gmail.walles.johan.multipliders;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinate system is Y=[0%-100%] where 0% is on top and 100% is on bottom, X coordinates are as
 * wide as Y coordinates are high but go from left to right with 0% being in the middle of the
 * screen.
 */
public class Model {
    private static final long NEVER_UPDATED = 0L;

    /**
     * Accept time deltas at least this big. If we don't limit them, the physics would go nuts after
     * pausing and resuming the app.
     */
    private static final long MAX_STEP_MS = 100L;

    /**
     * Add new objects at most this close to each other.
     */
    private static final int FALLING_OBJECTS_SPACING_PERCENT = 17;

    private List<PhysicalObject> objects = new ArrayList<>();
    private long lastUpdatedToMs = NEVER_UPDATED;

    /**
     * Update model to the given timestamp.
     */
    public void updateTo(long timestampMillis) {
        if (lastUpdatedToMs == NEVER_UPDATED) {
            lastUpdatedToMs = timestampMillis;
            return;
        }

        long deltaMs = timestampMillis - lastUpdatedToMs;
        if (deltaMs < 0) {
            // Clock went backwards, keep up
            lastUpdatedToMs = timestampMillis;
            return;
        }

        if (deltaMs > MAX_STEP_MS) {
            deltaMs = MAX_STEP_MS;
        }

        addMoreChallenges();
        for (PhysicalObject object: objects) {
            object.stepMs(deltaMs);
        }
        lastUpdatedToMs = timestampMillis;
    }

    private void addMoreChallenges() {
        if (shouldAddChallenge()) {
            objects.add(new PhysicalObject());
        }
    }

    private boolean shouldAddChallenge() {
        if (objects.isEmpty()) {
            // No objects,
            return true;
        }

        // Find highest y coordinate
        for (PhysicalObject object: objects) {
            if (object.getY() <= FALLING_OBJECTS_SPACING_PERCENT) {
                // Something's in the way
                return false;
            }
        }

        return true;
    }

    /**
     * Render the model onto the given canvas.
     */
    public void drawOn(Canvas canvas) {
        for (PhysicalObject object: objects) {
            object.drawOn(canvas);
        }
    }
}