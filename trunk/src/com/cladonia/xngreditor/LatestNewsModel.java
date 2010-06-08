/*
 * $Id: LatestNewsModel.java,v 1.0 5 Jun 2007 09:54:16 Administrator Exp $
 * 
 * Copyright (C) 2005, Cladonia Ltd. All rights reserved.
 * 
 * This software is the proprietary information of Cladonia Ltd. Use is subject
 * to license terms.
 */

package com.cladonia.xngreditor;

import java.util.Date;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import com.l2fprod.common.swing.tips.DefaultTip;
import com.l2fprod.common.swing.tips.DefaultTipModel;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherEvent;
import com.sun.syndication.fetcher.FetcherListener;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.sun.syndication.fetcher.impl.SyndFeedInfo;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * 
 * 
 * @version $Revision: 1.0 $, $Date: 5 Jun 2007 09:54:16 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class LatestNewsModel extends DefaultTipModel {

	private static final boolean DEBUG = false;
	private static final String FEED_FILE = "latest_news.feed";
	private static final String FEED_URL_STRING = "http://www.cladonia.com/tipoftheday/tipoftheday.xml";
	
		
	/**
	 * Initialise the class LatestNewsModel.java
	 */
	public LatestNewsModel() {

		
		// rss
		// http://www.quotationspage.com/data/qotd.rss
		try {
			URL feedUrl = new URL(FEED_URL_STRING);

			try {
				FeedFetcherCache feedInfoCache = HashMapFeedInfoCache
						.getInstance();
				FeedFetcher fetcher = new HttpURLFeedFetcher(feedInfoCache);
				FetcherEventListenerImpl listener = new FetcherEventListenerImpl();
				fetcher.addFetcherEventListener(listener);


				SyndFeed loadedFeed = loadFeed();
				Date loadedFeedDate = null;
				if(loadedFeed != null) {
				
					loadedFeedDate = loadedFeed.getPublishedDate();
				}
				
				if(DEBUG) System.err.println("Retrieving feed " + feedUrl);
				// Retrieve the feed.
				// We will get a Feed Polled Event and then a
				// Feed Retrieved event (assuming the feed is valid)
				SyndFeed feed = fetcher.retrieveFeed(feedUrl);

				if(DEBUG) System.err.println(feedUrl + " retrieved");
				if(DEBUG) System.err.println(feedUrl + " has a title: " + feed.getTitle()
						+ " and contains " + feed.getEntries().size()
						+ " entries.");
				
							
				if(feed != null) {
					
					Date newFeedDate = feed.getPublishedDate();
					
					if((loadedFeedDate == null) || (newFeedDate.getTime() > loadedFeedDate.getTime())) { 
						List entries = feed.getEntries();
						for(int cnt=0;cnt<entries.size();++cnt) {
							SyndEntry entry = (SyndEntry) entries.get(cnt);
							if(DEBUG) System.out.println(" -"+cnt+"- "+entry.getTitle()+"\n\t"+entry.getDescription().getValue());
							
							this.add(new DefaultTip(entry.getTitle(), "<html><body><b>"+entry.getTitle()+"</b><p>"+entry.getDescription().getValue()+"</p></body></html>"));
						}
						
						saveFeed(feed);
					}
					else {
						if(DEBUG) System.out.println("loadedFeedDate: "+loadedFeedDate+" - newFeedDate: "+newFeedDate);
					}
					
				}
				else {
					if(DEBUG) System.out.println("Tipofthedaymodel feed is null");
				}

			} catch (Exception ex) {
				System.out.println("ERROR: " + ex.getMessage());
				if(DEBUG) ex.printStackTrace();
			}

			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void saveFeed(SyndFeed feed) {
		SyndFeedOutput output = new SyndFeedOutput();
		
		File dir = new File( Main.XNGR_EDITOR_HOME);

		if ( !dir.exists()) {
			dir.mkdir();
		}
		
		File file = new File(dir, FEED_FILE);
		try {
			output.output(feed, file);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FeedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SyndFeed loadFeed() {
		File dir = new File( Main.XNGR_EDITOR_HOME);

		if ( !dir.exists()) {
			dir.mkdir();
		}
		
		File file = new File(dir, FEED_FILE);
		
		if(file.exists() == true) {
			
			SyndFeedInput input = new SyndFeedInput(true);
			
			
			try {
				
				SyndFeed feed = input.build(file);
				return(feed);
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (FeedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			
			
		}
		else {
			return(null);
		}
		
		return(null);		
	}
	
	/**
     * @param feedUrl
     * @param urlStr
     * @param method
     * @param feed
     * @return
     * @throws MalformedURLException
     */
    private SyndFeedInfo buildSyndFeedInfo(URL feedUrl, String urlStr, FeedFetcherCache cache, SyndFeed feed, String lastModified, String eTag) throws MalformedURLException {
        SyndFeedInfo syndFeedInfo;
        syndFeedInfo = new SyndFeedInfo();
        
        // this may be different to feedURL because of 3XX redirects
        syndFeedInfo.setUrl(new URL(urlStr));
        syndFeedInfo.setId(feedUrl.toString());                					
                
        	if (cache != null) {
			    // client is setup to use http delta encoding and the server supports it and has returned a delta encoded response
			    // This response only includes new items
			    SyndFeedInfo cachedInfo = cache.getFeedInfo(feedUrl);
			    if (cachedInfo != null) {
				    SyndFeed cachedFeed = cachedInfo.getSyndFeed();
				    
				    // set the new feed to be the orginal feed plus the new items
				    feed = combineFeeds(cachedFeed, feed);			        
			    }            
			}
		
        if (lastModified != null) {
            syndFeedInfo.setLastModified(lastModified);
        }
        
        if (eTag != null) {
            syndFeedInfo.setETag(eTag);
        }
        
        syndFeedInfo.setSyndFeed(feed);
        
        return syndFeedInfo;
    }
    
    /**
	 * <p>Combine the entries in two feeds into a single feed.</p>
	 * 
	 * <p>The returned feed will have the same data as the newFeed parameter, with 
	 * the entries from originalFeed appended to the end of its entries.</p>
	 * 
	 * @param originalFeed
	 * @param newFeed
	 * @return
	 */
	public static SyndFeed combineFeeds(SyndFeed originalFeed, SyndFeed newFeed) {
	    SyndFeed result;
        try {
            result = (SyndFeed) newFeed.clone();
            
            result.getEntries().addAll(result.getEntries().size(), originalFeed.getEntries());
            
            return result;
        } catch (CloneNotSupportedException e) {
            IllegalArgumentException iae = new IllegalArgumentException("Cannot clone feed");
            iae.initCause(e);
            throw iae;
        }        
	}

	/**
	 * Initialise the class LatestNewsModel.java
	 * 
	 * @param arg0
	 */
	public LatestNewsModel(Tip[] arg0) {

		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Initialise the class LatestNewsModel.java
	 * 
	 * @param arg0
	 */
	public LatestNewsModel(Collection arg0) {

		super(arg0);
		// TODO Auto-generated constructor stub
	}

	static class FetcherEventListenerImpl implements FetcherListener {

		/**
		 * @see com.sun.syndication.fetcher.FetcherListener#fetcherEvent(com.sun.syndication.fetcher.FetcherEvent)
		 */
		public void fetcherEvent(FetcherEvent event) {

			String eventType = event.getEventType();
			if (FetcherEvent.EVENT_TYPE_FEED_POLLED.equals(eventType)) {
				//System.err.println("\tEVENT: Feed Polled. URL = "+ event.getUrlString());
			} else if (FetcherEvent.EVENT_TYPE_FEED_RETRIEVED.equals(eventType)) {
				//System.err.println("\tEVENT: Feed Retrieved. URL = "+ event.getUrlString());
			} else if (FetcherEvent.EVENT_TYPE_FEED_UNCHANGED.equals(eventType)) {
				//System.err.println("\tEVENT: Feed Unchanged. URL = " + event.getUrlString());
			}
		}
	}

}
