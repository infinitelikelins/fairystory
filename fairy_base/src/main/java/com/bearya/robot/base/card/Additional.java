package com.bearya.robot.base.card;

import com.bearya.robot.base.protocol.EquipmentCard;

import java.util.ArrayList;
import java.util.List;

public class Additional {
    private List<Card> cards = new ArrayList<>();

    public Additional(EquipmentCard card) {
        cards.add(new PropCard(card.getValue()));
    }

    public Additional(PropCard propCard) {
        cards.add(propCard);
    }

    public boolean match(Additional value) {
        return value != null && cards != null && cards.size() > 0 && cards.get(0).getOid() == value.getCards().get(0).getOid();
    }

    public List<Card> getCards() {
        return cards;
    }

}