package fr.entasia.creativetools.utils;

import com.intellectualcrafters.plot.object.Plot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class SBManager {


	public CreaPlayer cp;
	public Scoreboard scoreboard;
	public Objective objective;

	public String money = "";
	public String[] plots = new String[]{"", "", "", ""};

	public SBManager(CreaPlayer cp){
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		register();
		this.cp = cp;
		refresh();
	}

	public void register(){
		objective = scoreboard.registerNewObjective("creatif", "dummy");
	}

	public void softSet(){
		if(cp.p.getScoreboard()!=scoreboard)refresh();
		System.out.println("fin du set");
	}

	public void refresh(){
		cp.p.setScoreboard(scoreboard);
		clear();
		objective.setDisplayName("§7Créatif");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.getScore("§b§m-----------").setScore(50);
		refreshMoney();
		objective.getScore(" ").setScore(48);
		objective.getScore("§b§m----------- ").setScore(40);
		objective.getScore("§bplay.enta§7sia.fr").setScore(10);
	}

	public void clear(){
		scoreboard.getEntries().forEach(a -> scoreboard.resetScores(a));
	}

	public void refreshMoney(){
		scoreboard.resetScores(money);
		money = "§7Monnaie : §b"+cp.money;
		objective.getScore(money).setScore(49);
	}

	public void setPlot(Plot plot){
		System.out.println("setting sb for plot");
		for(String s : plots)scoreboard.resetScores(s);

		if(plot==null) {
			plots[0] = "§bPlot : §9Aucun";
			objective.getScore(plots[0]).setScore(47);
		}else{
			if(plot.hasOwner()){
				plots[0] = "§7Plot : ";
				objective.getScore(plots[0]).setScore(47);
				Player p = Bukkit.getPlayer(plot.getOwners().iterator().next());
				if(p==null) plots[1] = "§7Owner : §bInconnu";
				else plots[1] = "§7Owner : §b"+p.getDisplayName();
				objective.getScore(plots[1]).setScore(46);

				double av = plot.getAverageRating();
				plots[2] = "§7Note : §b ";
				if(Double.isNaN(av))plots[2]+="Non noté";
				else{
					double a = Math.round(av*100)/100d;
					String b = Double.toString(a);
					if(a%1==0)b = b.substring(b.length(), 2);
					plots[2]+=b+"§7/§b10";
				}
				objective.getScore(plots[2]).setScore(45);

				plots[3] = "§7Role : §b ";
				if(plot.isOwner(cp.p.getUniqueId()))plots[3]+="§cOwner";
				else if(plot.getTrusted().contains(cp.p.getUniqueId()))plots[3]+="§aMembre";
				else if(plot.getTrusted().contains(cp.p.getUniqueId()))plots[3]+="§eCOOP";
				else if(plot.isDenied(cp.p.getUniqueId()))plots[3]+="§4Banni";
				else plots[3]+="§bNeutre";
				objective.getScore(plots[3]).setScore(44);
			}else{
				plots[0] = "§7Plot : §bVide";
				objective.getScore(plots[0]).setScore(47);
			}

		}
	}

}
