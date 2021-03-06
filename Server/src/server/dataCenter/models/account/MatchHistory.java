package server.dataCenter.models.account;

import server.gameCenter.models.game.Player;

import java.util.Date;

public class MatchHistory {
    private String oppName;
    private boolean amIWinner;
    private Date date;

    public MatchHistory(Player player, boolean amIWinner) {
        if (player.getUserName().equals("AI")) {
            this.oppName = player.getDeck().getDeckName();
        } else {
            this.oppName = player.getUserName();
        }
        this.amIWinner = amIWinner;
        this.date = new Date(System.currentTimeMillis());
    }

    public String getOppName() {
        return this.oppName;
    }

    public String getDate() {
        return date.toLocaleString();
    }

    public boolean isAmIWinner() {
        return amIWinner;
    }
}