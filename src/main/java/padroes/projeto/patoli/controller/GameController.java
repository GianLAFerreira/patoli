package padroes.projeto.patoli.controller;

import padroes.projeto.patoli.model.Game;
import padroes.projeto.patoli.model.Piece;
import padroes.projeto.patoli.view.MainFrame;

import javax.swing.*;

public class GameController {
    private final Game game;
    private MainFrame view;

    public GameController(Game game) {
        this.game = game;
    }

    public void setView(MainFrame view) {
        this.view = view;
        updateView();
    }

    public void onRoll() {
        if (game.getLastRoll() != -1) {
            JOptionPane.showMessageDialog(view, "Você já rolou neste turno. Mova uma peça ou passe se não houver jogada.");
            return;
        }
        int val = game.roll();
        updateView();
        if (val == 0) {
            JOptionPane.showMessageDialog(view, "Você tirou 0, Sem movimento.");
            onPassIfRequired();
        }
    }

    public void onEnterNewPiece() {
        if (game.enterNewPiece()) {
            endMovePhase();
        } else {
            JOptionPane.showMessageDialog(view, "Não é possível inserir nova peça agora.");
        }
        updateView();
    }

    public void onPieceClicked(Piece piece) {
        if (game.getLastRoll() <= 0) {
            JOptionPane.showMessageDialog(view, "Role as moedas antes de mover.");
            return;
        }
        if (game.movePiece(piece)) {
            endMovePhase();
            updateView();
        } else {
            JOptionPane.showMessageDialog(view, "Movimento inválido para esta peça.");
        }
    }

    public void onPassIfRequired() {
        if (game.mustPass()) {
            game.nextTurnIfNeeded();
            checkGameOverOrContinue();
            updateView();
        } else {
            JOptionPane.showMessageDialog(view, "Você ainda tem jogadas válidas. Não pode passar.");
        }
    }

    private void endMovePhase() {
        // Após executar a ação (entrar peça ou mover), finaliza turno conforme regras
        game.nextTurnIfNeeded();
        checkGameOverOrContinue();
    }

    private void checkGameOverOrContinue() {
        if (game.isGameOver()) {
            JOptionPane.showMessageDialog(view, game.gameOverMessage());
        }
    }

    private void updateView() {
        if (view != null) view.refresh();
    }

    public Game getGame() {
        return game;
    }
}