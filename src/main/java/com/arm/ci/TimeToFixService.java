package com.arm.ci;

import hudson.AbortException;
import hudson.Util;
import hudson.XmlFile;
import hudson.model.TopLevelItem;
import hudson.model.Job;
import hudson.model.Run;
import hudson.tasks.junit.PackageResult;
import hudson.tasks.junit.SuiteResult;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.CaseResult;
import hudson.tasks.junit.CaseResult.Status;
import hudson.util.HeapSpaceStringConverter;
import hudson.util.RunList;
import hudson.util.XStream2;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

import com.thoughtworks.xstream.XStream;

import jenkins.model.Jenkins;

public class TimeToFixService {

	public TimeToFixService() {
		// TODO Auto-generated constructor stub
	}
	
	public String create() {
		String rows = "";
		List<TopLevelItem> jobs = Jenkins.getInstance().getItems();
		for(TopLevelItem job : jobs){
			if(job instanceof Job){
				RunList runList = ((Job)job).getBuilds();
				Iterator it = runList.iterator();
				int i = 0;
				while(it.hasNext()){
					try {
						rows = rows + createRow(job.getName(), (Run)it.next()) + ",\n";
					} catch (AbortException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		if(rows.length() > 10){
			rows = rows.substring(0, rows.length() - 2);
		}
		
		return "[\n"+rows+"\n]";
	}
	
	private String createRow(String name, Run run) throws AbortException {
		TestResult result = getTestResult(run.getRootDir());
		
		if(result == null){
			return "[]";
		}

		// Average failures
		int averageFail = 0;
		
		// Failure runtime 
		int durationFailure = 0;
		
		// Age
		int maxAge = 0;
		int totalAge = 0;
		int averageAge = 0;
		
		// Case
		if(result.getFailedTests() != null){
			if(result.getTotalCount() > 0){
				averageFail = result.getFailCount() * 100 / result.getTotalCount();
			}
			
			for(SuiteResult suite : result.getSuites()){
				for(CaseResult c : suite.getCases()){
					if(c.getStatus() == Status.FAILED){
						int age = c.getFailedSince();
						if(age > maxAge){
							maxAge = age;
						}
						totalAge += age; 
						
						durationFailure += c.getDuration();
					}
				}
			}
			
			if(result.getFailCount() > 0){
				averageAge = totalAge * 100 / result.getFailCount();
			}
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy,MM,dd");
		
		return "['"+name
				+"',new Date("+sdf.format(run.getTime())+"),"
				+durationFailure+","+result.getDuration()+","+
				+result.getFailCount()+","+result.getTotalCount()+","+
				+ averageFail + ","
				+ averageAge + ","
				+ maxAge +","
				+ totalAge +"]";
	}

	private TestResult getTestResult(File ws) throws AbortException{
	     File f = new File(ws.getAbsolutePath() + File.separator + "junitResult.xml");
	     
	     TestResult result = new TestResult();
	     if(f.exists()){
	         try {
	        	 result = (TestResult)(new XmlFile(XSTREAM,f)).read();
	         } catch (IOException e) {
	             log("Failed to load ");
	         }
	         result.tally();
	     }else{
	    	 log("file not exists! : "+f.getAbsolutePath());
	     }
	     
	     return result;
	}

	private void log(String message){
		Logger.getLogger("TimeToFix").log(Level.WARNING, message);
	}
	
	 private static final XStream XSTREAM = new XStream2();

    static {
        XSTREAM.alias("result",TestResult.class);
        XSTREAM.alias("suite",SuiteResult.class);
        XSTREAM.alias("case",CaseResult.class);
        XSTREAM.registerConverter(new HeapSpaceStringConverter(),100);
    }
}
