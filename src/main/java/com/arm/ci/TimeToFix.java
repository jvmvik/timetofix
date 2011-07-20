package com.arm.ci;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jenkins.model.Jenkins;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jfree.util.Log;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.Item;
import hudson.model.RootAction;
import hudson.model.TopLevelItem;
import hudson.model.Run;

/**
 * @author victor Benarbia
 */
@Extension
public class TimeToFix implements RootAction {

	public String getIconFileName() {
		return "gear.gif";
	}

	public String getDisplayName() {
		return "Time to fix";
	}

	public String getUrlName() {
		return "timetofix";
	}

	/*
	 * [
	      ['Apples',new Date (1988,0,1),1000,3000,10,100,2.3],
	      ['Oranges',new Date (1988,0,1),1150,2000,1,1000,2.0],
	      ['Bananas',new Date (1988,0,1),300,2500,1,300,1.2],
	      ['Apples',new Date (1989,6,1),1200,4000,99,100,2.5],
	      ['Oranges',new Date (1989,6,1),750,1500,1,1040,4.3],
	      ['Bananas',new Date (1989,6,1),788,6170,0,300,3.3]
	      ]
	 */
	public String getAll(){
		TimeToFixService service = new TimeToFixService();
		String rows = service.create();
		return rows;
	}
	

/*
	private Map<String, List<File>> getJUnitLocations() {
		String junitReport = "junitResult.xml";
		Map<String, List<File>>  array = new HashMap<String, List<File>>();
		
		for (Job job : getJobs()) {
			String projectName = job.getName();
			
			List<File> junitReports = new ArrayList<File>();
			log("Scan " + projectName);
			List<Run> builds = job.getBuilds();
			for(Run run : builds){
				//Date date = run.getTime();
				File f = new File(run.getRootDir() + File.separator + junitReport);
				if(f.exists()){
					junitReports.add(f);
				}else{
					log("File not found  : " + f);
				}
			}
			
			log(projectName+ " has " + junitReports.size() + "report");
			if(junitReports.size() > 0){
				array.put(projectName, junitReports);
			}
		}
		return array;
	}

	private void log(String message){
		Logger.getLogger(this.getClass().getName()).log(Level.WARNING, message);
	}
	
	private synchronized List<Job> getJobs() {
	    List<Job> jobs = new ArrayList<Job>();
	    
	    for (Item item : Jenkins.getInstance().getAllItems()) {
	      if (item instanceof Job) {
	    	  jobs.add((Job) item);
	      }
	    }

	    return jobs;
	  }*/
}
