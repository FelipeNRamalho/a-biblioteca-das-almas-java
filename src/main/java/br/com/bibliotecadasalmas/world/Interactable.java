package br.com.bibliotecadasalmas.world;

import br.com.bibliotecadasalmas.core.GameController;

/** O mesmo contrato abre uma investigação ou atravessa uma passagem. */
public sealed interface Interactable {
    void interact(GameController controller);

    record Investigation(String sceneId) implements Interactable {
        @Override public void interact(GameController controller) { controller.investigate(sceneId); }
    }

    record Passage(String locationId, String gateScene) implements Interactable {
        @Override public void interact(GameController controller) { controller.travel(locationId, gateScene); }
    }
}
