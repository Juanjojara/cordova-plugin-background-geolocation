package com.tenforwardconsulting.cordova.bgloc.data;

public interface CardDAO {
	public Card[] getInternetPendingCards();
	public boolean persistCard(Card l);
	public void deleteCard(Card l);
}
