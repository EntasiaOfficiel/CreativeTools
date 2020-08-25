package fr.entasia.creativetools.utils;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
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
		for(String s : plots)scoreboard.resetScores(s);

		if(plot==null) {
			plots[0] = "§bPlot : §9Aucun";
			objective.getScore(plots[0]).setScore(47);
		}else{
			if(plot.hasOwner()){
				plots[0] = "§7Plot : ";
				objective.getScore(plots[0]).setScore(47);
				if(plot.hasOwner()){
					PlotSquared.get().getImpromptuUUIDPipeline().getNames(plot.getOwners()).thenAcceptAsync(map -> {
						plots[1] = "§7Chef : §b"+map.get(0).getUsername();
						objective.getScore(plots[1]).setScore(46);
					});
				} else{
					plots[1] = "§7Chef : §bInconnu";
					objective.getScore(plots[1]).setScore(46);
				}

				double av = plot.getAverageRating();
				plots[2] = "§7Note : §b";
				if(Double.isNaN(av))plots[2]+="Non noté";
				else{
					double a = Math.round(av*100)/100d;
					String b = Double.toString(a);
					if(a%1==0)b = b.substring(b.length(), 2);
					plots[2]+=b+"§7/§b10";
				}
				objective.getScore(plots[2]).setScore(45);

				plots[3] = "§7Role : §b";
				if(plot.isOwner(cp.p.getUniqueId()))plots[3]+="§cChef";
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
