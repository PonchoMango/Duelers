package shared.models.card;

import shared.models.card.spell.Spell;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

public class Card implements ICard {
    private final transient PropertyChangeSupport support = new PropertyChangeSupport(this);
    private final String name;
    private final String description;
    private String cardId;
    private final String spriteName;
    private final CardType type;
    private final ArrayList<Spell> spells;
    private final int defaultAp;
    private final int defaultHp;
    private final int manaCost;
    private final int price;
    private final AttackType attackType;
    private final int range;
    private int remainingNumber = 20;

    // This is only used in tests right now. All other cards are loaded from json with gson.
    public Card(String name,
                String cardId,
                String description,
                String spriteName,
                CardType type,
                ArrayList<Spell> spells,
                int defaultAp,
                int defaultHp,
                int manaCost,
                int price,
                AttackType attackType,
                int range) {
        this.name = name;
        this.cardId = cardId;
        this.description = description;
        this.spriteName = spriteName;
        this.type = type;
        this.spells = spells;
        this.defaultAp = defaultAp;
        this.defaultHp = defaultHp;
        this.manaCost = manaCost;
        this.price = price;
        this.attackType = attackType;
        this.range = range;
    }

    public Card(Card referenceCard, String username, int number) {
        this(referenceCard);
        this.cardId = (username + "_" + referenceCard.name + "_" + number).replaceAll(" ", "");
    }

    //dangerous
    public Card(Card referenceCard) {
        this.name = referenceCard.name;
        this.description = referenceCard.description;
        this.cardId = referenceCard.cardId;
        this.spriteName = referenceCard.spriteName;
        this.type = referenceCard.type;
        this.spells = new ArrayList<>();
        if (referenceCard.spells != null) {
            for (Spell spell : referenceCard.spells) {
                this.spells.add(new Spell(spell));
            }
        }
        this.defaultAp = referenceCard.defaultAp;
        this.defaultHp = referenceCard.defaultHp;
        this.manaCost = referenceCard.manaCost;
        this.price = referenceCard.price;
        this.attackType = referenceCard.attackType;
        this.range = referenceCard.range;
        this.remainingNumber = referenceCard.remainingNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (!this.getClass().getName().equals(obj.getClass().getName())) return false;
        Card card = (Card) obj;
        return this.cardId.equalsIgnoreCase(card.cardId);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getCardId() {
        return this.cardId;
    }

    public void setCardId(String cardId) {//TODO:Should be removed!
        this.cardId = cardId;
    }

    public String getSpriteName() {
        return spriteName;
    }

    @Override
    public CardType getType() {
        return this.type;
    }

    public ArrayList<Spell> getSpells() {
        return this.spells;
    }

    @Override
    public int getDefaultAp() {
        return this.defaultAp;
    }

    @Override
    public int getDefaultHp() {
        return this.defaultHp;
    }

    public int getManaCost() {
        return this.manaCost;
    }

    public AttackType getAttackType() {
        return this.attackType;
    }

    public int getRange() {
        return this.range;
    }

    @Override
    public int getPrice() {
        return price;
    }

    public boolean nameContains(String cardName) {
        return name.toLowerCase().contains(cardName.toLowerCase());
    }

    public boolean isSameAs(String cardName) {
        return name.equalsIgnoreCase(cardName);
    }

    public int getRemainingNumber() {
        return remainingNumber;
    }

    public void setRemainingNumber(int remainingNumber) {
        int old = this.remainingNumber;
        this.remainingNumber = remainingNumber;
        support.firePropertyChange("new_value", old, remainingNumber);
    }

    public void addSpell(Spell spell) {
        spells.add(spell);
    }

}