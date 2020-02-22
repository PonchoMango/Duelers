package server.gameCenter.models.game;

import server.dataCenter.models.card.CardType;
import server.exceptions.LogicException;
import server.gameCenter.models.game.availableActions.Attack;
import server.gameCenter.models.game.availableActions.AvailableActions;
import server.gameCenter.models.game.availableActions.Insert;
import server.gameCenter.models.game.availableActions.Move;
import server.gameCenter.models.map.Cell;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class AI {

    private Player player;
    private Game game;

    private String username = null;
    private final int delay = 800; // millisecs

    public AI (String username, Game game, Player player){
        this.game = game;
        this.player = player;
        this.username = username;
    }

    public void playCurrentTurnAtRandom() throws LogicException {
        try {
            AvailableActions actions = new AvailableActions();
            actions.calculateAvailableActions(game);

            moveUnits(actions);
            actions.calculateAvailableActions(game);
            makeAttacks(actions);
            actions.calculateAvailableActions(game);
            playCardFromHand(actions);
        }
        catch (LogicException | InterruptedException e) {
            System.out.println(e);
        }
        finally {
            game.changeTurn(username);
        }

    }

    private void moveUnits(AvailableActions actions) throws LogicException, InterruptedException {
        List<Move> moves = actions.getMoves();
        if (moves.size() < 1) return;

        List<Move> attemptToMoveNTroops = pickNRandomElements(moves, new Random().nextInt(moves.size()+1));
        for (Move troop: attemptToMoveNTroops){
            if (!troop.getTroop().canMove()) continue;

            Cell newPosition = pickNRandomElements(troop.getTargets(), 1).get(0);
            game.moveTroop(username, troop.getTroop().getCard().getCardId(), newPosition);
            actions.calculateAvailableActions(game);
            Thread.sleep(delay);
        }
    }

    private void makeAttacks(AvailableActions actions) throws LogicException, InterruptedException {
        List<Attack> attacks = actions.getAttacks();
        if (attacks.size() < 1) return;

        List<Attack> attemptToAttackNTimes = pickNRandomElements(attacks, new Random().nextInt(attacks.size()+1));
        for (Attack atk: attemptToAttackNTimes){
            if (!atk.getAttackerTroop().canAttack()) continue;

            Troop defender = pickNRandomElements(atk.getDefenders(), 1).get(0);
            if (defender == null) continue;
            game.attack(username, atk.getAttackerTroop().getCard().getCardId(), defender.getCard().getCardId());
            actions.calculateAvailableActions(game);
            Thread.sleep(delay);
        }
    }

    private void playCardFromHand(AvailableActions actions) throws LogicException, InterruptedException {
        List<Insert> hand = actions.getHandInserts();

        if (hand.size() < 1) return;

        String handMsg = username + "'s hand: " + actions.printHand();
        System.out.println(handMsg);

        List<Insert> attemptToPlayNCards = pickNRandomElements(hand, new Random().nextInt(hand.size()+1));
        for(Insert insert: attemptToPlayNCards){

            if (insert.getCard().getType() == CardType.MINION || insert.getCard().getType() == CardType.HERO){

                if (player.getCurrentMP() < insert.getCard().getManaCost()) continue ;
                deployUnitToBattleground(insert);
            }
            else if (insert.getCard().getType() == CardType.SPELL){
                continue; // ToDo implement ability to play spells.
            }
            actions.calculateAvailableActions(game);
        }
    }

    private void deployUnitToBattleground (Insert insert) throws LogicException, InterruptedException {
        // Skew probability distribution towards favoring squares closer to Hero position.
        int[] offsets = new int[]{-3, -2, -2, -1, -1, -1, 0, 0, 0, 1, 1, 1, 2, 2, 3};

        Cell HeroPosition = player.getHero().getCell();

        // Attempt (max n tries) to place minion on a random square.
        for (int attempts = 0; attempts < 20; attempts++) {

            int x = offsets[new Random().nextInt(offsets.length)];
            int y = offsets[new Random().nextInt(offsets.length)];

            // Get a random square, force it to be within bounds.
            int x2 = Math.max(0, Math.min(x + HeroPosition.getRow(), game.getGameMap().getRowNumber() - 1));
            int y2 = Math.max(0, Math.min(y + HeroPosition.getColumn(), game.getGameMap().getColumnNumber() - 1));

            Cell c = new Cell(x2, y2);

            game.insert(username, insert.getCard().getCardId(), c);
            Thread.sleep(delay);
            return;
        }
    }


    public String getUsername(){
        return username;
    }

    // ToDo Move these functions to some shared "helper functions" Library.
    private static <E> List<E> pickNRandomElements(List<E> lst, int n) {
        List<E> copy = new LinkedList<E>(lst);
        Collections.shuffle(copy);
        return copy.subList(0, n);
    }
}
