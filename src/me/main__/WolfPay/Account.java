package me.main__.WolfPay;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name="wolfpay_accounts")
public class Account {

	@Id
	@NotNull
	private String playerName;
	
	@NotNull
	private int boughtwolves;

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
