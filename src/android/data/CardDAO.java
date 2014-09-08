package com.tenforwardconsulting.cordova.bgloc.data;

public interface CardDAO {
	public void internetPendingCards();
	public int getCardId();
	public boolean persistCard(String table, Card card);
	//public void deleteCard(Card l);
}
