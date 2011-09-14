package me.main__.WolfPay;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name="wolfpay_accounts")
public class Account {
//	@Id
//	private int id;
	
	@NotNull
	private String playerName;
	
	@NotNull
	private int boughtwolves;

//	public int getId() {
//		return id;
//	}
//
//	public void setId(int id) {
//		this.id = id;
//	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getBoughtwolves() {
		return boughtwolves;
	}

	public void setBoughtwolves(int boughtwolves) {
		this.boughtwolves = boughtwolves;
	}
	
}
