package com.tenforwardconsulting.cordova.bgloc.data;

public interface CardDAO {
	public Card[] geoPendingCards();
	public Card getCardById(String tableName, int cardId);
	public void geoCards();
	public int getCardId();
	public boolean persistCard(String table, Card card);
	public void deleteCard(String tableName, Card card);
	public void closeDB();
}
